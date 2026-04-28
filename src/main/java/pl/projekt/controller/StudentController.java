package pl.projekt.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pl.projekt.service.StudentService;
import pl.projekt.models.Student;

public class StudentController{

    private TextField nameField;

    private TextField surnameField;

    private TextField albumNumberField;

    private StudentService service = new StudentService();

    public void addStudent(){
        Student student = new Student(nameField.getText(), surnameField.getText(), albumNumberField.getText());
        if(service.addStudent(student)) 
            System.out.println("Successfully added student to base!");
        else
            System.out.println("Make sure student is not already in base.");
    }




}