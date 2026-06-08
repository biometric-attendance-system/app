package pl.projekt.Service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.projekt.models.Statistics;
import pl.projekt.models.Student;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StatisticsService;
import pl.projekt.service.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock
    private StudentService studentService;

    @Mock
    private AttendanceService attendanceService;

    private StatisticsService statisticsService;
    private ArrayList<Student> students;

    @BeforeEach
    public void setUp(){
        statisticsService = new StatisticsService(studentService,attendanceService);
        students = new ArrayList<>(List.of(new Student("Jakis", "Koles", "111111")));
    }

    @Test
    public void Attendance100Test() {
        when(studentService.getStudents()).thenReturn(students);
        when(attendanceService.countAttendance("111111")).thenReturn(10);
        when(attendanceService.countPresent("111111")).thenReturn(10);

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(1.0, result.get(0).getMean(), 0.001);
        assertEquals(10, result.get(0).getPresent());
    }

    @Test
    public void Attendance50Test() {
        when(studentService.getStudents()).thenReturn(students);
        when(attendanceService.countAttendance("111111")).thenReturn(10);
        when(attendanceService.countPresent("111111")).thenReturn(5);

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(0.5, result.get(0).getMean(), 0.001);
        assertEquals(5, result.get(0).getPresent());
    }

    @Test
    public void Attendance0Test() {
        when(studentService.getStudents()).thenReturn(students);
        when(attendanceService.countAttendance("111111")).thenReturn(10);
        when(attendanceService.countPresent("111111")).thenReturn(0);

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(0.0, result.get(0).getMean(), 0.001);
        assertEquals(0, result.get(0).getPresent());
    }

    @Test
    public void ListEmptyCalculateStatisticsTest() {
        when(studentService.getStudents()).thenReturn(new ArrayList<>());
        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertNull(result);
    }
}
