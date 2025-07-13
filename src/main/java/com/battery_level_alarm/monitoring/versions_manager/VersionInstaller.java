package com.battery_level_alarm.monitoring.versions_manager;
import com.battery_level_alarm.monitoring.versions_manager.update_ui.FileDownloaderWithProgress;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseNotifier.releaseLog;

public class VersionInstaller {
	private static final String[] LINKS = {
			"https://github.com/MuathHassoun/battorion-version/releases/download/sv0.0.0/silent-setup-run.bat",
			"https://github.com/MuathHassoun/battorion-version/releases/download/sv0.0.0/_battorion-0.0.0-silent-setup.exe"
	};
	static final String RELEASE_FOLDER = "release_v";
	static Path releaseFolderPath;
	static String MSG;
	
	static boolean versionInstaller(ProgressBar progressBar) {
		try {
			releaseLog("ℹ️ Starting release installation process...");
			releaseLog("\u200B");
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH),
					entry -> Files.isDirectory(entry) && entry.getFileName().toString().startsWith(RELEASE_FOLDER))) {
				for (Path existing : stream) {
					try (DirectoryStream<Path> contents = Files.newDirectoryStream(existing)) {
						for (Path file : contents) {
							deleteDirectoryRecursively(file);
						}
					}
					releaseFolderPath = existing;
					releaseLog("♻️ Reusing existing release folder: " + existing.getFileName());
					break;
				}
			}
			
			if (releaseFolderPath == null) {
				releaseFolderPath = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, RELEASE_FOLDER);
				Files.createDirectories(releaseFolderPath);
				releaseLog("✅ Created new release directory: " + releaseFolderPath.getFileName());
			} if (latestVersion == null || latestVersion.isBlank()) {
				MSG = "⚠️ latestVersion is null or blank. Aborting installation.";
				releaseLog(MSG);
				logger.severe(MSG);
				return false;
			}
			releaseLog("\u200B");
			releaseLog("ℹ️ Using release version: " + latestVersion);
			
			if (!isInternetAvailable()) {
				MSG = "❌ Internet connection unavailable. Please check your network.";
				releaseLog(MSG);
				logger.severe(MSG);
				return false;
			}
			releaseLog("✅ Internet connection verified.");
			releaseLog("\u200B");
			releaseLog("ℹ️ Starting to download release files...");
			for (String link : LINKS) {
				String updatedUrl = link.replace("0.0.0", latestVersion);
				String fileName = updatedUrl.substring(updatedUrl.lastIndexOf('/') + 1);
				Path filePath = releaseFolderPath.resolve(fileName);
				
				FileDownloaderWithProgress downloader = new FileDownloaderWithProgress(progressBar);
				boolean success = downloader.downloadToFile(updatedUrl, filePath.toFile());
				if (success) {
					releaseLog("✅ Downloaded: " + fileName);
				} else {
					releaseLog("❌ Failed to download with progress: " + fileName);
				}
			}
			releaseLog("ℹ️ Moving release folder to versioned name...");
			Path newFolderName = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH + "/" + RELEASE_FOLDER + latestVersion);
			Files.move(releaseFolderPath, newFolderName, StandardCopyOption.REPLACE_EXISTING);
			
			releaseLog("✅ Files moved to: " + RELEASE_FOLDER + latestVersion);
			releaseLog("✅ Folder Path: " + releaseFolderPath.toAbsolutePath());
			releaseLog("🎉 Release installation completed successfully.");
			return true;
		} catch (IOException e) {
			MSG = "❌ Installation failed: " + e.getMessage();
			releaseLog(MSG);
			logger.severe("[EXCEPTION]: " + e.getMessage());
			return false;
		}
	}
	
	static void deleteDirectoryRecursively(Path path) {
		try {
			if (Files.exists(path)) {
				try (Stream<Path> walk = Files.walk(path)) {
					walk.sorted(Comparator.reverseOrder())
						.forEach(p -> {
							try {
								Files.deleteIfExists(p);
								releaseLog("🗑️ Deleted: " + p.getFileName());
							} catch (IOException e) {
								releaseLog("⚠️ Failed to delete: " + p.getFileName() + " → " + e.getMessage());
							}
						});
				}
				releaseLog("✅ Successfully cleared directory: " + path.getFileName());
			} else {
				releaseLog("ℹ️ Directory does not exist: " + path.getFileName());
			}
		} catch (Exception e) {
			releaseLog("❌ Exception during directory cleanup: " + e.getMessage());
			logger.severe("[EXCEPTION]: " + e.getMessage());
		}
	}
}
