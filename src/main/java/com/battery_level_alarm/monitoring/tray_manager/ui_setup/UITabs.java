package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.DashboardTab.createDashboardTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.SettingsTab.createSettingsTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.StatisticsTab.createStatisticsTab;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;
import java.util.Stack;

public class UITabs {
	private static final String BACK_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/back.png";
	private static final Stack<Tab> tabHistory = new Stack<>();
	private static boolean isNavigatingBack = false;
	
	static TabPane createTabsPanel() {
		TabPane tabPane = new TabPane(createDashboardTab(), createSettingsTab(), createStatisticsTab());
		setupTabNavigation(tabPane);
		return tabPane;
	}
	
	private static void setupTabNavigation(TabPane tabPane) {
		tabPane.getSelectionModel().selectedItemProperty().addListener((_, oldTab, newTab) -> {
			if (!isNavigatingBack && oldTab != null && newTab != null && oldTab != newTab) {
				tabHistory.push(oldTab);
			}
		});
	}
	
	static Tab createTab(String title, javafx.scene.Node content) {
		Tab tab = new Tab(title, content);
		tab.setClosable(false);
		return tab;
	}
	
	static Button createBackButton() {
		Button back = new Button("Back");
		back.setPrefWidth(100);
		back.setPrefHeight(30);
		Image backImage = new Image(Objects.requireNonNull(UITabs.class.getResourceAsStream(BACK_ICON_PATH)));
		ImageView icon = new ImageView(backImage);
		icon.setFitHeight(12);
		icon.setFitWidth(12);
		back.setGraphic(icon);
		back.setOnAction(_ -> {
			if (!tabHistory.isEmpty()) {
				isNavigatingBack = true;
				((TabPane) primaryStage.getScene().getRoot()).getSelectionModel().select(tabHistory.pop());
				isNavigatingBack = false;
			}
		});
		return back;
	}
}
