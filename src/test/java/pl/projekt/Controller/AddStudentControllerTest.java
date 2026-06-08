package pl.projekt.controller;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.service.StudentService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddStudentControllerTest extends ApplicationTest {

    private AddStudentController controller;

    @Mock private CameraManager cameraManager;
    @Mock private StudentService studentService;
    @Mock private FaceDetector faceDetector;
    @Mock private FaceRecognition faceRecognition;

    private ImageView cameraView;
    private TextField nameField;
    private TextField surnameField;
    private TextField albumNumberField;
    private Label fieldsErrorLabel;
    private Label cameraErrorLabel;
    private Label errorLabel;

    @Start
    public void start(Stage stage) throws Exception {
        cameraView = new ImageView();
        nameField = new TextField();
        surnameField = new TextField();
        albumNumberField = new TextField();
        fieldsErrorLabel = new Label();
        cameraErrorLabel = new Label();
        errorLabel = new Label();

        controller = new AddStudentController(cameraManager, studentService, faceDetector, faceRecognition);

        injectField("cameraView", cameraView);
        injectField("name", nameField);
        injectField("surname", surnameField);
        injectField("albumNumber", albumNumberField);
        injectField("fieldsErrorLabel", fieldsErrorLabel);
        injectField("cameraErrorLabel", cameraErrorLabel);
        injectField("errorLabel", errorLabel);

        VBox root = new VBox(cameraView, nameField, surnameField, albumNumberField, fieldsErrorLabel, cameraErrorLabel, errorLabel);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = AddStudentController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private void setPrivateBoolean(String fieldName, boolean value) throws Exception {
        Field field = AddStudentController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(controller, value);
    }

    @Test
    void shouldShowErrorWhenFieldsAreEmptyOnRecordingStart() {
        interact(() -> controller.startStopRecording());
        assertEquals("All fields are required!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldCallStopRecordingIfCameraIsAlreadyActive() {
        when(cameraManager.isCameraActive()).thenReturn(true);
        when(faceRecognition.trainFace()).thenReturn(true);

        interact(() -> controller.startStopRecording());

        verify(cameraManager, times(1)).closeCamera();
        assertEquals(null, cameraView.getImage());
    }


    @Test
    void shouldShowSpecificErrorForWrongAlbumNumberFormat() {
        interact(() -> {
            nameField.setText("Anna");
            surnameField.setText("Nowak");
            albumNumberField.setText("123");
            controller.saveStudent();
        });
        assertEquals("Wrong album number!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldNotSaveStudentWhenRecordingIsNotDone() {
        interact(() -> {
            nameField.setText("Anna");
            surnameField.setText("Nowak");
            albumNumberField.setText("123456");
            controller.saveStudent();
        });
        assertEquals("Record first!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldSuccessfullyAddStudentWhenDataAndModelAreReady() throws Exception {
        setPrivateBoolean("done", true);
        when(studentService.addStudent(any())).thenReturn(true);

        interact(() -> {
            nameField.setText("Anna");
            surnameField.setText("Nowak");
            albumNumberField.setText("123456");
            controller.saveStudent();
        });

        assertEquals("Student added successfully!", cameraErrorLabel.getText());
        assertTrue(nameField.getText().isEmpty());
        assertTrue(surnameField.getText().isEmpty());
        assertTrue(albumNumberField.getText().isEmpty());
        verify(studentService, times(1)).addStudent(any());
    }

    @Test
    void shouldShowErrorWhenStudentAlreadyExistsInDatabase() throws Exception {
        setPrivateBoolean("done", true);
        when(studentService.addStudent(any())).thenReturn(false);

        interact(() -> {
            nameField.setText("Jan");
            surnameField.setText("Kowalski");
            albumNumberField.setText("654321");
            controller.saveStudent();
        });

        assertEquals("Error adding student, check if student already exists.", cameraErrorLabel.getText());
    }
}