package pl.projekt.controller;

import javafx.collections.FXCollections;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

import pl.projekt.service.StudentService;
import pl.projekt.models.Student;

public class StudentController{

    @FXML private Label errorLabel;   
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> surNameCol;
    @FXML private TableColumn<Student, String> albumNumberCol;

    @FXML private TextField filterField;

    @FXML private TextField albumNumberField;
    @FXML private Label deleteLabel;
    //@FXML private Label status;

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
                    errorLabel.setText("Error: can not load home screen");
                e.printStackTrace();
            } 
    }

    @FXML public void deleteStudent(){
        String num = albumNumberField.getText().trim();
        if (service.deleteStudent(num)){
            deleteLabel.setText("Student " + num +  " deleted.");  
            masterData.removeIf(s -> s.getAlbumNumber().equals(num));
        } else {
            deleteLabel.setText("Student " + num +  " does not exist.");
        }
        albumNumberField.clear();
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(2));
        visiblePause.setOnFinished(ev -> deleteLabel.setText(""));
        visiblePause.play();  
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
