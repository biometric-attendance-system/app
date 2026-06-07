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
import pl.projekt.service.LecturerService;
import pl.projekt.controller.LoginController;
import pl.projekt.controller.AddLecturerController;


/**
 * @brief Main application class responsible for initializing and launching
 * the Biometric Attendance System.
 */
public class App extends Application {

    /**
     * @brief Function initializes the stage, determines which view
     * to present (login or registration) based on Lecturer database state and
     * sets up camera closing on exit action.
     *
     * @param stage Stage onto which the application's scene can be set.
     */
    @Override
    public void start(Stage stage) {
        try {
            LecturerService check = new LecturerService();
            FXMLLoader loader;
            boolean login = false;
            
            if (check.isEmpty()) {
                loader = new FXMLLoader(getClass().getResource("/AddLecturerView.fxml"));
                AddLecturerController controller = loader.getController();
            }
            else {
                loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
                login = true;
            }
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setTitle("Biometric attendance system");
            stage.setScene(scene);

            if (login) {
                LoginController loginController = loader.getController();
                stage.setOnCloseRequest(event -> {
                    if (loginController != null) {
                        loginController.stopRecording(); 
                    }
                    Platform.exit(); 
                    System.exit(0);  
                });
            } else {
                stage.setOnCloseRequest(event -> {
                    Platform.exit(); 
                    System.exit(0);  
                });
            }
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