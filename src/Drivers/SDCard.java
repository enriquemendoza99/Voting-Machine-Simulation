package Drivers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class SDCard {
    private final String filepath;
    private Operation operation;
    private boolean status = false;
    private final Operation orginalOperation; // Demo only
    public SDCard(int slotNumber, Operation operation) {
        this.filepath = switch(slotNumber) {
            case 0 -> "resources/slot1.txt";
            case 1 -> "resources/slot2.txt";
            case 2 -> "resources/slot3.txt";
            default -> throw new IllegalStateException("Unexpected value: " + slotNumber);
        };
        this.operation = operation;
        orginalOperation = operation;
    }

    public List<String> read() throws IOException {
        if (operation == Operation.read) {
            return Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8);
        }
        else if (operation == null) throw new IOException("No SD card in slot");
        else throw new IOException("Unable to read from file as operation is not read");
    }

    public void write(String text) throws IOException {
        if (operation == Operation.write) {
            Path file = Paths.get(filepath);
            List<String> txt = Files.readAllLines(file,StandardCharsets.UTF_8);
            txt.add(text);
            Files.write(file, txt, StandardCharsets.UTF_8);
        }
        else if (operation == null) throw new IOException("No SD card in slot");
        else throw new IOException("Unable to write from file as operation is not write");
    }

    /**
     * Overwrites all text in the file with the given line. Consecutive calls of overwrite will overwrite the last call.
     * @param text String to overwrite with
     * @throws IOException Incorrect operating mode or SD card is not in slot
     */
    public void overwrite(String text) throws IOException {
        if (operation == Operation.overwrite) {
            Path file = Paths.get(filepath);
            Files.write(file, Collections.singleton(text), StandardCharsets.UTF_8);
        }
        else if (operation == null) throw new IOException("No SD card in slot");
        else throw new IOException("Unable to overwrite from file as operation is not overwrite");
    }

    public void eject() {
        operation = null;
    }

    public boolean failure() {
        return status;
    }

    public enum Operation {
        read, write, overwrite
    }

    public void setFailure(boolean status) {
        this.status = status;
    }

    public void reinsert() {
        operation = orginalOperation;
    }
}
