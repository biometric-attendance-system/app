package pl.projekt.controller;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

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

public class LoginController{
    @FXML
    private ImageView cameraView;

    private VideoCapture capture;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    LecturerService lecturerService = new LecturerService();

    private Image convMatImage(Mat frame){
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public void closeCamera(){
        if (timer != null && !timer.isShutdown()) {
            timer.shutdown();
        }

        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }

    public void initialize(){

        nu.pattern.OpenCV.loadLocally();
        capture = new VideoCapture();
        capture.open(0);

        if (capture.isOpened()){

            Runnable frameGrabber = () -> {
                Mat frame = new Mat();
                capture.read(frame);
                if (!frame.empty()) {
                    Image temp = convMatImage(frame);
                    Platform.runLater(() -> cameraView.setImage(temp));
                    frame.release();
                }
            };

            timer.scheduleAtFixedRate(frameGrabber, 0, 33L, TimeUnit.MILLISECONDS);

        } else {    
            System.out.println("Camera not opened");
        }
    }

    @FXML
    public void handleLogin(){

        if(!AuthenticationService.checkPin(password.getText(), lecturerService.getHashedPin(name.getText()))){
            errorLabel.setText("Try again!");  
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
            visiblePause.setOnFinished(event -> errorLabel.setText(""));
            
            visiblePause.play();         
        } else {
            closeCamera();
            //przechodzimy dalej
            }
    }
}
