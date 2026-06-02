package pl.projekt.service;
import java.util.ArrayList;

import pl.projekt.models.Student;
import pl.projekt.repository.StudentRepository;
import pl.projekt.repository.AttendanceRepository;

public class StudentService{

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentService() {
        this.studentRepository = new StudentRepository();
        this.attendanceRepository = new AttendanceRepository();
    }

    public Boolean deleteStudent(String albumNumber){
        attendanceRepository.deleteAttendances(albumNumber);
        return studentRepository.deleteStudent(albumNumber);
    }

    public Boolean addStudent(Student student){
        return studentRepository.addStudent(student);
    }

    public ArrayList<Student> getStudents(){
        return studentRepository.getStudents();
    }

    public Student getStudent(String albumNumber){
        return studentRepository.getStudent(albumNumber);
    }

    public Boolean setStudent(Student student){
        return studentRepository.setStudent(student);
    }
}