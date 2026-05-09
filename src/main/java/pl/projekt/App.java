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
        LecturerService l = new LecturerService();
        l.addLecturer(new Lecturer("1","Rysiek","bla","mojpin"));
        AttendanceService a = new AttendanceService();
        a.addAttendance(new Attendance("123456", "5/9/2026", "present"));
        StudentService s = new StudentService();
        s.addStudent(new Student("Jakis", "Student", "686868"));
        s.addStudent(new Student("Kolejny", "Stud", "987654"));
        launch();
        
       
    }

}