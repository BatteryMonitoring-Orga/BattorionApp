package com.battery_level_alarm.monitoring.versions_manager;
import java.io.File;
import java.io.IOException;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseNotifier.releaseLog;

public class InitializationProcess {
	static boolean initializationProcess(java.nio.file.Path releaseFolderPath) {
		String os = System.getProperty("os.name").toLowerCase();
		releaseLog("🚀 Starting initialization process...");
		if (os.contains("win")) {
			File setupFile = releaseFolderPath.resolve("silent-setup-run.bat").toFile();
			if (!setupFile.exists()) {
				releaseLog("❌ File not found: " + setupFile.getAbsolutePath());
				return false;
			}
			releaseLog("📦 Detected Windows OS.");
			releaseLog("⚙️ Executing: " + setupFile.getName());
			releaseLog("\u200B");
			
			try {
				Process process = new ProcessBuilder("cmd", "/c", setupFile.getAbsolutePath())
						.directory(releaseFolderPath.toFile())
						.start();
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					releaseLog("✅ Windows installation completed successfully.");
					return true;
				} else {
					releaseLog("❌ Windows setup exited with code: " + exitCode);
					return false;
				}
			} catch (IOException | InterruptedException e) {
				releaseLog("❌ Exception during Windows setup: " + e.getMessage());
				Thread.currentThread().interrupt();
				return false;
			}
		} else {
			File exeFile = releaseFolderPath.resolve("battorion-4.0.0-silent-setup.exe").toFile();
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
	}
}