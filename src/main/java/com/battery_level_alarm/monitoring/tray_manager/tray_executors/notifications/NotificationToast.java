package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.APP_THEME;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.isToastNotifyEnabled;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.STYLES_FILES_DIR_PATH;

public class NotificationToast {
	private static boolean isShowingToast = false;
	
	public static void showNotification(String message, Stage owner, boolean isWelcomeMSG) {
		if (isShowingToast || !isToastNotifyEnabled) return;
		isShowingToast = true;
		
		Label label = new Label(message);
		label.setFont(Font.font(16));
		label.setId("notification-label");
		
		Button closeButton = new Button("✕");
		closeButton.setFocusTraversable(false);
		
		StackPane.setAlignment(closeButton, Pos.CENTER_RIGHT);
		StackPane.setMargin(closeButton, new javafx.geometry.Insets(5, 8, 0, 0));
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
		HBox content = new HBox(label, spacer, closeButton);
		content.setAlignment(Pos.CENTER_LEFT);
		content.setSpacing(10);
		
		StackPane pane = new StackPane(content);
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
		toastStage.setHeight(50);
		
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
		slide.setFromY(-25);
		slide.setToY(0);
		slide.setInterpolator(Interpolator.EASE_OUT);
		slide.play();
		
		PauseTransition wait = getPauseTransition(pane, toastStage);
		closeButton.setOnAction(_ -> {
			wait.stop();
			toastStage.close();
			isShowingToast = false;
			if(!isWelcomeMSG) {
				isToastNotifyEnabled = false;
			}
		});
	}
	
	private static @NotNull PauseTransition getPauseTransition(StackPane pane, Stage toastStage) {
		PauseTransition wait = new PauseTransition(Duration.seconds(5));
		wait.setOnFinished(_ -> {
			FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pane);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.setOnFinished(_ -> {
				if (toastStage.isShowing()) {
					toastStage.close();
				}
				PauseTransition delayAfterClose = new PauseTransition(Duration.seconds(5));
				delayAfterClose.setOnFinished(_ -> isShowingToast = false);
				delayAfterClose.play();
			});
			fadeOut.play();
		});
		wait.play();
		return wait;
	}
	
	private static String getCssFile() {
		String mode = prefs.get(APP_THEME, AS_SYSTEM.toString());
		String cssFile;
		if (mode != null && mode.equalsIgnoreCase(String.valueOf(LIGHT))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_light.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_dark.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(GRAY))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_gray.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(CREAM))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_cream.css";
		} else {
			TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK) ? STYLES_FILES_DIR_PATH + "/notification_toast_dark.css" : STYLES_FILES_DIR_PATH + "/notification_toast_light.css";
		}
		return cssFile;
	}
}