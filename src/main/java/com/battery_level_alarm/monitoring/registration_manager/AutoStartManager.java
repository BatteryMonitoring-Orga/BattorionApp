package com.battery_level_alarm.monitoring.registration_manager;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class AutoStartManager {
	public static void enableAutoStart(String appName, String targetPath) {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"reg",
					"add",
					"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
					"/v",
					appName,
					"/t",
					"REG_SZ",
					"/d",
					"\"" + targetPath + "\"",
					"/f"
			);
			pb.inheritIO();
			Process p = pb.start();
			p.waitFor();
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static void disableAutoStart(String appName) {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"reg",
					"delete",
					"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
					"/v",
					appName,
					"/f"
			);
			pb.inheritIO();
			Process p = pb.start();
			p.waitFor();
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static boolean isAutoStartEnabled(String appName) {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"reg",
					"query",
					"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
					"/v",
					appName
			);
			Process p = pb.start();
			int exitCode = p.waitFor();
			return exitCode == 0;
		} catch (Exception e) {
			printErrorMessage(e);
			return false;
		}
	}
}