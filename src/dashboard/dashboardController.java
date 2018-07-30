package dashboard;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import jcode.BiometricHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    @FXML
    private VBox vbox_options;
    @FXML
    private BorderPane border_main;

    private static int currentPane;

    BiometricHelper biohelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        border_main.setCenter(new Label("Choose Option"));

        receiveButton();
        registerButton();

        new Thread(() -> {
            biohelper = new BiometricHelper();
            if (biohelper.setLed(true) != 0) {
                Platform.runLater(() -> border_main.setCenter(new Label("Cannot connect to biometric device")));
            } else {
                biohelper.setLed(false);
            }
        }).start();
    }

    //---------------------------BUTTONS-------------------------
    private void buttonSubSettings(JFXButton button, String btnName) {
        button.setText(btnName.toUpperCase());
//        Image image = new Image(getClass().getResourceAsStream("/res/img/" + btnName.toLowerCase() + ".png"));
        button.setMinSize(vbox_options.getPrefWidth(), 35);
        button.setMaxSize(vbox_options.getPrefWidth(), 35);
        button.getStyleClass().add("btn");

//        button.setGraphic(new ImageView(image));
        button.setAlignment(Pos.CENTER_LEFT);
        Platform.runLater(() -> vbox_options.getChildren().add(button));
    }

    private void buttonSettings(String btnName, String path, int paneNo) {
        JFXButton button = new JFXButton();
        buttonSubSettings(button, btnName);
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> changeSelection(button, path, paneNo));
    }

    private void changeSelection(JFXButton btn, String path, int pane) {

        for (Node node : vbox_options.getChildren()) {
            node.getStyleClass().remove("btnMenuBoxPressed");
        }

        btn.getStyleClass().add("btnMenuBoxPressed");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (currentPane == pane) {
                    return;
                }

                Platform.runLater(() -> {
                    try {
                        border_main.setCenter(FXMLLoader.load(getClass().getClassLoader().getResource(path)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentPane = pane;
                });
            }
        }).start();

    }

    JFXButton receiveBtn = new JFXButton("Receive");
    JFXButton registerBtn = new JFXButton("Receive");

    private void receiveButton() {
        buttonSettings("Receive", "receiving/activity_receiving.fxml", 1);
    }
    private void registerButton() {
        buttonSettings("Register", "registration/activity_registration.fxml", 2);
    }


}
