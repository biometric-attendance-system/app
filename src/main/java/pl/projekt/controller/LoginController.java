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

/**
 * @brief Class responsible for the view to handle both
 * facial recognition and password authentication.
 */
public class LoginController{
    @FXML
    private ImageView cameraView;

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    private final CameraManager cameraManager;
    private final LecturerService lecturerService;
    private final FaceDetector faceDetector;
    private final FaceRecognition faceRecognition;
    boolean found = false;

    /**
     * @brief Primary constructor.
     */
    public LoginController() {
        this.cameraManager = new CameraManager();
        this.lecturerService = new LecturerService();
        this.faceDetector = new FaceDetector();
        this.faceRecognition = new FaceRecognition();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public LoginController(CameraManager cameraManager, LecturerService lecturerService,
                           FaceDetector faceDetector, FaceRecognition faceRecognition) {
        this.cameraManager = cameraManager;
        this.lecturerService = lecturerService;
        this.faceDetector = faceDetector;
        this.faceRecognition = faceRecognition;
    }

    /**
     * @brief Function closes camera
     * and clears the camera view.
     */
    public void stopRecording(){
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
        }
    }

    /**
     * @brief Function initializes the controller, sets up the camera
     * and searches through captured faces to automatically log in.
     */
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
                
                if (labels != null && labels.length > 0){
                    for (String label : labels){
                        if (label.equals(ID)){
                            found = true;
                            Platform.runLater(this::loadHome);
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

    /**
     * @brief After successful logging in function
     * redirects user to home screen.
     */
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

    /**
     * @brief Function checks if both login and password match,
     * displaying an error message if false.
     *
     * @param event ActionEvent triggered by an attempt to log in.
     */
    @FXML
    public void handleLogin(ActionEvent event){
        Lecturer lec = lecturerService.getLecturer();
        if(!(lec.getID().equals(name.getText().trim()) && AuthenticationService.checkPassword(password.getText().trim(), lec.getPasswordHash()))){
            errorLabel.setText("Try again!");  
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
            visiblePause.setOnFinished(ev -> errorLabel.setText(""));
            
            visiblePause.play();         
        } else {
            loadHome();
        }
    }
}
