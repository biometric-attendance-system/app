package pl.projekt.service;
import java.util.ArrayList;

import pl.projekt.models.Student;
import pl.projekt.repository.StudentRepository;
import pl.projekt.repository.AttendanceRepository;

/**
 * @brief Class Responsible for Student logic, directly
 * communicates with StudentRepository.
 */
public class StudentService{

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * @brief Constructor initializes repositories.
     */
    public StudentService() {
        this.studentRepository = new StudentRepository();
        this.attendanceRepository = new AttendanceRepository();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public StudentService(StudentRepository studentRepository, AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    /**
     * @brief Clears whole database.
     */
    public void clear(){
        studentRepository.clear();
    }

    /**
     * @brief Function deletes student from the database.
     *
     * @param albumNumber Student's album number.
     * @return True if deleted false otherwise.
     */
    public boolean deleteStudent(String albumNumber){
        attendanceRepository.deleteAttendances(albumNumber);
        return studentRepository.deleteStudent(albumNumber);
    }

    /**
     * @brief Function adds student to the database.
     *
     * @param student Given student.
     * @return True if added false otherwise.
     */
    public boolean addStudent(Student student){
        return studentRepository.addStudent(student);
    }

    /**
     * Function returns all students form the database.
     *
     * @return ArrayList of students.
     */
    public ArrayList<Student> getStudents(){
        return studentRepository.getStudents();
    }

}