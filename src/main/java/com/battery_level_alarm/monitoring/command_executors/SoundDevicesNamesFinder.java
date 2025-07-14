package com.battery_level_alarm.monitoring.command_executors;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import java.io.*;
import java.util.*;

public class SoundDevicesNamesFinder {
	private static final String OUTPUT_FILE = CONFIGURATIONS_MAIN_FOLDER_PATH + "/output.csv";
	private static final String SOUND_VOLUME_VIEW_PATH = CONFIGURATIONS_MAIN_FOLDER_PATH + "/SoundVolumeView-main/soundvolumeview-x64";
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
	
	private static final Set<String> knownOutputKeywords = new HashSet<>(Arrays.asList(
			"Speakers", "Headphones", "Headset", "Earphones", "USB Audio", "USB",
			"سماعات", "سماعة", "سماعة رأس", "مكبر"
	));
	
	public static String findFirstDefaultValidRenderDevice() {
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
			printErrorMessage(e);
		} finally {
			try {
				Thread.sleep(5000);
				deleteOutputFile();
			} catch (Exception ex) {
				printErrorMessage(ex);
			}
		}
		return null;
	}
	
	public static List<String> extractAudioOutputFamilies() {
		Set<String> families = new LinkedHashSet<>();
		try {
			runSoundVolumeProcess();
			try (BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_FILE))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(",");
					if (parts.length < 4) continue;
					
					String type = parts[1].trim();
					String direction = parts[2].trim();
					if (!type.equalsIgnoreCase("Device") || !direction.equalsIgnoreCase("Render")) continue;
					
					String name = parts[0].trim();
					String deviceName = parts[3].trim();
					String candidate = extractMatchingKeyword(name);
					if (candidate == null) {
						candidate = extractMatchingKeyword(deviceName);
					} if (candidate != null) {
						families.add(candidate);
					}
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
		} finally {
			deleteOutputFile();
		}
		return new ArrayList<>(families);
	}
	
	private static String extractMatchingKeyword(String text) {
		for (String keyword : SoundDevicesNamesFinder.knownOutputKeywords) {
			if (text.toLowerCase().contains(keyword.toLowerCase())) {
				return keyword;
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
				return;
			} if (file.delete()) {
				return;
			}
			
			File renamed = new File(file.getAbsolutePath() + ".tmp");
			if (file.renameTo(renamed)) {
				if (renamed.delete()) {
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
	}
}