package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.getAudioOutputDevice;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.REFRESH_ICON_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.UPDATE_FREQUENCY;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.isWaitingForInternet;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.restartMonitorLoop;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.currentDeviceLabel;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.labeledNode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;
import static com.battery_level_alarm.monitoring.website.Website.createFXWebsiteCaller;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.MiniToast;
import com.battery_level_alarm.monitoring.versions_manager.update_ui.TrayReleaseNotify;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class DashboardTab {
	public static ProgressBar progressBar;
	public static Label batteryStatus;
	public static Label batteryLevel;
	public static Label batteryEstimatedTime;
	public static Label audioOutput;
	public static Label batteryMonitoring;
	
	public static Tab createDashboardTab() {
		VBox content = new VBox(25);
		content.setPadding(insets);
		content.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(content, Priority.ALWAYS);
		
		VBox batteryBox = createBatteryInfoBox();
		VBox infoBox = createInfoBox();
		
		Thread.ofVirtual().start(() -> {
			if(!isWaitingForInternet && thereIsNewVersion) {
				TrayReleaseNotify.latestVersionTray = latestVersion;
				VBox notifyBox = TrayReleaseNotify.getNotifyBox();
				content.getChildren().addAll(notifyBox, batteryBox, new Separator(), infoBox);
			} else {
				content.getChildren().addAll(batteryBox, new Separator(), infoBox);
			}
		});
		
		Pane viewport = new Pane(content);
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(viewport.widthProperty());
		clip.heightProperty().bind(viewport.heightProperty());
		viewport.setClip(clip);
		viewport.setOnScroll(event -> handleScroll(event, content, viewport));
		return createTab("Dashboard", viewport);
	}
	
	private static VBox createBatteryInfoBox() {
		batteryStatus = new Label("");
		HBox batteryStatusBox = new HBox(new Label("Battery Status: "), batteryStatus);
		batteryStatusBox.setStyle("-fx-padding: 0 5px 0 5px !important;");
		
		batteryLevel = new Label("");
		HBox batteryLevelBox = new HBox(new Label("Battery Level: "), batteryLevel);
		batteryLevelBox.setStyle("-fx-padding: 5px 70px 5px 70px !important;");
		
		batteryEstimatedTime = new Label("");
		HBox batteryEstimatedTimeBox = new HBox(new Label("Estimated Time: "), batteryEstimatedTime);
		batteryEstimatedTimeBox.setMaxWidth(Double.MAX_VALUE);
		batteryEstimatedTimeBox.setAlignment(Pos.CENTER);
		batteryEstimatedTimeBox.setPadding(new Insets(5, 10, 5, 25));
		
		VBox batteryBox = new VBox(10,
				batteryStatusBox,
				createStyledProgressBar(),
				batteryLevelBox,
				batteryEstimatedTimeBox
		);
		batteryBox.setPadding(new Insets(10));
		batteryBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		return batteryBox;
	}
	
	private static VBox createInfoBox() {
		batteryMonitoring = new Label("");
		HBox batteryMonitoringBox = new HBox(new Label("Battery monitoring is "), batteryMonitoring);
		
		VBox infoBox = new VBox(12,
				createAudioDeviceBox(),
				batteryMonitoringBox,
				createFXWebsiteCaller(Pos.CENTER_LEFT),
				new Separator(),
				createUpdateSpeedSelector()
		);
		infoBox.setPadding(new Insets(10));
		infoBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		return infoBox;
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
			MiniToast.show(refreshAudioDevice.localToScreen(0, 0), "Using: " + deviceName, 0.75, false, null);
			primaryStage.requestFocus();
		});
		
		Image refreshImage = new Image(Objects.requireNonNull(UITabs.class.getResourceAsStream(REFRESH_ICON_PATH + "refresh.png")));
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
		UpdateSpeed savedValue = UpdateSpeed.valueOf(prefs.get(UPDATE_FREQUENCY, UpdateSpeed.MEDIUM.name()));
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
			prefs.put(UPDATE_FREQUENCY, selected.name());
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
	
	static void handleScroll(ScrollEvent event, VBox content, Pane viewport) {
		double deltaY = event.getDeltaY();
		double newTranslate = content.getTranslateY() + deltaY;
		newTranslate = Math.min(newTranslate, 0);
		newTranslate = Math.max(newTranslate, -1 * (content.getHeight() - viewport.getHeight()));
		content.setTranslateY(newTranslate);
	}
}
