package Display;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class VotingMachinePage {
    private final Scene scene;
    private final Button previousButton;
    private final Button submitButton;
    private final Button nextButton;
    private int selectionsCount = 0;

    public VotingMachinePage(Template t) {
        // Title
        Label title = new Label(t.getTitle());
        int maxSelections = t.getMaxSelections();
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        title.setMinHeight(40);
        title.setMaxHeight(40);
        title.setMaxWidth(600);

        // Description
        Label description = new Label(t.getDescription());
        description.setAlignment(Pos.CENTER);
        description.setStyle("-fx-font-size: 18px;");
        description.setWrapText(true);
        description.setMinHeight(140);
        description.setMaxHeight(140);
        description.setPrefWidth(600);

        // Instructions
        Label instructions = new Label(t.getInstructions());
        instructions.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        instructions.setMinHeight(40);
        instructions.setMaxHeight(40);
        instructions.setPrefWidth(600);

        // Options
        VBox optionsBox = new VBox();
        optionsBox.setMaxHeight(500);
        optionsBox.setPrefWidth(600);
        optionsBox.setAlignment(Pos.CENTER);

        Button button1 = new Button(t.getButton1().getText());
        if (t.getButton1().getActive()) {
            setDimensions(button1);
            // add a listener to mark the option
            button1.setOnAction(event -> {
                handleOptionButtonClick(button1, t.getButton1(), maxSelections);
            });
        }
        else {
            button1.setVisible(false);
        }

        Button button2 = new Button(t.getButton2().getText());
        if (t.getButton2().getActive()) {
            setDimensions(button2);
            // add a listener to mark the option
            button2.setOnAction(event -> {
                handleOptionButtonClick(button2, t.getButton2(), maxSelections);
            });
        }
        else {
            button2.setVisible(false);
        }

        Button button3 = new Button(t.getButton3().getText());
        if (t.getButton3().getActive()) {
            setDimensions(button3);
            // add a listener to mark the option
            button3.setOnAction(event -> {
                handleOptionButtonClick(button3, t.getButton3(), maxSelections);
            });
        }
        else {
            button3.setVisible(false);
        }


        Button button4 = new Button(t.getButton4().getText());
        if (t.getButton4().getActive()) {
            setDimensions(button4);
            // add a listener to mark the option
            button4.setOnAction(event -> {
                handleOptionButtonClick(button4, t.getButton4(), maxSelections);
            });
        }
        else {
            button4.setVisible(false);
        }

        Button button5 = new Button(t.getButton5().getText());
        if (t.getButton5().getActive()) {
            setDimensions(button5);
            // add a listener to mark the option
            button5.setOnAction(event -> {
                handleOptionButtonClick(button5, t.getButton5(), maxSelections);
            });
        }
        else {
            button5.setVisible(false);
        }

        optionsBox.getChildren().addAll(
            button1,
            button2,
            button3,
            button4,
            button5
        );

        // Bottom Buttons
        this.previousButton = new Button("← Previous");
        previousButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        setDimensionsOnButton(previousButton);
        this.previousButton.setOnAction(event -> t.getLeftButton().pressButton());

        this.submitButton = new Button("Submit ✔");
        submitButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        setDimensionsOnButton(submitButton);
        this.submitButton.setOnAction(event -> t.getMiddleButton().pressButton());


        this.nextButton = new Button("Next →");
        nextButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        setDimensionsOnButton(nextButton);
        this.nextButton.setOnAction(event -> t.getRightButton().pressButton());

        HBox navigationButtons = new HBox(0, previousButton, submitButton, nextButton);
        navigationButtons.setAlignment(Pos.BOTTOM_CENTER);

        // Set status of buttons
        if (!t.getLeftButton().getActive()) {
            previousButton.setDisable(true);
            previousButton.setVisible(false);
        }
        if (!t.getMiddleButton().getActive()) {
            submitButton.setDisable(true);
            submitButton.setVisible(false);
        }
        if (!t.getRightButton().getActive()) {
            nextButton.setDisable(true);
            nextButton.setVisible(false);
        }

        // Pad out options height
        Region optionSpacer = new Region();
        VBox.setVgrow(optionSpacer, Priority.ALWAYS);

        // Layout
        VBox mainContent = new VBox(0, title, description, instructions, optionsBox, optionSpacer, navigationButtons);
        //mainContent.setPadding(new Insets(15, 15, 25, 15));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(600);
        mainContent.setMinHeight(800);
        mainContent.setMaxHeight(800);

        // init scene
        scene = new Scene(mainContent, 600, 800);
        // Load the stylesheet
        scene.getStylesheets().add("Display/style.css");
    }

    private void setDimensionsOnButton(Button button) {
        button.setMinSize(200, 80);
        button.setMaxSize(200, 80);
    }

    public Scene getScene() {return scene;}
    public Button getPreviousButton() {return previousButton;}
    public Button getSubmitButton() {return submitButton;}
    public Button getNextButton() {return nextButton;}

    private void setDimensions(Button button) {
        button.setMinSize(600, 75);
        button.setMaxSize(600, 75);
    }

    private void handleOptionButtonClick(Button button, ButtonData buttonData, int maxSelections) {
        System.out.println("Button clicked: " + button.getText());
        System.out.println("Is active: " + buttonData.getActive());
        if (buttonData.getActive()) {
            // if the button is not pressed
            if (!buttonData.getPressed()) {
                if (selectionsCount < maxSelections) {
                    buttonData.pressButton();
                    selectionsCount++;
                    System.out.println(button.getText() + " selected. Total selections: " + selectionsCount);
                    button.setStyle("-fx-font-size: 18px; -fx-background-color: #d6bf3c;");
                } else {
                    System.out.println("Maximum selections (" + maxSelections + ") reached.");
                }
            } else {
                buttonData.pressButton();
                selectionsCount--;
                System.out.println(button.getText() + " deselected. Total selections: " + selectionsCount);
                button.setStyle("-fx-font-size: 18px;"); // Reset to default style
            }
        }
    }
}
