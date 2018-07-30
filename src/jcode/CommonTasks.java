package jcode;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class CommonTasks {

    public CommonTasks() {

    }

    public static void inflateDialog(String title, String path) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CommonTasks.class.getResource(path));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            Platform.setImplicitExit(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font(11.5));
        return label;
    }

}
