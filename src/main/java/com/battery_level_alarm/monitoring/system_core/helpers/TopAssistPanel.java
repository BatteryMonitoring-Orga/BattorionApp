package com.battery_level_alarm.monitoring.system_core.helpers;
import com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker;
import com.battery_level_alarm.monitoring.download_tracker.DownloadProgressSwingWithFX;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.StatisticsContainerClass;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.ENABLE_COMPUTER_NOTIFICATIONS;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setButtonBackgroundColor;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.checkForVersionUpdates;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.isWaitingForInternet;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.BordersConfiguration.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PanelIdentifiers.IS_A_DASHBOARD_PANEL;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.IMAGES_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.progressBarInFirstMode;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.progressBarInVerticalMode;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.updateUIBasedOnVersion;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.createPopup;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.setProgressBarMode;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.setUpProgressPanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.dashboardButton;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance.getPopupMenu;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeIcons.THEME_ICON_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.website.Website.websiteCaller;

public class TopAssistPanel {
	public static JPanel topAssistantPartialPanelsContainer;
	public static RoundedPanel firstTopAssistantPartialPanel;
	public static RoundedPanel secondTopAssistantPartialPanel;
	public static RoundedPanel thirdTopAssistantPartialPanel;
	
	private static JPanel downloaderPanel;
	private static int clickCount = 0;
	public static boolean isSilentMode = false;
	
	public static JPanel createTopAssistPanel(Color color) {
		createDownloaderPanel();
		JButton updateButton = createUpdateButton();
		JButton websiteButton = createWebsiteButton();
		JButton progressBarModeButton = createProgressBarModeButton();
		JButton themeButton = createThemeButton();
		JButton brightnessButton = createBrightnessButton();
		JButton resetButton = createResetButton(color);
		JButton notificationModeButton = createNotificationModeButton();
		JButton notificationButton = createNotificationButton();
		
		topAssistantPartialPanelsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		firstTopAssistantPartialPanel = createRoundedPanel(color);
		secondTopAssistantPartialPanel = createRoundedPanel(color);
		thirdTopAssistantPartialPanel = createRoundedPanel(color);
		
		firstTopAssistantPartialPanel.add(updateButton);
		firstTopAssistantPartialPanel.add(downloaderPanel);
		firstTopAssistantPartialPanel.add(websiteButton);
		secondTopAssistantPartialPanel.add(progressBarModeButton);
		secondTopAssistantPartialPanel.add(themeButton);
		secondTopAssistantPartialPanel.add(brightnessButton);
		thirdTopAssistantPartialPanel.add(resetButton);
		thirdTopAssistantPartialPanel.add(notificationModeButton);
		thirdTopAssistantPartialPanel.add(notificationButton);
		
		topAssistantPartialPanelsContainer.add(firstTopAssistantPartialPanel);
		topAssistantPartialPanelsContainer.add(secondTopAssistantPartialPanel);
		topAssistantPartialPanelsContainer.add(thirdTopAssistantPartialPanel);
		refreshTopAssistantPartialPanelsShadow(color);
		return topAssistantPartialPanelsContainer;
	}
	
	private static void createDownloaderPanel() {
		Color bg = UIManager.getColor("Panel.background");
		DownloadProgressSwingWithFX downloader = new DownloadProgressSwingWithFX(bg);
		downloaderPanel = downloader.getDownloadProgressPanel();
		downloaderPanel.setPreferredSize(new Dimension(30, 30));
		downloaderPanel.setMaximumSize(new Dimension(30, 30));
	}
	
