package pl.projekt.controller;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.bytedeco.opencv.opencv_core.Rect;

import pl.projekt.models.Lecturer;
import pl.projekt.service.AuthenticationService;
import pl.projekt.service.LecturerService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

public class LoginController{
    @FXML
    private ImageView cameraView;

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();
    private final LecturerService lecturerService = new LecturerService();
    private final FaceDetector faceDetector = new FaceDetector();
    private final FaceRecognition faceRecognition = new FaceRecognition();
    boolean found = false;

    public void stopRecording(){
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
        }
    }

    public void initialize(){
        Lecturer lec = lecturerService.getLecturer();
        if (lec == null){
            System.err.println("Error: Could not find lecturer in database.");
            return;
        }
        String ID = lec.getID();
        
        boolean started = cameraManager.openCamera(frame -> {
            if (found) return;
            Rect[] faces = faceDetector.getRectFaces(frame);
            
            if (faces != null && faces.length>0){
                String[] labels = faceRecognition.recognize(frame, faces);
                
                if (labels != null && labels.length>0){
                    for (String label : labels){
                        if (label.equals(ID)){
                            found = true;
                            Platform.runLater(() -> this.loadHome());
                        }
                    }
                }
                faceDetector.drawFaces(frame, faces);
            }

            Image imageToShow = cameraManager.convertMatToImage(frame);
            Platform.runLater(() -> cameraView.setImage(imageToShow));
        });
        if (!started) {
            errorLabel.setText("No camera found!");
        }
    }

    public void loadHome(){
        stopRecording();
        System.out.println("Logged in succesfully. Changing view to home screen.");
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeScreenView.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) errorLabel.getScene().getWindow();
                stage.setTitle("Home screen");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                if( errorLabel != null )
                    errorLabel.setText("Error: can not load home screen");
                e.printStackTrace();
            } 
    }

    @FXML
    public void handleLogin(ActionEvent event){
        Lecturer lec = lecturerService.getLecturer();
        if(!(lec.getID().equals(name.getText().trim()) && AuthenticationService.checkPin(password.getText().trim(), lec.getPinHash()))){
            errorLabel.setText("Try again!");  
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
            visiblePause.setOnFinished(ev -> errorLabel.setText(""));
            
            visiblePause.play();         
        } else {
            loadHome();
        }
    }
}
