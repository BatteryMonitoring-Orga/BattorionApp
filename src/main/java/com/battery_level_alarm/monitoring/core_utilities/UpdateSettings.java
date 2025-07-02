package com.battery_level_alarm.monitoring.core_utilities;

public class UpdateSettings {
	private static boolean checkForUpdatesAutomatically;
	private static boolean downloadUpdatesAutomatically;
	private static boolean notifyBeforeInstalling;
	private static boolean autoRestartAfterUpdate;
	private static String previousVersion;
	
	public static boolean isCheckForUpdatesAutomatically() {
		return checkForUpdatesAutomatically;
	}
	
	public static void setCheckForUpdatesAutomatically(boolean checkForUpdatesAutomatically) {
		UpdateSettings.checkForUpdatesAutomatically = checkForUpdatesAutomatically;
	}
	
	public static boolean isDownloadUpdatesAutomatically() {
		return downloadUpdatesAutomatically;
	}
	
	public static void setDownloadUpdatesAutomatically(boolean downloadUpdatesAutomatically) {
		UpdateSettings.downloadUpdatesAutomatically = downloadUpdatesAutomatically;
	}
	
	public static boolean isNotifyBeforeInstalling() {
		return notifyBeforeInstalling;
	}
	
	public static void setNotifyBeforeInstalling(boolean notifyBeforeInstalling) {
		UpdateSettings.notifyBeforeInstalling = notifyBeforeInstalling;
	}
	
	public static boolean isAutoRestartAfterUpdate() {
		return autoRestartAfterUpdate;
	}
	
	public static void setAutoRestartAfterUpdate(boolean autoRestartAfterUpdate) {
		UpdateSettings.autoRestartAfterUpdate = autoRestartAfterUpdate;
	}
	
	public static String getPreviousVersion() {
		return previousVersion;
	}
	
	public static void setPreviousVersion(String previousVersion) {
		UpdateSettings.previousVersion = previousVersion;
	}
}