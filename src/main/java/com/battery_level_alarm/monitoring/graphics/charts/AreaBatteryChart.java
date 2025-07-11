package com.battery_level_alarm.monitoring.graphics.charts;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.isShowDataPoints;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;

public class AreaBatteryChart extends BaseBatteryChart {
	@Override
	public AreaChart<Number, Number> createGraph(NumberAxis xAxis, NumberAxis yAxis) {
		series.setName("Battery Percentage");
		AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
		chart.setCreateSymbols(isShowDataPoints());
		return chart;
	}
	
	@Override
	public void applyNodeColor(String color) {
		Platform.runLater(() -> {
			String nodeStyle = getNodeStyle(color);
			if (series.getNode() != null) {
				Node fill = series.getNode().lookup(".chart-series-area-fill");
				if (fill != null) {
					fill.setStyle("-fx-fill: " + color + "; -fx-opacity: 0.5;");
				}
				Node line = series.getNode().lookup(".chart-series-area-line");
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