package com.battery_level_alarm.monitoring.system_core.helpers;
import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.battery_report.ChooseAction.choose;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.main_browser;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.ABOUT_APP;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.BUTTON_ICONS_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.createButton;

public class MainButtons {
	public static JButton westSideButton;
	public static JButton dashboardButton;
	public static JButton actionButton;
	public static JButton settingsButton;
	public static JButton statisticsButton;
	public static JButton simulatorButton;
	public static JButton guideButton;
	public static JButton graphPainter;
	public static JButton reportButton;
	public static JButton aboutButton;
	
	public static void createAndAddButtons(JPanel panel) {
		createMainButtons();
		hyalineButton(westSideButton, true, false, true, false);
		hyalineButton(dashboardButton, true, false, true, false);
		hyalineButton(statisticsButton, true, false, true, false);
		hyalineButton(reportButton, true, false, true, false);
		hyalineButton(graphPainter, true, false, true, false);
		hyalineButton(guideButton, true, false, true, false);
		hyalineButton(simulatorButton, true, false, true, false);
		hyalineButton(actionButton, true, false, true, false);
		hyalineButton(aboutButton, true, false, true, false);
		hyalineButton(settingsButton, true, false, true, false);
		
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(westSideButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(dashboardButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(statisticsButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(reportButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(graphPainter);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(guideButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(simulatorButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(actionButton);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		for(int i=0; i<2; i++) {
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
		}
		panel.add(aboutButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(settingsButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		setButtonBackgroundColor();
		dashboardButton.setBackground(DARK_BLUE);
	}
	
	private static void createMainButtons() {
		graphPainter = createGraphButton();
		westSideButton = createButton(WEST_SIDE_BUTTON_TEXT, "Disappear the west side",
				BUTTON_ICONS_PATH, "side_bar", _ -> setupWestSideButton());
		dashboardButton = createButton(DASHBOARD_BUTTON_TEXT, "Go to dashboard panel",
				BUTTON_ICONS_PATH, "dashboard", _ ->{
					if(!DashboardPanel.isVisible()){
						setupDashboardPanel();
						refreshMotherFrame();
					}
				});
		statisticsButton = createButton(STATISTICS_BUTTON_TEXT, "Go to statistics panel",
				BUTTON_ICONS_PATH, "statistics", _ -> {
					if (!StatisticsContainer.isVisible()) {
						setupStatisticsPanel();
						refreshMotherFrame();
					}
				});
		guideButton = createButton(GUIDE_BUTTON_TEXT, "Open application comprehensive guide",
				ASSETS_FOLDER_PATH, "guide", _ -> Thread.ofVirtual().start(() -> main_browser(new String[]{})));
		simulatorButton = createButton(SIMULATOR_BUTTON_TEXT, "View simulator",
				BUTTON_ICONS_PATH, "simulator", _ -> {
					if (!SimulatorMainPanel.isVisible()) {
						setupSimulatorPanel();
						refreshMotherFrame();
					}
				});
		actionButton = createButton(START_BUTTON_TEXT, "Click to start monitoring",
				BUTTON_ICONS_PATH, "start", _ -> {
					if (actionButton.getText().contains("Start") || !isMonitorRunning) {
						startMonitoring();
					} else {
						stopMonitoring();
					}
					refreshMotherFrame();
				});
		settingsButton = createButton(SETTINGS_BUTTON_TEXT, "Open settings tabbed panel",
				BUTTON_ICONS_PATH, "settings", _ -> {
					if (!SettingsContainer.isVisible()) {
						setupSettingPanel();
						refreshMotherFrame();
					}
				});
		reportButton = createButton(
				REPORT_BUTTON_TEXT, "Generate battery life report",
				BUTTON_ICONS_PATH, "report", _ -> choose());
		aboutButton = createButton(
				ABOUT_BUTTON_TEXT, "About the application",
				BUTTON_ICONS_PATH, "about",
				_ -> Thread.ofVirtual().start(() -> launchAndOpenTopic(ABOUT_APP, 0)));
	}
}
