package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import com.battery_level_alarm.monitoring.battery_report.BatteryLiveInfoReader;
import com.battery_level_alarm.monitoring.battery_report.BatteryReportAnalyzer;
import com.battery_level_alarm.monitoring.battery_report.ChooseActionPanel;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static com.battery_level_alarm.monitoring.battery_report.BatteryDataOrganizer.groupBatteryData;
import static com.battery_level_alarm.monitoring.battery_report.BatteryJsonAnalyzer.extractBatteryInfo;
import static com.battery_level_alarm.monitoring.battery_report.ChooseActionPanel.reanalyzeButton;
import static com.battery_level_alarm.monitoring.core_utilities.BatteryInfo.*;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.LIFE_REPORT_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.BATTERY_REPORT_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.HYPERLINK_HOVER_COLOR;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.addSeparator;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabelWithMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.DEFAULT_FONT;

public class LifeReportPanelUI {
	private static Map<String, Map<String, String>> liveInfo;
	private static Map<String, String> batteryData;
	
	public static Map<String, Map<String, String>> updateBatteryLiveInfo() {
		BatteryReportAnalyzer.analyze(BATTERY_REPORT_PATH);
		liveInfo = BatteryLiveInfoReader.getBatteryInfoAsMap();
		batteryData = extractBatteryInfo();
		estimatedRemainingTime.setText("Battery Life Estimate: " + BatteryInfo.getEstimatedTimeRemaining());
		return groupBatteryData(liveInfo, batteryData);
	}
	
	public static JPanel lifeReportPanel() {
		JPanel lifeReportPanel = new JPanel(new BorderLayout());
		lifeReportPanel.add(ChooseActionPanel.create(), BorderLayout.SOUTH);
		Map<String, Map<String, String>> grouped = groupBatteryData(liveInfo, batteryData);
		if(grouped == null) {
			lifeReportPanel.add(createEmptyMSGPanel(), BorderLayout.CENTER);
			reanalyzeButton.setEnabled(false);
		} else {
			lifeReportPanel.add(createLifeReportPanel(grouped), BorderLayout.CENTER);
			reanalyzeButton.setEnabled(true);
		}
		return lifeReportPanel;
	}
	
	public static void refreshReportPanel(boolean switchToLifeReportPanel) {
		Map<String, Map<String, String>> grouped = updateBatteryLiveInfo();
		if(grouped == null) {
			return;
		} if (LifeReportPanel == null) {
			LifeReportPanel = new JPanel(new BorderLayout());
		} if(reanalyzeButton != null) {
			reanalyzeButton.setEnabled(true);
		}
		
		LifeReportPanel.removeAll();
		LifeReportPanel.add(ChooseActionPanel.create(), BorderLayout.SOUTH);
		LifeReportPanel.add(createLifeReportPanel(grouped), BorderLayout.CENTER);
		LifeReportPanel.revalidate();
		LifeReportPanel.repaint();
		if (switchToLifeReportPanel) {
			motherPanel.add(LifeReportPanel, BorderLayout.CENTER);
			refreshMotherFrame();
		}
	}
	
	private static JScrollPane createLifeReportPanel(Map<String, Map<String, String>> grouped) {
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		JLabel titleLabel = new JLabel("🔋 Battery Health Report", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		titlePanel.add(titleLabel);
		
		setDimension(0, 0);
		addLabelWithMouseListener(
				new GridBagConstraints(), aboutPanel, "About Life Report Panel ", HYPERLINK_HOVER_COLOR,
				() -> Thread.ofVirtual().start(() -> launchAndOpenTopic(LIFE_REPORT_QUESTIONNAIRE, 0)), DEFAULT_FONT
		);
		
		headerPanel.add(titlePanel);
		headerPanel.add(aboutPanel);
		mainPanel.add(headerPanel, BorderLayout.NORTH);
		
		JPanel valuesPanel = new JPanel(new GridBagLayout());
		valuesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 30, 6, 30);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;
		
		Font keyFont = new Font("Serif", Font.PLAIN, 16);
		Font valueFont = new Font("Serif", Font.BOLD, 16);
		
		addKeyValue(valuesPanel, "Designed Capacity", getDesignedCapacity() + " mWh", gbc, keyFont, valueFont);
		addKeyValue(valuesPanel, "Full Charge Capacity", getFullChargeCapacity() + " mWh", gbc, keyFont, valueFont);
		addKeyValue(valuesPanel, "Battery Health",
				String.format("%.2f%%", getHealthPercentage()) + "  (" + evaluateHealthStatus() + ")",
				gbc, keyFont, valueFont);
		
		int separatorRow = gbc.gridy;
		addSeparatorLine(separatorRow, valuesPanel);
		gbc.gridy = separatorRow + 1;
		
		for (Map.Entry<String, Map<String, String>> group : grouped.entrySet()) {
			String sectionTitle = group.getKey();
			Map<String, String> sectionValues = group.getValue();
			
			addSubTitle(valuesPanel, sectionTitle, gbc);
			for (Map.Entry<String, String> entry : sectionValues.entrySet()) {
				addKeyValue(valuesPanel, entry.getKey(), entry.getValue(), gbc, keyFont, valueFont);
			}
		}
		mainPanel.add(valuesPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}
	
	private static void addSubTitle(JPanel panel, String key, GridBagConstraints gbc) {
		JLabel sectionLabel = new JLabel(key);
		sectionLabel.setFont(new Font("Serif", Font.BOLD, 18));
		if(!key.contains("Basic")) {
			JLabel label = new JLabel(" ");
			panel.add(label, gbc);
			gbc.gridy++;
		}
		
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		panel.add(sectionLabel, gbc);
		gbc.gridy++;
		gbc.gridwidth = 1;
	}
	
	private static void addKeyValue(JPanel panel, String key, String value,
	                                GridBagConstraints gbc, Font keyFont, Font valueFont) {
		JLabel keyLabel = new JLabel(key + ":");
		keyLabel.setFont(keyFont);
		JLabel valueLabel = new JLabel(value);
		valueLabel.setFont(valueFont);
		
		gbc.gridx = 0;
		panel.add(keyLabel, gbc);
		gbc.gridx = 1;
		panel.add(valueLabel, gbc);
		gbc.gridy++;
	}
	
	private static void addSeparatorLine(int separatorRow, JPanel valuesPanel) {
		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridwidth = GridBagConstraints.REMAINDER;
		separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		separatorConstraints.insets = new Insets(15, 0, 15, 0);
		setDimension(separatorRow, 0);
		addSeparator(separatorConstraints, valuesPanel, 200);
	}
	
	private static JPanel createEmptyMSGPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
		
		JLabel titleLabel = new JLabel("🔋 Battery Health Report");
		titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setForeground(Color.GRAY);
		
		JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>"
				+ "No data available to display right now.<br>"
				+ "This may happen because the program has just started.<br>"
				+ "Please wait up to 1 minute for the program to analyze the data."
				+ "</div></html>");
		infoLabel.setFont(new Font("Serif", Font.PLAIN, 16));
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setForeground(Color.DARK_GRAY);
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(titleLabel, gbc);
		gbc.gridy++;
		panel.add(infoLabel, gbc);
		return panel;
	}
}