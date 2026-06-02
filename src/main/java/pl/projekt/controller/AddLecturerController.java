package pl.projekt.controller;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

import pl.projekt.models.Lecturer;
import pl.projekt.service.LecturerService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

public class AddLecturerController {

    @FXML private ImageView cameraView;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField IDnumber;
    @FXML private PasswordField psswd;
    @FXML private PasswordField confirmPsswd;
    @FXML private Label fieldsErrorLabel;
    @FXML private Label cameraErrorLabel;
    @FXML private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();
    private final LecturerService lecturerService = new LecturerService();
    private final FaceDetector faceDetector = new FaceDetector();
    private final FaceRecognition faceRecognition = new FaceRecognition();

    private long lastSaveTime = 0;
    private final long intervalsTime = 500;
    boolean done = false;

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
        if (!faceRecognition.createDir(IDnumber.getText())){
            return false;
        }
        return cameraManager.openCamera(frame -> {
            Rect[] faces = faceDetector.getRectFaces(frame);
            faceDetector.drawFaces(frame, faces);

            long currTime = System.currentTimeMillis();
            if (currTime - lastSaveTime >= intervalsTime){
                faceRecognition.saveFace(frame, faces);
                lastSaveTime = currTime;

                int photoCount = faceRecognition.getPhotoCount();
                Platform.runLater(() -> 
                cameraErrorLabel.setText("Photos: " + photoCount + "/60-100")
            );
            }

            Image imageToShow = cameraManager.convertMatToImage(frame);
            Platform.runLater(() -> cameraView.setImage(imageToShow));
        });
    }

    private void stopRecording() {
        boolean flag = faceRecognition.trainFace();
        cameraManager.closeCamera();
        Platform.runLater(() -> {
            cameraView.setImage(null);
            if (flag) {
                cameraErrorLabel.setText("Camera off. Face model updated");
                done = true;
            } else {
                cameraErrorLabel.setText("Camera off. Error: Not enough photos");
            }
        });
    }

    @FXML
    public void saveLecturer() {
        if (!validateFields()) {
            return;
        } else if (!done){
            fieldsErrorLabel.setText("Record first!");
            return;
        }

        try {
            boolean success = lecturerService.addLecturer(new Lecturer(IDnumber.getText().trim(), name.getText().trim(), surname.getText().trim(), psswd.getText()));

            if (success) {
                cameraErrorLabel.setText("Lecturer added successfully!");
                clearFields();
                this.goHome();

            } else {
                cameraErrorLabel.setText("Error adding Lecturer");
            }
        } catch (Exception e) {
            cameraErrorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        name.clear();
        surname.clear();
        psswd.clear();
        IDnumber.clear();
    }

    public void initialize() {
        fieldsErrorLabel.setText("");
        cameraErrorLabel.setText("");
        
        name.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));
        surname.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));
        IDnumber.textProperty().addListener((obs, old, nw) -> fieldsErrorLabel.setText(""));

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

        IDnumber.textProperty().addListener((obs, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            IDnumber.setText(newValue.replaceAll("[^\\d]", ""));
        }
    });
        setupCapitalizationFilter(name);
        setupCapitalizationFilter(surname);
    }

    private void setupCapitalizationFilter(TextField textField) {
        textField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String text = change.getText();
            if (change.getControlNewText().length() > 0 && change.getRangeStart() == 0) {
                if (!text.isEmpty()) {
                    change.setText(text.substring(0, 1).toUpperCase() + text.substring(1));
                }
            }
            return change;
        }));
    }

    private boolean validatePsswd(){
        char[] password = psswd.getText().toCharArray();
        boolean capitalizedLetter = false;
        boolean specialCharacter = false;
        boolean number = false;

        if (password.length < 8) return false;

        for (char curr : password) {
            if (Character.isWhitespace(curr)) return false;
            if (Character.isDigit(curr)) number = true;
            if (Character.isUpperCase(curr)) capitalizedLetter = true; 
            if (!Character.isLetterOrDigit(curr)) specialCharacter = true;
        }

        return capitalizedLetter && specialCharacter && number;
    }

    private boolean validateFields() {

        if (name.getText().trim().isEmpty() || surname.getText().trim().isEmpty() || IDnumber.getText().trim().isEmpty() || psswd.getText().trim().isEmpty()) {
            fieldsErrorLabel.setText("All fields are required!");
            return false;
        }

        if (!IDnumber.getText().matches("\\d{8}")) {
            fieldsErrorLabel.setText("Wrong ID number!");
            return false;
        }

        if (!validatePsswd()) {
            fieldsErrorLabel.setText("Wrong password!");
            return false;
        }

        if (!psswd.getText().equals(confirmPsswd.getText())){
            fieldsErrorLabel.setText("Passwords are not the same!");
            return false;
        }

        return true;
    }

    public void goHome(){
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
}