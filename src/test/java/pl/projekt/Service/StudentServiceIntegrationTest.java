package pl.projekt.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Attendance;
import pl.projekt.models.Student;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.repository.StudentRepository;
import pl.projekt.service.StudentService;

import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceIntegrationTest {

    @TempDir
    Path tempDir;

    private StudentService studentService;
    private StudentRepository studentRepository;
    private AttendanceRepository attendanceRepository;

    @BeforeEach
    public void setUp() {
        String studentsDb = tempDir.resolve("students.db").toAbsolutePath().toString();
        String attendanceDb = tempDir.resolve("attendance.db").toAbsolutePath().toString();

        studentRepository = new StudentRepository("jdbc:sqlite:" + studentsDb);
        attendanceRepository = new AttendanceRepository("jdbc:sqlite:" + attendanceDb);

        studentService = new StudentService(studentRepository, attendanceRepository);
    }

    @Test
    public void addedStudentIsReadableThroughRealRepository() {
        assertTrue(studentService.addStudent(new Student("Jan", "Kowalski", "123456")));

        ArrayList<Student> all = studentService.getStudents();
        assertEquals(1, all.size());
        assertEquals("123456", all.get(0).getAlbumNumber());
    }

    @Test
    public void deletingStudentAlsoRemovesHisAttendanceFromRealDatabase() {
        studentRepository.addStudent(new Student("Jan", "Kowalski", "123456"));
        attendanceRepository.addAttendance(new Attendance("123456", "2024-01-01", "08:00", "present"));
        attendanceRepository.addAttendance(new Attendance("123456", "2024-01-02", "08:05", "present"));

        assertTrue(studentService.deleteStudent("123456"));

        assertTrue(studentService.getStudents().isEmpty());
        assertEquals(0, attendanceRepository.countAttendance("123456"));
    }
}