package vouchers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import dashboard.dashboardController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import jcode.BiometricMain;
import jcode.FileHelper;
import jcode.OrcCon;
import jcode.Toast;
import objects.Employee;
import objects.FingerPrint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static jcode.CommonTasks.createLabel;

public class VouchersController implements Initializable {

    private OrcCon orcCon;
    private FileHelper fileHelper;
    private BiometricMain bioMain;

    @FXML
    private VBox vbox_vouchers, vbox_details;
    @FXML
    private JFXComboBox<String> combo_voucher;
    @FXML
    private JFXTextField txt_voucher;
    @FXML
    private ImageView img_dp;
    @FXML
    private JFXButton btn_verify;
    @FXML
    private Label label_info;

    private Employee emp;

    boolean verified;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orcCon = new OrcCon();
        fileHelper = new FileHelper();
        bioMain = new BiometricMain(dashboardController.deviceNo);

        combo_voucher.getItems().addAll(orcCon.getVoucherList());

        txt_voucher.setOnAction(event -> {
            vbox_details.getChildren().clear();
            String[] details = null;
            label_info.setText("");
            try {
                details = orcCon.getVoucherDetails(
                        Integer.parseInt(combo_voucher.getSelectionModel().getSelectedItem().split("-")[0].trim()), //srno
                        fileHelper.readYRNO(),
                        Integer.parseInt(txt_voucher.getText().toString())
                ).split(",");
                emp = orcCon.getCompleteEmployeeDetails(Integer.parseInt(details[4]));
                File file = new File("img/" + emp.getCode());
                Image image = new Image(file.toURI().toString());
                img_dp.setImage(image);
                try {
                    if (details[5].equals("Y")) {
                        verified = true;
                    } else {
                        verified = false;
                    }
                } catch (Exception e) {
                    verified = false;
                }
                btn_verify.setDisable(false);
            } catch (NullPointerException e) {
                img_dp.setImage(null);
                vbox_details.getChildren().add(createLabel("Invalid Voucher Code"));
                btn_verify.setDisable(true);
                return;
            }
            TextArea textArea = new TextArea(details[1]);
            textArea.setMaxHeight(50);
            textArea.setMaxWidth(300);
            textArea.setEditable(false);
            textArea.setStyle("-fx-background-color: transparent");
            vbox_details.getChildren().addAll(createLabel("Date: " + details[0]),
                    createLabel("Amount: " + details[2] + " RS"),
                    createLabel("Employee: " + emp.getName()),
                    createLabel("Verified: " + verified),
                    textArea);
        });

        btn_verify.setOnAction(event -> new Thread(() -> {
            if (verified) {
                Platform.runLater(() -> label_info.setText("Voucher already verified"));
                return;
            }
            if (verifyPrint()) {
                orcCon.verifyVoucher(Integer.parseInt(combo_voucher.getSelectionModel().getSelectedItem().split("-")[0].trim()), //srno
                        fileHelper.readYRNO(),
                        Integer.parseInt(txt_voucher.getText().toString()));
                Platform.runLater(() -> {
                    label_info.setText("Verification Successful");
                    vbox_details.getChildren().remove(3);
                    vbox_details.getChildren().add(3, createLabel("Verified: true"));
                });
            } else {
                Platform.runLater(() -> label_info.setText("Verification Not Successful"));
            }
        }).start());
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14; -fx-text-fill: #000000;");
        return label;
    }

    private boolean verifyPrint() {
        if (emp.getPrint().getISO19794_one() == null) {
            return false;
        }

        byte[] b = bioMain.scanAndReturnISO(label_info);
        if (b == null) {
            Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "An error occurred during thumb scanning!"));
            return false;
        }
        if (bioMain.matchPrints(b, emp.getPrint().getISO19794_one())) {
            Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Verified Successfully!"));
            return true;
        } else {
            if (emp.getPrint().getISO19794_two() == null)
                return false;
            if (bioMain.matchPrints(b, emp.getPrint().getISO19794_two())) {
                Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Verified Successfully!"));
                return true;
            } else {
                Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Invalid Verification!"));
                return false;
            }
        }
    }

}
