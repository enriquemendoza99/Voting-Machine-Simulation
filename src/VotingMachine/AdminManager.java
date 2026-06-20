package VotingMachine;

import Drivers.LLSensor;
import Drivers.ScreenDriver;
import Drivers.CardReader;
import Display.ButtonData;
import Display.Template;

/**
 * Manages the election lifecycle when an admin card is inserted, guiding
 * the administrator through election setup, voting setup, and election
 * closure via a state machine.
 */
public class AdminManager {
    private LLSensor latch;
    private ScreenDriver screenDriver;
    private CardReader cardReader;
    private boolean electionOpen = false;
    private boolean votingOpen = false;
    private AdminState currentState = AdminState.WAITING_FOR_CARD;

    // Cached "insert card" template — safe to reuse since it has no
    // clickable options and therefore no ButtonData state that can go stale.
    private final Template insertCardTemplate;

    public AdminManager(
            LLSensor latch,
            ScreenDriver screenDriver,
            CardReader cardReader
    ) {
        this.latch = latch;
        this.screenDriver = screenDriver;
        this.cardReader = cardReader;
        this.insertCardTemplate = buildInsertCardTemplate();
    }

    /**
     * Starts the admin process when an admin card is detected. Builds a
     * fresh set of templates for this session so no button state can
     * carry over from a previous admin session.
     */
    public void run() {
        currentState = AdminState.ELECTION_SETUP;

        Template electionSetUp = buildElectionSetupTemplate();
        Template votingSetUp = buildVotingSetupTemplate();
        Template electionClosedTemplate = buildElectionClosedTemplate();
        Template removeCardTemplate = buildRemoveCardTemplate();

        sendTemplateAndClearStaleButtons(electionSetUp);

        boolean adminSessionActive = true;
        while (adminSessionActive) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Admin Manager interrupted");
            }

            Boolean[] pressedButtons = screenDriver.getPressedButtons();
            if (pressedButtons == null) continue;

            switch (currentState) {
                case WAITING_FOR_CARD:
                    break;

                case ELECTION_SETUP:
                    if (pressedButtons[6] && pressedButtons[0]) {
                        electionOpen = true;
                        currentState = AdminState.VOTING_SETUP;
                        sendTemplateAndClearStaleButtons(votingSetUp);
                    }
                    break;

                case VOTING_SETUP:
                    if (pressedButtons[6]) {
                        if (pressedButtons[0] && !pressedButtons[1]) {
                            votingOpen = true;
                            currentState = AdminState.WAITING_FOR_CARD;
                            adminSessionActive = false;
                            screenDriver.sendTemplate(removeCardTemplate);
                        } else if (pressedButtons[1] && !pressedButtons[0]) {
                            electionOpen = false;
                            votingOpen = false;
                            currentState = AdminState.ELECTION_CLOSED;
                            sendTemplateAndClearStaleButtons(electionClosedTemplate);
                        }
                    }
                    break;

                case VOTING_ACTIVE:
                    if (pressedButtons[6] && pressedButtons[0]) {
                        votingOpen = false;
                        currentState = AdminState.VOTING_SETUP;
                        sendTemplateAndClearStaleButtons(votingSetUp);
                    }
                    break;

                case ELECTION_CLOSED:
                    if (pressedButtons[6]) {
                        adminSessionActive = false;
                    }
                    break;
            }
        }
    }

    /**
     * Sending a template triggers an asynchronous UI update on the JavaFX
     * side, which can return before the new screen actually renders. If
     * getPressedButtons() is called too soon afterward, it can read stale
     * button values left over from the previous screen. This method sends
     * the template, then discards one getPressedButtons() read before the
     * caller starts trusting the results, "flushing" any stale state.
     */
    private void sendTemplateAndClearStaleButtons(Template template) {
        screenDriver.sendTemplate(template);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        screenDriver.getPressedButtons();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Template buildElectionSetupTemplate() {
        Template t = new Template(
                "Election Setup",
                "Follow these steps before starting the election process: \n" +
                        " - Open back door and insert all SD cards and paper trail cartridge.\n" +
                        " - Close the door.",
                "Select \"Open Election\" once all steps are completed, and select \"Submit\"",

                new ButtonData(true, "Open Election"),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),

                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );
        t.setMaxSelections(1);
        return t;
    }

    private Template buildVotingSetupTemplate() {
        Template t = new Template(
                "Voting Setup",
                "Select \"Open Voting\" to begin the voting session \n" +
                        "or select \"Close Election\" to close election session.",
                "Please select only one option below: ",

                new ButtonData(true, "Open Voting"),
                new ButtonData(true, "Close Election"),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),

                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );
        t.setMaxSelections(1);
        return t;
    }

    private Template buildRemoveCardTemplate() {
        return new Template(
                "",
                "Please Remove Supervisor Card.",
                "",

                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),

                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false)
        );
    }

    private Template buildElectionClosedTemplate() {
        return new Template(
                "Election Closed",
                "All ballots have been recorded.",
                "Select \"Submit\" to power off the voting machine.",

                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),

                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );
    }

    private Template buildInsertCardTemplate() {
        return new Template(
                "",
                "Please Insert Supervisor Card or Voter Card.",
                "",

                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),

                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false)
        );
    }

    public boolean isElectionOpen() {
        return electionOpen;
    }

    public boolean isVotingOpen() {
        return votingOpen;
    }

    public void setState(AdminState state) {
        this.currentState = state;
        if (state == AdminState.WAITING_FOR_CARD) {
            screenDriver.sendTemplate(insertCardTemplate);
        }
    }

    public Template getInsertCardTemplate() {
        return insertCardTemplate;
    }

    public enum AdminState {
        WAITING_FOR_CARD,
        ELECTION_SETUP,
        VOTING_SETUP,
        VOTING_ACTIVE,
        ELECTION_CLOSED
    }
}
