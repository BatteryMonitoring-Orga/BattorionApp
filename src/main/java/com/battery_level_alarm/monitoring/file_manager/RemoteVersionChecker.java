package com.battery_level_alarm.monitoring.file_manager;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.setPreviousVersion;
import static com.battery_level_alarm.monitoring.core_utilities.VersionReader.version;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveUpdateVersionConfigurations;
import static com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader.unzip;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;
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
		
		try {
			Path zipPath = Paths.get(MAIN_FOLDER_PATH, repoName + ".zip");
			try (InputStream in = URI.create(zipUrl).toURL().openStream()) {
				Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
			}
			Path extractedFolder = Paths.get(MAIN_FOLDER_PATH, repoName);
			unzip(zipPath.toString());
			
			Path configPath = findConfigFile(extractedFolder);
			if (configPath == null) {
				JOptionPane.showMessageDialog(null, "config.enc file not found in extracted directory.");
				return;
			}
			
			latestVersion = version(configPath.toString());
			Files.deleteIfExists(zipPath);
			deleteDirectory(extractedFolder);
			if(!latestVersion.equalsIgnoreCase(APP_VERSION)) {
				thereIsNewVersion = true;
				setPreviousVersion(APP_VERSION);
				saveUpdateVersionConfigurations();
			}
		} catch (IOException e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		}
	}
	
	private static Path findConfigFile(Path root) throws IOException {
		try (Stream<Path> stream = Files.walk(root)) {
			return stream
					.filter(path -> path.getFileName().toString().equalsIgnoreCase("config.enc"))
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
}