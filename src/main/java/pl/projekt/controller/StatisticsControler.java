package pl.projekt.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

public class StatisticsController {

    private TableView<String> table;

    private TableColumn<String, String> nameCol;

    private TableColumn<String, String> percentCol;

    private StatisticsService statistics = new StatisticsService();

    public void initialize() {

        

        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        percentCol.setCellValueFactory(data -> new SimpleStringProperty(statistics.calculateStatistics()));

    }
}