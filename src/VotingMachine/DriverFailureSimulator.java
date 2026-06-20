package VotingMachine;

import Drivers.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriverFailureSimulator implements Runnable {
    private final Battery battery;
    private final CardReader cardReader;
    private final LLSensor latch;
    private final Printer printer;
    private final SDCard sd1;
    private final SDCard sd2;
    private final SDCard sd3;
    private final ScreenDriver screenDriver;

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public DriverFailureSimulator(
            Battery battery,
            CardReader cardReader,
            LLSensor latch,
            Printer printer,
            SDCard sd1,
            SDCard sd2,
            SDCard sd3,
            ScreenDriver screenDriver
    ) {
        this.battery = battery;
        this.cardReader = cardReader;
        this.latch = latch;
        this.printer = printer;
        this.sd1 = sd1;
        this.sd2 = sd2;
        this.sd3 = sd3;
        this.screenDriver = screenDriver;
    }

    /**
     * Display a menu of drivers that can be failed
     */
    private void displayMenu() {
        System.out.println("\nDRIVER FAILURE SIMULATOR");
        System.out.println("Select a driver to fail:");
        System.out.println("1. Battery");
        System.out.println("2. Card Reader");
        System.out.println("3. Latch Sensor");
        System.out.println("4. Printer");
        System.out.println("5. SD Card 1 (Ballot)");
        System.out.println("6. SD Card 2 (Voting Storage 1)");
        System.out.println("7. SD Card 3 (Voting Storage 2)");
        System.out.println("8. Screen Driver");
        System.out.println("9. Exit Simulator");
        System.out.println("10. Reset All Drivers");
        System.out.print("Enter your choice (1-10): ");
    }

    /**
     * Process user input to trigger driver failures
     */
    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                battery.setFailure(true);
                System.out.println("Battery failure triggered");
                break;
            case 2:
                cardReader.setFailure(true);
                System.out.println("Card Reader failure triggered");
                break;
            case 3:
                latch.setFailure(true);
                System.out.println("Latch Sensor failure triggered");
                break;
            case 4:
                printer.setFailure(true);
                System.out.println("Printer failure triggered");
                break;
            case 5:
                sd1.setFailure(true);
                System.out.println("SD Card 1 (Ballot) failure triggered");
                break;
            case 6:
                sd2.setFailure(true);
                System.out.println("SD Card 2 (Voting Storage 1) failure triggered");
                break;
            case 7:
                sd3.setFailure(true);
                System.out.println("SD Card 3 (Voting Storage 2) failure triggered");
                break;
            case 8:
                // Screen driver cannot be directly set to fail,
                // but we can eject/remove SD cards to cause issues
                System.out.println("Screen Driver failure cannot be directly simulated");
                break;
            case 9:
                System.out.println("Exiting failure simulator...");
                running = false;
                break;
            case 10:
                resetAllDrivers();
                System.out.println("All drivers reset to normal operation");
                break;
            default:
                System.out.println("Invalid choice. Please enter a number between 1 and 10.");
        }
    }

    /**
     * Reset all drivers to their normal operation state
     */
    private void resetAllDrivers() {
        battery.setFailure(false);
        cardReader.setFailure(false);
        latch.setFailure(false);
        printer.setFailure(false);
        sd1.setFailure(false);
        sd2.setFailure(false);
        sd3.setFailure(false);
        // Screen driver cannot be reset directly
    }

    /**
     * Main simulation loop
     */
    @Override
    public void run() {
        System.out.println("Starting Driver Failure Simulator...");

        while (running) {
            displayMenu();
            int choice;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                processChoice(choice);

                // If choice is 3 (latch sensor), offer additional options
                if (choice == 3) {
                    System.out.println("\nWould you like to simulate door operations? (y/n)");
                    String doorChoice = scanner.nextLine();
                    if (doorChoice.toLowerCase().startsWith("y")) {
                        simulateDoorOperations();
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }

            try {
                // Give some time for the failure to be detected
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Simulator interrupted");
            }
        }
    }

    /**
     * Simulate specific behaviors of the machines for demonstration
     */
    public void simulateDoorOperations() {
        System.out.println("\nDOOR OPERATIONS SIMULATOR");
        System.out.println("1. Open Door");
        System.out.println("2. Close Door");
        System.out.println("3. Latch Door");
        System.out.println("4. Unlatch Door");
        System.out.println("5. Back to main menu");
        System.out.print("Enter your choice (1-5): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    if (latch.openDoor()) {
                        System.out.println("Door opened successfully");
                    } else {
                        System.out.println("Failed to open door. Make sure it's unlatched first.");
                    }
                    break;
                case 2:
                    if (latch.closeDoor()) {
                        System.out.println("Door closed successfully");
                    } else {
                        System.out.println("Failed to close door. Door might already be closed.");
                    }
                    break;
                case 3:
                    if (latch.latch()) {
                        System.out.println("Door latched successfully");
                    } else {
                        System.out.println("Failed to latch door. Make sure door is closed first.");
                    }
                    break;
                case 4:
                    if (latch.unlatch()) {
                        System.out.println("Door unlatched successfully");
                    } else {
                        System.out.println("Failed to unlatch door. Make sure door is closed.");
                    }
                    break;
                case 5:
                    // Do nothing, just go back to main menu
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear the invalid input
        }
    }
    public static void main(String[] args) {
        // This would typically be initialized with real drivers from the main application
        // Here we create placeholder instances just for demo purposes
        Battery battery = new Battery();
        CardReader cardReader = new CardReader();
        LLSensor latch = new LLSensor();
        Printer printer = new Printer();

        // Initialize with dummy SD cards
        SDCard sd1 = new SDCard(0, SDCard.Operation.read);
        SDCard sd2 = new SDCard(1, SDCard.Operation.write);
        SDCard sd3 = new SDCard(2, SDCard.Operation.write);

        ScreenDriver screenDriver = null;
        try {
            screenDriver = new ScreenDriver();
        } catch (Exception e) {
            System.out.println("Failed to initialize ScreenDriver, using null reference");
        }

        DriverFailureSimulator simulator = new DriverFailureSimulator(
                battery, cardReader, latch, printer, sd1, sd2, sd3, screenDriver
        );

        // Run simulator in its own thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(simulator);

        // Cleanly shut down when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down simulator...");
            executor.shutdown();
        }));
    }
}