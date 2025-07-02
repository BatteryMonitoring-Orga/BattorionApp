package com.battery_level_alarm.monitoring.versions_manager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isAutoRestartAfterUpdate;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isNotifyBeforeInstalling;
import static com.battery_level_alarm.monitoring.core_utilities.VersionReader.version;
import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.UpdateSettingsGUI.downloadButtonName;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.UpdateSettingsGUI.downloadUpdateButton;
import static com.battery_level_alarm.monitoring.versions_manager.InitializationProcess.initializationProcess;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseNotifier.releaseLog;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseNotifier.showAllLogs;
import static com.battery_level_alarm.monitoring.versions_manager.VersionInstaller.*;

public class ReleaseManager {
	public static boolean isReleaseInstallProcessRunning = false;
	private static boolean isManagerStarted = false;
	private static boolean hasSucceeded = false;
	
	public static void releaseManager() {
		if(isNotifyBeforeInstalling()) {
			showFXAlert(
					"Installation Notice", 350, 150,
					"You're about to start downloading and installing the latest release.\n\nDo you want to continue?",
					Map.of(
							ButtonType.CANCEL, () -> {
								releaseLog("🚫 Installation cancelled by user.");
								if(downloadUpdateButton != null) {
									downloadUpdateButton.setText(downloadButtonName);
									downloadUpdateButton.setEnabled(true);
								}
							},
							ButtonType.OK, ReleaseManager::startInstallationProcess
					),
					ButtonType.OK,
					ButtonType.CANCEL
			);
		} else {
			startInstallationProcess();
		}
	}
	
	private static void startInstallationProcess() {
		if(isManagerStarted) {
			return;
		}
		
		Thread.ofVirtual().start(() -> {
			isManagerStarted = true;
			try {
				releaseLog("🚀 Launching release installation task...");
				isReleaseInstallProcessRunning = true;
				hasSucceeded = versionInstaller(new ProgressBar(0));
				if (!hasSucceeded) {
					releaseLog("⚠️ Installation process was not successful. Displaying logs...");
					showAllLogs("Release Tracker Log");
					return;
				}
				
				releaseLog("\u200B");
				releaseLog("⏳ Waiting for background processes to finish...");
				isReleaseInstallProcessRunning = false;
				Thread.sleep(2_000);
				
				isReleaseInstallProcessRunning = true;
				releaseFolderPath = Paths.get(MAIN_FOLDER_PATH + "/" + RELEASE_FOLDER + latestVersion);
				hasSucceeded = initializationProcess(releaseFolderPath);
				if (!hasSucceeded) {
					isReleaseInstallProcessRunning = false;
					releaseLog("⚠️ Initialization process was not successful. Displaying logs...");
					showAllLogs("Release Tracker Log");
					return;
				}
				
				if(isAutoRestartAfterUpdate()) {
					restartApplication();
				} else {
					if(downloadUpdateButton != null) {
						downloadUpdateButton.setText(downloadButtonName);
						downloadUpdateButton.setEnabled(true);
					}
				}
			} catch (Exception ex) {
				Thread.currentThread().interrupt();
				releaseLog("❌ Unexpected exception in release manager thread: " + ex.getMessage());
				logger.severe("[EXCEPTION]: " + ex.getMessage());
				isReleaseInstallProcessRunning = false;
			}
		});
	}
	
	public static void cleanupAfterInstallation() {
		releaseLog("🧹 Starting cleanup after installation...");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(MAIN_FOLDER_PATH),
				entry -> Files.isDirectory(entry) && entry.getFileName().toString().startsWith(RELEASE_FOLDER))) {
			for (Path existing : stream) {
				try (DirectoryStream<Path> contents = Files.newDirectoryStream(existing)) {
					for (Path file : contents) {
						deleteDirectoryRecursively(file);
					}
				}
				break;
			}
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		}
	}
	
	public static void restartApplication() {
		try {
			prefs.put("new-release", String.valueOf(true));
			releaseLog("✅ Initialization done. Restarting app...");
			String pf64 = System.getenv("ProgramW6432");
			Path rootFolder = Paths.get(pf64, "Battorion");
			
			if (!Files.exists(rootFolder)) {
				String pf86 = System.getenv("ProgramFiles(x86)");
				rootFolder = Paths.get(pf86, "Battorion");
			} if (!Files.exists(rootFolder)) {
				releaseLog("❌ Battorion.exe not found in standard installation paths.");
				isReleaseInstallProcessRunning = false;
				return;
			}
			
			Path configPath = rootFolder.resolve("config.enc");
			String installedVersion = version(configPath.toAbsolutePath().toString());
			if(installedVersion.equalsIgnoreCase(latestVersion)) {
				Path exePath = rootFolder.resolve("Battorion.exe");
				releaseLog("🔄 Restarting Battorion from: " + exePath);
				releaseLog("✅ Release manager task completed.");
				showAllLogs("Release Tracker Log");
				new ProcessBuilder(exePath.toString()).start();
				System.exit(0);
			} else {
				releaseLog("⚠️ Installation might have failed or user cancelled permission prompt.");
				isReleaseInstallProcessRunning = false;
			}
		} catch (Exception e) {
			releaseLog("❌ Failed to restart Battorion.exe: " + e.getMessage());
			isReleaseInstallProcessRunning = false;
		}
	}
	
	public static void showFXAlert(
			String title, int width, int height, String message,
			Map<ButtonType, Runnable> actions, ButtonType... buttons
	) {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.NONE);
			alert.setResizable(false);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.getButtonTypes().setAll(buttons);
			alert.getDialogPane().setPrefSize(width, height);
			
			Optional<ButtonType> result = alert.showAndWait();
			result.ifPresent(button -> {
				Runnable action = actions.get(button);
				if (action != null) {
					action.run();
				}
			});
		});
	}
}