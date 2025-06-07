package com.battery_level_alarm.monitoring.tray_manager.tray_executors;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import java.io.*;

public class AutoStartManager {
	public static void enableAutoStart(String appName, String targetPath) {
		try {
			appName = appName.replaceAll("[\\\\/:*?\"<>|]", "_");
			String startupPath = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
			String shortcutPath = startupPath + "\\" + appName + ".lnk";
			
			String psCommand =
					"$WshShell = New-Object -ComObject WScript.Shell;" +
							"$Shortcut = $WshShell.CreateShortcut('" + shortcutPath.replace("\\", "\\\\") + "');" +
							"$Shortcut.TargetPath = '" + targetPath.replace("\\", "\\\\") + "';" +
							"$Shortcut.Save();";
			
			File tempScript = File.createTempFile("createShortcut", ".ps1");
			try (FileWriter writer = new FileWriter(tempScript)) {
				writer.write(psCommand);
			}
			
			ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", tempScript.getAbsolutePath());
			pb.inheritIO();
			Process process = pb.start();
			process.waitFor();
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static void disableAutoStart(String appName) {
		try {
			String startupPath = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
			File shortcut = new File(startupPath, appName + ".lnk");
			if (shortcut.exists()) {
				shortcut.delete();
				System.out.println("Startup shortcut removed.");
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static boolean isAutoStartEnabled(String appName) {
		String startupPath = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
		File shortcut = new File(startupPath, appName + ".lnk");
		return shortcut.exists();
	}
}
