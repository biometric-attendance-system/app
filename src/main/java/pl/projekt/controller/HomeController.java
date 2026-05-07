package pl.projekt.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pl.projekt.util.CameraManager;

import java.io.IOException;

public class HomeController{

    @FXML
    private ImageView cameraView;
    @FXML
    private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();

    @FXML
    public void startStopRecording(){
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
        } else {
            boolean success = cameraManager.openCamera(frame -> {
                Platform.runLater( () -> cameraView.setImage(frame)); 
            });

            if (!success)
                errorLabel.setText("Błąd! Nie wykryto kamery.");
            else
                errorLabel.setText("");
        }
    }
    
    @FXML
    public void addStudent(ActionEvent event){
        loadScene(event, "/pl/projekt/resources/AddStudentView.fxml", "Add Student");
    }

    @FXML
    public void showStatistics(ActionEvent event){
        loadScene(event, "/pl/projekt/resources/StatisticsView.fxml", "Statistics");
    }

    @FXML 
    public void showStudents(ActionEvent event){
        loadScene(event, "/pl/projekt/resources/StudentsView.fxml", "Students List");
    }

    @FXML 
    public void showAttendance(ActionEvent event){
        loadScene(event, "/pl/projekt/resources/AttendanceView.fxml", "Attendance");
    }

    private void loadScene(ActionEvent event, String fxmlPath, String title){
        if(cameraManager.isCameraActive())
            cameraManager.closeCamera();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            if( errorLabel != null )
                errorLabel.setText("Błąd! Nie można załadować " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e){
            if( errorLabel != null )
                errorLabel.setText("Błąd! Plik FXML nie istnieje.");
        }
    }

}

