package com.battery_level_alarm.monitoring.graphics;
import static com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.graphics.GraphicRecordsManager.*;
import static com.battery_level_alarm.monitoring.graphics.GraphicRecordsManager.loadDataFromJSON;
import static com.battery_level_alarm.monitoring.graphics.GraphsDefinitions.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphButtonsHelper {
    static ToolBar createButtonsPanel(BorderPane borderPane) {
        Button zoomInButton = createButton("Zoom In", _ -> {
            lineChart.setScaleX(lineChart.getScaleX() * 1.1);
            lineChart.setScaleY(lineChart.getScaleY() * 1.1);
        });
        Button zoomOutButton = createButton("Zoom Out", _ -> {
            lineChart.setScaleX(lineChart.getScaleX() / 1.1);
            lineChart.setScaleY(lineChart.getScaleY() / 1.1);
        });
        Button resetZoomButton = createButton("Reset Zoom", _ -> {
            lineChart.setScaleX(1);
            lineChart.setScaleY(1);
        });
        Button saveCSVButton = createButton("Save as CSV", _ -> saveDataAsCSV());
        Button saveJSONButton = createButton("Save as JSON", _ -> saveDataAsJSON());

        ToolBar toolBar = new ToolBar(
                saveCSVButton, saveJSONButton,
                zoomInButton, zoomOutButton, resetZoomButton);
        borderPane.setTop(toolBar);
        return toolBar;
    }

    static BorderPane createLoadGraphPanel(Stage stage) {
        BorderPane borderPane = new BorderPane();
        Button loadCSVButton = createButton("Load CSV", _ -> loadDataFromCSV(stage));
        Button loadJSONButton = createButton("Load JSON", _ -> loadDataFromJSON(stage));
        alterLineChart = createLevelGraph(alterSeries, 1);

        ToolBar toolBar = new ToolBar(loadCSVButton, loadJSONButton);
        borderPane.setTop(toolBar);
        borderPane.setCenter(mainGraphScrolls[1]);
        return borderPane;
    }

    private static Button createButton(String title, EventHandler<ActionEvent> actionEvent){
        Button button = new Button(title);
        button.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-font-style: italic; -fx-text-fill: #D35400;");
        button.setOnAction(actionEvent);
        button.setOnMouseEntered(_ -> button.setCursor(Cursor.HAND));
        button.setOnMouseExited(_ -> button.setCursor(Cursor.DEFAULT));
        return button;
    }
}