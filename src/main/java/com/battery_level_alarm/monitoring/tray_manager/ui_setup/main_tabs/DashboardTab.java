package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.restartMonitorLoop;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.labeledNode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createBackButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardTab {
	public static ProgressBar progressBar;
	public static Label batteryStatus;
	public static Label batteryLevel;
	public static Label audioOutput;
	public static Label batteryMonitoring;
	
	public static Tab createDashboardTab() {
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
				batteryMonitoringBox,
				new Separator(),
				createUpdateSpeedSelector()
		);
		infoBox.setPadding(new Insets(10));
		infoBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		
		VBox content = new VBox(20,
				backButton,
				batteryBox,
				new Separator(),
				infoBox
		);
		content.setPadding(new Insets(10, 20, 25, 20));
		return createTab("Dashboard", content);
	}
 
	private static HBox createUpdateSpeedSelector() {
		UpdateSpeed savedValue = UpdateSpeed.valueOf(prefs.get("UpdateFrequency", UpdateSpeed.MEDIUM.name()));
		ComboBox<UpdateSpeed> comboBox = new ComboBox<>(
			FXCollections.observableArrayList(
				UpdateSpeed.FAST,
				UpdateSpeed.MEDIUM,
				UpdateSpeed.SLOW
			)
		);
		comboBox.setValue(savedValue);
		comboBox.setCursor(Cursor.HAND);
		comboBox.setPrefSize(150, 30);
		comboBox.setMaxSize(150, 30);
		comboBox.setMinSize(150, 30);
		Tooltip tooltip = new Tooltip("Choose how frequently the battery status should be updated.\n"
				+ "Faster updates consume more system resources.");
		Tooltip.install(comboBox, tooltip);
		comboBox.setTooltip(tooltip);
		
		comboBox.setOnAction(_ -> {
			UpdateSpeed selected = comboBox.getValue();
			prefs.put("UpdateFrequency", selected.name());
			restartMonitorLoop(primaryStage.isShowing());
		});
		return new HBox(20, labeledNode("Update Speed:\u2003", comboBox));
	}
	
	private static ProgressBar createStyledProgressBar() {
		progressBar = new ProgressBar(0.51);
		progressBar.getStyleClass().add("custom-progress-bar");
		progressBar.setPrefWidth(250);
		return progressBar;
	}
}
