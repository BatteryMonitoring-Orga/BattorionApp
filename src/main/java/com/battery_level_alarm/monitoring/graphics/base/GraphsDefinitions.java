package com.battery_level_alarm.monitoring.graphics.base;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphsDefinitions {
    public static String CSS_FILE_NAME;
    public static final XYChart.Series<Number, Number> alterSeries = new XYChart.Series<>();
    public static LineChart<Number, Number> alterLineChart;
    
    public static final int WIDTH_INCREASE_VALUE = 200;
    public static final int INCREASE_WIDTH_EACH_TIMES = 10;
}