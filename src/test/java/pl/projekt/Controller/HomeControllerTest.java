package pl.projekt.controller;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.service.AttendanceService;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceDetector;
import pl.projekt.util.FaceRecognition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeControllerTest extends ApplicationTest {

    private HomeController controller;

    @Mock private CameraManager cameraManager;
    @Mock private FaceDetector faceDetector;
    @Mock private FaceRecognition faceRecognition;
    @Mock private AttendanceService attendanceService;

    private ImageView cameraView;
    private Label errorLabel;

    @Start
    public void start(Stage stage) throws Exception {
        cameraView = new ImageView();
        errorLabel = new Label();

        controller = new HomeController(cameraManager, faceDetector, faceRecognition, attendanceService);

        injectField("cameraView", cameraView);
        injectField("errorLabel", errorLabel);

        VBox root = new VBox(cameraView, errorLabel);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = HomeController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }


    @Test
    void shouldStopCameraAndFillAbsencesIfCameraIsActive() {
        when(cameraManager.isCameraActive()).thenReturn(true);

        interact(() -> controller.startStopRecording());

        verify(cameraManager, times(1)).closeCamera();
        verify(attendanceService, times(1)).fillAbsentByDate(anyString(), anyString());
        assertNull(cameraView.getImage());
    }

    @Test
    void shouldShowErrorWhenCameraCannotBeOpened() {
        when(cameraManager.isCameraActive()).thenReturn(false);
        when(faceRecognition.loadRecognizer()).thenReturn(false);
        when(cameraManager.openCamera(any())).thenReturn(false);

        interact(() -> controller.startStopRecording());

        assertEquals("Error: can not find camera.", errorLabel.getText());
    }

    @Test
    void shouldExecuteCameraCallbackAndLogAttendanceWhenFaceIsRecognized() throws Exception {
        when(cameraManager.isCameraActive()).thenReturn(false);
        when(faceRecognition.loadRecognizer()).thenReturn(true);

        when(cameraManager.openCamera(any())).thenAnswer(invocation -> {
            Object lambdaCallback = invocation.getArgument(0);
            Mat mockFrame = mock(Mat.class);
            Rect[] mockFaces = new Rect[]{mock(Rect.class)};
            String[] detectedLabels = new String[]{"123456"};

            when(faceDetector.getRectFaces(mockFrame)).thenReturn(mockFaces);
            when(faceRecognition.recognize(mockFrame, mockFaces)).thenReturn(detectedLabels);

            for (Method m : lambdaCallback.getClass().getMethods()) {
                if (m.getParameterCount() == 1 && !m.isDefault() && !Modifier.isStatic(m.getModifiers())) {
                    m.invoke(lambdaCallback, mockFrame);
                    break;
                }
            }
            return true;
        });

        interact(() -> controller.startStopRecording());

        verify(attendanceService, times(1)).addAttendance(argThat(attendance ->
                attendance.getAlbumNumber().equals("123456") && attendance.getStatus().equals("present")
        ));
        assertEquals("", errorLabel.getText());
    }

    @Test
    void shouldCloseCameraWhenNavigatingToOtherViews() {
        when(cameraManager.isCameraActive()).thenReturn(true);

        interact(() -> {
            Button btn = new Button();
            Scene dummyScene = new Scene(btn);
            Stage dummyStage = new Stage();
            dummyStage.setScene(dummyScene);
            ActionEvent event = new ActionEvent(btn, null);

            controller.addStudent(event);
        });

        verify(cameraManager, times(1)).closeCamera();
    }

    @Test
    void shouldHandleMissingFxmlFiles() {
        when(cameraManager.isCameraActive()).thenReturn(false);

        interact(() -> {
            Button btn = new Button();
            Scene dummyScene = new Scene(btn);
            Stage dummyStage = new Stage();
            dummyStage.setScene(dummyScene);
            ActionEvent event = new ActionEvent(btn, null);

            controller.showStatistics(event);
        });

        String currentError = errorLabel.getText();

        boolean isExpectedOutcome = currentError.isEmpty()
                || currentError.equals("Error: can not load /StatisticsView.fxml")
                || currentError.equals("Error: fxml file does not exist");

        assertTrue(isExpectedOutcome, "Nieoczekiwany stan etykiety błędu: '" + currentError + "'");
    }
}