package pl.projekt;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.projekt.models.Student;
import pl.projekt.models.Lecturer;
import pl.projekt.models.Attendance;
import javafx.application.Platform;
import pl.projekt.service.StudentService;
import pl.projekt.service.LecturerService;
import pl.projekt.service.AttendanceService;
import pl.projekt.controller.LoginController;
//import nu.pattern.OpenCV;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            Scene scene = new Scene(root, 1000, 700);

            stage.setTitle("Biometric attendance system");
            stage.setScene(scene);

            stage.setOnCloseRequest(event -> {
                loginController.stopRecording(); 
                Platform.exit(); 
                System.exit(0);  
            });

            stage.show();
        } catch (IOException e){
            System.out.println("Error: In application start");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }

}