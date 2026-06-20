package VotingMachine;

import BML.Ballot;
import Display.*;
import Drivers.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    // BUG FIX: Original pool size was 2, causing thread starvation once
    // monitor, cardHolderTerminal, adminManager, and votingManager are all
    // submitted concurrently. Increased to 6.
    private static ExecutorService executor = Executors.newFixedThreadPool(6);
    private static Template defaultTemplate;

    public static void main(String[] args) {
        LLSensor latch = new LLSensor();
        SDCard ballotSDCard = new SDCard(0, SDCard.Operation.read);
        SDCard votingSDCard1 = new SDCard(1, SDCard.Operation.write);
        SDCard votingSDCard2 = new SDCard(2, SDCard.Operation.write);
        Battery battery = new Battery();
        Printer printer = new Printer();
        ScreenDriver screenDriver;
        StringBuilder ballotString = new StringBuilder();

        try {
            screenDriver = new ScreenDriver();
        } catch (IOException e) {
            System.out.println("Error initializing socket for the display");
            return;
        }

        try {
            List<String> ballot = ballotSDCard.read();
            for (String str : ballot) {
                ballotString.append(str);
            }
        } catch (IOException e) {
            System.out.println("Error reading ballot file");
            return;
        }
        System.out.println(ballotString);

        Ballot blankBallot = new Ballot(ballotString.toString());

        Monitor monitor = new Monitor(
                latch,
                ballotSDCard,
                votingSDCard1,
                votingSDCard2,
                screenDriver,
                battery,
                printer
        );

        CardReader adminCardReader = new CardReader();

        AdminManager adminManager = new AdminManager(
                latch,
                screenDriver,
                adminCardReader
        );

        VotingManager votingManager = new VotingManager(
                blankBallot,
                ballotSDCard,
                votingSDCard1,
                votingSDCard2,
                screenDriver
        );

        CardHolder cardHolder = new CardHolder();
        CardHolderTerminal cardHolderTerminal = new CardHolderTerminal(cardHolder);

        DriverFailureSimulator failureSimulator = new DriverFailureSimulator(
                battery,
                cardHolder.getCardReader(),
                latch,
                printer,
                ballotSDCard,
                votingSDCard1,
                votingSDCard2,
                screenDriver
        );

        if (screenDriver.isReady()) {
            defaultTemplate = new Template(
                    "",
                    "",
                    "Please Insert Supervisor Card or Voter Card."
            );
            screenDriver.sendTemplate(defaultTemplate);
        }

        votingControl(
                monitor,
                adminManager,
                votingManager,
                cardHolder,
                cardHolderTerminal,
                screenDriver
        );
    }

    private static void votingControl(
            Monitor monitor,
            AdminManager adminManager,
            VotingManager votingManager,
            CardHolder cardHolder,
            CardHolderTerminal cardHolderTerminal,
            ScreenDriver screenDriverDriver
    ) {
        executor.execute(monitor);
        executor.execute(cardHolderTerminal);

        adminManager.setState(AdminManager.AdminState.WAITING_FOR_CARD);

        // BUG FIX: tracks whether the admin session task is currently
        // running, so we know exactly when it finishes instead of
        // guessing with a fixed Thread.sleep(1000). Without this, the
        // card terminal was never re-submitted after the admin session
        // actually completed, leaving the system stuck with no prompt.
        Future<?> adminTask = null;
        boolean waitingForNewCard = false;

        while (true) {
            if (monitor.getFailure()) {
                System.out.println("System failure detected.");
                break;
            }

            if (cardHolder.failure()) {
                System.out.println("Card reader failure detected.");
                continue;
            }

            // Check if a previously running admin session has finished
            if (adminTask != null && adminTask.isDone()) {
                System.out.println("Main: Admin session finished, ejecting card");
                cardHolder.ejectCard();
                adminTask = null;
                waitingForNewCard = true;
            }

            if (cardHolder.hasCard()) {
                CardHolder.CardType cardType = cardHolder.getType();

                switch (cardType) {
                    case ADMIN:
                        if (adminTask == null) {
                            System.out.println("Admin card detected");
                            adminTask = executor.submit(adminManager::run);
                        }
                        break;

                    case VOTER:
                        System.out.println("Main: Voter card detected");
                        if (adminManager.isElectionOpen() && adminManager.isVotingOpen()) {
                            if (votingManager.notStarted()) {
                                System.out.println("Main: Starting voting process");
                                executor.execute(votingManager);
                            } else if (votingManager.isVotingDone()) {
                                System.out.println("Main: Voting process done");
                                cardHolder.ejectCard();
                                System.out.println("Card ejected");
                                screenDriverDriver.sendTemplate(defaultTemplate);
                                waitingForNewCard = true;
                            }
                        } else {
                            Template errorTemplate = new Template(
                                    "Voting Error",
                                    "Voting is not currently open. Please remove your card and try again later.",
                                    "",
                                    new ButtonData(false),
                                    new ButtonData(false),
                                    new ButtonData(false),
                                    new ButtonData(false),
                                    new ButtonData(false),
                                    new ButtonData(false),
                                    new ButtonData(true, "OK"),
                                    new ButtonData(false)
                            );
                            screenDriverDriver.sendTemplate(errorTemplate);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("Error wait interrupted");
                            }
                            cardHolder.ejectCard();
                            waitingForNewCard = true;
                        }
                        break;

                    case NONE:
                        System.out.println("Card type detected is NONE. Ejecting");
                        break;

                    default:
                        System.out.println("Unknown card type");
                        break;
                }
            } else {
                if (screenDriverDriver.isReady()) {
                    screenDriverDriver.sendTemplate(adminManager.getInsertCardTemplate());
                }
                // Re-prompt for a new card once the previous session
                // is fully done and the card has been ejected
                if (waitingForNewCard) {
                    System.out.println("Main: Ready for a new card");
                    executor.execute(cardHolderTerminal);
                    waitingForNewCard = false;
                }
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println("Voting control thread interrupted");
            }
        }

        System.out.println("Voting Control: Voting machine shutting down");
        monitor.presentFailureTemplate();

        boolean shutdownConfirmed = false;
        while (!shutdownConfirmed) {
            Template template = screenDriverDriver.returnTemplate();
            if (template != null) {
                Boolean[] pressedButtons = screenDriverDriver.getPressedButtons();
                if (pressedButtons != null && pressedButtons[6]) {
                    shutdownConfirmed = true;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Shutdown wait interrupted");
            }
        }

        System.out.println("Voting machine shutdown complete");
        System.exit(0);
    }
}
