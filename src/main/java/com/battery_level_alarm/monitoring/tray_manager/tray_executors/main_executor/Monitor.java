package com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.core_utilities.UserChoices.getAlertBeforeRiskPhaseBy;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.BATTERY_REPORT_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.AS_SYSTEM;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.monitorSystemTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.stopSystemThemeMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.currentDeviceLabel;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.primaryStage;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.DashboardTab.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.StatisticsTab.*;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.getAudioOutputDevice;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryLevel;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryStatus;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.*;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.battery_report.BatteryLiveInfoReader;
import com.battery_level_alarm.monitoring.battery_report.BatteryReportAnalyzer;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.NotificationToast;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.BatteryIconWindow;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.NotificationPopup;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.visual_effects.AlertSound;
import com.notifications.system_tray_notifications.influence.PlaySounds;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import java.util.concurrent.*;

public class Monitor {
	private static ScheduledExecutorService MAIN_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);
	private static ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
	private static ScheduledFuture<?> monitorTask;
	
	private static final Object pauseLock = new Object();
	public static Color batteryLevelColor;
	private static String msg = "";
	
	private static volatile boolean isCharging;
	private static volatile boolean isAlive = false;
	private static volatile boolean paused = false;
	private static volatile boolean alert = false;
	private static volatile boolean lastVisibility = false;
	private static volatile boolean forceDiskUpdate = false;
	private static volatile boolean isApplicationStarted = true;
	private static volatile boolean isBatterEstimatedTimeThreadLive = false;
	public static volatile boolean isShouldUpdateTrayDashboard = true;
	public static volatile boolean isToastNotifyEnabled = true;
	public static volatile int chargeLevel;
	
	private static boolean isEnableDevelopedSystemNotificationSound;
	private static boolean isBatteryTrayNestedIconAllowToAdd;
	private static boolean isEnableToastNotification;
	private static boolean callFlag = false;
	private static boolean isAlertInProgress = true;
	public static boolean isAlertPopupShown = false;
	public static boolean changeFlag = false;
	
	private static boolean lastCharging = false;
	private static long lastDiskUpdateTime = 0;
	private static int lastCharge = -1;
	
	public static void backgroundProcessMonitoring(String theme) {
		if (!isAlive) {
			start();
			if(prefs.getBoolean(TOAST_NOTIFICATION_ENABLE, true)) {
				msg = "👋 Welcome! Battery Monitor is running.";
				NotificationToast.showNotification(msg, primaryStage, true);
			} if(theme.equalsIgnoreCase(AS_SYSTEM.toString())) {
				monitorSystemTheme();
			}
		}
	}
	
	private static void start() {
		isAlive = true;
		fetchUserPreferences();
		if (MAIN_EXECUTOR_SERVICE.isShutdown() || MAIN_EXECUTOR_SERVICE.isTerminated()) {
			MAIN_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
		} if (EXECUTOR_SERVICE.isShutdown() || EXECUTOR_SERVICE.isTerminated()) {
			EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);
		} if (VIRTUAL_EXECUTOR.isShutdown() || VIRTUAL_EXECUTOR.isTerminated()) {
			VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
		}
		
		startMonitorLoop(primaryStage.isShowing());
		MAIN_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			boolean currentVisibility = primaryStage != null && primaryStage.isShowing();
			if (currentVisibility != lastVisibility) {
				lastVisibility = currentVisibility;
				restartMonitorLoop(currentVisibility);
			}
		}, 0, 500, TimeUnit.MILLISECONDS);
	}
	
	private static void startMonitorLoop(boolean isVisible) {
		String speedMode = prefs.get(UPDATE_FREQUENCY, BattorionTrayUI.UpdateSpeed.MEDIUM.name());
		long durationMs = BattorionTrayUI.UpdateSpeed.valueOf(speedMode).getIntervalMs();
		long interval = isVisible || isApplicationStarted ? 500 : durationMs;
		forceDiskUpdate = interval <= 1000;
		
		monitorTask = MAIN_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			if (!isAlive) return;
			waitIfPaused();
			if (changeFlag) {
				fetchUserPreferences();
				changeFlag = false;
			}
			
			try {
				updateBatteryStatus();
				evaluateAlertConditions();
				handleNotifications();
				updateUI();
				if (isBatteryTrayNestedIconAllowToAdd) {
					updateTrayIcon();
				}
				isApplicationStarted = false;
			} catch (Exception e) {
				printErrorMessage(e);
			}
		}, 0, interval, TimeUnit.MILLISECONDS);
	}
	
	public static void restartMonitorLoop(boolean isVisible) {
		if (monitorTask != null && !monitorTask.isCancelled()) {
			try {
				monitorTask.cancel(true);
			} catch (Exception e) {
				printErrorMessage(e);
			}
		}
		startMonitorLoop(isVisible);
	}
	
	private static void fetchUserPreferences() {
		isEnableDevelopedSystemNotificationSound = prefs.getBoolean(TRAY_NOTIFICATION_ENABLE, true);
		isEnableToastNotification = prefs.getBoolean(TOAST_NOTIFICATION_ENABLE, true);
		isBatteryTrayNestedIconAllowToAdd = prefs.getBoolean(SHOW_BATTERY_ICON, false);
	}
	
	private static void updateBatteryStatus() {
		try {
			boolean previousCharging = isCharging;
			isCharging = getBatteryStatus();
			chargeLevel = (int) getBatteryLevel();
			int maxValue = UserChoices.getMaximumLevel();
			int minValue = UserChoices.getMinimumLevel();
			batteryLevelColor = getBatteryColor(chargeLevel, minValue, maxValue);
			
			if(isCharging != previousCharging) {
				isToastNotifyEnabled = true;
			}
		} catch (Exception e) {
			printErrorMessage(e);
			isCharging = false;
			chargeLevel = 0;
		}
	}
	
	private static void evaluateAlertConditions() {
		int maxValue = UserChoices.getMaximumLevel();
		int minValue = UserChoices.getMinimumLevel();
		int phase = getAlertBeforeRiskPhaseBy();
		
		alert = true;
		if((chargeLevel >= maxValue) && isCharging) {
			msg = "Battery is too high! Please unplug the charger...";
		} else if((chargeLevel <= minValue) && !isCharging) {
			msg = "Battery is too low! Please plug the charger...";
		} else if(isEnableToastNotification && (chargeLevel >= maxValue - phase) && isCharging && isToastNotifyEnabled) {
			msg = "Notice: Battery level is getting high (" + chargeLevel + "%)";
			Platform.setImplicitExit(false);
			Platform.runLater(() -> NotificationToast.showNotification(msg, primaryStage, false));
			alert = false;
		} else if(isEnableToastNotification && (chargeLevel <= minValue + phase) && !isCharging && isToastNotifyEnabled) {
			msg = "Notice: Battery level is getting low (" + chargeLevel + "%)";
			Platform.setImplicitExit(false);
			Platform.runLater(() -> NotificationToast.showNotification(msg, primaryStage, false));
			alert = false;
		} else {
			alert = false;
		}
	}
	
	private static void handleNotifications() {
		if (isEnableDevelopedSystemNotificationSound && alert) {
			try {
				VIRTUAL_EXECUTOR.submit(() -> organizationOfRecallProcess(msg));
			} catch (Exception e) {
				printErrorMessage(e);
			}
		}
	}
	
	private static void updateUI() {
		String[] audioDevice;
		boolean shouldUpdateBatteryUI = chargeLevel != lastCharge || isCharging != lastCharging;
		if (shouldUpdateBatteryUI || isShouldUpdateTrayDashboard || isApplicationStarted) {
			lastCharge = chargeLevel;
			lastCharging = isCharging;
			audioDevice = getAudioOutputDevice();
		} else {
			audioDevice = null;
		}
		
		Platform.runLater(() -> {
			if (shouldUpdateBatteryUI || isShouldUpdateTrayDashboard || isApplicationStarted) {
				progressBar.setProgress(chargeLevel / 100.0);
				progressBar.setStyle("-fx-accent: #" + colorToHex(batteryLevelColor) + ";");
				batteryStatus.setText(isCharging ? "Charging" : "Discharging");
				batteryLevel.setText(chargeLevel + "%");
				String device = (audioDevice == null) ? audioOutput.getText() : audioDevice[1];
				
				audioOutput.setText(device);
				currentDeviceLabel.setText("Current Device:   " + device);
				batteryMonitoring.setText("active.");
				isShouldUpdateTrayDashboard = false;
				
				if(!isBatterEstimatedTimeThreadLive) {
					isBatterEstimatedTimeThreadLive = true;
					Thread.ofVirtual().start(() -> {
						BatteryReportAnalyzer.analyze(BATTERY_REPORT_PATH);
						BatteryLiveInfoReader.getBatteryInfoAsMap();
						Platform.runLater(() -> batteryEstimatedTime.setText(BatteryInfo.getEstimatedTimeRemaining()));
						isBatterEstimatedTimeThreadLive = false;
					});
				}
			}
			
			long now = System.currentTimeMillis();
			if (!forceDiskUpdate || (now - lastDiskUpdateTime >= 300000) || isApplicationStarted) {
				lastDiskUpdateTime = now;
				Thread.ofVirtual().start(() -> {
					DiskSpaceInfo.DiskSpace();
					String filesNum = DiskSpaceInfo.getFilesNumber();
					String dirNum = DiskSpaceInfo.getDirNumber();
					String filesSize = DiskSpaceInfo.getFilesSize();
					String dirSize = DiskSpaceInfo.getDirSize();
					
					Platform.runLater(() -> {
						tempFiles.setText(filesNum);
						folders.setText(dirNum);
						tempSize.setText(filesSize);
						diskSpace.setText(dirSize);
					});
				});
			}
		});
	}
	
	private static void updateTrayIcon() {
		try {
			Thread.ofVirtual().start(() -> BatteryIconWindow.show(chargeLevel / 100.0, isCharging));
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	private static void waitIfPaused() {
		synchronized (pauseLock) {
			while (paused) {
				try {
					pauseLock.wait();
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					printErrorMessage(ex);
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
		pauseThreads();
		MAIN_EXECUTOR_SERVICE.schedule(Monitor::resumeThreads, 5, TimeUnit.SECONDS);
	}
	
	public static void stop() {
		isAlive = false;
		resumeThreads();
		EXECUTOR_SERVICE.shutdownNow();
		MAIN_EXECUTOR_SERVICE.shutdownNow();
		VIRTUAL_EXECUTOR.shutdownNow();
		stopSystemThemeMonitoring();
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
		String fallbackMessage = "Please check your battery status!";
		callNotifierWithFallback(msg, fallbackMessage);
	}
	
	private static void callNotifierWithFallback(String primaryMsg, String fallbackMsg) {
		if (callFlag) return;
		callFlag = true;
		isAlertPopupShown = false;
		
		try {
			triggerAudioAlert(primaryMsg);
			EXECUTOR_SERVICE.schedule(() -> Platform.runLater(() -> {
				if (!isAlertPopupShown) {
					triggerAudioAlert(fallbackMsg);
				}
				callFlag = false;
			}), 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static void triggerAudioAlert(String msg) {
		isAlertPopupShown = true;
		Platform.setImplicitExit(false);
		Platform.runLater(() -> new NotificationPopup(
				"Battorion Alert", msg,
				"/com/battery_level_alarm/monitoring/images/13228401.png",
				"/com/battery_level_alarm/monitoring/images/alert_stn.png",
				5000
		).show());
		
		if(isAlertInProgress) {
			try {
				isAlertInProgress = false;
				setupAudioSettingsBeforeAlert();
				PlaySounds.playSound(getNotificationSoundFileName());
				
				Thread.sleep(4500);
				AlertSound.cleanupAudioSettingsAfterAlert();
				isAlertInProgress = true;
			} catch (Exception e) {
				printErrorMessage(e);
			}
		}
	}
}
