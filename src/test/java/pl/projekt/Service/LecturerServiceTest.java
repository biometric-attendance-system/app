package pl.projekt.Service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;
import pl.projekt.service.LecturerService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LecturerServiceTest {
    @Mock
    private LecturerRepository lecturerRepository;

    LecturerService lecturerService;

    @BeforeEach
    public void setUp(){
        lecturerService = new LecturerService(lecturerRepository);
    }

    @Test
    public void AddLecturerTest() {
        String rawPassword = "MyPassword1!";
        Lecturer lecturer = new Lecturer("ID", "Name", "Surname", rawPassword);
        when(lecturerRepository.addLecturer(any(Lecturer.class))).thenReturn(true);

        assertTrue(lecturerService.addLecturer(lecturer));
        verify(lecturerRepository, times(1)).addLecturer(lecturer);
        assertNotEquals(rawPassword, lecturer.getPasswordHash());
    }
}
