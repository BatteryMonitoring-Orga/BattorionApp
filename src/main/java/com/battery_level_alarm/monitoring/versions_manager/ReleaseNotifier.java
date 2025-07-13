package com.battery_level_alarm.monitoring.versions_manager;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.versions_manager.VersionInstaller.RELEASE_FOLDER;

public class ReleaseNotifier {
	private static final String STYLE_PATH = "/com/battery_level_alarm/monitoring/Styles";
	private static final List<String> logs = new ArrayList<>();
	private static boolean isAlertShown = false;
	public static String callBackMSG;
	
	public static void releaseLog(String message) {
		if (message != null && !message.isBlank()) {
			logs.add(message);
		}
	}
	
	public static boolean isAlertShown() {
		return isAlertShown;
	}
	
	public static void showAllLogs(String title) {
		if (logs.isEmpty()) {
			callBackMSG = "ℹ️ No release logs to display.";
			return;
		} else if (isAlertShown) {
			callBackMSG = "⚠️ A release status alert is already being displayed.";
			return;
		}
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			isAlertShown = true;
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Release Status");
			alert.setHeaderText(title != null ? title : "Installation Feedback");
			((Stage) alert.getDialogPane().getScene().getWindow())
					.getIcons().add(new Image(Objects.requireNonNull(
							ReleaseNotifier.class.getResourceAsStream("/com/battery_level_alarm/monitoring/Assets/log.png")
					)));
			
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.setId("releaseDialogPane");
			try {
				dialogPane.getStylesheets().add(Objects.requireNonNull(
						ReleaseNotifier.class.getResource(STYLE_PATH + "/release-alert.css")).toExternalForm()
				);
			} catch (Exception e) {
				System.err.println("⚠️ Failed to load style: " + e.getMessage());
			}
			
			StringBuilder content = new StringBuilder();
			for (String msg : logs) {
				content.append(msg).append("\n");
			}
			
			TextArea textArea = new TextArea(content.toString());
			textArea.setId("releaseLogArea");
			textArea.getStyleClass().add("log-area");
			textArea.setEditable(false);
			textArea.setWrapText(true);
			textArea.setPrefWidth(500);
			textArea.setPrefHeight(300);
			
			ButtonType openFolderBtn = new ButtonType("Open Folder", ButtonBar.ButtonData.OK_DONE);
			alert.getButtonTypes().setAll(openFolderBtn, ButtonType.OK);
			alert.setResultConverter(dialogButton -> {
				if (dialogButton == openFolderBtn) {
					try {
						Path newFolderName = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH + "/" + RELEASE_FOLDER + latestVersion);
						if (Files.exists(newFolderName)) {
							Desktop.getDesktop().open(newFolderName.toFile());
						} else {
							releaseLog("❌ Folder does not exist: " + newFolderName);
						}
					} catch (IOException e) {
						releaseLog("❌ Failed to open folder: " + e.getMessage());
					}
				}
				return dialogButton;
			});
			
			dialogPane.setContent(textArea);
			dialogPane.setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			isAlertShown = false;
			logs.clear();
		});
	}
	
	public static void showLastLog(String title) {
		if (logs.isEmpty()) {
			callBackMSG = "ℹ️ No release logs to display.";
			return;
		}
		String last = logs.getLast();
		showSingleMessage(title != null ? title : "Last Log Message", last);
	}
	
	private static void showSingleMessage(String title, String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Release Notification");
			alert.setHeaderText(title);
			((Stage) alert.getDialogPane().getScene().getWindow())
					.getIcons().add(new Image(Objects.requireNonNull(
							ReleaseNotifier.class.getResourceAsStream("/com/battery_level_alarm/monitoring/Assets/cream_log.png")
					)));
			
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.setId("releaseDialogPane");
			try {
				dialogPane.getStylesheets().add(Objects.requireNonNull(
						ReleaseNotifier.class.getResource(STYLE_PATH + "/release-alert.css")).toExternalForm()
				);
			} catch (Exception e) {
				System.err.println("⚠️ Failed to load style: " + e.getMessage());
			}
			
			TextArea textArea = new TextArea(message);
			textArea.setId("releaseLogArea");
			textArea.getStyleClass().add("log-area");
			textArea.setEditable(false);
			textArea.setWrapText(true);
			textArea.setPrefWidth(600);
			textArea.setPrefHeight(200);
			
			dialogPane.setContent(textArea);
			dialogPane.setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
		});
	}
}