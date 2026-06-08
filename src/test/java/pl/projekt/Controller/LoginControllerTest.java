package pl.projekt.controller;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import pl.projekt.models.Lecturer;
import pl.projekt.service.AuthenticationService;
import pl.projekt.service.LecturerService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ApplicationTest {

    private LoginController controller;

    @Mock private CameraManager cameraManager;
    @Mock private LecturerService lecturerService;
    @Mock private FaceDetector faceDetector;
    @Mock private FaceRecognition faceRecognition;
    @Mock private Lecturer mockLecturer;

    private ImageView cameraView;
    private TextField nameField;
    private PasswordField passwordField;
    private Label errorLabel;

    @Start
    public void start(Stage stage) throws Exception {
        cameraView = new ImageView();
        nameField = new TextField();
        passwordField = new PasswordField();
        errorLabel = new Label();

        controller = new LoginController(cameraManager, lecturerService, faceDetector, faceRecognition);

        injectField("cameraView", cameraView);
        injectField("name", nameField);
        injectField("password", passwordField);
        injectField("errorLabel", errorLabel);

        VBox root = new VBox(cameraView, nameField, passwordField, errorLabel);
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = LoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }


    @Test
    void shouldCloseCameraAndClearViewOnStopRecording() {
        when(cameraManager.isCameraActive()).thenReturn(true);

        interact(() -> controller.stopRecording());

        verify(cameraManager, times(1)).closeCamera();
        assertNull(cameraView.getImage());
    }


    @Test
    void shouldShowErrorWhenCameraFailsToOpenDuringInitialize() {
        when(lecturerService.getLecturer()).thenReturn(mockLecturer);
        when(mockLecturer.getID()).thenReturn("LECT_001");
        when(cameraManager.openCamera(any())).thenReturn(false);

        interact(() -> controller.initialize());

        assertEquals("No camera found!", errorLabel.getText());
    }

    @Test
    void shouldLoginAutomaticallyWhenLecturerFaceIsRecognized() throws Exception {
        when(lecturerService.getLecturer()).thenReturn(mockLecturer);
        when(mockLecturer.getID()).thenReturn("LECT_001");

        when(cameraManager.openCamera(any())).thenAnswer(invocation -> {
            Object lambdaCallback = invocation.getArgument(0);
            Mat mockFrame = mock(Mat.class);
            Rect[] mockFaces = new Rect[]{mock(Rect.class)};
            String[] labels = new String[]{"LECT_001"};

            when(faceDetector.getRectFaces(mockFrame)).thenReturn(mockFaces);
            when(faceRecognition.recognize(mockFrame, mockFaces)).thenReturn(labels);

            for (Method m : lambdaCallback.getClass().getMethods()) {
                if (m.getParameterCount() == 1 && !m.isDefault() && !Modifier.isStatic(m.getModifiers())) {
                    m.invoke(lambdaCallback, mockFrame);
                    break;
                }
            }
            return true;
        });

        interact(() -> controller.initialize());

        WaitForAsyncUtils.waitForFxEvents();

        Field foundField = LoginController.class.getDeclaredField("found");
        foundField.setAccessible(true);
        assertTrue((boolean) foundField.get(controller));

        String currentError = errorLabel.getText();
        boolean validNavigationOutcome = currentError.isEmpty() || currentError.equals("Error: can not load home screen");
        assertTrue(validNavigationOutcome);
    }


    @Test
    void shouldLoginSuccessfullyWithCorrectPasswordCredentials() {
        when(lecturerService.getLecturer()).thenReturn(mockLecturer);
        when(mockLecturer.getID()).thenReturn("admin");
        when(mockLecturer.getPasswordHash()).thenReturn("secure_hash");

        interact(() -> {
            nameField.setText("admin");
            passwordField.setText("mypassword");
        });

        interact(() -> {
            try (MockedStatic<AuthenticationService> mockedAuth = mockStatic(AuthenticationService.class)) {
                mockedAuth.when(() -> AuthenticationService.checkPassword("mypassword", "secure_hash"))
                        .thenReturn(true);

                controller.handleLogin(new ActionEvent());
            }
        });

        assertNotEquals("Try again!", errorLabel.getText());
    }

    @Test
    void shouldShowTemporaryErrorWithIncorrectPasswordCredentials() {
        when(lecturerService.getLecturer()).thenReturn(mockLecturer);
        when(mockLecturer.getID()).thenReturn("admin");
        when(mockLecturer.getPasswordHash()).thenReturn("secure_hash");

        interact(() -> {
            nameField.setText("admin");
            passwordField.setText("wrong_password");
        });

        interact(() -> {
            try (MockedStatic<AuthenticationService> mockedAuth = mockStatic(AuthenticationService.class)) {
                mockedAuth.when(() -> AuthenticationService.checkPassword("wrong_password", "secure_hash"))
                        .thenReturn(false);

                controller.handleLogin(new ActionEvent());
            }
        });

        assertEquals("Try again!", errorLabel.getText());
    }
}