	private static JButton createUpdateButton() {
		JButton button = BattorionButtonHelper.createButton(
				"Check for latest app release", IMAGES_FOLDER_PATH, "2878768", null);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					try {
						checkForVersionUpdates();
						if (thereIsNewVersion) {
							isWaitingForInternet = false;
							updateUIBasedOnVersion();
							setButtonBackgroundColor();
							dashboardButton.setBackground(DARK_BLUE);
						} else {
							createPopup("You're already using the latest version.", button);
						}
					} catch (Exception ex) {
						printErrorMessage(ex);
					}
				}
			}
		});
		return button;
	}
	
	private static JButton createResetButton(Color color) {
		return BattorionButtonHelper.createButton(
				"Reset the alert statement then update Disk \nInformation tab and audio output device name",
				IMAGES_FOLDER_PATH, "3808356",
				_ -> Thread.ofVirtual().start(() -> {
					alertLabel.setText("");
					checkAndReset(color);
					StatisticsContainerClass.refreshDiskInfoTab();
					AudioOutputDeviceNameChecker.doExecutionSingleton();
				})
		);
	}
	
	private static JButton createWebsiteButton() {
		return BattorionButtonHelper.createButton(
				"Visit Battorion Website",
				IMAGES_FOLDER_PATH, "website.svg",
				_ -> websiteCaller()
		);
	}
	
	private static JButton createThemeButton() {
		JButton button = BattorionButtonHelper.createButton(
				"Switch the theme, right-click to open the context menu",
				THEME_ICON_FOLDER_PATH, Appearance.iconName,
				_ -> {
					Appearance.switchToOtherMode();
					ConfigurationFilesManager.saveGeneralConfigurations();
					rebuild();
				}
		);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = getPopupMenu();
					menu.show(button, 0, button.getHeight());
				}
			}
		});
		return button;
	}
	
	private static JButton createBrightnessButton() {
		JButton button = BattorionButtonHelper.createButton(
				"Change the brightness of the screen", IMAGES_FOLDER_PATH, "brightness", null);
		button.addActionListener(_ -> {
			Brightness.BrightnessProcess(0, true);
			JPopupMenu menu = Brightness.createBrightnessMenu(Brightness.getCurrentBrightness());
			menu.show(button, -100, button.getHeight() + 12);
		});
		return button;
	}
	
	private static JButton createProgressBarModeButton() {
		return BattorionButtonHelper.createButton(
				"Convert to the other mode", IMAGES_FOLDER_PATH, "9213472",
				_ -> {
					progressBarInVerticalMode = !progressBarInVerticalMode;
					clickCount++;
					if (clickCount % 2 == 0) {
						progressBarInFirstMode = !progressBarInFirstMode;
					}
					
					setProgressBarMode();
					setUpProgressPanel(progressBarInFirstMode);
					setVisibleFalse();
					motherPanel.add(DashboardPanel, BorderLayout.CENTER);
					setVisibleTrue(IS_A_DASHBOARD_PANEL);
					motherPanel.repaint();
					motherPanel.revalidate();
					saveGeneralConfigurations();
				}
		);
	}
	
	private static JButton createNotificationModeButton() {
		String mode = isSilentMode? "silent" : "ring_mode";
		String tooltip = isSilentMode
				? "Switch to Ring Mode, to hear sound notifications"
				: "Switch to Silent Mode, to mute all notifications";
		
		JButton silentRingButton = BattorionButtonHelper.createButton(
				tooltip, IMAGES_FOLDER_PATH, mode,
				_ -> {
					isSilentMode = !isSilentMode;
					saveGeneralConfigurations();
		});
		silentRingButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String mode = isSilentMode? "silent" : "ring_mode";
				String tooltip = isSilentMode
						? "Switch to Ring Mode, to hear sound notifications"
						: "Switch to Silent Mode, to mute all notifications";
				
				ImageIcon icon = CallResources.getImage(
						IMAGES_FOLDER_PATH, mode,
						new Dimension(20, 20), Image.SCALE_SMOOTH
				);
				silentRingButton.setIcon(icon);
				silentRingButton.setToolTipText(tooltip);
			}
		});
		return silentRingButton;
	}
	
	private static JButton createNotificationButton() {
		return BattorionButtonHelper.createButton(
				"Learn more about how notifications work", IMAGES_FOLDER_PATH, "9783934",
				_ -> Thread.ofVirtual().start(() -> launchAndOpenTopic(ENABLE_COMPUTER_NOTIFICATIONS, 0))
		);
	}
	
	private static RoundedPanel createRoundedPanel(Color color) {
		return new RoundedPanel(LAYOUT_MANAGER, true, color, RADIUS, THICKNESS, false);
	}
}