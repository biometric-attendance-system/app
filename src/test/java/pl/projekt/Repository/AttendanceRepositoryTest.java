package pl.projekt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.projekt.models.Attendance;

import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceRepositoryTest {

    @TempDir
    Path tempDir;

    private AttendanceRepository repository;
    private Attendance att;

    @BeforeEach
    public void setUp() {
        String tempPath = tempDir.resolve("tempAtt.db").toAbsolutePath().toString();
        repository = new AttendanceRepository("jdbc:sqlite:" + tempPath);
        att = new Attendance("123456", "date", "time", "present");
    }

    @Test
    public void GetStatusTest() {
        repository.addAttendance(att);
        String status = repository.getStatus("123456", "date");

        assertEquals("present", status);
    }

    @Test
    public void DuplicateAttendanceTest() {
        repository.addAttendance(att);
        Attendance att2 = new Attendance("123456", "date", "time", "absent");
        repository.addAttendance(att2);

        String currentStatus = repository.getStatus("123456", "date");
        assertEquals("present", currentStatus);
        assertEquals(1, repository.countAttendance("123456"));
    }

    @Test
    public void UpdatingStatusTest() {
        repository.addAttendance(att);

        Attendance att2 = new Attendance("123456", "date", "time", "absent");
        repository.setStatus(att2);

        String status = repository.getStatus("123456", "date");
        assertEquals("absent", status);
    }

    @Test
    public void CountingAttendancesTest() {
        repository.addAttendance(new Attendance("555555", "date1", "time", "present"));
        repository.addAttendance(new Attendance("555555", "date2", "time", "present"));
        repository.addAttendance(new Attendance("555555", "date3", "time", "absent"));

        assertEquals(3, repository.countAttendance("555555"));
        assertEquals(2, repository.countPresent("555555"));
    }

    @Test
    public void GetAttendanceByDateTest() {
        repository.addAttendance(new Attendance("111111", "date1", "time", "present"));
        repository.addAttendance(new Attendance("222222", "date1", "time", "absent"));
        repository.addAttendance(new Attendance("333333", "date2", "time", "present"));

        ArrayList<Attendance> result = repository.getAttendanceByDate("date1");

        assertNotNull(result);
        assertEquals(2, result.size());

        String s1 = result.get(0).getAlbumNumber();
        String s2 = result.get(1).getAlbumNumber();

        assertTrue((s1.equals("111111") && s2.equals("222222")) ||
                (s1.equals("222222") && s2.equals("111111")));
    }

    @Test
    public void DeletingStudentAttendancesTest() {
        repository.addAttendance(new Attendance("111111", "date1", "time", "present"));
        repository.addAttendance(new Attendance("111111", "date2", "time", "absent"));

        repository.deleteAttendances("111111");

        assertEquals(0, repository.countAttendance("111111"));
    }
}