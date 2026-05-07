package pl.projekt.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.projekt.service.LecturerService;
import pl.projekt.service.AuthenticationService;
import pl.projekt.util.CameraManager;

import java.io.IOException;

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

    public void stopRecording(){
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
        }
    }

    public void initialize(){
        boolean started = cameraManager.openCamera(frame -> {
            Platform.runLater(() -> cameraView.setImage(frame));
        });

        if (!started) {
            errorLabel.setText("No camera found!");
        }
    }

    @FXML
    public void handleLogin(ActionEvent event){

        if(!AuthenticationService.checkPin(password.getText().trim(), lecturerService.getHashedPin(name.getText().trim()))){
            errorLabel.setText("Try again!");  
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
            visiblePause.setOnFinished(ev -> errorLabel.setText(""));
            
            visiblePause.play();         
        } else {
            stopRecording();
            System.out.println("Logged in succesfully. Changing view to home screen.");
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomeScreenView.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Home screen");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                if( errorLabel != null )
                    errorLabel.setText("Error: can not load home screen");
                e.printStackTrace();
            } 
        }
    }
}
