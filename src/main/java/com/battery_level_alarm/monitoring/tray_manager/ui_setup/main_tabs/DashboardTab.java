package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.getAudioOutputDevice;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.restartMonitorLoop;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.currentDeviceLabel;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.labeledNode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createBackButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.MiniToast;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Objects;

public class DashboardTab {
	private static final String REFRESH_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/Icons/refresh.png";
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
		
		batteryMonitoring = new Label("");
		HBox batteryMonitoringBox = new HBox(new Label("Battery monitoring is "), batteryMonitoring);
		
		VBox infoBox = new VBox(12,
				createAudioDeviceBox(),
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
		content.setPadding(insets);
		return createTab("Dashboard", content);
	}
	
	private static HBox createAudioDeviceBox() {
		Button refreshAudioDevice = new Button("");
		refreshAudioDevice.setMinSize(25,25);
		refreshAudioDevice.setPrefSize(25,25);
		refreshAudioDevice.setMaxSize(25,25);
		refreshAudioDevice.getStyleClass().add("refresh-button");
		refreshAudioDevice.setId("refresh-button-id");
		refreshAudioDevice.setTooltip(new Tooltip("Refresh audio devices"));
		refreshAudioDevice.setOnAction(_ -> {
			String[] audioDevice = getAudioOutputDevice();
			String deviceName = audioDevice[1];
			audioOutput.setText(deviceName);
			currentDeviceLabel.setText("Current Device:   " + deviceName);
			MiniToast.show(refreshAudioDevice.localToScreen(0, 0), "Using: " + deviceName, 0.75);
			primaryStage.requestFocus();
		});
		
		Image refreshImage = new Image(Objects.requireNonNull(UITabs.class.getResourceAsStream(REFRESH_ICON_PATH)));
		ImageView icon = new ImageView(refreshImage);
		icon.setFitHeight(20);
		icon.setFitWidth(20);
		refreshAudioDevice.setGraphic(icon);
		refreshAudioDevice.setStyle("-fx-z-index: 1000px;");
		
		audioOutput = new Label("");
		audioOutput.setMinHeight(25);
		audioOutput.setAlignment(Pos.CENTER_LEFT);
		
		ScrollPane scrollableLabelPane = new ScrollPane(audioOutput);
		scrollableLabelPane.getStyleClass().add("invisible-scroll");
		scrollableLabelPane.setFitToHeight(true);
		scrollableLabelPane.setFitToWidth(false);
		scrollableLabelPane.setPrefViewportWidth(145);
		scrollableLabelPane.setMinHeight(25);
		scrollableLabelPane.setMaxHeight(25);
		scrollableLabelPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollableLabelPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollableLabelPane.setPannable(false);
		scrollableLabelPane.addEventFilter(ScrollEvent.SCROLL, event -> {
			if (event.getDeltaY() != 0) {
				event.consume();
			}
		});
		
		HBox audioOutputBox = new HBox(5);
		audioOutputBox.setAlignment(Pos.CENTER_LEFT);
		audioOutputBox.getChildren().addAll(new Label("Audio Output:"), scrollableLabelPane, refreshAudioDevice);
		return audioOutputBox;
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
