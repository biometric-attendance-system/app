package pl.projekt.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import pl.projekt.service.AuthenticationService;

public class LoginController{
    private ImageView cameraView;

    private TextField name;

    private TextField password;

    public void handleLogin(){

        AuthenticationService service = new AuthenticationService();
        String pin = service.pinToHash(password.getText());
        if(service.checkPin(password.getText(), pin))
            System.out.println("You're loged in now!");
        else 
            System.out.println("Try again!");
            
    }
}
