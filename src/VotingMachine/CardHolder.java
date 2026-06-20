package VotingMachine;
import Drivers.CardReader;

import java.io.IOException;


public class CardHolder {
    public enum CardType {
        VOTER,
        ADMIN,
        NONE;
    }

    private final CardReader reader;
    private CardType type;

    public CardHolder(){
        reader = new CardReader();
        type = CardType.NONE;
    }

    public boolean hasCard(){
        return reader.isCardPresent();
    }

    public CardType getType(){
        return type;
    }

    public void insertCard(String data){
        try {
            reader.insertCard(data);
        } catch (IOException e) {
            System.out.println("Insert card error");
        }

        String cardLabel = parseCardData(reader.getCardData());

        if (cardLabel.equals("Voter")) {
            type = CardType.VOTER;
        }

        else if (cardLabel.equals("Admin")) {
            type = CardType.ADMIN;
        }

        else ejectCard();
    }

    /**
     * For actual machine, would compare the hash, but for simulation, just looks if it is equal to Admin or Voter
     *
     * @param data
     * @return
     */
    private String parseCardData(String data) {
        String label = "Invalid";
        if (data.equals("Voter")) label = "Voter";
        if (data.equals("Admin")) label = "Admin";
        return label;
    }

    public void ejectCard(){
        if (type == CardType.VOTER) {
            try {
                reader.eraseCard();
            } catch (IOException e) {
                System.out.println("Card erase error");
            }
        }

        try {
            reader.ejectCard();
        } catch (IOException e) {
            System.out.println("Card eject error");
        }

        type = CardType.NONE;
    }

    public boolean failure(){ return reader.failure(); }

    public CardReader getCardReader() {
        return reader;
    }

}
