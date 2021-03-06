package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import jcode.BioZKTHelper;
import jcode.CommonTasks;
import jcode.OrcCon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static dashboard.dashboardController.deviceNo;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField user_field;
    @FXML
    private JFXPasswordField pass_field;
    @FXML
    private JFXButton login_btn;
    @FXML
    private Label error_message;
    @FXML
    private JFXButton btn_settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        error_message.setMaxWidth(Double.MAX_VALUE);
//        AnchorPane.setLeftAnchor(error_message, 0.0);
//        AnchorPane.setRightAnchor(error_message, 0.0);
        error_message.setAlignment(Pos.CENTER);     //Setting Error Message Center

        pass_field.setOnAction(event -> loginAction());

        login_btn.setOnAction(event -> loginAction());

        Image image = new Image(getClass().getResourceAsStream("/login/Settings-icon.png"));
        btn_settings.setGraphic(new ImageView(image));
        btn_settings.setOnAction(event -> CommonTasks.inflateDialog("Settings", "/settings/activity_settings.fxml"));

    }

    private void loginAction() {
        String users = user_field.getText(), pass = pass_field.getText();

        if (users.equals("") || pass.equals("")) {
            error_message.setText("Incomplete Values");
        } else {
            new Thread(() -> authenticateLogin(user_field.getText(), pass_field.getText())).start();
        }
    }

    private void authenticateLogin(String username, String password) {

        OrcCon orc = new OrcCon();
        boolean succ = orc.authenticateLogin(username, password);

        Platform.runLater(() -> {
                    // Update UI here. Running on main Thread
                    if (succ == false) {     //Unsuccessful Login
                        error_message.setText("Incorrect ID or Password");
                    } else {                //Successful Login
                        Stage stage2 = (Stage) login_btn.getScene().getWindow();
                        stage2.close();

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource
                                ("dashboard/activity_dashboard.fxml"));
                        Parent root1 = null;
                        try {
                            root1 = fxmlLoader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Stage stage = new Stage();
                        stage.setTitle("Biometric Receiving");
                        stage.setScene(new Scene(root1, 800, 450));
                        stage.setOnCloseRequest(event -> {
                            switch (deviceNo) {
                                case 1: {
                                    break;
                                }
                                case 2: {
                                    BioZKTHelper.FreeSensor();
                                    break;
                                }
                                default:
                                    break;
                            }
                        });
                        stage.show();
                    }
                }
        );


    }
}
