package pl.projekt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Lecturer;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LecturerRepositoryTest {
    @TempDir
    Path tempDir;

    private LecturerRepository repository;
    private Lecturer lecturer;

    @BeforeEach
    public void setUp() {
        String tempPath = tempDir.resolve("tempLect.db").toAbsolutePath().toString();
        repository = new LecturerRepository("jdbc:sqlite:" + tempPath);
        lecturer = new Lecturer("Chris", "Blabla", "111111", "hashedPassword");
    }

    @Test
    public void IsDatabaseEmptyTest() {
        assertTrue(repository.isEmpty());
    }

    @Test
    public void GetLecturerReturnsNullTest() {
        assertNull(repository.getLecturer());
    }

    @Test
    public void AddingLecturerCorrectlyTest() {
        boolean result = repository.addLecturer(lecturer);

        assertTrue(result);
        assertFalse(repository.isEmpty());

        Lecturer savedLecturer = repository.getLecturer();
        assertNotNull(savedLecturer);
        assertEquals("111111", savedLecturer.getID());
        assertEquals("Chris", savedLecturer.getFirstName());
        assertEquals("Blabla", savedLecturer.getLastName());
        assertEquals("hashedPassword", savedLecturer.getPasswordHash());
    }

    @Test
    public void AddingLecturerDuplicateTest() {
        repository.addLecturer(lecturer);
        boolean result2 = repository.addLecturer(lecturer);

        assertFalse(result2);

        Lecturer savedLecturer = repository.getLecturer();
        assertEquals("Chris", savedLecturer.getFirstName());
    }
}