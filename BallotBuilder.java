package TextEditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BallotBuilder {
    private PreambleBuilder preamble;
    private List<PropositionBuilder> propositions;

    public BallotBuilder() {}

    public BallotBuilder(
            PreambleBuilder preamble,
            List<PropositionBuilder> propositions) {
        this.preamble = preamble;
        this.propositions = propositions;
    }

    public PreambleBuilder getPreamble() {
        return this.preamble;
    }

    public List<PropositionBuilder> getPropositions() {
        return propositions;
    }

    public void buildJSON() {
        ObjectMapper mapper = new ObjectMapper();
        String timeCreated = LocalDate.now().toString() + LocalTime.now().toString();
        String fileName = ("Ballot_SD_Card" + File.separator
                + preamble.getBallotID() + "_" + timeCreated + ".json");
        File directory = new File("Ballot_SD_Card");

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
//            mapper.writeValue(new File(fileName), this);

            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(fileName), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
