package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createBackButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.insets;

import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.questionnaires.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.StatisticsContainerClass;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.util.Objects;

public class StatisticsTab {
	public static boolean isUnderTracking = false;
	public static Label tempFiles;
	public static Label folders;
	public static Label tempSize;
	public static Label diskSpace;
	
	public static Tab createStatisticsTab() {
		Button backButton = createBackButton();
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(20);
		grid.setPadding(new Insets(15));
		grid.setStyle("-fx-border-color: #ccc; -fx-border-radius: 8; -fx-border-width: 1;");
		
		tempFiles = new Label("");
		folders = new Label("");
		tempSize = new Label("");
		diskSpace = new Label("");
		
		grid.addRow(0, new Label("Temporary Files Found:"), tempFiles);
		grid.addRow(1, new Label("Affected Folders:"), folders);
		grid.addRow(2, new Label("Total Temp Size:"), tempSize);
		grid.addRow(3, new Label("Available Disk Space:"), diskSpace);
		
		VBox statsBox = new VBox(15, grid, createCleanButton(), createInfoButton());
		statsBox.setPadding(new Insets(10));
		statsBox.setStyle("-fx-alignment: center;");
		
		VBox content = new VBox(20, backButton, statsBox);
		content.setPadding(insets);
		return createTab("Statistics", content);
	}
	
	private static Button createCleanButton() {
		Button clean = new Button("Clean Temp Files");
		clean.setTooltip(new Tooltip("Deletes all temporary files to free up space."));
		clean.setOnAction(_ -> Thread.ofVirtual().start(() -> {
			if(!isUnderTracking) {
				isUnderTracking = true;
				DiskSpaceInfo.cleanTempFiles();
				StatisticsContainerClass.refreshDiskInfoTab();
			}
		}));
		return clean;
	}
	
	private static Button createInfoButton() {
		Button info = new Button("What Are Temp Files?");
		info.setTooltip(new Tooltip("Temporary files are created during program execution and can be safely deleted."));
		info.setOnAction(_ -> {
			WebView webView = new WebView();
			webView.getEngine().loadContent(StaticQuestionnaire.getDiskInfoExplanation());
			
			Dialog<Void> dialog = new Dialog<>();
			dialog.setTitle("What Are Temp Files?");
			dialog.getDialogPane().setContent(webView);
			dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
			dialog.setResizable(false);
			dialog.getDialogPane().setPrefSize(600, 400);
			
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.setResizable(false);
			stage.getIcons().add(new Image(Objects.requireNonNull(UITabs.class.getResource(
					"/com/battery_level_alarm/monitoring/Tray/Icons/info.png")).toExternalForm()));
			dialog.showAndWait();
		});
		return info;
	}
}
