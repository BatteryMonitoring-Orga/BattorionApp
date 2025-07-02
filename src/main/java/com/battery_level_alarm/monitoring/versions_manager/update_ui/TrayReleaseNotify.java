package com.battery_level_alarm.monitoring.versions_manager.update_ui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.releaseManager;
import static com.battery_level_alarm.monitoring.versions_manager.update_ui.FileDownloaderWithProgress.getDownloadProgressBar;
import static com.battery_level_alarm.monitoring.versions_manager.update_ui.FileDownloaderWithProgress.isDownloading;

public class TrayReleaseNotify {
	public static String latestVersionTray;
	private static HBox container;
	
	public static VBox getNotifyBox() {
		Label title = new Label("🚀 Update Ready!");
		title.setFont(Font.font("System", FontWeight.BOLD, 17));
		title.setTextFill(Color.web("#2c3e50"));
		
		Label subTitle = new Label("A new version is now available.");
		subTitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
		subTitle.setTextFill(Color.web("#666"));
		
		Label versionLabel = new Label("v" + latestVersionTray);
		versionLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
		versionLabel.setTextFill(Color.web("#1a73e8"));
		
		HBox versionBox = new HBox(8, new Label("📦 Version:"), versionLabel);
		versionBox.setAlignment(Pos.CENTER_LEFT);
		
		container = getButtonContainer();
		VBox box = new VBox(10, title, subTitle, versionBox, container);
		box.setPadding(new Insets(16));
		box.setStyle("""
			-fx-border-color: #ccc;
			-fx-border-radius: 12;
			-fx-background-radius: 12;
			-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
		""");
		box.setMaxWidth(Double.MAX_VALUE);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}
	
	private static @NotNull HBox getButtonContainer() {
		Button downloadButton = new Button("⬇ Download Update");
		downloadButton.setMinWidth(180);
		downloadButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
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
						    -fx-pref-width: 250px;
						""");
						container.getChildren().setAll(progressBar);
					});
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		});
		
		downloadButton.setStyle("""
			-fx-background-color: #1a73e8;
			-fx-text-fill: white;
			-fx-font-weight: bold;
			-fx-background-radius: 8;
			-fx-padding: 9 14;
		""");
		downloadButton.setOnMouseEntered(_ -> downloadButton.setStyle("""
			-fx-background-color: #1669d2;
			-fx-text-fill: white;
			-fx-font-weight: bold;
			-fx-background-radius: 8;
			-fx-padding: 9 14;
		"""));
		downloadButton.setOnMouseExited(_ -> downloadButton.setStyle("""
			-fx-background-color: #1a73e8;
			-fx-text-fill: white;
			-fx-font-weight: bold;
			-fx-background-radius: 8;
			-fx-padding: 9 14;
		"""));
		
		HBox buttonContainer = new HBox(downloadButton);
		buttonContainer.setAlignment(Pos.CENTER);
		buttonContainer.setPadding(new Insets(12, 0, 0, 0));
		return buttonContainer;
	}
}