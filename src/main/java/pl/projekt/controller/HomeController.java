package pl.projekt.controller;

import pl.projekt.util.FaceDetector;

import javafx.scene.image.Image;
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
import org.opencv.core.Rect;

import java.io.IOException;

public class HomeController{

    @FXML
    private ImageView cameraView;
    @FXML
    private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();
    private FaceDetector faceDetector = new FaceDetector();

    @FXML
    public void startStopRecording(){
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
        } else {
            boolean success = cameraManager.openCamera(frame -> {
                Rect[] faces = faceDetector.getRectFaces(frame);
                faceDetector.drawFaces(frame, faces);
                Image imageToShow = cameraManager.convertMatToImage(frame);
                Platform.runLater(() -> cameraView.setImage(imageToShow));
            });

            if (!success)
                errorLabel.setText("Error: can not find camera.");
            else
                errorLabel.setText("");
        }
    }
    
    /*public void initialize(){
        startStopRecording();
    }*/

    @FXML
    public void addStudent(ActionEvent event){
        loadScene(event, "/AddStudentView.fxml", "Add Student");
    }

    @FXML
    public void showStatistics(ActionEvent event){
        loadScene(event, "/StatisticsView.fxml", "Statistics");
    }

    @FXML 
    public void showStudents(ActionEvent event){
        loadScene(event, "/StudentsView.fxml", "Students List");
    }

    @FXML 
    public void showAttendance(ActionEvent event){
        loadScene(event, "/AttendanceView.fxml", "Attendance");
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
                errorLabel.setText("Error: can not load " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e){
            if( errorLabel != null )
                errorLabel.setText("Error: fxml file does not exist");
        }
    }

}

