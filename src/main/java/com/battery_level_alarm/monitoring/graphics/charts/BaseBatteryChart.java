package com.battery_level_alarm.monitoring.graphics.charts;
import com.battery_level_alarm.monitoring.graphics.base.BatteryChart;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;

import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.graphics.executor.LocalScheduledExecutorService.counter;
import static com.battery_level_alarm.monitoring.graphics.records.GraphicRecordsManager.saveDataAsCSV;
import static com.battery_level_alarm.monitoring.graphics.records.GraphicRecordsManager.saveDataAsJSON;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_HEIGHT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_WIDTH;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.graphics.base.GraphsDefinitions.*;

public abstract class BaseBatteryChart implements BatteryChart {
	protected static XYChart<Number, Number> chart;
	protected static ScrollPane scrollPane;
	protected static NumberAxis xAxis, yAxis;
	
	protected static long mainCounter = 0;
	protected static int chartIndex = 0;
	
	@Override
	public Chart createChart() {
		xAxis = createXAxis();
		yAxis = createYAxis();
		chart = createGraph(xAxis, yAxis);
		chart.setMinWidth(FRAME_WIDTH);
		chart.setMinHeight(FRAME_HEIGHT);
		chart.setHorizontalGridLinesVisible(isShowGridLines());
		chart.setVerticalGridLinesVisible(isShowGridLines());
		
		if (getBackgroundColor() != null) {
			chart.setStyle("-fx-background-color: " + toRgbString(getBackgroundColor()) + ";");
		} if (isShowDataPoints()) {
			chart.getData().add(series);
		}
		Platform.runLater(this::applyAxisStyles);
		
		setupScrollPane();
		setupZoomBehavior();
		setupDragBehavior();
		return chart;
	}
	
	@Override
	public void addDataPoint(double totalTime, double elapsedTime, int batteryLevel) {
		XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(totalTime, batteryLevel);
		if (isShowValuesOnHover()) {
			Tooltip tooltip = getTooltip(totalTime, elapsedTime, batteryLevel);
			tooltip.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-background-color: #0086b3; -fx-text-fill: white;");
			dataPoint.setExtraValue(new Object[]{elapsedTime, tooltip});
			dataPoint.nodeProperty().addListener((_, _, newNode) -> {
				if (newNode != null) setupTooltipHandlers(newNode, tooltip);
			});
		}
		
		Platform.runLater(() -> {
			series.getData().add(dataPoint);
			NumberAxis xAxis = (NumberAxis) chart.getXAxis();
			mainCounter++;
			
			if ((totalTime > xAxis.getUpperBound()) && (counter < INCREASE_WIDTH_EACH_TIMES)) {
				xAxis.setUpperBound(xAxis.getUpperBound() + INCREASE_WIDTH_EACH_TIMES);
				chartWidth += WIDTH_INCREASE_VALUE;
				chart.setMinWidth(chartWidth);
				counter = 1;
			} else if (counter == INCREASE_WIDTH_EACH_TIMES) {
				xAxis.setUpperBound(xAxis.getUpperBound() + INCREASE_WIDTH_EACH_TIMES);
				chartWidth += WIDTH_INCREASE_VALUE;
				chart.setMinWidth(chartWidth);
				counter = 1;
			} else {
				counter++;
			}
			applyNodeColor(getCurrentChartColor());
			
			if(isAutoSave() && mainCounter > getSaveAfterNumOfRecords()) {
				mainCounter = 0;
				if(getSaveFormat().equalsIgnoreCase("CSV")) {
					saveDataAsCSV();
				} else {
					saveDataAsJSON();
				}
			}
		});
	}
	
