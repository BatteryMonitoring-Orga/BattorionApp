package com.battery_level_alarm.monitoring.notifications;
import com.battery_level_alarm.monitoring.command_executors.CallCommandLine;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.battery_level_alarm.monitoring.notifications.alerts.AlertSound.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.CSS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.APP_THEME;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.pcVolumeSpinner;

public class ToastNotification {
	private static boolean isShowingToast = false;
	private static final String STYLES_FILES_DIR_PATH = "/com/battery_level_alarm/monitoring/tray-res/styles/notification_toast";
	private static final String BATTERY_ICON_HIGH_LEVEL = "battery_toast_high.png";
	private static final String BATTERY_ICON_LOW_LEVEL = "battery_toast_low.png";
	private static Popup activeSoundPopup = null;
	
	public static void showNotification(String message, int batteryLevel, boolean isWelcomeMSG) {
		if (isShowingToast || !isAppToastNotifyEnabled) return;
		if (batteryLevel > 100 || batteryLevel < 0) return;
		isShowingToast = true;
		
		ImageView batteryIcon = new ImageView(new Image(Objects.requireNonNull(
				ToastNotification.class.getResource(ICONS_FOLDER_PATH + (batteryLevel > 50 ? BATTERY_ICON_HIGH_LEVEL : BATTERY_ICON_LOW_LEVEL))).toExternalForm()
		));
		batteryIcon.setFitWidth(60);
		batteryIcon.setFitHeight(60);
		
		Label batteryLevelLabel = new Label(batteryLevel + "");
		batteryLevelLabel.setId("notification-label");
		
		StackPane batteryStack = new StackPane(batteryIcon, batteryLevelLabel);
		batteryStack.setAlignment(Pos.CENTER);
		
		Label messageLabel = new Label(message);
		messageLabel.setId("notification-label");
		messageLabel.setWrapText(true);
		messageLabel.setMaxWidth(260);
		
		Text textMeasure = new Text(message);
		textMeasure.setFont(messageLabel.getFont());
		textMeasure.setWrappingWidth(messageLabel.getMaxWidth());
		double textHeight = textMeasure.getLayoutBounds().getHeight();
		double textWidth = Math.min(textMeasure.getLayoutBounds().getWidth(), messageLabel.getMaxWidth());
		double width = 260 + textWidth;
		if (width < 400) width = 400;
		double height = Math.max(80, textHeight + 40);
		messageLabel.setMaxWidth(width - 200);
		
		HBox controls = getControls();
		HBox.setHgrow(controls, Priority.ALWAYS);
		controls.setAlignment(Pos.CENTER);
		controls.setMaxWidth(Double.MAX_VALUE);
		
		Button closeButton = new Button("✖");
		closeButton.setPrefSize(40, 40);
		closeButton.setFont(Font.font(14));
		closeButton.setFocusTraversable(false);
		
		Button controlsButton = new Button("\uD83D\uDEE0");
		controlsButton.setPrefSize(40, 40);
		controlsButton.setFont(Font.font("System", FontWeight.BOLD, 14));
		controlsButton.setFocusTraversable(false);
		controlsButton.setManaged(!isWelcomeMSG);
		controlsButton.setVisible(!isWelcomeMSG);
		
		HBox controlsBox = new HBox(5, controlsButton, closeButton);
		HBox.setHgrow(controls, Priority.ALWAYS);
		controlsBox.setAlignment(Pos.CENTER);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox rootLayout = new HBox(10, batteryStack, messageLabel, spacer, controlsBox);
		rootLayout.setAlignment(Pos.CENTER_LEFT);
		
		StackPane pane = new StackPane(rootLayout);
		pane.setId("notification-pane");
		pane.setAlignment(Pos.CENTER_LEFT);
		pane.setStyle("-fx-background-radius: 10; -fx-padding: 15 15;");
		
		String cssFile = getCssFile();
		Scene scene = new Scene(pane);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(Objects.requireNonNull(ToastNotification.class.getResource(cssFile)).toExternalForm());
		messageLabel.setStyle("-fx-font-size: 14px;");
		batteryLevelLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black; -fx-font-family: serif;");
		
		Stage toastStage = new Stage();
		toastStage.initStyle(StageStyle.TRANSPARENT);
		toastStage.initModality(Modality.WINDOW_MODAL);
		toastStage.setAlwaysOnTop(true);
		toastStage.setScene(scene);
		toastStage.setWidth(width);
		toastStage.setHeight(height);
		
		Image icon = new Image(Objects.requireNonNull(
				ToastNotification.class.getResource("/com/battery_level_alarm/monitoring/tray-res/icons/hint.png")
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
			if (!isWelcomeMSG) {
				isAppToastNotifyEnabled = false;
			}
		});
		controlsButton.setOnAction(_ ->
				Platform.runLater(() -> {
					int index = rootLayout.getChildren().indexOf(messageLabel);
					if (index != -1) {
						rootLayout.getChildren().set(index, controls);
					} else {
						int idx = rootLayout.getChildren().indexOf(controls);
						if (idx != -1) {
							rootLayout.getChildren().set(idx, messageLabel);
						}
					}
				})
		);
	}
	
