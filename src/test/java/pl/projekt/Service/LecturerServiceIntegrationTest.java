package pl.projekt.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;
import pl.projekt.service.AuthenticationService;
import pl.projekt.service.LecturerService;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LecturerServiceIntegrationTest {

    @TempDir
    Path tempDir;

    private LecturerService lecturerService;
    private LecturerRepository lecturerRepository;

    @BeforeEach
    public void setUp() {
        String dbPath = tempDir.resolve("lecturer.db").toAbsolutePath().toString();
        lecturerRepository = new LecturerRepository("jdbc:sqlite:" + dbPath);
        lecturerService = new LecturerService(lecturerRepository);
    }

    @Test
    public void HashingPasswordTest() {
        String rawPassword = "password";
        Lecturer lecturer = new Lecturer("Jan", "Jakis", "111111", rawPassword);

        assertTrue(lecturerService.addLecturer(lecturer));

        Lecturer fromDb = lecturerRepository.getLecturer();
        assertNotNull(fromDb);
        assertEquals("111111", fromDb.getID());
        assertNotEquals(rawPassword, fromDb.getPasswordHash());
    }

    @Test
    public void CorrectPasswordTest() {
        String rawPassword = "password";
        Lecturer lecturer = new Lecturer("Jan", "Jakis", "111111", rawPassword);
        lecturerService.addLecturer(lecturer);

        Lecturer databaseLecturer = lecturerRepository.getLecturer();

        assertTrue(AuthenticationService.checkPassword(rawPassword, databaseLecturer.getPasswordHash()));
    }

    @Test
    public void addingLecturerWithDuplicateIdShouldFail() {
        Lecturer lecturer1 = new Lecturer("Adam", "Kowalski", "999999", "pass1");
        Lecturer lecturer2 = new Lecturer("Ewa", "Nowak", "999999", "pass2");

        assertTrue(lecturerService.addLecturer(lecturer1));
        assertFalse(lecturerService.addLecturer(lecturer2));
    }

    @Test
    public void IncorrectPasswordTest() {
        String rawPassword = "mySecretPassword";
        Lecturer lecturer = new Lecturer("Piotr", "Zły", "555555", rawPassword);
        lecturerService.addLecturer(lecturer);

        Lecturer databaseLecturer = lecturerRepository.getLecturer();

        assertFalse(AuthenticationService.checkPassword("wrongPassword", databaseLecturer.getPasswordHash()));
    }
}