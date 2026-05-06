package pl.projekt.controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import pl.projekt.models.Statistics;
import pl.projekt.service.StatisticsService;


public class StatisticsController {

    private TableView<Statistics> table;

    private TableColumn<Statistics, String> nameCol;
    private TableColumn<Statistics, String> albumNumberCol;
    private TableColumn<Statistics, String> percentCol;
    private TableColumn<Statistics, String> presentCol;
    private TableColumn<Statistics, String> allCol;
    
    private final StatisticsService statistics = new StatisticsService();

    public void initialize() {

        ArrayList<Statistics> statisticsData = statistics.calculateStatistics();

        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));
        albumNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlbumNumber()));
        percentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMean().toString()));
        presentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPresent().toString()));
        allCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAll().toString()));

        table.getItems().setAll(statisticsData);

    }
}