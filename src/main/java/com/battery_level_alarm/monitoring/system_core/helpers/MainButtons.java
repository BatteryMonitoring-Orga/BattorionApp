package com.battery_level_alarm.monitoring.system_core.helpers;
import javax.swing.*;
import java.awt.*;

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
	public static boolean isNormalClick = true;
	public static JButton westSideButton;
	public static JButton dashboardButton;
	public static JButton statisticsButton;
	public static JButton reportButton;
	public static JButton simulatorButton;
	public static JButton graphPainter;
	public static JButton guideButton;
	public static JButton actionButton;
	public static JButton aboutButton;
	public static JButton settingsButton;
	public static JButton feedbackButton;
	
	public static void createAndAddButtons(JPanel panel) {
		createMainButtons();
		hyalineButton(westSideButton, false, false, true, false);
		hyalineButton(dashboardButton, true, false, true, false);
		hyalineButton(statisticsButton, true, false, true, false);
		hyalineButton(reportButton, true, false, true, false);
		hyalineButton(simulatorButton, true, false, true, false);
		hyalineButton(graphPainter, false, false, true, false);
		hyalineButton(guideButton, false, false, true, false);
		hyalineButton(actionButton, false, false, true, false);
		hyalineButton(aboutButton, false, false, true, false);
		hyalineButton(settingsButton, true, false, true, false);
		hyalineButton(feedbackButton, true, false, true, false);
		
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(dashboardButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(statisticsButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(reportButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(simulatorButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(graphPainter);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(guideButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(settingsButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(aboutButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(feedbackButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(westSideButton);
		
		setButtonBackgroundColor();
		dashboardButton.setBackground(DARK_BLUE);
	}
	
	private static void createMainButtons() {
		actionButton = createButton("Click to start monitoring",
			BUTTON_ICONS_PATH, "start", _ -> {
				if (isNextActiveMonitorMode || !isMonitorRunning) {
					isNextActiveMonitorMode = false;
					startMonitoring();
				} else {
					isNextActiveMonitorMode = true;
					stopMonitoring();
				}
				refreshMasterFrame();
			});
		
		graphPainter = createGraphButton();
		dashboardButton = createButton(DASHBOARD_BUTTON_TEXT, "Go to dashboard panel",
			BUTTON_ICONS_PATH, "dashboard", _ -> {
				if(!DashboardPanel.isVisible()){
					setupDashboardPanel();
					refreshMasterFrame();
				}
			});
		statisticsButton = createButton(STATISTICS_BUTTON_TEXT, "Go to statistics panel",
			BUTTON_ICONS_PATH, "statistics", _ -> {
				if (!StatisticsContainer.isVisible()) {
					setupStatisticsPanel();
					refreshMasterFrame();
//					Platform.runLater(FeedbackFlowChat::callSmartFeedbackStepsFlow);
				}
			});
		reportButton = createButton(REPORT_BUTTON_TEXT, "Generate battery life report",
			BUTTON_ICONS_PATH, "report", _ ->Thread.ofVirtual().start(() -> {
				setupLifeReportPanel();
				refreshMasterFrame();
			}));
		simulatorButton = createButton(SIMULATOR_BUTTON_TEXT, "View simulator",
			BUTTON_ICONS_PATH, "simulator", _ -> {
				if (!SimulatorMainPanel.isVisible()) {
					setupSimulatorPanel();
					refreshMasterFrame();
				}
			});
		guideButton = createButton(GUIDE_BUTTON_TEXT, "Open application comprehensive guide",
			ASSETS_FOLDER_PATH, "guide", _ -> Thread.ofVirtual().start(() -> main_browser(new String[]{})));
		settingsButton = createButton(SETTINGS_BUTTON_TEXT, "Open settings tabbed panel",
			BUTTON_ICONS_PATH, "settings", _ -> {
				if(!isNormalClick) {
					if (!SettingsContainer.isVisible()) {
						setupSettingPanel();
						refreshMasterFrame();
					}
					isNormalClick = true;
				} else {
					Thread.ofVirtual().start(() -> {
						if (!SettingsContainer.isVisible()) {
							setupSettingPanel();
							refreshMasterFrame();
						}
					});
				}
			});
		
		aboutButton = createButton(ABOUT_BUTTON_TEXT, "About the application",
				BUTTON_ICONS_PATH, "about",
				_ -> Thread.ofVirtual().start(() -> launchAndOpenTopic(ABOUT_APP, 0)));
		feedbackButton = createButton(FEEDBACK_BUTTON_TEXT, "Send us your opinion or issue",
				ASSETS_FOLDER_PATH, "feedback", _ -> {
					setupFeedbackPanel();
					refreshMasterFrame();
				});
		westSideButton = createButton(WEST_SIDE_BUTTON_TEXT, "Disappear the west side",
				BUTTON_ICONS_PATH, "side_bar", _ -> setupWestSideButton());
	}
}
