import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jcode.BiometricHelper;
import jcode.OrcCon;
import objects.FingerPrint;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
    }


    public static void main(String[] args) {
//        launch(args);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Name of the FingerPrint Owner: ");
        String name = scanner.nextLine();
        System.out.println("Place finger on scanner!");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Scanning..");
        BiometricHelper bioHelper = new BiometricHelper();
        OrcCon orc = new OrcCon();

        byte[] minutiae = bioHelper.scanAndReturnISO();

        FingerPrint fp = new FingerPrint();
        fp.setOwner(name);
        fp.setISO19794(minutiae);

        orc.insertFingerPrint(fp);

    }
}
