package pl.projekt.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Attendance;
import pl.projekt.models.Statistics;
import pl.projekt.models.Student;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.repository.StudentRepository;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StatisticsService;
import pl.projekt.service.StudentService;

import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsServiceIntegrationTest {

    @TempDir
    Path tempDir;

    private StatisticsService statisticsService;
    private StudentRepository studentRepository;
    private AttendanceRepository attendanceRepository;

    @BeforeEach
    public void setUp() {
        String studentsDb = tempDir.resolve("students.db").toAbsolutePath().toString();
        String attendanceDb = tempDir.resolve("attendance.db").toAbsolutePath().toString();

        studentRepository = new StudentRepository("jdbc:sqlite:" + studentsDb);
        attendanceRepository = new AttendanceRepository("jdbc:sqlite:" + attendanceDb);

        StudentService studentService = new StudentService(studentRepository, attendanceRepository);
        AttendanceService attendanceService = new AttendanceService(attendanceRepository, studentService);
        statisticsService = new StatisticsService(studentService, attendanceService);

        studentRepository.addStudent(new Student("Jan", "Kowalski", "123456"));
        attendanceRepository.addAttendance(new Attendance("123456", "date1", "08:00", "present"));
        attendanceRepository.addAttendance(new Attendance("123456", "date2", "08:00", "absent"));
        attendanceRepository.addAttendance(new Attendance("123456", "date3", "08:00", "present"));
    }

    @Test
    public void calculatesRealStatisticsAcrossWholeStack() {
        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertNotNull(result);
        assertEquals(1, result.size());

        Statistics stats = result.get(0);
        assertEquals("123456", stats.getAlbumNumber());
        assertEquals(2, stats.getPresent());
        assertEquals(3, stats.getAll());
        assertEquals(2.0 / 3.0, stats.getMean(), 0.001);
    }

    @Test
    public void calculatesStatisticsForStudentWithZeroPresence() {
        studentRepository.addStudent(new Student("Anna", "Nowak", "654321"));
        attendanceRepository.addAttendance(new Attendance("654321", "date1", "08:00", "absent"));
        attendanceRepository.addAttendance(new Attendance("654321", "date2", "08:00", "absent"));

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(2, result.size());

        Statistics annaStats = result.stream()
                .filter(s -> s.getAlbumNumber().equals("654321"))
                .findFirst()
                .orElseThrow();

        assertEquals(0, annaStats.getPresent());
        assertEquals(2, annaStats.getAll());
        assertEquals(0.0, annaStats.getMean(), 0.001);
    }

    @Test
    public void calculatesStatisticsForStudentWithNoAttendanceRecords() {
        studentRepository.addStudent(new Student("Piotr", "Zalewski", "999999"));

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(2, result.size());

        Statistics piotrStats = result.stream()
                .filter(s -> s.getAlbumNumber().equals("999999"))
                .findFirst()
                .orElseThrow();

        assertEquals(0, piotrStats.getPresent());
        assertEquals(0, piotrStats.getAll());
        assertEquals(0.0, piotrStats.getMean(), 0.001);
    }

    @Test
    public void calculatesStatisticsForMultipleStudents() {
        studentRepository.addStudent(new Student("Ewa", "Kania", "111222"));
        attendanceRepository.addAttendance(new Attendance("111222", "date4", "08:00", "present"));
        attendanceRepository.addAttendance(new Attendance("111222", "date5", "08:00", "present"));

        ArrayList<Statistics> result = statisticsService.calculateStatistics();

        assertEquals(2, result.size());

        Statistics kowalskiStats = result.stream()
                .filter(s -> s.getAlbumNumber().equals("123456"))
                .findFirst()
                .orElseThrow();

        Statistics kaniaStats = result.stream()
                .filter(s -> s.getAlbumNumber().equals("111222"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, kowalskiStats.getPresent());
        assertEquals(3, kowalskiStats.getAll());

        assertEquals(2, kaniaStats.getPresent());
        assertEquals(2, kaniaStats.getAll());
        assertEquals(1.0, kaniaStats.getMean(), 0.001);
    }
}