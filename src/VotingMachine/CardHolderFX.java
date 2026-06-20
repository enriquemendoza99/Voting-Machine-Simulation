package VotingMachine;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class CardHolderFX extends VBox {
    private final CardHolder cardHolder;
    private final TextField cardInput;
    private final Label statusLabel;
    private final Rectangle cardVisual;

    public CardHolderFX(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);

        cardInput = new TextField();
        cardInput.setPromptText("Enter card type: Voter or Admin");
        cardInput.setPrefWidth(200);
        HBox inputBox = new HBox(cardInput);
        inputBox.setAlignment(Pos.CENTER);

        Button insertButton = new Button("Insert Card");
        insertButton.setOnAction(e -> { insertCard(); });

        //Commented out in case we decide to add it back in for presentation functionality
        //Button ejectButton = new Button("Eject Card");
        //ejectButton.setOnAction(e -> { ejectCard(); });

        statusLabel = new Label("Card Slot Empty");

        cardVisual = new Rectangle(100, 150, Color.LIGHTGRAY);
        cardVisual.setArcHeight(10);
        cardVisual.setArcWidth(10);
        cardVisual.setTranslateY(120);

        // Not happy with the card growing instead of moving, tried some of this to fix it, might come back to it
        //StackPane cardPane = new StackPane(cardVisual);
        //cardPane.setPrefSize(100, 130);
        //cardPane.setMaxSize(100, 130);
        //cardPane.setAlignment(Pos.BOTTOM_CENTER);
        //Rectangle clip = new Rectangle(100, 130);
        //cardPane.setClip(clip);


        this.getChildren().addAll(inputBox, insertButton, statusLabel, cardVisual);
    }

    private void updateStatus() {
        if (cardHolder.hasCard()) {
            statusLabel.setText("Card Inserted: " + cardHolder.getType());
        } else {
            statusLabel.setText("Card Slot Empty");
        }
        cardInput.setText("");
    }

    private void insertCard() {
        if (!cardHolder.hasCard()) {
            String data = cardInput.getText().trim();
            insertFX();

            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(event -> {
                cardHolder.insertCard(data);
                if (!cardHolder.hasCard()) {
                    ejectFX();
                }
                updateStatus();
            });
            pause.play();
        }
    }

    private void insertFX() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), cardVisual);
        tt.setFromY(120);
        tt.setToY(40);  // into view
        tt.play();
    }

    public void ejectCard(){
        if (cardHolder.hasCard()) {
            cardHolder.ejectCard();
            ejectFX();
        }
        updateStatus();
    }

    private void ejectFX() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), cardVisual);
        tt.setFromY(40);
        tt.setToY(120);  // out of view
        tt.play();
    }


}
