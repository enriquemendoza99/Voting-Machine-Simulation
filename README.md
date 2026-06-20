# Voting Machine Simulation — Java/JavaFX

A complete software simulation of a secure electronic voting machine,
modeling the hardware abstraction layer, election lifecycle state
machine, fault detection system, ballot markup language parser, and
JavaFX display interface. Runs as two separate JVM processes
communicating over a local socket.

## Project Structure
src/

BML/              — Ballot JSON parser (Ballot, Proposition, Preamble)

Drivers/          — Hardware abstraction layer (7 drivers)

Display/          — JavaFX display: Template, ButtonData, SocketHandler

VotingMachine/    — Logic controller, state machines, fault monitor

TextEditor/       — Ballot creation GUI

Test/             — Unit tests for BML parsing

resources/

slot1.txt    — Ballot SD card (read mode)

slot2.txt    — Vote storage SD card 1 (write mode)

slot3.txt    — Vote storage SD card 2 (write mode)

## How to Run

**Step 1 — Start the display server:**
java VotingMachine.MainJavaFX

**Step 2 — Start the logic controller (separate terminal):**
java VotingMachine.Main

**Step 3 — Optional: test hardware failures (separate terminal):**
java VotingMachine.DriverFailureSimulator

If using a Maven project with JavaFX as a dependency, add these JVM
options to each run configuration:
--module-path "<path-to-javafx-sdk>\lib" --add-modules javafx.controls,javafx.fxml

## Election Lifecycle

1. **Admin — Election setup:** Insert Admin card, select "Open Election", Submit
2. **Admin — Voting setup:** Select "Open Voting", Submit
3. **Voter session:** Insert Voter card, navigate propositions with
   Next/Previous, Submit when done
4. **Vote recording:** Ballot is written redundantly to `slot2.txt` and `slot3.txt`
5. **Admin — Close election:** Insert Admin card, select "Close Election", Submit

## Ballot Format (BML)

```json
{
  "preamble": {
    "ballotTitle": "General Election",
    "county": "Bernalillo",
    "state": "NM",
    "ballotID": "NM2025001",
    "endDate": "11/04/2025"
  },
  "propositions": [
    {
      "id": 0,
      "title": "Presidential Election",
      "description": "Vote for one candidate",
      "maxSelections": 1,
      "options": ["Alice", "Bob", "Carol"]
    }
  ]
}
```

## Creating a Ballot
java TextEditor.BallotEditor

## Hardware Failure Detection
The `Monitor` thread continuously polls all hardware drivers. On any
failure, the system halts and displays an error screen.

## Testing Notes

The system was verified through multiple complete election cycles with
independent voter and admin sessions, confirming each session's recorded
selections remain isolated from other sessions and that redundant SD
card storage stays consistent.
