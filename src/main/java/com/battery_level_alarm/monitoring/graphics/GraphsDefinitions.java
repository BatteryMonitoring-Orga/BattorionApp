package com.battery_level_alarm.monitoring.graphics;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphsDefinitions {
    static final String CSS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Styles/";
    public static String CSS_FILE_NAME;
    static final XYChart.Series<Number, Number> alterSeries = new XYChart.Series<>();
    static LineChart<Number, Number> alterLineChart;
}