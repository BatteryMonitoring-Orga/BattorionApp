package com.battery_level_alarm.monitoring.graphics;
import static com.battery_level_alarm.monitoring.graphics.GraphPaneHelper.*;
import static com.battery_level_alarm.monitoring.graphics.GraphsDefinitions.*;
import static com.battery_level_alarm.monitoring.graphics.LocalScheduledExecutorService.counter;
import static com.battery_level_alarm.monitoring.graphics.LocalScheduledExecutorService.createMainScheduledExecutor;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.ICONS_FOLDER_PATH;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class BatteryLevelGraph extends Application {
    static final ScrollPane[] mainGraphScrolls = new ScrollPane[2];
    static final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    static LineChart<Number, Number> lineChart;

    public static ScheduledExecutorService scheduler;
    public static Stage alternativeStage;
    static Label batteryStatusLabel;
    
    static volatile boolean isValueChanged = true;
    static volatile boolean isInitialized = false;
    public static volatile boolean isRunning = true;
    
    static double totalTime = 0;
    static double chartWidth;
    static double chartHeight;
    static int elapsedTime = 0;
    static int batteryLevelForGraphics;
    static int previousLevel = 0;

    public static void initialize() {
        if (!isInitialized) {
//            new Thread(() -> Application.launch(BatteryLevelGraph.class)).start();
            Platform.runLater(() -> new BatteryLevelGraph().start(new Stage()));
            isInitialized = true;
        }
    }
    
    static {
        try {
            Platform.startup(() -> {
                mainGraphScrolls[0] = new ScrollPane();
                mainGraphScrolls[1] = new ScrollPane();
            });
        } catch (Exception e) {
            logger.severe("[EXCEPTION]: " + e.getMessage());
            Platform.runLater(() -> {
                mainGraphScrolls[0] = new ScrollPane();
                mainGraphScrolls[1] = new ScrollPane();
            });
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        chartWidth = FRAME_WIDTH;
        chartHeight = FRAME_HEIGHT;
        lineChart = createLevelGraph(series, 0);
        primaryStage.setScene(createMainScene(primaryStage));
        createMainScheduledExecutor();

        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource(
                        ICONS_FOLDER_PATH + "11473508.png"
                )).toExternalForm())
        );
        primaryStage.setTitle("Battery Level Graph");
        alternativeStage = primaryStage;
        alternativeStage.show();
    }

    public static void display() {
        if (alternativeStage != null) {
            Platform.runLater(() -> alternativeStage.show());
        }
    }

    static LineChart<Number, Number> createLevelGraph(
            XYChart.Series<Number, Number> series, int index
    ){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (Minutes)");
        xAxis.lookup(".axis-label").setStyle("-fx-text-fill: #D35400; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(15);
        xAxis.setTickUnit(1);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setLabel("Battery Level (%)");
        yAxis.lookup(".axis-label").setStyle("-fx-text-fill: #D35400; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");

        LineChart<Number, Number> localLineChart = new LineChart<>(xAxis, yAxis);
        localLineChart.setMinWidth(FRAME_WIDTH);
        localLineChart.setMinHeight(FRAME_HEIGHT);
        series.setName("Battery Percentage");
        localLineChart.getData().add(series);
        localLineChart.setOnScroll(event -> {
            double zoomFactor = 1.1;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }
            localLineChart.setScaleX(localLineChart.getScaleX() * zoomFactor);
            localLineChart.setScaleY(localLineChart.getScaleY() * zoomFactor);
            event.consume();
        });

        mainGraphScrolls[index] = new ScrollPane(localLineChart);
        mainGraphScrolls[index].setFitToWidth(true);
        mainGraphScrolls[index].setFitToHeight(true);
        mainGraphScrolls[index].setPannable(true);
        return localLineChart;
    }

    static void addDataPoint(double totalTime, double elapsedTime, int batteryLevel) {
        XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(totalTime, batteryLevel);
        Tooltip tooltip = new Tooltip(
                "✔ Battery Level: " + batteryLevel + "%\n" +
                        "✔ Elapsed Time: " + String.format("%.2f", (elapsedTime/60)) + " min\n" +
                        "✔ Total Time: " + String.format("%.2f", totalTime) + " min"
        );
        tooltip.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-background-color: #0086b3; -fx-text-fill: white;");

        Tooltip.install(dataPoint.getNode(), tooltip);
        dataPoint.nodeProperty().addListener((_, _, newNode) -> {
            if (newNode != null) {
                newNode.setOnMouseEntered(event -> {
                    newNode.setStyle("-fx-background-color: #0086b3; -fx-scale-x: 2; -fx-scale-y: 2;");
                    tooltip.show(newNode, event.getScreenX() + 10, event.getScreenY() + 10);
                });
                newNode.setOnMouseMoved(event -> {
                    tooltip.setX(event.getScreenX() + 10);
                    tooltip.setY(event.getScreenY() + 10);
                });
                newNode.setOnMouseExited(_ -> {
                    newNode.setStyle("");
                    tooltip.hide();
                });
            }
        });

        Platform.runLater(() -> {
            series.getData().add(dataPoint);
            NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
            if ((totalTime > xAxis.getUpperBound()) && (counter < INCREASE_WIDTH_EACH_TIMES)) {
                xAxis.setUpperBound(xAxis.getUpperBound() + INCREASE_WIDTH_EACH_TIMES);
                chartWidth += WIDTH_INCREASE_VALUE;
                lineChart.setMinWidth(chartWidth);
                counter = 1;
            } else if(counter == INCREASE_WIDTH_EACH_TIMES) {
                xAxis.setUpperBound(xAxis.getUpperBound() + INCREASE_WIDTH_EACH_TIMES);
                chartWidth += WIDTH_INCREASE_VALUE;
                lineChart.setMinWidth(chartWidth);
                counter = 1;
            } else {
                counter++;
            }
        });
    }
}