package com.battery_level_alarm.monitoring.system_core;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.LifeReportPanelUI;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.getCurrentAudioDevice;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_HEIGHT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.WEST_PANEL_OPEN_WIDTH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.progressBarInFirstMode;
import static com.battery_level_alarm.monitoring.system_core.MainComponentsCreator.*;
import static com.battery_level_alarm.monitoring.system_core.MainComponentsCreator.createLabel;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.isWaitingForInternet;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.createHBoxPanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.ifPanelsNullCreate;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.setUpProgressPanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.setupDashboardControlPanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.actionButton;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.createAndAddButtons;
import static com.battery_level_alarm.monitoring.system_core.helpers.ReleasePanel.setupReleasePanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.SaverModePanel.setupSaverModePanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.TopAssistPanel.createTopAssistPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.addTextField;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.setMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.textFieldFont;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.ONE_SPACE;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

public class InitializeMainPanels {
	static void initializePanels() {
		motherFrameContainer = new JPanel(new BorderLayout());
		motherFrameContainer = applyGradientBackground(motherFrameContainer, isDarkMode, false, 0, false);
		motherPanelContainer = new JPanel();
		motherPanelContainer = applyGradientBackground(
				motherPanelContainer, isDarkMode, false, 0, false
		);
		motherPanelContainer.setLayout(new BoxLayout(motherPanelContainer, BoxLayout.Y_AXIS));
		
		motherPanel = new RoundedPanel(30, new BorderLayout());
		motherPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		DashboardPanel = new JPanel(new BorderLayout());
		ifPanelsNullCreate();
	}
	
	public static void initializeDashboard(boolean isForRefreshDash) {
		if (isForRefreshDash) {
			prepareDashEastSidePanel();
			return;
		}
		
		JPanel threadControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		monitoringStatusLabel = createLabel("", 16, Font.ITALIC + Font.PLAIN);
		threadControlPanel.add(actionButton);
		threadControlPanel.add(monitoringStatusLabel);
		DashboardPanel.add(threadControlPanel, BorderLayout.NORTH);
		
		batteryLevel = getSafeBatteryLevel();
		Color color = getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel());
		createBatteryBar(color);
		ratioChargeLabel = createLabel("Battery Level: " + batteryLevel + "%", 15, Font.BOLD + Font.PLAIN);
		
		prepareDashEastSidePanel();
		setUpProgressPanel(progressBarInFirstMode);
		estimatedRemainingTime = createLabel("", 14, Font.ITALIC + Font.PLAIN);
		JPanel progressPanelContainer = new JPanel();
		progressPanelContainer.setLayout(new BoxLayout(progressPanelContainer, BoxLayout.Y_AXIS));
		JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		labelsPanel.add(estimatedRemainingTime);
		progressPanelContainer.add(progressPanel);
		progressPanelContainer.add(labelsPanel);
		DashboardPanel.add(progressPanelContainer, BorderLayout.CENTER);
		
		alertLabel = createAlertLabel();
		JScrollPane scroll = createScrollPane(alertLabel);
		DashboardPanel.add(scroll, BorderLayout.SOUTH);
		motherPanel.add(DashboardPanel, BorderLayout.CENTER);
		Thread.ofVirtual().start(LifeReportPanelUI::updateBatteryLiveInfo);
	}
	
	static void prepareDashEastSidePanel() {
		Component oldEast = ((BorderLayout)DashboardPanel.getLayout()).getLayoutComponent(BorderLayout.EAST);
		if (oldEast != null) {
			DashboardPanel.remove(oldEast);
		}
		
		JPanel eastSidePanel = new JPanel(new GridLayout(3, 1, 5, 5));
		eastSidePanel.add(setupDashboardControlPanel());
		if(!isWaitingForInternet && thereIsNewVersion) {
			setupReleasePanel();
			eastSidePanel.add(releasePanel);
		} else {
			eastSidePanel.add(new JLabel(""));
		}
		
		setupSaverModePanel();
		eastSidePanel.add(saverModePanel);
		DashboardPanel.add(eastSidePanel, BorderLayout.EAST);
		DashboardPanel.revalidate();
		DashboardPanel.repaint();
	}
	
	static void initializeButtonPanel() {
		mainButtonsContainer = createButtonPanel();
		createAndAddButtons(mainButtonsContainer);
		mainButtonsContainer.setPreferredSize(new Dimension(WEST_PANEL_OPEN_WIDTH, FRAME_HEIGHT));
		mainButtonsContainer.setMaximumSize(new Dimension(WEST_PANEL_OPEN_WIDTH, FRAME_HEIGHT));
	}
	
	static void initializeStatusPanel() {
		JPanel statusLabelPanel = new JPanel(new BorderLayout());
		statusLabel = createLabel(ONE_SPACE + "Battery Status: " + status, 20, Font.PLAIN + Font.BOLD);
		statusLabelPanel.add(statusLabel, BorderLayout.CENTER);
		getBatteryMode(getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel()));
		lastMode = status;
		
		JPanel secondLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		secondLabelPanel.add(new JLabel(ONE_SPACE));
		JLabel audioOutputDeviceDashLabel = addLabel(
				new GridBagConstraints(), new JPanel(),
				"Audio Output: ", textFieldFont
		);
		
		audioOutputDeviceDashTextField = addTextField(
				new GridBagConstraints(), new JPanel(),
				getCurrentAudioDevice(),
				160, 20, null, false
		);
		setMouseListener(
				audioOutputDeviceDashTextField,
				BattorionPanelHelper::audioLabelMouseAction,
				UIManager.getColor("TextField.Foreground"),
				new Color(0, 134, 179),
				false, false, true
		);
		
		secondLabelPanel.add(audioOutputDeviceDashLabel);
		secondLabelPanel.add(audioOutputDeviceDashTextField);
		statusLabelPanel.add(secondLabelPanel, BorderLayout.SOUTH);
		
		JPanel topComponentsContainer = new RoundedPanel(30, new GridLayout(1, 2));
		topComponentsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topComponentsContainer.add(statusLabelPanel);
		topComponentsContainer.add(createTopAssistPanel(
				getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel())));
		motherPanelContainer.add(topComponentsContainer);
		motherPanelContainer.add(Box.createRigidArea(new Dimension(0, 15)));
		createHBoxPanel();
		motherPanelContainer.add(HBoxPanel);
		
		motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.WEST);
		motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.EAST);
		motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.NORTH);
		motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.SOUTH);
		motherFrameContainer.add(motherPanelContainer, BorderLayout.CENTER);
		mainFrame.add(motherFrameContainer);
	}
}
