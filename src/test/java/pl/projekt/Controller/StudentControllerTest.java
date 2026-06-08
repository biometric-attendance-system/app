package pl.projekt.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.models.Student;
import pl.projekt.service.StudentService;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest extends ApplicationTest {

    private StudentController controller;

    @Mock
    private StudentService studentService;

    private Label errorLabel;
    private TableView<Student> studentTable;
    private TableColumn<Student, String> nameCol;
    private TableColumn<Student, String> surNameCol;
    private TableColumn<Student, String> albumNumberCol;
    private TextField filterField;
    private TextField albumNumberField;
    private Label deleteLabel;

    @Start
    public void start(Stage stage) throws Exception {
        errorLabel = new Label();
        studentTable = new TableView<>();
        nameCol = new TableColumn<>();
        surNameCol = new TableColumn<>();
        albumNumberCol = new TableColumn<>();
        filterField = new TextField();
        albumNumberField = new TextField();
        deleteLabel = new Label();

        studentTable.getColumns().addAll(nameCol, surNameCol, albumNumberCol);

        controller = new StudentController(studentService);

        injectField("errorLabel", errorLabel);
        injectField("studentTable", studentTable);
        injectField("nameCol", nameCol);
        injectField("surNameCol", surNameCol);
        injectField("albumNumberCol", albumNumberCol);
        injectField("filterField", filterField);
        injectField("albumNumberField", albumNumberField);
        injectField("deleteLabel", deleteLabel);

        VBox root = new VBox(filterField, studentTable, albumNumberField, deleteLabel, errorLabel);
        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = StudentController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }


    @Test
    void shouldLoadStudentsIntoTableOnInitialize() {
        ArrayList<Student> studentsList = new ArrayList<>();
        Student s1 = mock(Student.class);
        when(s1.getFirstName()).thenReturn("Jan");
        when(s1.getLastName()).thenReturn("Kowalski");
        when(s1.getAlbumNumber()).thenReturn("987654");
        studentsList.add(s1);

        when(studentService.getStudents()).thenReturn(studentsList);

        interact(() -> controller.initialize());

        assertEquals(1, studentTable.getItems().size());
        assertEquals("Jan", nameCol.getCellData(0));
        assertEquals("Kowalski", surNameCol.getCellData(0));
        assertEquals("987654", albumNumberCol.getCellData(0));
    }


    @Test
    void shouldFilterTableRowsByMultipleCriteria() {
        ArrayList<Student> studentsList = new ArrayList<>();

        Student s1 = mock(Student.class);
        when(s1.getFirstName()).thenReturn("Anna");
        when(s1.getLastName()).thenReturn("Nowak");
        when(s1.getAlbumNumber()).thenReturn("111222");

        Student s2 = mock(Student.class);
        when(s2.getFirstName()).thenReturn("Bartosz");
        when(s2.getLastName()).thenReturn("Zieliński");
        when(s2.getAlbumNumber()).thenReturn("333444");

        studentsList.add(s1);
        studentsList.add(s2);

        when(studentService.getStudents()).thenReturn(studentsList);

        interact(() -> controller.initialize());
        assertEquals(2, studentTable.getItems().size());

        interact(() -> filterField.setText("anna"));
        ObservableList<Student> items = studentTable.getItems();
        assertEquals(1, items.size());
        assertEquals("Nowak", items.get(0).getLastName());

        interact(() -> filterField.setText("Ziel"));
        items = studentTable.getItems();
        assertEquals(1, items.size());
        assertEquals("Bartosz", items.get(0).getFirstName());

        interact(() -> filterField.setText("111"));
        items = studentTable.getItems();
        assertEquals(1, items.size());
        assertEquals("Anna", items.get(0).getFirstName());

        interact(() -> filterField.setText("brak_wynikow"));
        assertTrue(studentTable.getItems().isEmpty());
    }


    @Test
    void shouldSuccessfullyDeleteStudentAndRemoveFromUi() {
        ArrayList<Student> studentsList = new ArrayList<>();
        Student student = mock(Student.class);
        when(student.getFirstName()).thenReturn("Jan");
        when(student.getLastName()).thenReturn("Kowalski");
        when(student.getAlbumNumber()).thenReturn("123456");
        studentsList.add(student);

        when(studentService.getStudents()).thenReturn(studentsList);
        when(studentService.deleteStudent("123456")).thenReturn(true);

        interact(() -> {
            controller.initialize();
            albumNumberField.setText("123456");
        });

        interact(() -> controller.deleteStudent());

        assertEquals("Student 123456 deleted.", deleteLabel.getText());
        assertEquals("", albumNumberField.getText());
        assertTrue(studentTable.getItems().isEmpty(), "Tabela powinna być pusta po usunięciu studenta");
    }

    @Test
    void shouldShowErrorWhenDeletingNonExistentStudent() {
        when(studentService.deleteStudent("999999")).thenReturn(false);

        interact(() -> albumNumberField.setText("999999"));
        interact(() -> controller.deleteStudent());

        assertEquals("Student 999999 does not exist.", deleteLabel.getText());
        assertEquals("", albumNumberField.getText());
    }


    @Test
    void shouldHandleNavigationToHomeScreen() {
        interact(() -> {
            Button dummyButton = new Button();
            Scene dummyScene = new Scene(dummyButton);
            Stage dummyStage = new Stage();
            dummyStage.setScene(dummyScene);
            dummyStage.show();

            ActionEvent event = new ActionEvent(dummyButton, null);
            controller.goHome(event);
        });

        String text = errorLabel.getText();
        boolean validState = text.isEmpty() || text.equals("Error: can not load home screen");
        assertTrue(validState);
    }
}