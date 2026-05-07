package pl.projekt.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

import pl.projekt.service.StudentService;
import pl.projekt.models.Student;

public class StudentController{

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> surNameCol;
    @FXML private TableColumn<Student, String> albumNumberCol;

    @FXML private TextField filterField;
    @FXML private Label status;

    private final StudentService service = new StudentService();
    private ObservableList<Student> masterData = FXCollections.observableArrayList();

    @FXML public void initialize(){
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        surNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        albumNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlbumNumber()));

        loadData();

        FilteredList<Student> filteredData = new FilteredList<>(masterData, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String filter = newValue.toLowerCase();

                if (student.getFirstName().toLowerCase().contains(filter)) return true;
                if (student.getLastName().toLowerCase().contains(filter)) return true;
                if (student.getAlbumNumber().contains(filter)) return true;
                return false;

            });
        });

        studentTable.setItems(filteredData);

    }

 /*   @FXML public void addStudent(){
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String album = albumNumberField.getText().trim();    

        if(name.isEmpty() || surname.isEmpty() || album.isEmpty()){
            status.setText("All fields are required!");    
            return;
        }

        Student student = new Student(name, surname, album);

        if(service.addStudent(student)){
            masterData.add(student);
            clearFields();
            status.setText("");
            System.out.println("Successfully added student: " + album);
        }
        else{
            status.setText("Student " + album + " already exists.");
        }
    }
*/
    private void loadData(){
        ArrayList<Student> students = service.getStudents();
        if(students != null)
            masterData.setAll(students);
    }
}
