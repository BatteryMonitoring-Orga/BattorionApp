package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.GRID_BAG_CONSTRAINTS_CONFIGURATION;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.DepartureModes.START_WITH_APPLICATION;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.DepartureModes.START_WITH_TRAY;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.prefs;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.createGridBagConstraints;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.addCheckbox;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;

public class TraySettingsGUI {
	private static final ScrollConfiguration TRAY_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
			false,
			true,
			true,
			false,
			null,
			new Dimension(600, 350)
	);
	
	static JScrollPane createTraySettingsGUI() {
		JPanel mainTrayPanel = new JPanel(new BorderLayout());
		mainTrayPanel.add(createCheckBoxesPanel(), BorderLayout.NORTH);
		JScrollPane scrollPaneContainer = new JScrollPane(mainTrayPanel);
		applyScrollConfigurationDetails(scrollPaneContainer, TRAY_SET_SCROLL_CONFIGURATION);
		return scrollPaneContainer;
	}
	
	private static JPanel createCheckBoxesPanel() {
		JPanel checkBoxesPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
		
		String modeToUse = prefs.get("StartBattorionWith", String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_APPLICATION));
		boolean isChecked = modeToUse.equals(String.valueOf(START_WITH_TRAY));
		addCheckbox(
				gbc, checkBoxesPanel, "Start with Tray window", isChecked,
				e -> {
					JCheckBox source = (JCheckBox) e.getSource();
					boolean selected = source.isSelected();
					String newMode = selected ? String.valueOf(START_WITH_TRAY) : String.valueOf(START_WITH_APPLICATION);
					prefs.put("StartBattorionWith", newMode);
				}
		);
		return checkBoxesPanel;
	}
}
