package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;
import java.util.Objects;

import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.NotificationPopup.NOTIFICATION_STYLES_PATH;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;

public class NotificationToast {
	private static boolean isShowingToast = false;
	
	public static void showNotification(String message, Stage owner) {
		if (isShowingToast) return;
		isShowingToast = true;
		
		Label label = new Label(message);
		label.setFont(Font.font(16));
		label.setId("notification-label");
		
		StackPane pane = new StackPane(label);
		pane.setId("notification-pane");
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-radius: 10; -fx-padding: 10 20;");
		
		String cssFile = getCssFile();
		Scene scene = new Scene(pane);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(Objects.requireNonNull(NotificationToast.class.getResource(cssFile)).toExternalForm());
		
		Stage toastStage = new Stage();
		toastStage.initOwner(owner);
		toastStage.initStyle(StageStyle.TRANSPARENT);
		toastStage.initModality(Modality.WINDOW_MODAL);
		toastStage.setAlwaysOnTop(true);
		toastStage.setScene(scene);
		toastStage.setWidth(400);
		toastStage.setHeight(40);
		
		Image icon = new Image(Objects.requireNonNull(
				NotificationToast.class.getResource("/com/battery_level_alarm/monitoring/Tray/Icons/hint.png")
		).toExternalForm());
		toastStage.getIcons().add(icon);
		
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		toastStage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - toastStage.getWidth()) / 2);
		toastStage.setY(screenBounds.getMinY() + 20);
		toastStage.show();
		toastStage.toFront();
		
		TranslateTransition slide = new TranslateTransition(Duration.millis(200), pane);
		slide.setFromY(-30);
		slide.setToY(0);
		slide.setInterpolator(Interpolator.EASE_OUT);
		slide.play();
		
		PauseTransition wait = new PauseTransition(Duration.seconds(5));
		wait.setOnFinished(_ -> {
			FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pane);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.setOnFinished(_ -> {
				toastStage.close();
				isShowingToast = false;
			});
			fadeOut.play();
		});
		wait.play();
	}
	
	private static String getCssFile() {
		String mode = prefs.get("appTheme", AS_SYSTEM.toString());
		String cssFile;
		if (mode != null && mode.equalsIgnoreCase(String.valueOf(LIGHT))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_toast_light.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_toast_dark.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(GRAY))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_toast_gray.css";
		} else {
			TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK) ? NOTIFICATION_STYLES_PATH + "/notification_toast_dark.css" : NOTIFICATION_STYLES_PATH + "/notification_toast_light.css";
		}
		return cssFile;
	}
}