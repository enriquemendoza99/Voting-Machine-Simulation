package Display;

import BML.Proposition;

import java.io.Serializable;

public class Template implements Serializable {
    // top section
    private final String title;
    private final String description;
    private final String instructions;

    // middle section
    private final ButtonData button1;
    private final ButtonData button2;
    private final ButtonData button3;
    private final ButtonData button4;
    private final ButtonData button5;

    // bottom section
    private final ButtonData leftButton;
    private final ButtonData middleButton;
    private final ButtonData rightButton;

    private int maxSelections;

    public Template(
            String title,
            String description,
            String instructions
    ) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;

        this.button1 = new ButtonData(false);
        this.button2 = new ButtonData(false);
        this.button3 = new ButtonData(false);
        this.button4 = new ButtonData(false);
        this.button5 = new ButtonData(false);

        this.leftButton = new ButtonData(false);
        this.middleButton = new ButtonData(false);
        this.rightButton = new ButtonData(false);
    }

    public Template(
            String title,
            String description,
            String instructions,

            ButtonData button1,
            ButtonData button2,
            ButtonData button3,
            ButtonData button4,
            ButtonData button5,

            ButtonData leftButton,
            ButtonData middleButton,
            ButtonData rightButton
    ) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;

        this.button1 = button1;
        this.button2 = button2;
        this.button3 = button3;
        this.button4 = button4;
        this.button5 = button5;

        this.leftButton = leftButton;
        this.middleButton = middleButton;
        this.rightButton = rightButton;
    }

    public int getMaxSelections() { return maxSelections; }

    public void setMaxSelections(int maxSelections) { this.maxSelections = maxSelections; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getInstructions() { return instructions; }

    public ButtonData getButton1() { return button1; }

    public ButtonData getButton2() { return button2; }

    public ButtonData getButton3() { return button3; }

    public ButtonData getButton4() { return button4; }

    public ButtonData getButton5() { return button5; }

    public ButtonData getLeftButton() { return leftButton; }

    public ButtonData getMiddleButton() { return middleButton; }

    public ButtonData getRightButton() { return rightButton; }

    public void resetButtons() {
        leftButton.resetButton();
        middleButton.resetButton();
        rightButton.resetButton();
    }
}
