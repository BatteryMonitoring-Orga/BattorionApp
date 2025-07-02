package com.battery_level_alarm.monitoring.mini_browser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;

public class MiniDocEmptyMessage {
	static VBox emptyMessagePane = new VBox();
	static void createEmptyMessagePane() {
		Label icon = new Label("📘");
		icon.setStyle("-fx-font-size: 64px;");
		
		String titleColor = isDarkMode ? "#eeeeee" : "#000000";
		String subtitleColor = isDarkMode ? "#aaaaaa" : "#666666";
		
		Label title = new Label("Battorion Comprehensive Guide");
		title.setStyle("-fx-font-size: 20px; -fx-text-fill: " + titleColor + ";");
		
		Label subtitle = new Label("Please select a topic from the list on the left.");
		subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + subtitleColor + ";");
		
		emptyMessagePane = new VBox(10, icon, title, subtitle);
		emptyMessagePane.setPadding(new Insets(30));
		emptyMessagePane.setStyle("-fx-alignment: center;");
		emptyMessagePane.setVisible(false);
		emptyMessagePane.setAlignment(Pos.CENTER);
		emptyMessagePane.setMouseTransparent(true);
	}
}