	private static @NotNull HBox getControls() {
		ImageView muteIcon = new ImageView(new Image(Objects.requireNonNull(
				ToastNotification.class.getResource(ICONS_FOLDER_PATH + "mute.png")
		).toExternalForm()));
		muteIcon.setFitWidth(20);
		muteIcon.setFitHeight(20);
		
		ImageView volumeIcon = new ImageView(new Image(Objects.requireNonNull(
				ToastNotification.class.getResource(ICONS_FOLDER_PATH + "sound_level.png")
		).toExternalForm()));
		volumeIcon.setFitWidth(20);
		volumeIcon.setFitHeight(20);
		
		ImageView stopIcon = new ImageView(new Image(Objects.requireNonNull(
				ToastNotification.class.getResource(ICONS_FOLDER_PATH + "stop.png")
		).toExternalForm()));
		stopIcon.setFitWidth(20);
		stopIcon.setFitHeight(20);
		return getControlButtons(muteIcon, volumeIcon, stopIcon);
	}
	
	private static @NotNull HBox getControlButtons(ImageView muteIcon, ImageView volumeIcon, ImageView stopIcon) {
		Button muteButton = new Button("", muteIcon);
		muteButton.setPrefSize(30, 30);
		muteButton.setOnAction(_ -> CallCommandLine.setSoundUnmute(1));
		
		Button volumeButton = getVolumeButton(volumeIcon);
		Button stopButton = new Button("", stopIcon);
		stopButton.setPrefSize(30, 30);
		stopButton.setOnAction(_ -> {
			stopWAV();
			stopMP3();
			cleanupAudioSettingsAfterAlert();
		});
		
		HBox controls = new HBox(10, muteButton, volumeButton, stopButton);
		controls.setAlignment(Pos.BOTTOM_RIGHT);
		return controls;
	}
	
	private static @NotNull Button getVolumeButton(ImageView volumeIcon) {
		Button volumeButton = new Button("", volumeIcon);
		volumeButton.setPrefSize(30, 30);
		volumeButton.setOnAction(_ -> {
			if (activeSoundPopup != null && activeSoundPopup.isShowing()) {
				activeSoundPopup.hide();
				activeSoundPopup = null;
				return;
			}
			
			int current = 50;
			if (pcVolumeSpinner != null) {
				current = (int) pcVolumeSpinner.getValue();
			}
			
			Popup popup = createSoundPopup(current);
			activeSoundPopup = popup;
			Point2D pt = volumeButton.localToScreen(0, volumeButton.getHeight());
			popup.show(volumeButton.getScene().getWindow(), pt.getX(), pt.getY());
		});
		return volumeButton;
	}
	
	public static Popup createSoundPopup(int soundLevel) {
		Label soundLabel = new Label(soundLevel + " %");
		soundLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#1e88ff;");
		
		Slider soundSlider = new Slider(0, 100, soundLevel);
		soundSlider.setPrefWidth(180);
		soundSlider.setShowTickMarks(false);
		soundSlider.setShowTickLabels(false);
		soundSlider.setBlockIncrement(1);
		soundSlider.getStyleClass().add("slider");
		soundSlider.getStylesheets().add(Objects.requireNonNull(ToastNotification.class.getResource(CSS_FOLDER_PATH + "slider.css")).toExternalForm());
		soundSlider.valueProperty().addListener((_, _, newV) -> {
			int v = (int) Math.round(newV.doubleValue());
			soundLabel.setText(v + " %");
		});
		
		Runnable commit = () -> {
			int v = (int) Math.round(soundSlider.getValue());
			new Thread(() -> CallCommandLine.setPCVolume(v)).start();
			if (pcVolumeSpinner != null) {
				Platform.runLater(() -> pcVolumeSpinner.setValue(v));
			}
		};
		
		soundSlider.setOnMouseReleased(_ -> commit.run());
		soundSlider.setOnTouchReleased(_ -> commit.run());
		soundSlider.setOnMouseClicked(_ -> commit.run());
		soundSlider.setOnKeyReleased(e -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.DOWN
					|| code == KeyCode.HOME || code == KeyCode.END || code == KeyCode.PAGE_UP
					|| code == KeyCode.PAGE_DOWN || code == KeyCode.ENTER) {
				commit.run();
			}
		});
		
		VBox content = new VBox(8, soundLabel, soundSlider);
		content.setPadding(new Insets(8));
		content.setStyle(
				"-fx-background-color: white;" +
				"-fx-background-radius: 10;" +
				"-fx-border-radius: 10;" +
				"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
				"-fx-border-color: #e0e0e0; -fx-border-width:1;"
		);
		
		Popup popup = new Popup();
		popup.getContent().add(content);
		popup.setAutoHide(true);
		popup.setAutoFix(true);
		popup.setHideOnEscape(true);
		popup.setOnHidden(_ -> activeSoundPopup = null);
		return popup;
	}
	
	private static @NotNull PauseTransition getPauseTransition(StackPane pane, Stage toastStage) {
		PauseTransition wait = new PauseTransition(Duration.seconds(10));
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
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK_BLUE))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_dark_blue.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(LAVENDER))) {
			cssFile = STYLES_FILES_DIR_PATH + "/notification_toast_lavender.css";
		} else {
			TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK)
					? STYLES_FILES_DIR_PATH + "/notification_toast_dark.css"
					: STYLES_FILES_DIR_PATH + "/notification_toast_light.css";
		}
		return cssFile;
	}
}
