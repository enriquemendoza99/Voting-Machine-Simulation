package Test;

import BML.Ballot;
import BML.Proposition;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        // ------------------------------------------------------
        // Reading the JSON file into a single string
        // ------------------------------------------------------
        String filename = "test.json";
        Path filePath = Paths.get(filename);
        StringBuilder content = new StringBuilder();

        try {
            if (Files.exists(filePath)) {
                Files.lines(filePath).forEach(content::append);
            } else {
                System.err.println("Error: File not found at " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // ------------------------------------------------------
        // Creating an instance of the Ballot class
        // ------------------------------------------------------
        String jsonString = content.toString();
        // Pass string to Ballot constructor
        Ballot ballot = new Ballot(jsonString);

        // ------------------------------------------------------
        // Displaying the preamble
        // ------------------------------------------------------
        System.out.println("Ballot Title: " + ballot.getPreamble().getBallotTitle());
        System.out.println("Title: " + ballot.getPreamble().getBallotTitle());
        System.out.println("Location: " + ballot.getPreamble().getCounty() + ", " + ballot.getPreamble().getState());
        System.out.println("ID: " + ballot.getPreamble().getBallotID());
        System.out.println("End Date:" + ballot.getPreamble().getEndDate());
        System.out.println();

        System.out.println("First Proposition: " + ballot.getProposition(0));
        System.out.println();

        // Exaple 1: Using for-each loop to iterate through propositions and options
        System.out.println("Example 1: For-each loop");
        System.out.println("============================================");
        for (Proposition p : ballot) {
            System.out.println("\nPropositionID: " + p.getId());
            System.out.println("Title: " + p.getTitle());
            System.out.println("Description: " + p.getDescription());
            System.out.println("MaxSelections: " + p.getMaxSelections());
            System.out.println("Options:");
            for (String option : p) {
                System.out.println("  - " + option + " is selected = " + p.isSelected(option));
            }
        }

        selectSampleOptions(ballot);

        // Example 2: Using forEach method
        System.out.println();
        System.out.println("Example 2");
        System.out.println("============================================");
        ballot.forEach(p -> {
            System.out.println("\nProposition: " + p.getTitle() + " (ID: " + p.getId() + ")");
            System.out.println("Selected options");

            p.forEach(option -> {
                String status = p.isSelected(option) ? "selected" : "not selected";
                System.out.println("  - " + option + " [" + status + "]");
            });

            int selectedCount = countSelectedOptions(p);
            System.out.println("Total selections: " + selectedCount);
        });

    }

    private static void selectSampleOptions (Ballot ballot) {
        Proposition presidential = ballot.getProposition(0);
        int bobIndex = presidential.getIndexOfOption("Bob (Republican)");
        presidential.markOption(bobIndex, true);

        // selecting "Bob" for the first proposition
        ballot.markOption(0, 1, true);

        // marking "Yes" for the second proposition
        ballot.markOption(1, 0, true);

        // selecting "Justine", "Mike", and "Craig" for the third proposition
        ballot.markOption(2, 0, true);
        ballot.markOption(2, 2, true);
        ballot.markOption(2, 4, true);
    }

    private static int countSelectedOptions(Proposition p) {
        int count = 0;
        for (String option : p) {
            if (p.isSelected(option)) {
                count++;
            }
        }
        return count;
    }
}