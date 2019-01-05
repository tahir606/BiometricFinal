package receiving;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import dashboard.dashboardController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jcode.*;
import objects.Employee;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static jcode.CommonTasks.createLabel;

public class ReceivingController implements Initializable {

    //    @FXML
//    private MenuItem menu_register;
    @FXML
    private JFXComboBox<String> combo_rec_type;
    @FXML
    private JFXTextField edit_search;
    @FXML
    private ListView<Employee> list_emp;
    @FXML
    private Label label_name;
    @FXML
    private ImageView img_dp;
    @FXML
    private JFXButton btn_verify;
    @FXML
    private HBox hbox_data;
    @FXML
    private VBox vbox_details;
    @FXML
    private Label label_info;

    private OrcCon orcCon;
    private BiometricMain bioMain;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orcCon = new OrcCon();

        bioMain = new BiometricMain(dashboardController.deviceNo);

        init();

        list_emp.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateDetails(newValue));
        combo_rec_type.getItems().addAll("Salaries");
//        menu_register.setOnAction(event -> CommonTasks.inflateDialog("Register Print", "/registration/activity_vouchers.fxml"));

        combo_rec_type.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            btn_verify.setDisable(false);
            switch (newValue) {
                case "Salaries": {
                    populateSalaries();
                    break;
                }
                default:
                    break;
            }
        });

    }

    private void populateDetails(Employee employee) {
        setEmpty();
        if (employee == null) {
            return;
        }

        combo_rec_type.getSelectionModel().select(null);
        hbox_data.getChildren().clear();

        orcCon.getEmployeeDetails(employee);

        label_name.setText(employee.getName());
        File file = new File("img/" + employee.getCode());
        Image image = new Image(file.toURI().toString());
        img_dp.setImage(image);
        vbox_details.getChildren().addAll(createLabel("Father Name: " + employee.getFatherName()),
                createLabel("Phone: " + employee.getPhone()));

        combo_rec_type.setDisable(false);
        btn_verify.setVisible(true);

        if (employee.getPrint().getISO19794_one() == null && employee.getPrint().getISO19794_two() == null) {
            btn_verify.setDisable(true);
            btn_verify.setText("Fingerprint unregistered.");
        } else {
            btn_verify.setDisable(false);
            btn_verify.setText("Verify");
        }

        if (combo_rec_type.getSelectionModel().getSelectedItem() == null) {
            btn_verify.setDisable(true);
        }
    }

    private void populateSalaries() {
        JFXDatePicker datePicker = new JFXDatePicker();
        hbox_data.getChildren().addAll(datePicker);
        btn_verify.setOnAction(event -> new Thread(() -> {
            LocalDate date = datePicker.getValue();
            int year = date.getYear();
            String month = date.getMonth().toString();
            System.out.println("Month: " + month + "\n" + "Year: " + year);
            if (verifyPrint()) {
                orcCon.updateSalary(list_emp.getSelectionModel().getSelectedItem(), month, year);
                Platform.runLater(() -> label_info.setText("Verification Successful"));
            } else {
                Platform.runLater(() -> label_info.setText("Verification Not Successful"));
            }
        }).start());
    }


    private void init() {
        ObservableList<Employee> observableList = FXCollections.observableArrayList(orcCon.getAllEmployees());
        FilteredList<Employee> filteredList = new FilteredList<>(observableList, s -> true);

        edit_search.textProperty().addListener((observable, oldValue, newValue) -> setSearch(filteredList));

        setSearch(filteredList);

        list_emp.setItems(filteredList);

        setEmpty();
    }

    private void setSearch(FilteredList<Employee> filteredList) {
        String filter = edit_search.getText();
        if (filter == null || filter.length() == 0) {
            filteredList.setPredicate(s -> true);
        } else {
            filteredList.setPredicate(s -> s.toString().toUpperCase().contains(filter.toUpperCase()));
        }
    }

    private void setEmpty() {
        label_name.setText("");
        combo_rec_type.setDisable(true);
        btn_verify.setVisible(false);
        hbox_data.getChildren().clear();
        img_dp.setImage(null);
        vbox_details.getChildren().clear();
        label_info.setText("");
    }

    private boolean verifyPrint() {
        byte[] b = bioMain.scanAndReturnISO(label_info);
        if (b == null) {
            Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "An error occurred during thumb scanning!"));
            return true;
        }
        if (bioMain.matchPrints(b, list_emp.getSelectionModel().getSelectedItem().getPrint().getISO19794_one())) {
            Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Verified Successfully!"));
            return true;
        } else {
            if (list_emp.getSelectionModel().getSelectedItem().getPrint().getISO19794_two() == null)
                return false;
            if (bioMain.matchPrints(b, list_emp.getSelectionModel().getSelectedItem().getPrint().getISO19794_two())) {
                Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Verified Successfully!"));
                return true;
            } else {
                Platform.runLater(() -> Toast.makeText((Stage) btn_verify.getScene().getWindow(), "Invalid Verification!"));
                return false;
            }
        }
    }


}
