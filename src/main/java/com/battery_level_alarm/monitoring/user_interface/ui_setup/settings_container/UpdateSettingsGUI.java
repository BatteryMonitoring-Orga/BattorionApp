package com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container;
import com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.*;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveUpdateVersionConfigurations;
import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.*;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocExternalFilesLoader.loadMarkdownAsHtml;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.*;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.GRID_BAG_CONSTRAINTS_CONFIGURATION;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.HYPERLINK_HOVER_COLOR;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.isWaitingForInternet;
import static com.battery_level_alarm.monitoring.system_core.Battorion.DashboardPanel;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_VERSION;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.updateUIBasedOnVersion;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setButtonBackgroundColor;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.dashboardButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.createGridBagConstraints;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.addCheckbox;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabelWithMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.DEFAULT_FONT;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.releaseManager;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance.isAfterNoon;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeNames.*;

public class UpdateSettingsGUI {
	private static final ScrollConfiguration UPDATE_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
			false, true, true, false, null, new Dimension(600, 350)
	);
	
	private static final GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
	public static final String downloadButtonName = "⬇ Download Update";
	public static JButton downloadUpdateButton;
	private static String latestReleaseVersion;
	private static String updateStatus;
	
	static JScrollPane createUpdateSettingsGUI() {
		JLabel statusLabel = createLabel("Update Status: " + updateStatus, new Font("Serif", Font.BOLD, 18));
		JLabel currentVersionLabel = createLabel("Current Version: " + APP_VERSION, new Font("Serif", Font.BOLD, 15));
		JLabel latestVersionLabel = createLabel("", new Font("Serif", Font.BOLD, 15));
		updateValues(statusLabel, latestVersionLabel);
		JLabel aboutUpdatePanel = addLabelWithMouseListener(
				gbc, new JPanel(), "About Update Panel", HYPERLINK_HOVER_COLOR,
				() -> Thread.ofVirtual().start(() -> launchAndOpenTopic(SETTINGS_QUESTIONNAIRE, 1500)), DEFAULT_FONT
		);
		
		JPanel headerPanel = createHeaderPanel(statusLabel, currentVersionLabel, latestVersionLabel, aboutUpdatePanel);
		JPanel contentPanel = createContentPanel();
		JPanel footerPanel = createFooterPanel(statusLabel, latestVersionLabel);
		
		JPanel combinedHBoxPanel = new JPanel();
		combinedHBoxPanel.setLayout(new BoxLayout(combinedHBoxPanel, BoxLayout.X_AXIS));
		combinedHBoxPanel.setOpaque(false);
		combinedHBoxPanel.add(Box.createHorizontalStrut(10));
		combinedHBoxPanel.add(headerPanel);
		combinedHBoxPanel.add(Box.createHorizontalStrut(30));
		combinedHBoxPanel.add(contentPanel);
		combinedHBoxPanel.add(Box.createHorizontalStrut(10));
		
		JPanel combinedPanel = new JPanel();
		combinedPanel.setLayout(new BoxLayout(combinedPanel, BoxLayout.Y_AXIS));
		combinedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		combinedPanel.setBackground(returnPanelBackgroundColor());
		combinedPanel.add(combinedHBoxPanel);
		combinedPanel.add(Box.createVerticalStrut(24));
		combinedPanel.add(footerPanel);
		
		JScrollPane scrollPane = new JScrollPane(combinedPanel);
		applyScrollConfigurationDetails(scrollPane, UPDATE_SET_SCROLL_CONFIGURATION);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(returnPanelBackgroundColor());
		return scrollPane;
	}
	
	private static JPanel createHeaderPanel(JLabel status, JLabel current, JLabel latest, JLabel about) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setOpaque(false);
		panel.add(Box.createVerticalStrut(10));
		panel.add(setLeftAlign(status));
		panel.add(Box.createVerticalStrut(5));
		panel.add(setLeftAlign(current));
		panel.add(Box.createVerticalStrut(5));
		panel.add(setLeftAlign(latest));
		panel.add(Box.createVerticalStrut(5));
		panel.add(setLeftAlign(about));
		panel.add(Box.createVerticalStrut(10));
		return panel;
	}
	
	private static JPanel createContentPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setOpaque(false);
		panel.add(Box.createVerticalStrut(10));
		panel.add(setLeftAlign(addCheckbox(gbc, new JPanel(), "Check for updates automatically", isCheckForUpdatesAutomatically(), e -> {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			setCheckForUpdatesAutomatically(checkBox.isSelected());
			saveUpdateVersionConfigurations();
		})));
		
		panel.add(Box.createVerticalStrut(5));
		panel.add(setLeftAlign(addCheckbox(gbc, new JPanel(), "Download updates automatically", isDownloadUpdatesAutomatically(), e -> {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			setDownloadUpdatesAutomatically(checkBox.isSelected());
			saveUpdateVersionConfigurations();
		})));
		
		panel.add(Box.createVerticalStrut(5));
		panel.add(setLeftAlign(addCheckbox(gbc, new JPanel(), "Notify before installing", isNotifyBeforeInstalling(), e -> {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			setNotifyBeforeInstalling(checkBox.isSelected());
			saveUpdateVersionConfigurations();
		})));
		
		panel.add(Box.createVerticalStrut(10));
		return panel;
	}
	
	private static JPanel createFooterPanel(JLabel status, JLabel latest) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setOpaque(false);
		
		Dimension btnSize = new Dimension(350, 35);
		panel.add(Box.createVerticalStrut(7));
		panel.add(createButtonPanel("Check for Updates Now", btnSize, _ -> {
			updateValues(status, latest);
			isWaitingForInternet = false;
			updateUIBasedOnVersion();
			setButtonBackgroundColor();
			dashboardButton.setBackground(DARK_BLUE);
		}));
		panel.add(createButtonPanel("View Latest Release Notes", btnSize, _ -> {
			checkForVersionUpdates();
			if(thereIsNewVersion) {
				Thread.ofVirtual().start(() -> {
					boolean isTrue = installLatestReleaseNotesFile();
					if(isTrue) {
						MiniDocTopicsBuilder.latestReleaseNote = loadMarkdownAsHtml(LATEST_RELEASE_NOTES_MD);
						if (TOPICS.isEmpty()) {
							buildTopicsMap();
						}
						TOPICS.put(LATEST_RELEASE_NEW, MiniDocTopicsBuilder::getLatestReleaseMarkdownText);
						launchAndOpenTopic(LATEST_RELEASE_NEW, 0);
					}
				});
			} else {
				Thread.ofVirtual().start(() -> launchAndOpenTopic(WHATS_NEW, 0));
			}
		}));
		panel.add(createButtonPanel(downloadButtonName, btnSize, _ -> releaseManager()));
		panel.add(createButtonPanel("Rollback to Previous Version", btnSize, _ -> {
			latestVersion = getPreviousVersion();
			releaseManager();
		}));
		panel.add(Box.createVerticalStrut(7));
		return panel;
	}
	
	private static JPanel createButtonPanel(String text, Dimension size, ActionListener action) {
		JButton button = new JButton(text);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(size);
		button.setMaximumSize(size);
		button.setMinimumSize(size);
		button.setFont(DEFAULT_FONT);
		button.setMargin(new Insets(2, 16, 2, 16));
		button.addActionListener(action);
		if(button.getText().equalsIgnoreCase(downloadButtonName)) {
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					button.setText("Downloading...");
					button.setEnabled(false);
				}
			});
			downloadUpdateButton = button;
		}
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		panel.setOpaque(false);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(button);
		return panel;
	}
	
	private static JLabel createLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}
	
	private static void updateValues(JLabel status, JLabel latest) {
		Thread.ofVirtual().start(() -> {
			checkForVersionUpdates();
			updateStatus = thereIsNewVersion ? "Update Available" : "Up to Date";
			latestReleaseVersion = latestVersion;
			latest.setText("Latest Available: " + latestReleaseVersion);
			status.setText("Update Status: " + updateStatus);
		});
	}
	
	private static JComponent setLeftAlign(JComponent component) {
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		return component;
	}
	
	private static Color returnPanelBackgroundColor() {
		return switch (Appearance.getThemeName()) {
			case DARK -> Color.BLACK;
			case LIGHT -> Color.WHITE;
			case TIME_BASED_FIRST_OPTION, TIME_BASED_SECOND_OPTION -> {
				if (isAfterNoon(true, null)) yield DashboardPanel.getBackground();
				else yield Color.WHITE;
			}
			default -> {
				if (isAfterNoon(true, null)) yield Color.BLACK;
				else yield Color.WHITE;
			}
		};
	}
}