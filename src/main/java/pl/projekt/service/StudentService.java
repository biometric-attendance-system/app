package pl.projekt.service;
import java.util.ArrayList;

import pl.projekt.models.Student;
import pl.projekt.repository.StudentRepository;

public class StudentService{

    private final StudentRepository repository;

    public StudentService() {
        this.repository = new StudentRepository();
    }

    public StudentService(StudentRepository repository){
        this.repository = repository;
    }

    public Boolean deleteStudent(String albumNumber){
        return repository.deleteStudent(albumNumber);
    }

    public Boolean addStudent(Student student){
        return repository.addStudent(student);
    }

    public ArrayList<Student> getStudents(){
        return repository.getStudents();
    }

    public Student getStudent(String albumNumber){
        return repository.getStudent(albumNumber);
    }

    public Boolean setStudent(Student student){
        return repository.setStudent(student);
    }
}