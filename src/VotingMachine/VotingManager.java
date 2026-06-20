package VotingMachine;

import BML.Ballot;
import BML.Proposition;
import Display.ButtonData;
import Drivers.ScreenDriver;
import Drivers.SDCard;
import Display.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * Manages a single voter's session: navigating the ballot, recording
 * selections, and writing the completed ballot redundantly to two SD
 * cards. A fresh ballot copy and fresh display templates are generated
 * for every voter session to guarantee complete isolation between voters.
 */
public class VotingManager implements Runnable {
    private final Ballot blankBallot;
    private final SDCard sdcard1;
    private final SDCard sdcard2;
    private final SDCard sdcard3;
    private final ScreenDriver screenDriver;

    private VotingProcessState votingState = VotingProcessState.NOT_IN_PROGRESS;

    public VotingManager(
            Ballot blankBallot,
            SDCard sdcard1,
            SDCard sdcard2,
            SDCard sdcard3,
            ScreenDriver screenDriver
    ) {
        this.blankBallot = blankBallot;
        this.sdcard1 = sdcard1;
        this.sdcard2 = sdcard2;
        this.sdcard3 = sdcard3;
        this.screenDriver = screenDriver;
    }

    public boolean notStarted() {
        return votingState == VotingProcessState.NOT_IN_PROGRESS;
    }

    public boolean isVotingDone() {
        if (votingState == VotingProcessState.DONE) {
            votingState = VotingProcessState.NOT_IN_PROGRESS;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        this.votingState = VotingProcessState.VOTING_IN_PROGRESS;

        Ballot voterBallot = loadFreshBallot();
        if (voterBallot == null) {
            System.out.println("VotingManager: Failed to load fresh ballot, aborting.");
            this.votingState = VotingProcessState.DONE;
            return;
        }

        List<Template> templates = createTemplates(voterBallot);

        boolean submitBallot = votingProcess(voterBallot, templates);

        if (submitBallot) {
            voteRecord(voterBallot);
        }

        this.votingState = VotingProcessState.DONE;
    }

    /**
     * Loads a fresh copy of the ballot from the SD card so each voter
     * gets an independent ballot with no pre-selected options.
     */
    private Ballot loadFreshBallot() {
        try {
            List<String> lines = sdcard1.read();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) sb.append(line);
            return new Ballot(sb.toString());
        } catch (IOException e) {
            System.out.println("VotingManager: Error reading ballot from SD card: " + e.getMessage());
            return null;
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

    private boolean votingProcess(Ballot voterBallot, List<Template> templates) {
        long timeout = 120;
        int propositionIndex = 0;

        sendTemplateAndClearStaleButtons(templates.get(0));

        long start = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());

        while (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - start < timeout) {
            Boolean[] buttonsPressed = this.screenDriver.getPressedButtons();

            if (buttonsPressed[7] || buttonsPressed[5]) {
                Proposition p = voterBallot.getProposition(propositionIndex);

                for (int i = 0; i < p.getNumOptions(); i++) {
                    p.markOption(i, buttonsPressed[i]);
                }

                if (buttonsPressed[7])
                    propositionIndex++;
                else
                    propositionIndex--;

                sendTemplateAndClearStaleButtons(templates.get(propositionIndex));

                start = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());

            } else if (buttonsPressed[6]) {
                return true;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Voting Process: Thread interrupted");
            }
        }
        return false;
    }

    private void voteRecord(Ballot voterBallot) {
        StringBuilder sb = new StringBuilder();
        for (Proposition p : voterBallot) {
            sb.append(p).append("\n");
            for (String option : p) {
                if (p.isSelected(option)) {
                    sb.append(option).append("\n");
                }
            }
        }
        sb.append("\n");
        try {
            sdcard2.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Vote Recorder: error writing to SD card 2");
        }
        try {
            sdcard3.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Vote Recorder: error writing to SD card 3");
        }
    }

    /**
     * Builds a brand new list of Templates (with brand new ButtonData
     * objects) for this voter's session, so no button "pressed" state
     * can carry over from a previous voter.
     */
    private List<Template> createTemplates(Ballot ballot) {
        List<Template> templates = new ArrayList<>();
        String[] numToString = {"one", "two", "three", "four"};
        ListIterator<Proposition> it = ballot.iterator();
        while (it.hasNext()) {
            int i;
            Proposition p = it.next();
            int numOptions = p.getNumOptions();
            int numSelections = p.getMaxSelections();
            ButtonData[] buttons = new ButtonData[5];

            for (i = 0; i < numOptions; i++) {
                buttons[i] = new ButtonData(true, p.getOption(i));
            }
            for (int j = i; j < 5; j++) {
                buttons[j] = new ButtonData(false);
            }
            String instructions = (numSelections == 1) ?
                    "Please select only one option below:" :
                    "Please select " + numToString[numSelections - 1] + " options below:";

            Template template = new Template(
                    p.getTitle(),
                    p.getDescription(),
                    instructions,
                    buttons[0],
                    buttons[1],
                    buttons[2],
                    buttons[3],
                    buttons[4],
                    new ButtonData(it.hasPrevious()),
                    new ButtonData(false),
                    new ButtonData(true)
            );
            template.setMaxSelections(p.getMaxSelections());
            templates.add(template);
        }

        Template confirmation = new Template(
                "Ballot Submission",
                "Press the \"Submit\" button to confirm your selections,\n or press the \"Previous\" button to verify or " +
                        "change your selections.",
                "",
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(true),
                new ButtonData(false)
        );
        templates.add(confirmation);
        return templates;
    }

    public enum VotingProcessState {
        NOT_IN_PROGRESS,
        VOTING_IN_PROGRESS,
        DONE
    }
}
