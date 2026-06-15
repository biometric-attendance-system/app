package pl.projekt.controller;

import javafx.scene.control.ComboBox;
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

import java.io.IOException;

import pl.projekt.models.Lecturer;
import pl.projekt.service.LecturerService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

/**
 * @brief Class responsible for the view to add a new lecturer,
 * capturing their face data, validating fields and saving the
 * record in the Lecturer database.
 */
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
    @FXML private ComboBox<String> cameraSelector;

    private final CameraManager cameraManager;
    private final LecturerService lecturerService;
    private final FaceDetector faceDetector;
    private final FaceRecognition faceRecognition;

    private long lastSaveTime = 0;
    private final long intervalsTime = 500;
    boolean done = false;

    /**
     * @brief Primary constructor.
     */
    public AddLecturerController(){
        cameraManager = new CameraManager();
        lecturerService = new LecturerService();
        faceDetector = new FaceDetector();
        faceRecognition = new FaceRecognition();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public AddLecturerController(CameraManager cameraManager, LecturerService lecturerService,
                          FaceDetector faceDetector, FaceRecognition faceRecognition){
        this.cameraManager = cameraManager;
        this.lecturerService = lecturerService;
        this.faceDetector = faceDetector;
        this.faceRecognition = faceRecognition;
    }

    /**
     * @brief Function toggles the camera recording state.
     * Before opening, it checks whether fields have been
     * filled in correctly.
     */
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
        }
    }

    /**
     * @brief Function is responsible for opening the camera, detecting faces and
     * capturing them at set intervals until it reaches satisfying number of images.
     *
     * @return True if the camera was successfully opened and directory created, false otherwise.
     */
    private boolean startRecording(){
        if (!faceRecognition.createDir(IDnumber.getText())){
            return false;
        }

        int cameraId = cameraSelector.getSelectionModel().getSelectedIndex();
        if (cameraId == -1) cameraId = 0;

        return cameraManager.openCamera(cameraId, frame -> {
            Rect[] faces = faceDetector.getRectFaces(frame);
            try {
                faceDetector.drawFaces(frame, faces);

                long currTime = System.currentTimeMillis();
                if (currTime - lastSaveTime >= intervalsTime) {
                    faceRecognition.saveFace(frame, faces);
                    lastSaveTime = currTime;

                    int photoCount = faceRecognition.getPhotoCount();
                    Platform.runLater(() ->
                            cameraErrorLabel.setText("Photos: " + photoCount + "/60-100")
                    );
                    if (photoCount >= 100) {
                        Platform.runLater(() -> {
                            if (cameraManager.isCameraActive()) {
                                stopRecording();
                            }
                        });
                        return;
                    }
                }

                Image imageToShow = cameraManager.convertMatToImage(frame);
                Platform.runLater(() -> cameraView.setImage(imageToShow));
            } finally {
                if (faces != null) {
                    for (Rect face : faces) {
                        if (face != null) {
                            face.close();
                        }
                    }
                }
            }
        });
    }

    /**
     * @brief Function closes the camera and invokes the
     * face model training process with captured photos.
     */
    private void stopRecording() {
        cameraManager.closeCamera();
        Platform.runLater(() -> {
            cameraView.setImage(null);
            if (faceRecognition.trainFace()) {
                cameraErrorLabel.setText("Camera off. Face model updated");
                done = true;
            } else {
                cameraErrorLabel.setText("Camera off. Error: Not enough photos");
            }
        });
    }

    /**
     * @brief Function checks if fields have been filled in
     * correctly and whether training model has been updated.
     * If true, student is added to the database.
     */
    @FXML
    public void saveLecturer() {
        if (!validateFields()) {
            return;
        } else if (!done){
            fieldsErrorLabel.setText("Record first!");
            return;
        }

        try {
            boolean success = lecturerService.addLecturer(new Lecturer(name.getText().trim(), surname.getText().trim(), IDnumber.getText().trim(), psswd.getText()));

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

    /**
     * @brief Function clears all fields.
     */
    private void clearFields() {
        name.clear();
        surname.clear();
        psswd.clear();
        IDnumber.clear();
    }

    /**
     * @brief Function initializes the controller, setts up ComboBox and
     * event listeners for input validation and text formatting.
     */
    public void initialize() {
        int cameraCount = cameraManager.countCameras();

        for (int i = 0; i < cameraCount; i++) {
            cameraSelector.getItems().add("Camera " + i);
        }
        if (cameraCount > 0) {
            cameraSelector.getSelectionModel().selectFirst();
        } else {
            cameraSelector.setPromptText("No cameras");
            cameraSelector.setDisable(true);
        }

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

    /**
     * @brief Function capitalizes first letter of a text field.
     * @param textField Given text field.
     */
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

    /**
     * @brief Function validates the password with requirements: at least 8 characters,
     * one uppercase letter, one digit, one special character and no whitespaces.
     *
     * @return True if the password meets all requirements, false otherwise.
     */
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

    /**
     * @brief Function checks if all fields are filled correctly.
     * @return True if all fields are valid, false otherwise.
     */
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

    /**
     * @brief Function navigates back to the home view.
     */
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
                    errorLabel.setText("Error: Can not load home screen");
                e.printStackTrace();
            } 
    }
}