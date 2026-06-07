package pl.projekt.controller;

import pl.projekt.util.FaceDetector;
import pl.projekt.service.AttendanceService;
import pl.projekt.models.Attendance;

import javafx.scene.image.Image;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pl.projekt.util.CameraManager;
import pl.projekt.util.FaceRecognition;
import org.bytedeco.opencv.opencv_core.Rect;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import java.io.IOException;

/**
 * @brief Class responsible for managing home screen, handling view for
 * facial recognition, logging attendance and navigating to other views.
 */
public class HomeController{

    @FXML
    private ImageView cameraView;
    @FXML
    private Label errorLabel;

    private final CameraManager cameraManager = new CameraManager();
    private final FaceDetector faceDetector = new FaceDetector();
    private final FaceRecognition faceRecognition = new FaceRecognition();
    private final AttendanceService attendanceService = new AttendanceService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final Set<String> markedPresent = new HashSet<>();

    /**
     * @brief Function toggles the camera recording state.
     * When started, captures faces on the camera feed to mark students as present.
     * When stopped, it fills remaining students as absent and closes the camera.
     */
    @FXML
    public void startStopRecording(){
        String date = LocalDate.now().toString();
        
        if(cameraManager.isCameraActive()){
            cameraManager.closeCamera();
            cameraView.setImage(null);
            attendanceService.fillAbsentByDate(LocalDate.now().toString() , LocalTime.now().format(formatter));
        } else {
            markedPresent.clear();
            boolean success;

            if (faceRecognition.loadRecognizer()){
                success = cameraManager.openCamera(frame -> {
                    Rect[] faces = faceDetector.getRectFaces(frame);
                    String[] labels = faceRecognition.recognize(frame, faces);

                    faceDetector.drawFaces(frame, faces, labels);

                    String time = LocalTime.now().format(formatter);
                    
                    if (labels != null) {
                        for (String label : labels) {
                            if (!label.equals("Unknown") && !markedPresent.contains(label) && label.length()==6) {
                                attendanceService.addAttendance(new Attendance(label, date, time, "present"));
                                markedPresent.add(label);
                            }
                        }
                    }

                    Image imageToShow = cameraManager.convertMatToImage(frame);
                    Platform.runLater(() -> cameraView.setImage(imageToShow));
                });
            } else {
                success = cameraManager.openCamera(frame -> {
                    Rect[] faces = faceDetector.getRectFaces(frame);
                    faceDetector.drawFaces(frame, faces);
                    Image imageToShow = cameraManager.convertMatToImage(frame);
                    Platform.runLater(() -> cameraView.setImage(imageToShow));
                });
            }

            if (!success)
                errorLabel.setText("Error: can not find camera.");
            else
                errorLabel.setText("");
        }
    }

    /**
     * @brief Function handles navigation to Add Student view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
    @FXML
    public void addStudent(ActionEvent event){
        loadScene(event, "/AddStudentView.fxml", "Add Student");
    }

    /**
     * @brief Function handles navigation to Statistics view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
    @FXML
    public void showStatistics(ActionEvent event){
        loadScene(event, "/StatisticsView.fxml", "Statistics");
    }

    /**
     * @brief Function handles navigation to Students view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
    @FXML 
    public void showStudents(ActionEvent event){
        loadScene(event, "/StudentsView.fxml", "Students List");
    }

    /**
     * @brief Function handles navigation to Attendance view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
    @FXML 
    public void showAttendance(ActionEvent event){
        loadScene(event, "/AttendanceView.fxml", "Attendance");
    }

    /**
     * @brief Helper function to load a new scene (closes the camera if it is active).
     *
     * @param event ActionEvent triggered by the user interaction.
     * @param fxmlPath Path to the FXML file.
     * @param title Title for the new window.
     */
    private void loadScene(ActionEvent event, String fxmlPath, String title){
        if(cameraManager.isCameraActive())
            cameraManager.closeCamera();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            if( errorLabel != null )
                errorLabel.setText("Error: can not load " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e){
            if( errorLabel != null )
                errorLabel.setText("Error: fxml file does not exist");
        }
    }

}

