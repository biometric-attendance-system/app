package pl.projekt.Service;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.repository.StudentRepository;
import pl.projekt.service.StudentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        studentService = new StudentService(studentRepository, attendanceRepository);
    }

    @Test
    public void StudentExistsDeletionTest() {
        when(studentRepository.deleteStudent("123456")).thenReturn(true);
        assertTrue(studentService.deleteStudent("123456"));
        verify(attendanceRepository, times(1)).deleteAttendances("123456");
        verify(studentRepository, times(1)).deleteStudent("123456");
    }

    @Test
    public void StudentNotExistsDeletionTest() {
        when(studentRepository.deleteStudent("999999")).thenReturn(false);
        assertFalse(studentService.deleteStudent("999999"));
        verify(attendanceRepository, times(1)).deleteAttendances("999999");
        verify(studentRepository, times(1)).deleteStudent("999999");
    }
}
