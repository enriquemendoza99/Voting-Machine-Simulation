package Drivers;

import java.io.IOException;

public class CardReader {
    private String cardData = null;
    private boolean isCardInserted = false;
    private boolean status = false;

    public CardReader() {}

    public boolean isCardPresent() {
        return isCardInserted;
    }

    public String readCard() throws IOException {
        if (!isCardInserted || cardData == null) {
            throw new IOException("No card present to read.");
        }
        return cardData;
    }

    public void eraseCard() throws IOException {
        if (!isCardInserted || cardData == null) {
            throw new IOException("No card present to erase.");
        }
        cardData = "";
    }

    public void ejectCard() throws IOException {
        if (!isCardInserted) {
            throw new IOException("No card present to eject.");
        }
        isCardInserted = false;
        cardData = null;
    }

    public boolean failure() {
        return status;
    }

    // Demo functions below
    public void setFailure(boolean status) {
        this.status = status;
    }

    public void insertCard(String cardData) throws IOException {
        if (isCardInserted) {
            throw new IOException("Card already inserted.");
        }
        this.cardData = cardData;
        this.isCardInserted = true;
    }

    public String getCardData(){ return this.cardData; }
}
