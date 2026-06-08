package pl.projekt.controller;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.service.LecturerService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddLecturerControllerTest extends ApplicationTest {

    private AddLecturerController controller;

    @Mock private CameraManager cameraManager;
    @Mock private LecturerService lecturerService;
    @Mock private FaceDetector faceDetector;
    @Mock private FaceRecognition faceRecognition;

    private TextField nameField, surnameField, idField;
    private PasswordField psswdField, confirmPsswdField;
    private Label fieldsErrorLabel, cameraErrorLabel, errorLabel;

    @Start
    public void start(Stage stage) throws Exception {
        nameField = new TextField();
        surnameField = new TextField();
        idField = new TextField();
        psswdField = new PasswordField();
        confirmPsswdField = new PasswordField();
        fieldsErrorLabel = new Label();
        cameraErrorLabel = new Label();
        errorLabel = new Label();

        controller = new AddLecturerController(cameraManager, lecturerService, faceDetector, faceRecognition);

        injectField("name", nameField);
        injectField("surname", surnameField);
        injectField("IDnumber", idField);
        injectField("psswd", psswdField);
        injectField("confirmPsswd", confirmPsswdField);
        injectField("fieldsErrorLabel", fieldsErrorLabel);
        injectField("cameraErrorLabel", cameraErrorLabel);
        injectField("errorLabel", errorLabel);

        VBox root = new VBox(nameField, surnameField, idField, psswdField, confirmPsswdField, fieldsErrorLabel, cameraErrorLabel, errorLabel);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = AddLecturerController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @Test
    void shouldShowErrorWhenFieldsAreEmpty() {
        interact(() -> controller.startStopRecording());
        assertEquals("All fields are required!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldShowErrorForInvalidIdFormat() {
        interact(() -> {
            nameField.setText("Jan");
            surnameField.setText("Kowalski");
            idField.setText("123"); // Złe ID
            psswdField.setText("Haslo123!");
            confirmPsswdField.setText("Haslo123!");
            controller.startStopRecording();
        });
        assertEquals("Wrong ID number!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldShowErrorWhenPasswordsDoNotMatch() {
        interact(() -> {
            nameField.setText("Jan");
            surnameField.setText("Kowalski");
            idField.setText("12345678");
            psswdField.setText("Haslo123!");
            confirmPsswdField.setText("Inne123!");
            controller.startStopRecording();
        });
        assertEquals("Passwords are not the same!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldNotSaveWithoutFaceTraining() {
        interact(() -> {
            nameField.setText("Jan");
            surnameField.setText("Kowalski");
            idField.setText("12345678");
            psswdField.setText("Haslo123!");
            confirmPsswdField.setText("Haslo123!");
            controller.saveLecturer();
        });
        assertEquals("Record first!", fieldsErrorLabel.getText());
    }

    @Test
    void shouldSaveLecturerWhenAllIsValid() throws Exception {
        Field doneField = AddLecturerController.class.getDeclaredField("done");
        doneField.setAccessible(true);
        doneField.set(controller, true);

        when(lecturerService.addLecturer(any())).thenReturn(true);

        interact(() -> {
            nameField.setText("Jan");
            surnameField.setText("Kowalski");
            idField.setText("12345678");
            psswdField.setText("Haslo123!");
            confirmPsswdField.setText("Haslo123!");
            controller.saveLecturer();
        });

        assertEquals("Lecturer added successfully!", cameraErrorLabel.getText());
        verify(lecturerService, times(1)).addLecturer(any());
    }
}