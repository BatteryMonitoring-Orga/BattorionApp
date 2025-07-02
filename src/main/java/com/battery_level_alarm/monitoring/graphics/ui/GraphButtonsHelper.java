package com.battery_level_alarm.monitoring.graphics.ui;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.graphics.charts.AlternativeLineChart.createAlternativeLevelGraph;
import static com.battery_level_alarm.monitoring.graphics.records.GraphicRecordsManager.*;
import static com.battery_level_alarm.monitoring.graphics.records.GraphicRecordsManager.loadDataFromJSON;
import static com.battery_level_alarm.monitoring.graphics.base.GraphsDefinitions.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphButtonsHelper {
    public static ToolBar createButtonsPanel(BorderPane borderPane) {
        Button zoomInButton = createButton("Zoom In", true, _ -> {
            if(isZoomEnabled()) {
                activeChart.setScaleX(activeChart.getScaleX() * 1.1);
                activeChart.setScaleY(activeChart.getScaleY() * 1.1);
            }
        });
        Button zoomOutButton = createButton("Zoom Out", true, _ -> {
            if(isZoomEnabled()) {
                activeChart.setScaleX(activeChart.getScaleX() / 1.1);
                activeChart.setScaleY(activeChart.getScaleY() / 1.1);
            }
        });
        Button resetZoomButton = createButton("Reset Zoom", false, _ -> {
            activeChart.setScaleX(1);
            activeChart.setScaleY(1);
        });
        Button saveCSVButton = createButton("Save as CSV", false, _ -> saveDataAsCSV());
        Button saveJSONButton = createButton("Save as JSON", false, _ -> saveDataAsJSON());
        
        ToolBar toolBar = new ToolBar(
                saveCSVButton, saveJSONButton,
                zoomInButton, zoomOutButton, resetZoomButton);
        borderPane.setTop(toolBar);
        return toolBar;
    }

    public static BorderPane createLoadGraphPanel(Stage stage) {
        BorderPane borderPane = new BorderPane();
        Button loadCSVButton = createButton("Load CSV", false, _ -> loadDataFromCSV(stage));
        Button loadJSONButton = createButton("Load JSON", false, _ -> loadDataFromJSON(stage));
        alterLineChart = createAlternativeLevelGraph(alterSeries, 1);
        
        ToolBar toolBar = new ToolBar(loadCSVButton, loadJSONButton);
        borderPane.setTop(toolBar);
        borderPane.setCenter(mainGraphScrolls[1]);
        return borderPane;
    }
    
    private static Button createButton(String title, boolean isZoomSet, EventHandler<ActionEvent> actionEvent) {
        Button button = new Button(title);
        button.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-font-style: italic; -fx-text-fill: #D35400;");
        if(isZoomSet && !isZoomEnabled()) {
            button.setDisable(true);
        }
        
        button.setOnAction(actionEvent);
        button.setOnMouseEntered(_ -> button.setCursor(Cursor.HAND));
        button.setOnMouseExited(_ -> button.setCursor(Cursor.DEFAULT));
        return button;
    }
}