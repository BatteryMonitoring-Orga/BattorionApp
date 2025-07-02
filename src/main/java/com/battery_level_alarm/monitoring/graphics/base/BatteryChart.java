package com.battery_level_alarm.monitoring.graphics.base;
import javafx.scene.chart.Chart;

public interface BatteryChart {
	Chart createChart();
	void addDataPoint(double totalTime, double elapsedTime, int batteryLevel);
	void updateChartData();
	void applyNodeColor(String color);
}