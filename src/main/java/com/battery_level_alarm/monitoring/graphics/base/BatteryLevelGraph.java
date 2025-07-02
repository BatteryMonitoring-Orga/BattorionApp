package com.battery_level_alarm.monitoring.graphics.base;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.graphics.base.ChartType.AREA;
import static com.battery_level_alarm.monitoring.graphics.base.ChartType.LINE;
import static com.battery_level_alarm.monitoring.graphics.ui.GraphPaneHelper.createMainScene;
import static com.battery_level_alarm.monitoring.graphics.executor.LocalScheduledExecutorService.createMainScheduledExecutor;
import static com.battery_level_alarm.monitoring.system_core.Battorion.batteryLevel;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.ColorUtils.*;
import static com.battery_level_alarm.monitoring.visual_effects.ColorUtils.ColorType.*;

import com.battery_level_alarm.monitoring.graphics.charts.AreaBatteryChart;
import com.battery_level_alarm.monitoring.graphics.charts.LineBatteryChart;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class BatteryLevelGraph extends Application {
    public static final String DEFAULT_NODES_COLOR = "#f3622d";
    public static final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    public static final ScrollPane[] mainGraphScrolls = new ScrollPane[2];
    
    static ChartType currentType;
    public static BatteryChart batteryChartImpl;
    public static Chart activeChart;
    
    public static ScheduledExecutorService scheduler;
    public static Stage alternativeStage;
    public static Label batteryStatusLabel;
    
    public static volatile boolean isValueChanged = true;
    static volatile boolean isInitialized = false;
    public static volatile boolean isRunning = true;
    
    public static double totalTime = 0;
    public static double chartWidth;
    public static double chartHeight;
    public static int elapsedTime = 0;
    public static int batteryLevelForGraphics;
    public static int previousLevel = 0;
    
    public static boolean isLanguageArabic() {
        return getLanguage().equalsIgnoreCase("العربية");
    }
    
    public static void initialize() {
        if (!isInitialized) {
            Platform.runLater(() -> new BatteryLevelGraph().start(new Stage()));
            isInitialized = true;
        } else {
            reloadGraphUI(false);
        }
    }
    
    public static void reloadGraphUI(boolean isSaveAction) {
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            if (alternativeStage != null) {
                alternativeStage.close();
                alternativeStage = null;
            } if (isSaveAction) {
                createBatteryChart();
            }
            
            alternativeStage = new Stage();
            BatteryLevelGraph graph = new BatteryLevelGraph();
            try {
                graph.start(alternativeStage);
                batteryChartImpl.updateChartData();
                batteryChartImpl.applyNodeColor(getCurrentChartColor());
            } catch (Exception e) {
                logger.severe("[EXCEPTION]: " + e.getMessage());
            }
        });
    }
    
    static void createBatteryChart() {
        if (getChartType().equals(AREA.name())) {
            currentType = AREA;
        } else {
            currentType = LINE;
        }
        
        switch (currentType) {
            case LINE:
                batteryChartImpl = new LineBatteryChart();
                break;
            case AREA:
                batteryChartImpl = new AreaBatteryChart();
                break;
        }
        activeChart = batteryChartImpl.createChart();
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
        createBatteryChart();
        
        primaryStage.setScene(createMainScene(primaryStage));
        createMainScheduledExecutor();
        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResource(ICONS_FOLDER_PATH + "11473508.png")).toExternalForm()
        ));
        primaryStage.setTitle("Battery Level Graph");
        alternativeStage = primaryStage;
    }
    
    public static void display() {
        if (alternativeStage != null) {
            Platform.setImplicitExit(false);
            Platform.runLater(alternativeStage::show);
        }
    }
    
    public static @NotNull Tooltip getTooltip(double totalTime, double elapsedTime, int batteryLevel) {
        String content = isLanguageArabic()
                ? "✔ مستوى البطارية: " + batteryLevel + "%\n" +
                "✔ الوقت المنقضي: " + String.format("%.2f", (elapsedTime / 60)) + " دقيقة\n" +
                "✔ الوقت الكلي: " + String.format("%.2f", totalTime) + " دقيقة"
                : "✔ Battery Level: " + batteryLevel + "%\n" +
                "✔ Elapsed Time: " + String.format("%.2f", (elapsedTime / 60)) + " min\n" +
                "✔ Total Time: " + String.format("%.2f", totalTime) + " min";
        return new Tooltip(content);
    }
    
    public static String getCurrentChartColor() {
        String cssColor = batteryLevel <= getAlertThreshold()
                ? toCssCompatibleColor(getAlertColor())
                : toCssCompatibleColor(getSketchColor());
        
        if (!detectColorType(cssColor).equals(HEX)) {
            cssColor = DEFAULT_NODES_COLOR;
        }
        return cssColor;
    }
    
    public static String getHoverChartColor() {
        String cssColor = batteryLevel <= getAlertThreshold()
                ? toCssCompatibleAlphaColor(getAlertColor())
                : toCssCompatibleAlphaColor(getSketchColor());
        
        if (!detectColorType(cssColor).equals(RGBA)) {
            cssColor = DEFAULT_NODES_COLOR;
        }
        return cssColor;
    }
    
    public static String getNodeStyle(String color) {
        return "-fx-background-color: " + color + ", white;" +
                "-fx-background-insets: 0, 2;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 5px;";
    }
    
    public static String toRgbString(Color c) {
        return String.format("rgb(%d, %d, %d)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }
}