package Test;

import BML.Ballot;
import BML.Proposition;
import javafx.util.Pair;

public class TestBallotOperations {
    public static void main(String[] args) {
        String jsonString = "{\n" +
                "\"ballot\": {\n" +
                "\t\"preamble\": {\n" +
                "\t\t\"ballotTitle\": \"General Election Ballot\",\n" +
                "\t\t\"county\": \"Bernalillo\",\n" +
                "\t\t\"state\": \"NM\",\n" +
                "\t\t\"ballotID\": \"Bernalillo0123456\",\n" +
                "\t\t\"endDate\": \"04/13/2025\"\n" +
                "\t},\n" +
                "\t\"propositions\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 0,\n" +
                "\t\t\t\"title\": \"2025 Presidential Election\",\n" +
                "\t\t\t\"description\": \"\",\n" +
                "\t\t\t\"maxSelections\": 1,\n" +
                "\t\t\t\"options\": [\n" +
                "\t\t\t\t\"Alice (Democrat)\",\n" +
                "\t\t\t\t\"Bob (Republican)\",\n" +
                "\t\t\t\t\"Carol (Independent)\"\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                "}";

        // Create a ballot from the JSON
        Ballot ballot = new Ballot(jsonString);

        System.out.println("Initial Ballot State");
        displayBallot(ballot);

        // Test 1: Mark an option in the ballot
        System.out.println("\nAfter Marking Option");
        ballot.markOption(0, 1, true); // Mark the second option (Bob) as selected
        displayBallot(ballot);

        // Test 2: Extract propositions
        System.out.println("\nExtracted Propositions");
        for (Proposition prop : ballot) {
            System.out.println("Proposition ID: " + prop.getId());
            System.out.println("Title: " + prop.getTitle());
        }

        // Test 3: Extract a proposition template with reset selections
        System.out.println("\nExtract Proposition Template");
//        Proposition template = ballot.extractPropositionTemplate(0);
//        System.out.println("Template Proposition ID: " + template.getId());
//        System.out.println("Template Title: " + template.getTitle());
//        System.out.println("Template Options:");
//        for (int i = 0; i < template.getNumOptions(); i++) {
//            System.out.println("  - " + template.getOption(i) + ": " + template.getSelectedOption(i));
//        }

        // Verify the original proposition wasn't changed
        System.out.println("\nVerify Original Proposition");
        displayBallot(ballot);
    }

    private static void displayBallot(Ballot ballot) {
        for (Proposition p : ballot) {
            System.out.println("Proposition: " + p.getTitle() + " (ID: " + p.getId() + ")");
//            for (Pair<String, Boolean> option : p) {
//                System.out.println("  - " + option.getKey() + ": " + option.getValue());
//            }
        }
    }
}
