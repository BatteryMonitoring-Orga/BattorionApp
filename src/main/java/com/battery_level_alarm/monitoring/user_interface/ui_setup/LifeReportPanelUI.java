package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import com.battery_level_alarm.monitoring.battery_report.BatteryLiveInfoReader;
import com.battery_level_alarm.monitoring.battery_report.BatteryReportAnalyzer;
import com.battery_level_alarm.monitoring.battery_report.ChooseActionPanel;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

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
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.TWO_SPACE;

public class LifeReportPanelUI {
	public static Map<String, String> updateBatteryLiveInfo() {
		BatteryReportAnalyzer.analyze(BATTERY_REPORT_PATH);
		Map<String, String> liveInfo = BatteryLiveInfoReader.getBatteryInfoAsMap();
		estimatedRemainingTime.setText("Battery Life Estimate: " + BatteryInfo.getEstimatedTimeRemaining());
		return liveInfo;
	}
	
	public static JPanel lifeReportPanel() {
		Map<String, String> liveInfo = updateBatteryLiveInfo();
		JPanel lifeReportPanel = new JPanel(new BorderLayout());
		lifeReportPanel.add(ChooseActionPanel.create(), BorderLayout.SOUTH);
		lifeReportPanel.add(createLifeReportPanel(liveInfo), BorderLayout.CENTER);
		return lifeReportPanel;
	}
	
	public static void refreshReportPanel(boolean switchToLifeReportPanel) {
		Map<String, String> liveInfo = updateBatteryLiveInfo();
		if (LifeReportPanel == null) {
			LifeReportPanel = new JPanel(new BorderLayout());
		}
		
		LifeReportPanel.removeAll();
		LifeReportPanel.add(ChooseActionPanel.create(), BorderLayout.SOUTH);
		LifeReportPanel.add(createLifeReportPanel(liveInfo), BorderLayout.CENTER);
		LifeReportPanel.revalidate();
		LifeReportPanel.repaint();
		if (switchToLifeReportPanel) {
			motherPanel.add(LifeReportPanel, BorderLayout.CENTER);
			refreshMotherFrame();
		}
	}
	
	private static JScrollPane createLifeReportPanel(Map<String, String> liveInfo) {
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
		addKeyValue(valuesPanel, "Battery Health", String.format("%.2f %%", getHealthPercentage()), gbc, keyFont, valueFont);
		
		int separatorRow = gbc.gridy;
		addSeparatorLine(separatorRow, valuesPanel);
		gbc.gridy = separatorRow + 1;
		
		for (Map.Entry<String, String> entry : liveInfo.entrySet()) {
			addKeyValue(valuesPanel, entry.getKey(), entry.getValue(), gbc, keyFont, valueFont);
		}
		mainPanel.add(valuesPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
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
}