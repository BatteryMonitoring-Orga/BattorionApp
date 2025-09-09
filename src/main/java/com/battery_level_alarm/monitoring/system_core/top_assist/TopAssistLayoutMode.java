package com.battery_level_alarm.monitoring.system_core.top_assist;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.setLayoutModeID;
import static com.battery_level_alarm.monitoring.core_utilities.LayoutMode.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.top_assist.InitializeTopAssistPanel.assistMenu;
import static com.battery_level_alarm.monitoring.system_core.top_assist.InitializeTopAssistPanel.rebuildTopAssistContainer;

public class TopAssistLayoutMode {
	public static class ControlButton {
		public static final String UPDATE_ARROW = "change.png";
		public static final String TO_RIGHT_ARROW = "to_right.png";
		public static final String TO_LEFT_ARROW = "to_left.png";
		
		public static final String[] TOOLTIPS = new String[] {
				"Switch to left layout",
				"Switch to center layout",
				"Switch to right layout",
				"Switch to popup layout",
				"Toggle layout mode"
		};
		
		public static JButton controlButton(
				ActionListener actionListener, String iconName, String tooltip
		) {
			return BattorionButtonHelper.createButton(tooltip, ASSETS_FOLDER_PATH, iconName, actionListener);
		}
	}
	
	public static class Actions {
		public static final ActionListener[] LAYOUT_ACTIONS = new ActionListener[5];
		
		public static void buildLayoutActions() {
			LAYOUT_ACTIONS[0] = _ -> {
				setLayoutModeID(LEFT_MODE.getId());
				Thread.ofVirtual().start(ConfigurationFilesManager::saveGeneralConfigurations);
				rebuildTopAssistContainer();
				if(assistMenu != null) assistMenu.setVisible(false);
			};
			LAYOUT_ACTIONS[1] = _ -> {
				setLayoutModeID(CENTER_MODE.getId());
				Thread.ofVirtual().start(ConfigurationFilesManager::saveGeneralConfigurations);
				rebuildTopAssistContainer();
				if(assistMenu != null) assistMenu.setVisible(false);
			};
			LAYOUT_ACTIONS[2] = _ -> {
				setLayoutModeID(RIGHT_MODE.getId());
				Thread.ofVirtual().start(ConfigurationFilesManager::saveGeneralConfigurations);
				rebuildTopAssistContainer();
				if(assistMenu != null) assistMenu.setVisible(false);
			};
			LAYOUT_ACTIONS[3] = _ -> {
				setLayoutModeID(POPUP_MODE.getId());
				Thread.ofVirtual().start(ConfigurationFilesManager::saveGeneralConfigurations);
				rebuildTopAssistContainer();
				if(assistMenu != null) assistMenu.setVisible(false);
			};
			LAYOUT_ACTIONS[4] = _ -> {
				setLayoutModeID(TOGGLE_MODE.getId());
				Thread.ofVirtual().start(ConfigurationFilesManager::saveGeneralConfigurations);
				rebuildTopAssistContainer();
				if(assistMenu != null) assistMenu.setVisible(false);
			};
		}
	}
}