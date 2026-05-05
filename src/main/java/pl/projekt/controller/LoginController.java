package pl.projekt.controller;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import pl.projekt.service.AuthenticationService;
import pl.projekt.service.LecturerService;
import pl.projekt.camera.CameraManager;

public class LoginController{
    @FXML
    private ImageView cameraView;

    private CameraManager cameraManager;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    LecturerService lecturerService = new LecturerService();

    public void stopRecording(){
        if (timer != null && !timer.isShutdown()) {
            timer.shutdown();
        }

        cameraManager.closeCamera();
    }

    public void initialize(){

        nu.pattern.OpenCV.loadLocally();
        cameraManager = new CameraManager();

        if (cameraManager.open(0)){

            Runnable frameGrabber = () -> {
                Mat frame = cameraManager.getFrame();
                if (!frame.empty()) {
                    Image temp = cameraManager.matToImage(frame);
                    Platform.runLater(() -> cameraView.setImage(temp));
                    frame.release();
                }
            };

            timer.scheduleAtFixedRate(frameGrabber, 0, 17L, TimeUnit.MILLISECONDS);

        } else {    
            System.out.println("Camera not opened");
        }
    }

    @FXML
    public void handleLogin(){

        if(!AuthenticationService.checkPin(password.getText(), lecturerService.getHashedPin(name.getText().trim()))){
            errorLabel.setText("Try again!");  
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
            visiblePause.setOnFinished(event -> errorLabel.setText(""));
            
            visiblePause.play();         
        } else {
            stopRecording();
            //przechodzimy dalej
            }
    }
}
