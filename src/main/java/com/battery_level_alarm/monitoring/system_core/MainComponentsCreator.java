package com.battery_level_alarm.monitoring.system_core;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.setProgressBarMode;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;

public class MainComponentsCreator {
	static JLabel createLabel(String text, int fontSize, int fontStyle) {
		int allowedStyles = Font.PLAIN | Font.BOLD | Font.ITALIC;
		fontStyle &= allowedStyles;
		JLabel label = new JLabel(text);
		label.setFont(new Font("Serif", fontStyle, fontSize));
		return label;
	}
	
	static void createBatteryBar(Color color) {
		batteryBar = new JProgressBar(0, 100);
		setProgressBarMode();
		batteryBar.setForeground(color);
		batteryBar.setBorder(new LineBorder(Appearance.getBorderColor(), 3));
		batteryBar.setValue(batteryLevel);
		batteryBar.setStringPainted(false);
	}
	
	static JLabel createAlertLabel() {
		JLabel label = new JLabel("");
		label.setFont(new Font("Serif", Font.PLAIN + Font.ITALIC, 16));
		label.setOpaque(true);
		label.setForeground(Color.RED);
		return label;
	}
	
	static JScrollPane createScrollPane(JLabel label) {
		ScrollConfiguration configuration = new ScrollConfiguration(
				false, true, false,
				false, null,
				new Dimension(motherPanel.getWidth() - 100, 50)
		);
		JScrollPane scroll = new JScrollPane(label);
		applyScrollConfigurationDetails(scroll, configuration);
		return scroll;
	}
	
	static JPanel createButtonPanel() {
		JPanel panel = new RoundedPanel(30, new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(110, Integer.MAX_VALUE));
		return panel;
	}
}
