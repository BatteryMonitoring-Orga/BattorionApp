package com.battery_level_alarm.monitoring.command_executors;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;

import java.io.*;
import java.util.*;

public class DefaultSoundDeviceNameFinder {
	private static final String OUTPUT_FILE = MAIN_FOLDER_PATH + "/output.csv";
	private static final String SOUND_VOLUME_VIEW_PATH = MAIN_FOLDER_PATH + "/SoundVolumeView-main/soundvolumeview-x64";
	
	private static final List<String> VALID_DEVICE_NAMES = Arrays.asList(
			"Realtek Audio",
			"Realtek(R) Audio",
			"Jabra Speaker",
			"Creative Sound Blaster",
			"ASUS Xonar",
			"Behringer UMC22",
			"Focusrite Scarlett",
			"Universal Audio Apollo",
			"PreSonus AudioBox",
			"RME Babyface / Fireface",
			"Motu Audio Device"
	);
	
	public static String findFirstValidRenderDevice() {
		try {
			runSoundVolumeProcess();
			try (BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_FILE))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(",");
					if (parts.length < 4) continue;
					
					String name = parts[0].trim();
					String type = parts[1].trim();
					String direction = parts[2].trim();
					String deviceName = parts[3].trim();
					
					for (String valid : VALID_DEVICE_NAMES) {
						if (deviceName.toLowerCase().contains(valid.toLowerCase())
								&& type.equalsIgnoreCase("Device")
								&& direction.equalsIgnoreCase("Render")) {
							deleteOutputFile();
							return name;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		} finally {
			try {
				Thread.sleep(5000);
				deleteOutputFile();
			} catch (Exception ex) {
				logger.severe("[EXCEPTION]: " + ex.getMessage());
			}
		}
		return null;
	}
	
	private static void runSoundVolumeProcess() throws IOException, InterruptedException {
		String soundVolumeViewPath = SOUND_VOLUME_VIEW_PATH + "/SoundVolumeView.exe";
		java.io.File exeFile = new java.io.File(soundVolumeViewPath);
		if (!exeFile.exists()) {
			throw new UnsupportedOperationException("File not found: " + soundVolumeViewPath);
		}
		
		ProcessBuilder pb = new ProcessBuilder(
				soundVolumeViewPath,
				"/scomma",
				OUTPUT_FILE
		);
		
		pb.redirectErrorStream(true);
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			throw new RuntimeException("SoundVolumeView process failed with exit code: " + exitCode);
		}
	}
	
	private static void deleteOutputFile() {
		File file = new File(OUTPUT_FILE);
		for (int i = 0; i < 5; i++) {
			if (!file.exists()) {
				logger.info("[INFO]: output.csv not found, no need to delete");
				return;
			} if (file.delete()) {
				logger.info("[INFO]: ✔ output.csv deleted");
				return;
			}
			
			File renamed = new File(file.getAbsolutePath() + ".tmp");
			if (file.renameTo(renamed)) {
				if (renamed.delete()) {
					logger.info("[INFO]: ✔ Renamed and deleted output.csv.tmp");
					return;
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		file.deleteOnExit();
		logger.severe("[ERROR]: ❌ Failed to delete output.csv after retries, will try on JVM exit");
	}
}