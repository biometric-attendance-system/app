package pl.projekt;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.projekt.models.Lecturer;
import pl.projekt.service.LecturerService;
//import nu.pattern.OpenCV;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));

            Scene scene = new Scene(root, 400, 300);

            stage.setTitle("Login Panel");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        //OpenCV.loadLocally();
        LecturerService s = new LecturerService();
        s.addLecturer(new Lecturer("1","Rysiek","bla","mojpin", "twarz"));
        launch();
        
       
    }

}