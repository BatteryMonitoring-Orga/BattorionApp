package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.UITabs.createBackButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.UITabs.createTab;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardTab {
	public static ProgressBar progressBar;
	public static Label batteryStatus;
	public static Label batteryLevel;
	public static Label audioOutput;
	public static Label batteryMonitoring;
	
	static Tab createDashboardTab() {
		Button backButton = createBackButton();
		batteryStatus = new Label("");
		HBox batteryStatusBox = new HBox(new Label("Battery Status: "), batteryStatus);
		batteryStatusBox.setStyle("-fx-padding: 0 5px 0 5px !important;");
		
		batteryLevel = new Label("");
		HBox batteryLevelBox = new HBox(new Label("Battery Level: "), batteryLevel);
		batteryLevelBox.setStyle("-fx-padding: 5px 70px 5px 70px !important;");
		
		VBox batteryBox = new VBox(10,
				batteryStatusBox,
				createStyledProgressBar(),
				batteryLevelBox
		);
		batteryBox.setPadding(new Insets(10));
		batteryBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		
		audioOutput = new Label("");
		HBox audioOutputBox = new HBox(new Label("Audio Output: "), audioOutput);
		batteryMonitoring = new Label("");
		HBox batteryMonitoringBox = new HBox(new Label("Battery monitoring is "), batteryMonitoring);
		
		VBox infoBox = new VBox(12,
				audioOutputBox,
				batteryMonitoringBox
		);
		infoBox.setPadding(new Insets(10));
		infoBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		
		VBox content = new VBox(20,
				backButton,
				batteryBox,
				new Separator(),
				infoBox
		);
		content.setPadding(new Insets(20));
		return createTab("Dashboard", content);
	}
	
	private static ProgressBar createStyledProgressBar() {
		progressBar = new ProgressBar(0.51);
		progressBar.getStyleClass().add("custom-progress-bar");
		progressBar.setPrefWidth(250);
		return progressBar;
	}
}
