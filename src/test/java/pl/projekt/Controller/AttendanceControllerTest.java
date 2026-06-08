package pl.projekt.controller;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.models.Attendance;
import pl.projekt.models.Student;
import pl.projekt.service.AttendanceService;
import pl.projekt.service.StudentService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceControllerTest extends ApplicationTest {

    private AttendanceController controller;

    @Mock private AttendanceService attendanceService;
    @Mock private StudentService studentService;

    private Label infoLabel;
    private TableView<AttendanceController.AttendanceRecord> attendanceTable;
    private TableColumn<AttendanceController.AttendanceRecord, String> albumCol;
    private TableColumn<AttendanceController.AttendanceRecord, String> nameCol;
    private TableColumn<AttendanceController.AttendanceRecord, String> statusCol;
    private TableColumn<AttendanceController.AttendanceRecord, String> timeCol;
    private DatePicker datePicker;
    private TextField filterField;

    @Start
    public void start(Stage stage) throws Exception {
        infoLabel = new Label();
        datePicker = new DatePicker();
        filterField = new TextField();

        attendanceTable = new TableView<>();
        albumCol = new TableColumn<>("Album");
        nameCol = new TableColumn<>("Name");
        statusCol = new TableColumn<>("Status");
        timeCol = new TableColumn<>("Time");

        attendanceTable.getColumns().addAll(albumCol, nameCol, statusCol, timeCol);

        when(studentService.getStudents()).thenReturn(new ArrayList<>());
        when(attendanceService.getAttendanceByDate(anyString())).thenReturn(new ArrayList<>());

        controller = new AttendanceController(attendanceService, studentService);

        injectField("infoLabel", infoLabel);
        injectField("attendanceTable", attendanceTable);
        injectField("albumCol", albumCol);
        injectField("nameCol", nameCol);
        injectField("statusCol", statusCol);
        injectField("timeCol", timeCol);
        injectField("datePicker", datePicker);
        injectField("filterField", filterField);

        controller.initialize();

        VBox root = new VBox(infoLabel, datePicker, filterField, attendanceTable);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = AttendanceController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }


    @Test
    void shouldShowNoDataMessageWhenServicesReturnNull() {
        when(studentService.getStudents()).thenReturn(null);
        when(attendanceService.getAttendanceByDate(anyString())).thenReturn(null);

        interact(() -> datePicker.setValue(LocalDate.now().plusDays(1)));

        assertEquals("No data found for selected date.", infoLabel.getText());
    }

    @Test
    void shouldCorrectlyMapStudentsAndAttendanceToTable() {
        LocalDate testDate = LocalDate.now().plusDays(2);

        Student mockStudent = mock(Student.class);
        when(mockStudent.getAlbumNumber()).thenReturn("123456");
        when(mockStudent.getFirstName()).thenReturn("Jan");
        when(mockStudent.getLastName()).thenReturn("Kowalski");

        ArrayList<Student> studentsList = new ArrayList<>();
        studentsList.add(mockStudent);

        Attendance mockAttendance = mock(Attendance.class);
        when(mockAttendance.getAlbumNumber()).thenReturn("123456");
        when(mockAttendance.getStatus()).thenReturn("Present");
        when(mockAttendance.getTime()).thenReturn("08:15:00");

        ArrayList<Attendance> attendanceList = new ArrayList<>();
        attendanceList.add(mockAttendance);

        when(studentService.getStudents()).thenReturn(studentsList);
        when(attendanceService.getAttendanceByDate(testDate.toString())).thenReturn(attendanceList);

        interact(() -> datePicker.setValue(testDate));

        ObservableList<AttendanceController.AttendanceRecord> items = attendanceTable.getItems();
        assertEquals(1, items.size());

        AttendanceController.AttendanceRecord record = items.get(0);
        assertEquals("123456", record.getAlbum());
        assertEquals("Jan Kowalski", record.getName());
        assertEquals("Present", record.getStatus());
        assertEquals("08:15:00", record.getTime());
        assertEquals("Data loaded for " + testDate, infoLabel.getText());
    }

    @Test
    void shouldAssignNoRecordStatusWhenAttendanceIsMissing() {
        LocalDate testDate = LocalDate.now().plusDays(3);

        Student mockStudent = mock(Student.class);
        when(mockStudent.getAlbumNumber()).thenReturn("999999");
        when(mockStudent.getFirstName()).thenReturn("Adam");
        when(mockStudent.getLastName()).thenReturn("Nowak");

        ArrayList<Student> studentsList = new ArrayList<>();
        studentsList.add(mockStudent);

        when(studentService.getStudents()).thenReturn(studentsList);
        when(attendanceService.getAttendanceByDate(testDate.toString())).thenReturn(new ArrayList<>());

        interact(() -> datePicker.setValue(testDate));

        AttendanceController.AttendanceRecord record = attendanceTable.getItems().get(0);
        assertEquals("no record", record.getStatus());
        assertEquals("no record", record.getTime());
    }

    @Test
    void shouldFilterTableRowsBasedOnFilterFieldInput() {
        LocalDate testDate = LocalDate.now().plusDays(4);

        Student s1 = mock(Student.class);
        when(s1.getAlbumNumber()).thenReturn("111111");
        when(s1.getFirstName()).thenReturn("Jan");
        when(s1.getLastName()).thenReturn("Kowalski");

        Student s2 = mock(Student.class);
        when(s2.getAlbumNumber()).thenReturn("222222");
        when(s2.getFirstName()).thenReturn("Anna");
        when(s2.getLastName()).thenReturn("Nowak");

        ArrayList<Student> studentsList = new ArrayList<>();
        studentsList.add(s1);
        studentsList.add(s2);

        when(studentService.getStudents()).thenReturn(studentsList);
        when(attendanceService.getAttendanceByDate(anyString())).thenReturn(new ArrayList<>());

        interact(() -> datePicker.setValue(testDate));
        assertEquals(2, attendanceTable.getItems().size());

        interact(() -> filterField.setText("Kowalski"));

        assertEquals(1, attendanceTable.getItems().size());
        assertEquals("Jan Kowalski", attendanceTable.getItems().get(0).getName());

        interact(() -> filterField.setText(""));
        assertEquals(2, attendanceTable.getItems().size());
    }
}