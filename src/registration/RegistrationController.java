package registration;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import jcode.BiometricHelper;
import jcode.CommonTasks;
import jcode.OrcCon;
import jcode.Toast;
import objects.Employee;
import objects.FingerPrint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static jcode.CommonTasks.createLabel;

public class RegistrationController implements Initializable {

    @FXML
    private JFXListView<Employee> list_employees;
    @FXML
    private AnchorPane edit_pane;
    @FXML
    private JFXTextField edit_name;
    @FXML
    private JFXButton btn_add_one, btn_add_two;
    @FXML
    private JFXTextField edit_search;
    @FXML
    private Label label_name;
    @FXML
    private ImageView img_dp;
    @FXML
    private VBox vbox_details;

    private OrcCon orcCon;
    private BiometricHelper bioHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orcCon = new OrcCon();
        bioHelper = new BiometricHelper();

        init();

        list_employees.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateDetails(newValue));

        list_employees.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
            @Override
            public ListCell<Employee> call(ListView<Employee> param) {
                ListCell<Employee> cell = new ListCell<Employee>() {
                    @Override
                    protected void updateItem(Employee item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.toString());
                            if (item.getPrint().getISO19794_one() == null && item.getPrint().getISO19794_two() == null) {    //If there is no fingerprint saved
                                getStyleClass().remove("fingerprintExists");
                            } else {
                                getStyleClass().add("fingerprintExists");
                            }
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        btn_add_one.setOnAction(event -> saveFingerprint(btn_add_one, 1));
        btn_add_two.setOnAction(event -> saveFingerprint(btn_add_two, 2));
    }

    private void init() {
        ObservableList<Employee> observableList = FXCollections.observableArrayList(orcCon.getAllEmployees());
        FilteredList<Employee> filteredList = new FilteredList<>(observableList, s -> true);

        edit_search.textProperty().addListener((observable, oldValue, newValue) -> setSearch(filteredList));

        setSearch(filteredList);

        list_employees.setItems(filteredList);

        setEmpty();
    }

    private void populateDetails(Employee employee) {
        setEmpty();
        if (employee == null) {
            return;
        }

        orcCon.getEmployeeDetails(employee);

        label_name.setText(employee.getName());
        File file = new File("img/" + employee.getCode());
        Image image = new Image(file.toURI().toString());
        img_dp.setImage(image);
        vbox_details.getChildren().addAll(createLabel("Father Name: " + employee.getFatherName()),
                createLabel("Phone: " + employee.getPhone()));

        btn_add_one.setVisible(true);
        btn_add_two.setVisible(true);

        if (employee.getPrint().getISO19794_one() == null)
            btn_add_one.setText("Set Fingerprint 1");
        else
            btn_add_one.setText("Change Fingerprint 1");

        if (employee.getPrint().getISO19794_two() == null)
            btn_add_two.setText("Set Fingerprint 2");
        else
            btn_add_two.setText("Change Fingerprint 2");

    }

    private void saveFingerprint(JFXButton btn, int no) {
        byte[] b = bioHelper.scanAndReturnISO();
        if (b == null) {
            Toast.makeText((Stage) btn.getScene().getWindow(), "An error has occured during thumb scanning!");
            return;
        }
        FingerPrint print = new FingerPrint();
        if (no == 1)
            print.setISO19794_one(b);
        else if (no == 2)
            print.setISO19794_two(b);
        print.setCode(list_employees.getSelectionModel().getSelectedItem().getCode());
        savePrintToDatabase(list_employees.getSelectionModel().getSelectedItem().getCode(), no, print);
//        if (btn.getText().contains("Set Fingerprint"))
//            orcCon.insertFingerPrint(print, no);
//        else
//            orcCon.updateFingerPrint(print, no);
        Toast.makeText((Stage) btn.getScene().getWindow(), "Fingerprint registered successfully!");
        btn.setText("Change Fingerprint " + no);
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
        img_dp.setImage(null);
        btn_add_one.setVisible(false);
        btn_add_two.setVisible(false);
        vbox_details.getChildren().clear();
    }

    private void savePrintToDatabase(int id, int no, FingerPrint fp) {
        //Check if prints are available in database
        boolean check = orcCon.checkForFingerprint(id);
        //If not available return insert query
        if (!check)
            orcCon.insertFingerPrint(fp, no);
            //If available run update query
        else
            orcCon.updateFingerPrint(fp, no);
    }

}
