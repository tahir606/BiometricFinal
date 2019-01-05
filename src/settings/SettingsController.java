package settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import jcode.FileHelper;
import jcode.Toast;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    JFXComboBox<String> combo_device;
    @FXML
    JFXButton btn_save;

    private FileHelper fileHelper;

    public static final String SECUGEN_HAMSTER = "Secugen Hamster Plus",
            ZKTECO = "ZKTeco 4500";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileHelper = new FileHelper();

        combo_device.getItems().addAll(SECUGEN_HAMSTER, ZKTECO);

        String deviceName = fileHelper.readDeviceName();
        combo_device.getSelectionModel().select(deviceName);

        btn_save.setOnAction(event -> {
            Stage thisStage = (Stage) btn_save.getScene().getWindow();
            String device = combo_device.getSelectionModel().getSelectedItem();
            if (device == null) {
                Toast.makeText(thisStage, "Please select an option!");
                return;
            }
            fileHelper.writeDeviceName(device);
            thisStage.close();
        });

    }

}
