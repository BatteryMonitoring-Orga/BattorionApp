package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;
import java.util.Objects;

public class MiniToast {
	private static final String STYLES_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles";
	
	public static void show(Point2D screenCoordinates, String msg, double durationSeconds) {
		Popup popup = new Popup();
		popup.setAutoFix(true);
		popup.setAutoHide(true);
		popup.setHideOnEscape(true);
		
		Label label = new Label(msg);
		label.setWrapText(false);
		label.setFont(Font.font("Arial", 13));
		label.getStyleClass().add("mini-toast-label");
		label.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		
		StackPane root = new StackPane(label);
		root.setPadding(new Insets(6, 12, 6, 12));
		root.getStyleClass().add("mini-toast-root");
		root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		
		String mode = prefs.get("appTheme", "As System");
		String cssFile;
		if (mode != null && mode.equalsIgnoreCase(String.valueOf(LIGHT))) {
			cssFile = STYLES_PATH + "/mini_toast_light.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK))) {
			cssFile = STYLES_PATH + "/mini_toast_dark.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(GRAY))) {
			cssFile = STYLES_PATH + "/mini_toast_gray.css";
		} else {
			var theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK) ? STYLES_PATH + "/mini_toast_dark.css" : STYLES_PATH + "/mini_toast_light.css";
		}
		
		root.getStylesheets().add(Objects.requireNonNull(MiniToast.class.getResource(cssFile)).toExternalForm());
		
		popup.getContent().add(root);
		popup.show(primaryStage, screenCoordinates.getX() - 150, screenCoordinates.getY() - 20);
		
		Timeline timeline = new Timeline(new KeyFrame(
				Duration.seconds(durationSeconds),
				_ -> popup.hide()));
		timeline.play();
	}
}
