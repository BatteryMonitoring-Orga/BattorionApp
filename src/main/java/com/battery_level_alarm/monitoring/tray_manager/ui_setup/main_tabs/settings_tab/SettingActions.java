package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutput$CMD.setAudioOutputDevice;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.setNotificationSoundFileName;
import static com.battery_level_alarm.monitoring.core_utilities.UserChoices.setMaximumLevel;
import static com.battery_level_alarm.monitoring.core_utilities.UserChoices.setMinimumLevel;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveComputerSettings;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveSettings;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.START_BATTORION_WITH;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.pauseThreads;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.stop;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.TrayIconManager.removeMainTrayIcon;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;

public class SettingActions {
	static class NotificationActions {
		static void updateNotificationSoundFileName(String device) {
			setNotificationSoundFileName(device);
			saveComputerSettings();
		}
		static void updateMinimumLevel(int min) {
			setMinimumLevel(min);
			saveSettings();
		}
		static void updateMaximumLevel(int max) {
			setMaximumLevel(max);
			saveSettings();
		}
	}
	
	static class RadioButtonsActions {
		static void addAudioDeviceAction() {
			boolean isAdded = ComputerSettings.addItemToAudioList(customDeviceField.getText());
			if(isAdded){
				ConfigurationFilesManager.saveComputerSettings();
				if (!audioDeviceSelect.getItems().contains(customDeviceField.getText())) {
					audioDeviceSelect.getItems().add(customDeviceField.getText());
				}
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[0]);
				customDeviceField.setStyle("-fx-text-fill: #009600; -fx-font-size: 14px;");
			} else {
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[1]);
				customDeviceField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
			}
		}
		
		static void deleteAudioDeviceAction() {
			boolean isDeleted = ComputerSettings.removeItemFromAudioList(customDeviceField.getText());
			if (isDeleted) {
				ConfigurationFilesManager.saveComputerSettings();
				if (!audioDeviceSelect.getItems().contains(customDeviceField.getText())) {
					audioDeviceSelect.getItems().remove(customDeviceField.getText());
				}
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[2]);
				customDeviceField.setStyle("-fx-text-fill: #009600; -fx-font-size: 14px;");
			} else {
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[3]);
				customDeviceField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
			}
		}
		
		static void setAsDefaultAOAction() {
			setAudioOutputDevice(customDeviceField.getText());
			currentDeviceLabel.setText("Current Device:   " + customDeviceField.getText());
			customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[4]);
			customDeviceField.setStyle("-fx-text-fill: #009600; -fx-font-size: 14px;");
		}
	}
	
	public static class AppInterface {
		public static void displayAppInterface() {
			try {
				boolean isTrayIconRemoved = removeMainTrayIcon();
				if (!isTrayIconRemoved) {
					logger.severe("[TrayIconManager] ERROR: Unable to remove system tray icon.");
				}
				
				primaryStage.hide();
				pauseThreads();
				stop();
			} catch (Exception ex) {
				printErrorMessage(ex);
			}
			
			try {
				prefs.put(START_BATTORION_WITH, String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_APPLICATION));
				isApplicationMode = true;
				build();
			} catch (Exception e) {
				printErrorMessage(e);
			}
		}
	}
}
