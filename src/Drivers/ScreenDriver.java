package Drivers;

import Display.SocketHandler;
import Display.Template;

import java.io.IOException;
import java.net.Socket;

public class ScreenDriver extends SocketHandler {
    private boolean failed = false;

    public ScreenDriver() throws IOException {
        super(new Socket("localhost", 12345));
    }

    /**
     *
     * @return A boolean value representing if Screen is ready to receive commands.
     */
    public synchronized boolean isReady() {
        try {
            output.writeObject("isready");
            Object returnob = input.readObject();
            if (returnob instanceof Boolean) {
                return (Boolean) returnob;
            } else return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failure occurred in isReady");
            this.failed = true;
            return false;
        }
    }

    /**
     * Sends a template to DisplayJavaFX to be displayed.
     * @param t Template to be displayed
     */
    public synchronized void sendTemplate(Template t) {
        try {
            output.writeObject(t);
            output.flush();
        } catch (IOException e) {
            System.out.println("Failure occurred in sendTemplate");
            this.failed = true;
        }
    }

    /**
     * fetchTemplate will make a call over the socket to fetch
     * the currently displayed template.
     *
     * @return Template received from DisplayJavaFX.
     * Null if a template could not be received.
     */
    public synchronized Template returnTemplate() {
        try {
            output.writeObject("gettemplate");
            output.flush();
            Template template = (Template) input.readObject();
            System.out.println("ScreenDriver: Received a template");
            System.out.println(template.getButton1().getPressed());
            return template;
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("ScreenDriver: Failure occurred in getTemplate");
            this.failed = true;
        }

        return null;
    }

    /**
     * This method will return an array of boolean values. These values
     * represent which buttons have been pressed.
     *
     * @return An array of boolean values (always 8)
     * */
    public synchronized Boolean[] getPressedButtons() {
        try {
            output.writeObject("getpressedbuttons");
            output.flush();
            return (Boolean[]) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ScreenDriver: Failure occurred in getPressedButtons");
            this.failed = true;
        }

        return null;
    }

    /**
     * This method essentially checks the connection for the screen.
     *
     * @return A boolean value representing if the connection is still
     * functioning. False otherwise.
     */
    public synchronized boolean screenFailure() {
        try {
            output.writeObject("failure");
            output.flush();
            Object returnob = input.readObject();
            if (returnob instanceof Boolean) {
                return (Boolean) returnob;
            } else return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failure occurred in failure");
            this.failed = true;
            return false;
        }
    }

    public boolean failure() {
        return this.screenFailure() && this.failed;
    }
}
