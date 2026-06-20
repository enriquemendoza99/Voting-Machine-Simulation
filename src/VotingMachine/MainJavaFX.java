package VotingMachine;

import Display.ButtonData;
import Display.Template;
import Display.VotingMachinePage;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainJavaFX extends Application {
    private Stage stage;

    private Template template;
    private Listener listener;
    private boolean failed = false;
    private boolean ready = true;
    private Label titleLabel;
    private VBox questionBox;
    private Button leftButton, middleButton, rightButton;
    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (!serverSocket.isClosed()) {
                System.out.printf("Port opened @ %s. Waiting for connection...\n", 12345);
                Socket socket = serverSocket.accept();
                System.out.printf("Reader connected from %s.\n", socket.getLocalAddress());
                listener = new Listener(socket, this);
                System.out.println("Closing port...");
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Port closed");
        }

        primaryStage.setTitle("Voting Machine");
//        primaryStage.resizableProperty().set(false);
        primaryStage.show();
        new Thread(listener).start();
    }

    public void receiveTemplate(Template template) {
        System.out.println("Display.Template received");
        this.template = template;
        // reset the value of the bottom buttons
        // to avoid a strange bug
        this.template.resetButtons();
        System.out.println("Buttons pressed: ");
        System.out.println(template.getLeftButton().getPressed());
        System.out.println(template.getRightButton().getPressed());
        ready = false;

        VotingMachinePage newPage = new VotingMachinePage(template);
        stage.setScene(newPage.getScene());
    }

    public boolean isReady() {
        return ready;
    }

    public boolean failure() { return failed;}

    public Template getTemplate() {
        return template;
    }

    public Boolean[] getPressedButtons() {
        Boolean[] buttonsPressed = new Boolean[8];
        buttonsPressed[0] = template.getButton1().getPressed();
        buttonsPressed[1] = template.getButton2().getPressed();
        buttonsPressed[2] = template.getButton3().getPressed();
        buttonsPressed[3] = template.getButton4().getPressed();
        buttonsPressed[4] = template.getButton5().getPressed();

        // bottom buttons
        buttonsPressed[5] = template.getLeftButton().getPressed();
        buttonsPressed[6] = template.getMiddleButton().getPressed();
        buttonsPressed[7] = template.getRightButton().getPressed();
        return buttonsPressed;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
