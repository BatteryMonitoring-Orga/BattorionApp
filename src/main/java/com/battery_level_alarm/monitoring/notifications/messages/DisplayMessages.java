package com.battery_level_alarm.monitoring.notifications.messages;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.Scene;

import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.FutureTask;

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
	
	public static Stage showNodeInStage(Node node, boolean hideHeader, String iconPath) {
		FutureTask<Stage> task = new FutureTask<>(() -> {
			Stage stage = new Stage();
			if (hideHeader) stage.initStyle(StageStyle.TRANSPARENT);
			
			StackPane container = new StackPane(node);
			container.setPadding(new Insets(10));
			
			container.setBackground(new Background(new BackgroundFill(
					Color.WHITE, new CornerRadii(20), Insets.EMPTY
			)));
			container.setEffect(new DropShadow(20, Color.gray(0, 0)));
			
			Scene scene = new Scene(container);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			if (iconPath != null && !iconPath.isEmpty()) {
				stage.getIcons().add(new Image(Objects.requireNonNull(
						DisplayMessages.class.getResourceAsStream(iconPath))));
			}
			
			stage.show();
			return stage;
		});
		
		if (Platform.isFxApplicationThread()) task.run();
		else Platform.runLater(task);
		try {
			return task.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}