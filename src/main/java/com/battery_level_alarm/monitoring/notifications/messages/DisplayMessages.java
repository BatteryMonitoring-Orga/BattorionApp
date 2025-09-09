package com.battery_level_alarm.monitoring.notifications.messages;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;

public class DisplayMessages {
	public static void printErrorMessage(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logger.severe("[EXCEPTION]: " + sw);
	}
	
	public static void displayProcessTrack(String title, String[] track, int dialogWidth, int dialogHeight) {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			ListView<String> listView = new ListView<>();
			listView.getItems().addAll(
					Arrays.stream(track)
							.filter(str -> str != null && !str.isEmpty())
							.toList()
			);
			
			listView.setStyle(
					"-fx-font-size: 14px;" +
					"-fx-padding: 10;" +
					"-fx-background-color: #f9f9f9;" +
					"-fx-border-color: #d0d0d0;" +
					"-fx-border-radius: 8;" +
					"-fx-background-radius: 8;"
			);
			
			VBox root = new VBox(listView);
			root.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-spacing: 10;");
			
			Dialog<Void> dialog = new Dialog<>();
			dialog.setTitle(title);
			dialog.initStyle(StageStyle.UTILITY);
			dialog.getDialogPane().setContent(root);
			dialog.getDialogPane().setPrefSize(dialogWidth, dialogHeight);
			dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
			dialog.setOnShown(_ -> {
				Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(Objects.requireNonNull(DisplayMessages.class.getResource(ASSETS_FOLDER_PATH + "log_icon.png")).toString()));
			});
			dialog.showAndWait();
		});
	}
}