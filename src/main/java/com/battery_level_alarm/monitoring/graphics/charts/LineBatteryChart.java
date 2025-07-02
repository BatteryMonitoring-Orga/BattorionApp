package com.battery_level_alarm.monitoring.graphics.charts;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.isShowDataPoints;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;

public class LineBatteryChart extends BaseBatteryChart {
	@Override
	public LineChart<Number, Number> createGraph(NumberAxis xAxis, NumberAxis yAxis) {
		series.setName(isLanguageArabic() ? "نسبة البطارية" : "Battery Percentage");
		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setCreateSymbols(isShowDataPoints());
		return chart;
	}
	
	@Override
	public void applyNodeColor(String color) {
		Platform.runLater(() -> {
			String nodeStyle = getNodeStyle(color);
			if (series.getNode() != null) {
				Node line = series.getNode().lookup(".chart-series-line");
				if (line != null) {
					line.setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px;");
				}
			}
			
			for (XYChart.Data<Number, Number> data : series.getData()) {
				Node node = data.getNode();
				if (node != null) {
					node.setStyle(nodeStyle);
				}
			}
		});
	}
}