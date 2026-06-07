package pl.projekt.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.io.IOException;

import pl.projekt.models.Statistics;
import pl.projekt.service.StatisticsService;
import java.util.ArrayList;

/**
 * @brief Class responsible for
 * displaying and filtering attendance data.
 */
public class StatisticsController {

    @FXML private Label infoLabel;
    @FXML private TableView<Statistics> statsTable;
    @FXML private TableColumn<Statistics, String> albumCol;
    @FXML private TableColumn<Statistics, String> nameCol;
    @FXML private TableColumn<Statistics, Integer> presentCol;
    @FXML private TableColumn<Statistics, Integer> allCol;
    @FXML private TableColumn<Statistics, String> percentCol;
    @FXML private TextField filterField;

    private final StatisticsService statisticsService = new StatisticsService();
    private final ObservableList<Statistics> masterData = FXCollections.observableArrayList();

    /**
     * @brief Function initializes the controller, loads in data,
     * sets up table columns and filtering mechanism.
     */
    @FXML
    public void initialize() {

        albumCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlbumNumber()));
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));
        presentCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPresent()).asObject());
        allCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAll()).asObject());
        
        percentCol.setCellValueFactory(data -> {
            double mean = data.getValue().getMean();
            return new SimpleStringProperty(String.format("%.1f%%", mean * 100));
        });

        setupFilter();
        loadStatisticsData();

    }

    /**
     * @brief Function loads statistics data
     * into the master data list.
     */
    private void loadStatisticsData() {
        masterData.clear();
        ArrayList<Statistics> statsList = statisticsService.calculateStatistics();

        if (statsList == null || statsList.isEmpty()) {
            infoLabel.setText("No statistics found.");
            return;
        }

        masterData.addAll(statsList);
        infoLabel.setText("Statistics loaded successfully.");
    }

    /**
     * @brief Function sets up search filter, allowing to filter
     * through table by student's name or album number.
     */
    private void setupFilter() {
        FilteredList<Statistics> filteredData = new FilteredList<>(masterData, p -> true);
        
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(stat -> {
                if (newVal == null || newVal.isEmpty()) return true;
                
                String filter = newVal.toLowerCase();
                String fullName = (stat.getFirstName() + " " + stat.getLastName()).toLowerCase();
                
                return fullName.contains(filter) ||
                       stat.getAlbumNumber().toLowerCase().contains(filter);
            });
        });
        
        statsTable.setItems(filteredData);
    }

    /**
     * @brief Function navigates back to the home screen.
     *
     * @param event ActionEvent triggered by user interaction.
     */
    @FXML 
    public void goHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomeScreenView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home Screen");
            stage.show();
        } catch (IOException e) {
            infoLabel.setText("Error loading home screen");
        }
    }
}