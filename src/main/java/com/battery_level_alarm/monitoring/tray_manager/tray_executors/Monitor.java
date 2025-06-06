package com.battery_level_alarm.monitoring.tray_manager.tray_executors;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.DashboardTab.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.StatisticsTab.*;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.getAudioOutputDevice;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryLevel;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryStatus;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.isEnableSystemNotificationSound;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_NAME;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.IMAGES_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javax.swing.Timer;

public class Monitor {
	public static AlarmSounds alarmSoundsBackgroundProcess;
	private static SystemTrayNotification stn;
	private static Notifications notify;
	
	private static Color batteryLevelColor;
	private static final int duration = 1000;
	private static int chargeLevel;
	
	private static boolean isCharging;
	private static volatile boolean isAlive = false;
	private static volatile boolean paused = false;
	private static final Object pauseLock = new Object();
	
	private static boolean isUpdatersStarted = false;
	private static boolean callFlag = false;
	
	public static void backgroundProcessMonitoring() {
		if (!isAlive) {
			configurationSystemTrayNotifications();
			start();
		}
	}
	
	private static void configurationSystemTrayNotifications() {
		notify = new Notifications(
				APP_NAME,
				IMAGES_FOLDER_PATH + "/13228401.png",
				"Battery Reminder",
				"Battery is in risk!",
				duration,
				false
		);
		stn = new SystemTrayNotification();
		
		int sequence = AlarmSounds.getIndexBySoundName(getNotificationSoundFileName());
		alarmSoundsBackgroundProcess = new AlarmSounds(sequence);
	}
	
	private static void start() {
		isAlive = true;
		Thread monitorThread = new Thread(() -> {
			while (isAlive) {
				waitIfPaused();
				
				int maxValue = UserChoices.getMaximumLevel();
				int minValue = UserChoices.getMinimumLevel();
				
				try {
					isCharging = getBatteryStatus();
					chargeLevel = getBatteryLevel();
					batteryLevelColor = getBatteryColor(chargeLevel, minValue, maxValue);
				} catch (Exception e) {
					isCharging = false;
					chargeLevel = 0;
				}
				
				String msg = "";
				boolean alert = true;
				if ((chargeLevel >= maxValue) && isCharging) {
					msg = "Battery is too high! Please unplug the charger...";
				} else if ((chargeLevel == (maxValue - 1)) && isCharging) {
					msg = "Battery is high! Please unplug the charger...";
				} else if ((chargeLevel <= minValue) && !isCharging) {
					msg = "Battery is too low! Please plug the charger...";
				} else {
					alert = false;
				}
				
				if (isEnableSystemNotificationSound() && alert) {
					organizationOfRecallProcess(msg);
				} else if (!isUpdatersStarted) {
					isUpdatersStarted = true;
					dashboardValuesUpdater();
					statisticsValuesUpdater();
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					printErrorMessage(ex);
				}
			}
		});
		monitorThread.start();
	}
	
	private static void dashboardValuesUpdater() {
		Thread dashboardUpdater = new Thread(() -> {
			while (isAlive) {
				waitIfPaused();
				
				String[] audioDevice = getAudioOutputDevice();
				Platform.setImplicitExit(false);
				Platform.runLater(() -> {
					progressBar.setProgress(chargeLevel / 100.0);
					progressBar.setStyle("-fx-accent: #" + colorToHex(batteryLevelColor) + ";");
					
					batteryStatus.setText(isCharging ? "Charging" : "Discharging");
					batteryLevel.setText(chargeLevel + "%");
					audioOutput.setText(audioDevice[1]);
					batteryMonitoring.setText("active.");
				});
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					printErrorMessage(ex);
				}
			}
		});
		dashboardUpdater.start();
	}
	
	private static void statisticsValuesUpdater() {
		Thread statisticsUpdater = new Thread(() -> {
			while (isAlive) {
				waitIfPaused();
				
				DiskSpaceInfo.DiskSpace();
				Platform.setImplicitExit(false);
				Platform.runLater(() -> {
					tempFiles.setText(DiskSpaceInfo.getFilesNumber());
					folders.setText(DiskSpaceInfo.getDirNumber());
					tempSize.setText(DiskSpaceInfo.getFilesSize());
					diskSpace.setText(DiskSpaceInfo.getDirSize());
				});
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					printErrorMessage(ex);
				}
			}
		});
		statisticsUpdater.start();
	}
	
	private static void waitIfPaused() {
		synchronized (pauseLock) {
			while (paused) {
				try {
					pauseLock.wait();
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	public static void pauseThreads() {
		paused = true;
	}
	
	public static void resumeThreads() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}
	
	public static void pushAndResume() {
		try {
			Monitor.pauseThreads();
			Thread.sleep(5000);
			Monitor.resumeThreads();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public static void stop() {
		isAlive = false;
		resumeThreads();
	}
	
	public static String colorToHex(Color color) {
		int red = (int) Math.round(color.getRed() * 255);
		int green = (int) Math.round(color.getGreen() * 255);
		int blue = (int) Math.round(color.getBlue() * 255);
		
		return String.format("%02X%02X%02X", red, green, blue);
	}
	
	private static Color getBatteryColor(int charge, int min, int max) {
		if (isCharging) {
			return Color.CYAN;
		} else if (charge >= (max - 1)) {
			return Color.DARKGRAY;
		} else if (charge > min) {
			if (charge > 60) {
				return Color.rgb(0, 140, 0);
			} else if (charge > 30) {
				return Color.rgb(202, 88, 25);
			} else {
				return Color.RED;
			}
		} else {
			return Color.RED;
		}
	}
	
	private static void organizationOfRecallProcess(String msg) {
		if (callFlag) {
			return;
		}
		callFlag = true;
		callNotifier(msg);
		
		Timer organizer = new Timer(
				5000,
				_ -> callFlag = false
		);
		organizer.setRepeats(false);
		organizer.start();
	}
	
	private static void callNotifier(String msg) {
		notify.setAlarmMessage(msg + "\nBattery level is: " + chargeLevel);
		stn.setIsToShowPanel(false);
		stn.CreateTrayIcon(notify, alarmSoundsBackgroundProcess);
	}
}
