package com.battery_level_alarm.monitoring.versions_manager.update_ui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.releaseManager;
import static com.battery_level_alarm.monitoring.versions_manager.update_ui.FileDownloaderWithProgress.getDownloadProgressBar;
import static com.battery_level_alarm.monitoring.versions_manager.update_ui.FileDownloaderWithProgress.isDownloading;

public class AppReleaseNotify {
	public static String latestVersionApp;
	private static HBox container;
	
	public static VBox getNotifyBox() {
		Label title = new Label("Update Available!");
		title.setFont(Font.font("System", FontWeight.BOLD, 15));
		HBox titleBox = new HBox(5, title);
		titleBox.setAlignment(Pos.CENTER);
		
		Label versionText = new Label("📦 Version:");
		versionText.setFont(Font.font("System", FontWeight.MEDIUM, 13));
		
		Label versionLabel = new Label("v" + latestVersionApp);
		versionLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
		
		if(isDarkMode) {
			title.setTextFill(Paint.valueOf("#ffffff"));
			versionText.setTextFill(Paint.valueOf("#ffffff"));
			versionLabel.setTextFill(Paint.valueOf("#1a73e8"));
		} else {
			title.setTextFill(Paint.valueOf("#000000"));
			versionText.setTextFill(Paint.valueOf("#000000"));
			versionLabel.setTextFill(Paint.valueOf("#1669d2"));
		}
		
		HBox versionBox = new HBox(5, versionText, versionLabel);
		versionBox.setAlignment(Pos.CENTER);
		
		Button button = getDownloadButton();
		container = new HBox(button);
		container.setAlignment(Pos.CENTER);
		
		VBox box = new VBox(10, titleBox, versionBox, container);
		box.setPadding(new Insets(8));
		box.setSpacing(8);
		box.setMaxWidth(200);
		box.setMaxHeight(160);
		box.setAlignment(Pos.TOP_LEFT);
		box.setStyle("-fx-background-color: transparent;");
		return box;
	}
	
	private static @NotNull Button getDownloadButton() {
		Button downloadButton = new Button("⬇ Download");
		downloadButton.setAlignment(Pos.CENTER);
		stylePrimaryButton(downloadButton);
		downloadButton.setOnAction(_ -> {
			releaseManager();
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(() -> {
				if (isDownloading()) {
					scheduler.shutdown();
					Platform.runLater(() -> {
						ProgressBar progressBar = getDownloadProgressBar();
						progressBar.setStyle("""
						    -fx-padding: 4;
						    -fx-pref-height: 20px;
						    -fx-pref-width: 200px;
						""");
						container.getChildren().setAll(progressBar);
					});
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		});
		return downloadButton;
	}
	
	private static void stylePrimaryButton(Button button) {
		button.setFont(Font.font("System", FontWeight.BOLD, 11));
		button.setTextFill(Color.WHITE);
		button.setCursor(Cursor.HAND);
		button.setPrefSize(110, 28);
		button.setMinSize(110, 28);
		button.setMaxSize(110, 28);
		button.setStyle("""
			-fx-background-color: #1a73e8;
			-fx-background-radius: 6;
			-fx-padding: 6 12;
			-fx-cursor: hand;
		""");
		
		button.setOnMouseEntered(_ -> button.setStyle("""
			-fx-background-color: #1669d2;
			-fx-background-radius: 6;
			-fx-padding: 6 12;
			-fx-text-fill: white;
			-fx-cursor: hand;
		"""));
		button.setOnMouseExited(_ -> button.setStyle("""
			-fx-background-color: #1a73e8;
			-fx-background-radius: 6;
			-fx-padding: 6 12;
			-fx-text-fill: white;
			-fx-cursor: default;
		"""));
	}
}