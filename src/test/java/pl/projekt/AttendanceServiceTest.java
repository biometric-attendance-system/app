package pl.projekt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.projekt.models.Attendance;
import pl.projekt.models.Student;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StudentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {
    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentService studentService;

    private AttendanceService attendanceService;
    private Attendance present;
    private Attendance absent;

    @BeforeEach
    public void setUp(){
        attendanceService = new AttendanceService(attendanceRepository, studentService);
        present = new Attendance("123456","date","time","present");
        absent = new Attendance("333333","date","time","absent");
    }

    @Test
    public void CheckIfPresentAdded(){
        when(attendanceRepository.getStatus("123456", "date")).thenReturn(null);
        attendanceService.addAttendance(present);
        verify(attendanceRepository, times(1)).addAttendance(present);
    }

    @Test
    public void CheckIfAbsentAdded(){
        when(attendanceRepository.getStatus("333333", "date")).thenReturn(null);
        attendanceService.addAttendance(absent);
        verify(attendanceRepository, times(1)).addAttendance(absent);
    }

    @Test
    public void CheckAddingSameAttendanceTwice(){
        when(attendanceRepository.getStatus("123456", "date")).thenReturn("present");
        attendanceService.addAttendance(present);
        verify(attendanceRepository, never()).addAttendance(any());
        verify(attendanceRepository, never()).setStatus(any());
    }

    @Test
    public void CheckIfAttendanceStatusChanged(){
        when(attendanceRepository.getStatus("123456", "date")).thenReturn("absent");
        attendanceService.addAttendance(present);
        verify(attendanceRepository, never()).addAttendance(any());
        verify(attendanceRepository, times(1)).setStatus(any());
    }

    @Test
    public void CheckIfAttendanceAdded(){
        when(attendanceRepository.getStatus("123456", "date")).thenReturn(null);
        attendanceService.addAttendance(present);
        verify(attendanceRepository, times(1)).addAttendance(any());
        verify(attendanceRepository, never()).setStatus(any());
    }

    @Test
    public void CheckIfAbsentFilled(){
        ArrayList<Student> std = new ArrayList<>(List.of(
                new Student("Chris", "Blabla", "123456"),
                new Student("Name", "Surn", "111111")
        ));

        ArrayList<Attendance> atd = new ArrayList<>(List.of(
                new Attendance("111111", "date", "time", "present")
        ));

        when(attendanceRepository.getAttendanceByDate("date")).thenReturn(atd);
        when(studentService.getStudents()).thenReturn(std);
        when(attendanceRepository.getStatus("123456", "date")).thenReturn(null);

        attendanceService.fillAbsentByDate("date", "time");

        verify(attendanceRepository, times(1)).addAttendance(argThat(attendance ->
                attendance.getAlbumNumber().equals("123456") &&
                        attendance.getStatus().equals("absent")
        ));
    }

    @Test
    public void CheckIfAllAbsentFilledNoAttendance() {
        ArrayList<Student> std = new ArrayList<>(List.of(
                new Student("Chris", "Blabla", "123456"),
                new Student("Name", "Surn", "111111")
        ));

        when(attendanceRepository.getAttendanceByDate("date")).thenReturn(null);
        when(studentService.getStudents()).thenReturn(std);
        when(attendanceRepository.getStatus("123456", "date")).thenReturn(null);
        when(attendanceRepository.getStatus("111111", "date")).thenReturn(null);

        attendanceService.fillAbsentByDate("date", "time");

        verify(attendanceRepository, times(2)).addAttendance(any(Attendance.class));
    }

    @Test
    public void CheckIfFillAbsentNoStudentList() {
        when(studentService.getStudents()).thenReturn(null);
        when(attendanceRepository.getAttendanceByDate("date")).thenReturn(new ArrayList<>());
        attendanceService.fillAbsentByDate("date", "time");

        verify(attendanceRepository, never()).addAttendance(any());
    }

    @Test
    public void CheckGetAttendanceByDate() {
        ArrayList<Attendance> expected = new ArrayList<>(List.of(present));
        when(attendanceRepository.getAttendanceByDate("date")).thenReturn(expected);
        ArrayList<Attendance> result = attendanceService.getAttendanceByDate("date");

        assertEquals(expected, result);
    }

    @Test
    public void CheckCountAttendance() {
        when(attendanceRepository.countAttendance("123456")).thenReturn(15);
        int result = attendanceService.countAttendance("123456");

        assertEquals(15, result);
    }

    @Test
    public void CheckCountPresent() {
        when(attendanceRepository.countPresent("123456")).thenReturn(10);
        int result = attendanceService.countPresent("123456");

        assertEquals(10, result);
    }

}
