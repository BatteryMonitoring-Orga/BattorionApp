package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Objects;

public class NotificationPopup {
	public static final String NOTIFICATION_STYLES_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles";
	private final Stage stage;
	private static final int width = 350;
	private static final int height = 150;
	private final int displayDurationMillis;
	
	public NotificationPopup(String title, String msg, String titleIconPath, String messageIconPath, int durationMillis) {
		this.displayDurationMillis = durationMillis;
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(messageIconPath))));
		this.stage.setAlwaysOnTop(true);
		
		ImageView titleIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(titleIconPath)).toExternalForm()));
		titleIcon.setFitWidth(24);
		titleIcon.setFitHeight(24);
		
		Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("Arial", 16));
		titleLabel.getStyleClass().add("title-label");
		
		HBox titleBox = new HBox(10, titleIcon, titleLabel);
		titleBox.setAlignment(Pos.CENTER_LEFT);
		titleBox.setPadding(new Insets(10, 10, 5, 10));
		titleBox.getStyleClass().add("title-box");
		
		ImageView messageIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(messageIconPath)).toExternalForm()));
		messageIcon.setFitWidth(48);
		messageIcon.setFitHeight(48);
		
		Label messageLabel = new Label(msg);
		messageLabel.setWrapText(true);
		messageLabel.setFont(Font.font("Arial", 14));
		messageLabel.getStyleClass().add("message-label");
		
		HBox messageBox = new HBox(15, messageIcon, messageLabel);
		messageBox.setAlignment(Pos.CENTER_LEFT);
		messageBox.setPadding(new Insets(5, 10, 10, 10));
		messageBox.getStyleClass().add("message-box");
		
		VBox root = new VBox(titleBox, new Label("  "), messageBox);
		root.getStyleClass().add("notification-root");
		
		String mode = prefs.get("appTheme", AS_SYSTEM.toString());
		String cssFile;
		if (mode != null && mode.equalsIgnoreCase(String.valueOf(LIGHT))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_popup_light.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_popup_dark.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(GRAY))) {
			cssFile = NOTIFICATION_STYLES_PATH + "/notification_popup_gray.css";
		} else {
			TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK) ? NOTIFICATION_STYLES_PATH + "/notification_popup_dark.css" : NOTIFICATION_STYLES_PATH + "/notification_popup_light.css";
		}
		
		Scene scene = new Scene(root, width, height);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm());
		this.stage.setScene(scene);
		this.stage.getScene().setFill(Color.TRANSPARENT);
		this.stage.initStyle(StageStyle.TRANSPARENT);
		
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX(screenBounds.getMaxX() - width - 20);
		stage.setY(screenBounds.getMaxY() - height - 40);
	}
	
	public void show() {
		Platform.runLater(() -> {
			stage.show();
			Timeline timeline = new Timeline(new KeyFrame(
					Duration.millis(displayDurationMillis),
					_ -> stage.close()));
			timeline.play();
		});
	}
}