package com.battery_level_alarm.monitoring.battery_report;
import static com.battery_level_alarm.monitoring.system_core.CoreStaticData.frameHeight;
import static com.battery_level_alarm.monitoring.system_core.CoreStaticData.frameWidth;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.ICONS_FOLDER_PATH;
import com.battery_level_alarm.monitoring.system_core.Battorion;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatteryLevelGraph extends Application {
    private static final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    public static ScheduledExecutorService scheduler;
    private static Timeline changeTimer;
    private static Stage alternativeStage;
    private static boolean isInitialized = false;
    public static volatile boolean isRunning = true;
    private static boolean isValueChanged = true;

    private static double totalTime = 0;
    private static int elapsedTime = 0;
    private static int batteryLevel;
    private static int previousLevel = 0;

    private static final String CSS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Styles/";
    public static String CSS_FILE_NAME;

    public static void initialize() {
        if (!isInitialized) {
            new Thread(() -> Application.launch(BatteryLevelGraph.class)).start();
            isInitialized = true;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        createTheTimer();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try{
                batteryLevel = Battorion.batteryLevel;
                if(batteryLevel != previousLevel){
                    isValueChanged = true;
                }
            } catch (Exception e) {
                System.out.println("Error");
                batteryLevel = previousLevel;
            }

            if (!isRunning) {
                scheduler.shutdown();
                return;
            }

            Platform.runLater(() -> {
                if (isRunning && isValueChanged) {
                    stopTimer();
                    toMinute();
                    addDataPoint(totalTime, elapsedTime, batteryLevel);
                    startTimer();
                }
                previousLevel = batteryLevel;
                isValueChanged = false;
            });
        }, 0, 1, TimeUnit.SECONDS);

        primaryStage.setScene(createLevelGraph());
        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource(
                        ICONS_FOLDER_PATH + "11473508.png"
                )).toExternalForm())
        );
        primaryStage.setTitle("Battery Level Graph");
        alternativeStage = primaryStage;
        alternativeStage.show();
    }

    private static Scene createLevelGraph() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (Minutes)");
        xAxis.lookup(".axis-label").setStyle("-fx-text-fill: #D35400; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Battery Level (%)");
        yAxis.lookup(".axis-label").setStyle("-fx-text-fill: #D35400; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMinWidth(frameWidth);
        lineChart.setMinHeight(frameHeight);
        series.setName("Battery Percentage");
        lineChart.getData().add(series);

        ScrollPane scrollPane = new ScrollPane(lineChart);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, frameWidth, frameHeight);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        BatteryLevelGraph.class.getResource(CSS_FOLDER_PATH + CSS_FILE_NAME)
                ).toExternalForm()
        );
        return scene;
    }

    public static void display() {
        if (alternativeStage != null) {
            Platform.runLater(() -> alternativeStage.show());
        }
    }

    private static void createTheTimer() {
        changeTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!isRunning) {
                stopTimer();
                return;
            }
            elapsedTime++;
        }));
        changeTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private static void startTimer() {
        elapsedTime = 0;
        if (changeTimer != null) {
            changeTimer.play();
        }
    }

    private static void stopTimer() {
        if (changeTimer != null) {
            changeTimer.stop();
        }
    }

    private static void toMinute(){
        double value = elapsedTime / 60.0;
        totalTime += value;
    }

    private static void addDataPoint(double totalTime, double elapsedTime, int batteryLevel) {
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
                newNode.setOnMouseExited(event -> {
                    newNode.setStyle("");
                    tooltip.hide();
                });
            }
        });

        Platform.runLater(() -> series.getData().add(dataPoint));
    }
}