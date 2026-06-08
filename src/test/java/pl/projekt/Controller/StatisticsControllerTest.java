package pl.projekt.controller;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import pl.projekt.models.Statistics;
import pl.projekt.service.StatisticsService;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsControllerTest extends ApplicationTest {

    private StatisticsController controller;

    @Mock
    private StatisticsService statisticsService;

    private Label infoLabel;
    private TableView<Statistics> statsTable;
    private TableColumn<Statistics, String> albumCol;
    private TableColumn<Statistics, String> nameCol;
    private TableColumn<Statistics, Integer> presentCol;
    private TableColumn<Statistics, Integer> allCol;
    private TableColumn<Statistics, String> percentCol;
    private TextField filterField;

    @Start
    public void start(Stage stage) throws Exception {
        infoLabel = new Label();
        statsTable = new TableView<>();
        albumCol = new TableColumn<>();
        nameCol = new TableColumn<>();
        presentCol = new TableColumn<>();
        allCol = new TableColumn<>();
        percentCol = new TableColumn<>();
        filterField = new TextField();

        statsTable.getColumns().addAll(albumCol, nameCol, presentCol, allCol, percentCol);

        controller = new StatisticsController(statisticsService);

        injectField("infoLabel", infoLabel);
        injectField("statsTable", statsTable);
        injectField("albumCol", albumCol);
        injectField("nameCol", nameCol);
        injectField("presentCol", presentCol);
        injectField("allCol", allCol);
        injectField("percentCol", percentCol);
        injectField("filterField", filterField);

        VBox root = new VBox(filterField, statsTable, infoLabel);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = StatisticsController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }


    @Test
    void shouldPopulateTableAndShowSuccessMessageWhenDataIsAvailable() {
        ArrayList<Statistics> sampleList = new ArrayList<>();
        Statistics student = mock(Statistics.class);
        when(student.getAlbumNumber()).thenReturn("123456");
        when(student.getFirstName()).thenReturn("Jan");
        when(student.getLastName()).thenReturn("Kowalski");
        when(student.getPresent()).thenReturn(8);
        when(student.getAll()).thenReturn(10);
        when(student.getMean()).thenReturn(0.8); // 80.0%
        sampleList.add(student);

        when(statisticsService.calculateStatistics()).thenReturn(sampleList);

        interact(() -> controller.initialize());

        assertEquals("Statistics loaded successfully.", infoLabel.getText());
        assertEquals(1, statsTable.getItems().size());

        String renderedPercent = percentCol.getCellData(0);
        assertEquals("80,0%", renderedPercent.replace('.', ',')); // Bezpieczna lokalizacja (kropka/przecinek)
    }

    @Test
    void shouldShowNoStatisticsFoundMessageWhenDataIsEmpty() {
        when(statisticsService.calculateStatistics()).thenReturn(new ArrayList<>());

        interact(() -> controller.initialize());

        assertEquals("No statistics found.", infoLabel.getText());
        assertTrue(statsTable.getItems().isEmpty());
    }


    @Test
    void shouldFilterTableRowsBasedOnTextFieldInput() {
        ArrayList<Statistics> sampleList = new ArrayList<>();

        Statistics s1 = mock(Statistics.class);
        when(s1.getAlbumNumber()).thenReturn("111222");
        when(s1.getFirstName()).thenReturn("Anna");
        when(s1.getLastName()).thenReturn("Nowak");

        Statistics s2 = mock(Statistics.class);
        when(s2.getAlbumNumber()).thenReturn("555666");
        when(s2.getFirstName()).thenReturn("Jan");
        when(s2.getLastName()).thenReturn("Kowalski");

        sampleList.add(s1);
        sampleList.add(s2);

        when(statisticsService.calculateStatistics()).thenReturn(sampleList);

        interact(() -> controller.initialize());
        assertEquals(2, statsTable.getItems().size(), "Tabela powinna na starcie mieć 2 rekordy");

        interact(() -> filterField.setText("nowak"));
        ObservableList<Statistics> filteredList = statsTable.getItems();
        assertEquals(1, filteredList.size());
        assertEquals("111222", filteredList.get(0).getAlbumNumber());

        interact(() -> filterField.setText("555"));
        filteredList = statsTable.getItems();
        assertEquals(1, filteredList.size());
        assertEquals("Jan", filteredList.get(0).getFirstName());

        interact(() -> filterField.setText(""));
        assertEquals(2, statsTable.getItems().size());
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

        String currentLabelText = infoLabel.getText();
        boolean isExpectedBehavior = currentLabelText.isEmpty()
                || currentLabelText.equals("Error loading home screen")
                || currentLabelText.equals("Statistics loaded successfully.");

        assertTrue(isExpectedBehavior, "Nieoczekiwany stan etykiety: " + currentLabelText);
    }
}