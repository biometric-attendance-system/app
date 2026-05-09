package pl.projekt.controller;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

import pl.projekt.models.Student;
import pl.projekt.service.StudentService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;

public class AddStudentController {

    @FXML private ImageView cameraView;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField albumNumber;
    @FXML private Label fieldsErrorLabel;
    @FXML private Label cameraErrorLabel;
    @FXML private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();
    private final StudentService studentService = new StudentService();
    private final FaceDetector faceDetector = new FaceDetector();

    @FXML 
    public void startStopRecording() {
        fieldsErrorLabel.setText("");
        cameraErrorLabel.setText("");

        if (cameraManager.isCameraActive()) {
            stopRecording();
            return;
        }

        if (validateFields()) {
            boolean started = startRecording();
            if (!started)
                cameraErrorLabel.setText("Error opening camera");
            else 
                cameraErrorLabel.setText("Recording...");
            
        } else {
            fieldsErrorLabel.setText("All fields are required!");
        }
    }

    private boolean startRecording(){
        return cameraManager.openCamera(frame -> {
            Rect[] faces = faceDetector.getRectFaces(frame);
            faceDetector.drawFaces(frame, faces);
            Image imageToShow = cameraManager.convertMatToImage(frame);
            Platform.runLater(() -> cameraView.setImage(imageToShow));
        });
    }

    private void stopRecording() {
        cameraManager.closeCamera();
        Platform.runLater(() -> {
            cameraView.setImage(null);
            cameraErrorLabel.setText("Camera is off.");
        });
    }

    @FXML
    public void saveStudent() {
        if (!validateFields()) {
            return;
        }

        try {
            boolean success = studentService.addStudent(new Student(name.getText().trim(), surname.getText().trim(), albumNumber.getText().trim()));

            if (success) {
                cameraErrorLabel.setText("Student added successfully!");
                clearFields();
            } else {
                cameraErrorLabel.setText("Error adding student.");
            }
        } catch (Exception e) {
            cameraErrorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        name.clear();
        surname.clear();
        albumNumber.clear();
    }

    public void initialize() {
        fieldsErrorLabel.setText("");
        cameraErrorLabel.setText("");
        
        name.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));
        surname.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));
        albumNumber.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));

        name.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]*")) {
                name.setText(newValue.replaceAll("[^a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]", ""));
            }
        });

        surname.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]*")) {
                surname.setText(newValue.replaceAll("[^a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]", ""));
            }
        });

        albumNumber.textProperty().addListener((obs, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            albumNumber.setText(newValue.replaceAll("[^\\d]", ""));
        }
    });
    }

    private boolean validateFields() {

        if (name.getText().trim().isEmpty() || surname.getText().trim().isEmpty() || albumNumber.getText().trim().isEmpty()) {
            fieldsErrorLabel.setText("All fields are required!");
            return false;
        }

        if (!albumNumber.getText().matches("\\d{6}")) {
            fieldsErrorLabel.setText("Wrong album number!");
            return false;
        }

        return true;
    }

    @FXML public void goHome(ActionEvent event){
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