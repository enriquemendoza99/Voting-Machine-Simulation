package Display;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.Serializable;

public class ButtonData implements Serializable {
    private boolean isActive;
    private String text;
    private boolean pressed;

    public ButtonData(boolean isActive) {
        this.isActive = isActive;
        this.text = "";
        this.pressed = false;
    }

    // Constructor
    public ButtonData(boolean isActive, String text) {
        this.isActive = isActive;
        this.text = text;
        this.pressed = false;
    }

    public void pressButton(){
        if (isActive) pressed = !pressed;
    }

    public void resetButton() { pressed = false; }


    public boolean getActive() {return isActive;}

    public boolean getPressed() { return pressed; }

    public String getText() {return text;}
}
