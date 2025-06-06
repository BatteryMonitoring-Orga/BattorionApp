package com.battery_level_alarm.monitoring.command_executors;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import java.io.*;
import java.util.*;

public class DefaultSoundDeviceNameFinder {
	private static final String OUTPUT_FILE = MAIN_FOLDER_PATH + "/output.csv";
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
			printErrorMessage(e);
		}
		deleteOutputFile();
		return null;
	}
	
	private static void runSoundVolumeProcess() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(
				"SoundVolumeView.exe",
				"/scomma",
				OUTPUT_FILE
		);
		pb.start().waitFor();
	}
	
	private static void deleteOutputFile() {
		try {
			File file = new File(OUTPUT_FILE);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
}