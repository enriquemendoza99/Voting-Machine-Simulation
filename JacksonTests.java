package Test;

import TextEditor.BallotBuilder;
import TextEditor.PreambleBuilder;
import TextEditor.PropositionBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class JacksonTests {
    public static void main(String[] args) {
        // Here we are only creating some instances of the builder
        // classes for the example
        PreambleBuilder preamble = new PreambleBuilder(
                "Test Ballot",
                "Test County",
                "Test State",
                "Test Ballot ID",
                "Test End Date"
        );

        PropositionBuilder proposition1 = new PropositionBuilder(
                1,
                "Test Proposition",
                "Test Description",
                new ArrayList<>(Arrays.asList("Test Option1", "test option2", "test option3")),
                1
        );PropositionBuilder proposition2 = new PropositionBuilder(
                1,
                "Test Proposition",
                "Test Description",
                new ArrayList<>(Arrays.asList("Test Option1", "test option2")),
                1
        );

        BallotBuilder ballot = new BallotBuilder(
                preamble,
                new ArrayList<>(Arrays.asList(proposition1, proposition2))
        );

        // Create a new ObjectMapper (this is used by Jackson to read/write JSON)
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Write the BallotBuilder object to a JSON file
            mapper.writeValue(new File("ballot.json"), ballot);

            // use this to write pretty-printed JSON (more readable)
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("ballot_pretty.json"), ballot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
