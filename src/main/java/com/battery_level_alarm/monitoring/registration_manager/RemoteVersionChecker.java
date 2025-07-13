package com.battery_level_alarm.monitoring.registration_manager;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.setPreviousVersion;
import static com.battery_level_alarm.monitoring.core_utilities.VersionReader.version;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveUpdateVersionConfigurations;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.unzip;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.LATEST_RELEASE_NOTES_MD;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.RELEASE_NOTES_MD;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_VERSION;

public class RemoteVersionChecker {
	public static String latestVersion = null;
	public static boolean thereIsNewVersion = false;
	private static final String[][] REPOSITORIES = {
			{"battorion-version-main", "https://github.com/MuathHassoun/battorion-version/archive/refs/heads/main.zip"}
	};
	
	public static void checkForVersionUpdates() {
		String repoName = REPOSITORIES[0][0];
		String zipUrl = REPOSITORIES[0][1];
		Path zipPath = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName + ".zip");
		Path extractedFolder = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName);
		
		try {
			try (InputStream in = URI.create(zipUrl).toURL().openStream()) {
				Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
			}
			unzip(zipPath.toString());
			
			Path configPath = findConfigFile(extractedFolder, "config.enc");
			if (configPath == null) {
				return;
			}
			
			latestVersion = version(configPath.toString());
			if(!latestVersion.equalsIgnoreCase(APP_VERSION)) {
				thereIsNewVersion = true;
				setPreviousVersion(APP_VERSION);
				saveUpdateVersionConfigurations();
			}
		} catch (IOException e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		} finally {
			try {
				Files.deleteIfExists(zipPath);
			} catch (IOException e) {
				logger.warning("Could not delete zip file: " + e.getMessage());
			}
			
			try {
				deleteDirectory(extractedFolder);
			} catch (IOException e) {
				logger.warning("Could not delete extracted folder: " + e.getMessage());
			}
		}
	}
	
	private static Path findConfigFile(Path root, String fileName) throws IOException {
		try (Stream<Path> stream = Files.walk(root)) {
			return stream
					.filter(path -> path.getFileName().toString().equalsIgnoreCase(fileName))
					.findFirst()
					.orElse(null);
		}
	}
	
	private static void deleteDirectory(Path path) throws IOException {
		if (Files.exists(path)) {
			try (Stream<Path> stream = Files.walk(path)) {
				stream
					.sorted(Comparator.reverseOrder())
					.forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							logger.severe("[EXCEPTION]: " + e.getMessage());
						}
					});
			}
		}
	}
	
	public static boolean ensureReleaseNotesExists() {
		Path targetFile = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, RELEASE_NOTES_MD);
		return Files.exists(targetFile);
	}
	
	public static void installCurrentReleaseNotesFile() {
		String repoName = REPOSITORIES[0][0];
		Path zipPath = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName + ".zip");
		Path extractedFolder = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName);
		
		try {
			Path releaseFile = installReleaseNotes();
			if (releaseFile == null) {
				return;
			}
			
			Path targetFile = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, releaseFile.getFileName().toString());
			Files.copy(releaseFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		} finally {
			try {
				Files.deleteIfExists(zipPath);
			} catch (IOException e) {
				logger.warning("Could not delete zip file: " + e.getMessage());
			}
			
			try {
				deleteDirectory(extractedFolder);
			} catch (IOException e) {
				logger.warning("Could not delete extracted folder: " + e.getMessage());
			}
		}
	}
	
	public static boolean installLatestReleaseNotesFile() {
		String repoName = REPOSITORIES[0][0];
		Path zipPath = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName + ".zip");
		Path extractedFolder = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName);
		
		try {
			Path releaseFile = installReleaseNotes();
			if (releaseFile == null) {
				return false;
			}
			
			Path targetFile = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, LATEST_RELEASE_NOTES_MD);
			Files.copy(releaseFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		} finally {
			try {
				Files.deleteIfExists(zipPath);
			} catch (IOException e) {
				logger.warning("Could not delete zip file: " + e.getMessage());
			}
			
			try {
				deleteDirectory(extractedFolder);
			} catch (IOException e) {
				logger.warning("Could not delete extracted folder: " + e.getMessage());
			}
		}
		return false;
	}
	
	private static Path installReleaseNotes() {
		String repoName = REPOSITORIES[0][0];
		String zipUrl = REPOSITORIES[0][1];
		Path zipPath = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName + ".zip");
		Path extractedFolder = Paths.get(CONFIGURATIONS_MAIN_FOLDER_PATH, repoName);
		
		try {
			try (InputStream in = URI.create(zipUrl).toURL().openStream()) {
				Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
			}
			unzip(zipPath.toString());
			return findConfigFile(extractedFolder, RELEASE_NOTES_MD);
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
			return null;
		}
	}
}