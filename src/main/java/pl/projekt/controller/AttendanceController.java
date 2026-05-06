package pl.projekt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import pl.projekt.models.Student;
import pl.projekt.models.Attendance;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StudentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceController{
    //@FXML
    private TableView<Student> attendanceTable;
    //@FXML
    private TableColumn<Student, String> nameCol;
    //@FXML
    private TableColumn<Student, Boolean> statusCol;
    //@FXML
    private DatePicker date;

    private final AttendanceService attendanceService = new AttendanceService();
    private final StudentService studentService = new StudentService();
    private final Map<String, SimpleBooleanProperty> attendanceMap = new HashMap<>();

    //@FXML
    public void initialize(){
        date.setValue(LocalDate.now());
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName() + data.getValue().getLastName()));
        statusCol.setCellFactory(CheckBoxTableCell.forTableColumn(statusCol));
        statusCol.setEditable(true);
        attendanceTable.setEditable(true);

        statusCol.setCellValueFactory(data -> {
            String albumNumber = data.getValue().getAlbumNumber();
            return attendanceMap.computeIfAbsent(albumNumber, k -> new SimpleBooleanProperty(false));
        });

        loadData();
    }

    public void loadData(){
        ArrayList<Student> students = studentService.getStudents();
        if(students != null) 
            attendanceTable.getItems().setAll(students);
    }

    //@FXML
    public void handleSave(){
        String selectedDate = date.getValue().toString();

        for(Student student : attendanceTable.getItems()){
            String albumNumber = student.getAlbumNumber();
            boolean isPresent = attendanceMap.getOrDefault(albumNumber, new SimpleBooleanProperty(false)).get();
            String status = isPresent ? "present" : "absent";

            Attendance attendance = new Attendance(albumNumber, selectedDate, status);
            boolean success = attendanceService.addAttendance(attendance);

            if(success)
                System.out.println("Zapisano studenta " + albumNumber + " jako " + status);
        }
    }

}