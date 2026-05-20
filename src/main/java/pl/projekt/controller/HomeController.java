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

    @FXML
    public void addStudent(ActionEvent event){
        loadScene(event, "/AddStudentView.fxml", "Add Student");
    }

    @FXML
    public void showStatistics(ActionEvent event){
        loadScene(event, "/StatisticsView.fxml", "Statistics");
    }

    @FXML 
    public void showStudents(ActionEvent event){
        loadScene(event, "/StudentsView.fxml", "Students List");
    }

    @FXML 
    public void showAttendance(ActionEvent event){
        loadScene(event, "/AttendanceView.fxml", "Attendance");
    }

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