	@Override
	public void updateChartData() {
		for (XYChart.Data<Number, Number> dataPoint : series.getData()) {
			double x = dataPoint.getXValue().doubleValue();
			int y = dataPoint.getYValue().intValue();
			Object[] extras = (Object[]) dataPoint.getExtraValue();
			double oldElapsed = (extras != null) ? ((Number) extras[0]).doubleValue() : 0.0;
			Tooltip oldTooltip = (extras != null) ? (Tooltip) extras[1] : new Tooltip();
			
			oldTooltip.hide();
			Tooltip newTooltip = getTooltip(x, oldElapsed, y);
			newTooltip.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-background-color: #0086b3; -fx-text-fill: white;");
			Node node = dataPoint.getNode();
			
			if (node != null) {
				setupTooltipHandlers(node, newTooltip);
			} else {
				dataPoint.nodeProperty().addListener((_, _, newNode) -> {
					if (newNode != null) setupTooltipHandlers(newNode, newTooltip);
				});
			}
			dataPoint.setExtraValue(new Object[]{oldElapsed, newTooltip});
		}
	}
	
	private void setupTooltipHandlers(Node node, Tooltip tooltip) {
		node.setOnMouseEntered(event -> {
			if(isShowValuesOnHover()) {
				node.setStyle("-fx-background-color: #0086b3; -fx-scale-x: 2; -fx-scale-y: 2;");
				tooltip.show(node, event.getScreenX() + 10, event.getScreenY() + 10);
			}
		});
		node.setOnMouseMoved(event -> {
			if(isShowValuesOnHover()) {
				tooltip.setX(event.getScreenX() + 10);
				tooltip.setY(event.getScreenY() + 10);
			}
		});
		node.setOnMouseExited(_ -> {
			if(isShowValuesOnHover()) {
				applyNodeColor(getHoverChartColor());
				node.setStyle(getNodeStyle(getHoverChartColor()));
				tooltip.hide();
			}
		});
	}
	
	private void setupScrollPane() {
		scrollPane = new ScrollPane(chart);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setPannable(false);
		mainGraphScrolls[chartIndex] = scrollPane;
	}
	
	private void setupZoomBehavior() {
		if (!isZoomEnabled()) return;
		chart.setOnScroll(event -> {
			double zoomFactor = (event.getDeltaY() < 0) ? 1 / 1.1 : 1.1;
			double scaleX = chart.getScaleX() * zoomFactor;
			double scaleY = chart.getScaleY() * zoomFactor;
			
			if (scaleX >= getZoomMin() && scaleX <= getZoomMax()) {
				chart.setScaleX(scaleX);
				chart.setScaleY(scaleY);
			}
			event.consume();
		});
	}
	
	private void setupDragBehavior() {
		final double[] mouseAnchorX = new double[1];
		final double[] mouseAnchorY = new double[1];
		final boolean[] isDraggingAllowed = {false};
		
		chart.setOnMousePressed(event -> {
			if (event.isPrimaryButtonDown()) {
				mouseAnchorX[0] = event.getSceneX();
				mouseAnchorY[0] = event.getSceneY();
				isDraggingAllowed[0] = true;
			}
		});
		
		chart.setOnMouseReleased(_ -> isDraggingAllowed[0] = false);
		chart.setOnMouseDragged(event -> {
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
	}
	
	private void applyAxisStyles() {
		if (getAxisColor() != null) {
			xAxis.lookup(".axis-label").setStyle("-fx-text-fill: " + toRgbString(getAxisColor()) +
					"; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");
			yAxis.lookup(".axis-label").setStyle("-fx-text-fill: " + toRgbString(getAxisColor()) +
					"; -fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-font-weight: bold;");
		}
	}
	
	private NumberAxis createXAxis() {
		NumberAxis axis = new NumberAxis(0, 15, 1);
		axis.setLabel(isLanguageArabic() ? "الوقت (دقائق)" : "Time (Minutes)");
		axis.setTickLabelsVisible(isShowXAxisLabels());
		axis.setAutoRanging(false);
		return axis;
	}
	
	private NumberAxis createYAxis() {
		NumberAxis axis = new NumberAxis(0, 100, 10);
		axis.setLabel(isLanguageArabic() ? "مستوى البطارية (%)" : "Battery Level (%)");
		axis.setTickLabelsVisible(isShowYAxisLabels());
		return axis;
	}
	
	static double clamp(double value) {
		return Math.max(0, Math.min(1, value));
	}
	
	public abstract XYChart<Number, Number> createGraph(NumberAxis xAxis, NumberAxis yAxis);
	public abstract void applyNodeColor(String color);
}