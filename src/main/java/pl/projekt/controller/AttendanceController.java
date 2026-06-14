package pl.projekt.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.io.IOException;
import pl.projekt.models.Student;
import pl.projekt.models.Attendance;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StudentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Class responsible for displaying and filtering
 * student attendances for specific dates.
 */
public class AttendanceController {

    @FXML private Label infoLabel;
    @FXML private TableView<AttendanceRecord> attendanceTable;
    @FXML private TableColumn<AttendanceRecord, String> albumCol;
    @FXML private TableColumn<AttendanceRecord, String> nameCol;
    @FXML private TableColumn<AttendanceRecord, String> statusCol;
    @FXML private TableColumn<AttendanceRecord, String> timeCol;
    @FXML private DatePicker datePicker;
    @FXML private TextField filterField;

    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final ObservableList<AttendanceRecord> masterData = FXCollections.observableArrayList();

    /**
     * @brief Primary constructor.
     */
    public AttendanceController() {
        this.attendanceService = new AttendanceService();
        this.studentService = new StudentService();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public AttendanceController(AttendanceService attendanceService, StudentService studentService) {
        this.attendanceService = attendanceService;
        this.studentService = studentService;
    }

    /**
     * @brief Helper class representing student and their
     * attendance details for display in the table.
     */
    public static class AttendanceRecord {
        private final String album;
        private final String name;
        private final String status;
        private final String time;

        public AttendanceRecord(String album, String name, String status, String time) {
            this.album = album;
            this.name = name;
            this.status = status;
            this.time = time;
        }
        public String getAlbum() { return album; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public String getTime() { return time; }
    }

    /**
     * @brief Function initializes the controller, sets up columns,
     * configures the date picker and loads data.
     */
    @FXML public void initialize() {
        datePicker.setValue(LocalDate.now());

        albumCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlbum()));
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));

        setupFilter();
        loadDataByDate(datePicker.getValue());

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadDataByDate(newVal);
        });
    }

    /**
     * @brief Function loads student and attendance data for a given date.
     *
     * @param date Selected date.
     */
    private void loadDataByDate(LocalDate date) {
        masterData.clear();
        ArrayList<Student> students = studentService.getStudents();
        ArrayList<Attendance> attendances = attendanceService.getAttendanceByDate(date.toString());

        if (students == null || attendances == null) {
            infoLabel.setText("No data found for selected date.");
            return;
        }

        for (Student s : students) {
            String status = attendances.stream()
                    .filter(a -> a.getAlbumNumber().equals(s.getAlbumNumber()))
                    .map(Attendance::getStatus)
                    .findFirst()
                    .orElse("no record");

            String time = attendances.stream()
                    .filter(a -> a.getAlbumNumber().equals(s.getAlbumNumber()))
                    .map(Attendance::getTime)
                    .findFirst()
                    .orElse("no record");
            
            masterData.add(new AttendanceRecord(
                s.getAlbumNumber(), 
                s.getFirstName() + " " + s.getLastName(), 
                status,
                time
            ));
        }
        infoLabel.setText("Data loaded for " + date.toString());
    }

    /**
     * @brief Function configures the search filter, allowing users to filter
     * table by student's name, album number or attendance status.
     */
    private void setupFilter() {
        FilteredList<AttendanceRecord> filteredData = new FilteredList<>(masterData, p -> true);
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(r -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return r.getName().toLowerCase().contains(filter) ||
                       r.getAlbum().toLowerCase().contains(filter) ||
                       r.getStatus().toLowerCase().contains(filter);
            });
        });
        attendanceTable.setItems(filteredData);
    }

    /**
     * @brief Function navigates back to the home view.
     *
     * @param event ActionEvent triggered by the user interaction.
     */
    @FXML public void goHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomeScreenView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home Screen");
            stage.show();
        } catch (IOException e) {
            infoLabel.setText("Error while loading home screen");
        }
    }
}