package com.battery_level_alarm.monitoring.tray_manager.tray_executors;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutput$CMD.setAudioOutputDevice;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.setEnableSystemNotificationSound;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.setNotificationSoundFileName;
import static com.battery_level_alarm.monitoring.core_utilities.UserChoices.setMaximumLevel;
import static com.battery_level_alarm.monitoring.core_utilities.UserChoices.setMinimumLevel;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveComputerSettings;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveSettings;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.Monitor.alarmSoundsBackgroundProcess;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.SettingsTab.*;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;

public class SettingActions {
	public static class NotificationActions {
		public static void updateNotificationSoundFileName(String device, int index) {
			alarmSoundsBackgroundProcess.setSoundSequenceNumber(index + 1);
			setNotificationSoundFileName(device);
			saveComputerSettings();
		}
		public static void updateMinimumLevel(int min) {
			setMinimumLevel(min);
			saveSettings();
		}
		public static void updateMaximumLevel(int max) {
			setMaximumLevel(max);
			saveSettings();
		}
	}
	
	public static class RadioButtonsActions {
		public static void addAudioDeviceAction() {
			boolean isAdded = ComputerSettings.addItemToAudioList(customDeviceField.getText());
			if(isAdded){
				ConfigurationFilesManager.saveComputerSettings();
				if (!audioDeviceSelect.getItems().contains(customDeviceField.getText())) {
					audioDeviceSelect.getItems().add(customDeviceField.getText());
				}
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[0]);
				customDeviceField.setStyle("-fx-text-fill: #009600;");
			} else {
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[1]);
				customDeviceField.setStyle("-fx-text-fill: red;");
			}
		}
		
		public static void deleteAudioDeviceAction() {
			boolean isDeleted = ComputerSettings.removeItemFromAudioList(customDeviceField.getText());
			if (isDeleted) {
				ConfigurationFilesManager.saveComputerSettings();
				if (!audioDeviceSelect.getItems().contains(customDeviceField.getText())) {
					audioDeviceSelect.getItems().remove(customDeviceField.getText());
				}
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[2]);
				customDeviceField.setStyle("-fx-text-fill: #009600;");
			} else {
				customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[3]);
				customDeviceField.setStyle("-fx-text-fill: red;");
			}
		}
		
		public static void setAsDefaultAOAction() {
			setAudioOutputDevice(customDeviceField.getText());
			currentDeviceLabel.setText("Current Device:   " + customDeviceField.getText());
			customDeviceField.setText(DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS[4]);
			customDeviceField.setStyle("-fx-text-fill: #009600;");
		}
	}
}
