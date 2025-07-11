package com.battery_level_alarm.monitoring.versions_manager;
import java.io.File;
import java.io.IOException;

import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.NEW_RELEASE;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.restartApplication;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseNotifier.releaseLog;

public class InitializationProcess {
	private static final String SILENT_SETUP_RUN_FILE = "silent-setup-run.bat";
	static boolean initializationProcess(java.nio.file.Path releaseFolderPath) {
		String os = System.getProperty("os.name").toLowerCase();
		releaseLog("🚀 Starting initialization process...");
		if (os.contains("win")) {
			File setupFile = releaseFolderPath.resolve(SILENT_SETUP_RUN_FILE).toFile();
			if (!setupFile.exists()) {
				releaseLog("❌ File not found: " + setupFile.getAbsolutePath());
				return false;
			}
			releaseLog("📦 Detected Windows OS.");
			releaseLog("⚙️ Executing: " + setupFile.getName());
			releaseLog("\u200B");
			
			try {
				prefs.put(NEW_RELEASE, String.valueOf(true));
				Process process = new ProcessBuilder("cmd", "/c", setupFile.getAbsolutePath())
						.directory(releaseFolderPath.toFile())
						.start();
				process.waitFor();
				restartApplication();
			} catch (IOException | InterruptedException e) {
				releaseLog("❌ Exception during Windows setup: " + e.getMessage());
				Thread.currentThread().interrupt();
				return false;
			}
		} else {
			File exeFile = releaseFolderPath.resolve("battorion-" + latestVersion + "-silent-setup.exe").toFile();
			if (!exeFile.exists()) {
				releaseLog("❌ File not found: " + exeFile.getAbsolutePath());
				return false;
			}
			releaseLog("🖥️ Non-Windows OS detected.");
			releaseLog("⚙️ Executing EXE directly: " + exeFile.getName());
			releaseLog("\u200B");
			
			try {
				Process process = new ProcessBuilder(
						"wine",
						exeFile.getAbsolutePath(),
						"/VERYSILENT",
						"/NORESTART"
				).directory(releaseFolderPath.toFile()).start();
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					releaseLog("✅ Silent setup completed via Wine.");
					return true;
				} else {
					releaseLog("❌ Silent setup via Wine exited with code: " + exitCode);
					return false;
				}
			} catch (IOException | InterruptedException e) {
				releaseLog("❌ Exception during Wine execution: " + e.getMessage());
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}
}