package pl.projekt.controller;

import javafx.scene.control.ComboBox;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
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
import pl.projekt.service.LecturerService;
import pl.projekt.service.StudentService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

/**
 * @brief Class responsible for the view to adding a new student, capturing
 * their face data and saving their information to the database.
 */
public class AddStudentController {

    @FXML private ImageView cameraView;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField albumNumber;
    @FXML private Label fieldsErrorLabel;
    @FXML private Label cameraErrorLabel;
    @FXML private Label errorLabel;
    @FXML private ComboBox<String> cameraSelector;

    private final CameraManager cameraManager;
    private final StudentService studentService;
    private final FaceDetector faceDetector;
    private final FaceRecognition faceRecognition;

    private long lastSaveTime = 0;
    private final long intervalsTime = 500;
    boolean done = false;

    /**
     * @brief Primary constructor.
     */
    public AddStudentController(){
        cameraManager = new CameraManager();
        studentService = new StudentService();
        faceDetector = new FaceDetector();
        faceRecognition = new FaceRecognition();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public AddStudentController(CameraManager cameraManager, StudentService studentService,
                          FaceDetector faceDetector, FaceRecognition faceRecognition){
        this.cameraManager = cameraManager;
        this.studentService = studentService;
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
        } else {
            fieldsErrorLabel.setText("All fields are required!");
        }
    }

    /**
     * @brief Function is responsible for opening the camera, detecting faces and
     * capturing them at set intervals until it reaches satisfying number of images.
     *
     * @return True if the camera was successfully opened and directory created, false otherwise.
     */
    private boolean startRecording(){
        if (!faceRecognition.createDir(albumNumber.getText())){
            return false;
        }

        int cameraId = cameraSelector.getSelectionModel().getSelectedIndex();
        if (cameraId == -1) cameraId = 0;

        return cameraManager.openCamera(cameraId, frame -> {
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
    public void saveStudent() {
        if (!validateFields()) {
            return;
        } else if (!done){
            fieldsErrorLabel.setText("Record first!");
            return;
        }

        try {
            boolean success = studentService.addStudent(new Student(name.getText().trim(), surname.getText().trim(), albumNumber.getText().trim()));

            if (success) {
                cameraErrorLabel.setText("Student added successfully!");
                clearFields();
            } else {
                cameraErrorLabel.setText("Error adding student, check if student already exists.");
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
        albumNumber.clear();
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

        setupCapitalizationFilter(name);
        setupCapitalizationFilter(surname);

    }

    /**
     * @brief Function checks if all fields are filled correctly.
     * @return True if all fields are valid, false otherwise.
     */
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
     * @brief Function navigates back to the home view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
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
                    errorLabel.setText("Error: Can not load home screen");
                e.printStackTrace();
            } 
    }
}