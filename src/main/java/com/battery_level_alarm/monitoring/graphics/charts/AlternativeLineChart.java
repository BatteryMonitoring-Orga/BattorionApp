package com.battery_level_alarm.monitoring.graphics.charts;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;

import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.getAxisColor;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.getBackgroundColor;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.getZoomMax;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.getZoomMin;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.isShowDataPoints;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.isShowGridLines;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.isZoomEnabled;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.graphics.charts.BaseBatteryChart.clamp;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_HEIGHT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_WIDTH;

public class AlternativeLineChart {
	public static LineChart<Number, Number> createAlternativeLevelGraph(XYChart.Series<Number, Number> series, int index) {
		series.setName(isLanguageArabic() ? "نسبة البطارية" : "Battery Percentage");
		NumberAxis xAxis = new NumberAxis();
		xAxis.setTickLabelsVisible(isShowXAxisLabels());
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(15);
		xAxis.setTickUnit(1);
		xAxis.setLabel(isLanguageArabic() ? "الوقت (دقائق)" : "Time (Minutes)");
		
		NumberAxis yAxis = new NumberAxis();
		yAxis.setTickLabelsVisible(isShowYAxisLabels());
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(100);
		yAxis.setLabel(isLanguageArabic() ? "مستوى البطارية (%)" : "Battery Level (%)");
		
		LineChart<Number, Number> localLineChart = new LineChart<>(xAxis, yAxis);
		localLineChart.setMinWidth(FRAME_WIDTH);
		localLineChart.setMinHeight(FRAME_HEIGHT);
		localLineChart.setHorizontalGridLinesVisible(isShowGridLines());
		localLineChart.setVerticalGridLinesVisible(isShowGridLines());
		if (getBackgroundColor() != null) {
			localLineChart.setStyle("-fx-background-color: " + toRgbString(getBackgroundColor()) + ";");
		}
		
		Platform.runLater(() -> {
			if (getAxisColor() != null) {
				xAxis.lookup(".axis-label").setStyle(
						"-fx-text-fill: " + toRgbString(getAxisColor()) + ";" +
								"-fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;"
				);
				yAxis.lookup(".axis-label").setStyle(
						"-fx-text-fill: " + toRgbString(getAxisColor()) + ";" +
								"-fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;"
				);
			}
		});
		
		if (isShowDataPoints()) {
			localLineChart.getData().add(series);
		} if (isZoomEnabled()) {
			localLineChart.setOnScroll(event -> {
				double zoomFactor = 1.1;
				if (event.getDeltaY() < 0) {
					zoomFactor = 1 / zoomFactor;
				}
				double scaleX = localLineChart.getScaleX() * zoomFactor;
				double scaleY = localLineChart.getScaleY() * zoomFactor;
				
				if (scaleX >= getZoomMin() && scaleX <= getZoomMax()) {
					localLineChart.setScaleX(scaleX);
					localLineChart.setScaleY(scaleY);
				}
				event.consume();
			});
		}
		
		ScrollPane scrollPane = new ScrollPane(localLineChart);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setPannable(false);
		mainGraphScrolls[index] = scrollPane;
		
		final double[] mouseAnchorX = new double[1];
		final double[] mouseAnchorY = new double[1];
		final boolean[] isDraggingAllowed = {false};
		
		localLineChart.setOnMousePressed(event -> {
			if (event.isPrimaryButtonDown()) {
				mouseAnchorX[0] = event.getSceneX();
				mouseAnchorY[0] = event.getSceneY();
				isDraggingAllowed[0] = true;
			}
		});
		
		localLineChart.setOnMouseReleased(_ -> isDraggingAllowed[0] = false);
		localLineChart.setOnMouseDragged(event -> {
			if (!isDraggingAllowed[0]) return;
			double deltaX = event.getSceneX() - mouseAnchorX[0];
			double deltaY = event.getSceneY() - mouseAnchorY[0];
			double hValue = scrollPane.getHvalue() - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth();
			double vValue = scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight();
			
			scrollPane.setHvalue(clamp(hValue));
			scrollPane.setVvalue(clamp(vValue));
			mouseAnchorX[0] = event.getSceneX();
			mouseAnchorY[0] = event.getSceneY();
		});
		
		return localLineChart;
	}
}
