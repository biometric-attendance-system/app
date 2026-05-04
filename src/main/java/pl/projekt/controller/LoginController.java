package pl.projekt.controller;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import pl.projekt.service.AuthenticationService;
import pl.projekt.service.LecturerService;

public class LoginController{
    private ImageView cameraView;
    
    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    LecturerService lecturerService = new LecturerService();

    
    @FXML
    public void handleLogin(){

    if(!AuthenticationService.checkPin(password.getText(), lecturerService.getHashedPin(name.getText()))){
        errorLabel.setText("Try again!");  
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
            
        visiblePause.setOnFinished(event -> errorLabel.setText(""));
            
        visiblePause.play();         
    } else {
            //przechodzimy dalej
        }
    }
}
