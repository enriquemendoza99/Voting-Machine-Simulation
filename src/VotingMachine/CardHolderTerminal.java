package VotingMachine;

import java.util.Scanner;

public class CardHolderTerminal implements Runnable {
    private CardHolder cardHolder;
    private Scanner scanner = new Scanner(System.in);

    public CardHolderTerminal(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    @Override
    public void run() {
        System.out.println("Enter card type (Voter or Admin): ");
        String data = scanner.nextLine();
        cardHolder.insertCard(data);
    }
}
