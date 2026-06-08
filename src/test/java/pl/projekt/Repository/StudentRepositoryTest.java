package pl.projekt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Student;

import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StudentRepositoryTest {
    @TempDir
    Path tempDir;

    private StudentRepository repository;
    private Student student;

    @BeforeEach
    public void setUp() {
        String tempPath = tempDir.resolve("tempStud.db").toAbsolutePath().toString();
        repository = new StudentRepository("jdbc:sqlite:" + tempPath);
        student = new Student("Chris", "Blabla", "111111");
    }

    @Test
    public void AddingStudentTest() {
        boolean result = repository.addStudent(student);

        assertTrue(result);

        ArrayList<Student> stud = repository.getStudents();
        assertEquals(1, stud.size());
        assertEquals("111111", stud.get(0).getAlbumNumber());
        assertEquals("Chris", stud.get(0).getFirstName());
    }

    @Test
    public void AddingDuplicateTest() {
        repository.addStudent(student);
        boolean result2 = repository.addStudent(student);

        assertFalse(result2);

        ArrayList<Student> stud = repository.getStudents();
        assertEquals(1, stud.size());
        assertEquals("Chris", stud.get(0).getFirstName());
    }

    @Test
    public void DeleteStudentWhenExistsTest() {
        repository.addStudent(student);
        boolean deleteResult = repository.deleteStudent("111111");

        assertTrue(deleteResult);
        ArrayList<Student> stud = repository.getStudents();
        assertTrue(stud.isEmpty());
    }

    @Test
    public void DeleteStudentWhenNotExistsTest() {
        boolean deleteResult = repository.deleteStudent("000000");

        assertFalse(deleteResult);
    }
}