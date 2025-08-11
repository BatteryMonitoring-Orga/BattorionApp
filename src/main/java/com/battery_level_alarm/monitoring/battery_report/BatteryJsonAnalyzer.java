package com.battery_level_alarm.monitoring.battery_report;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.EXPORT_HARDWARE_MONITOR_FILE;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class BatteryJsonAnalyzer {
	private static final String JSON_FILE_PATH = CONFIGURATIONS_MAIN_FOLDER_PATH + "/hardware-monitor.json";
	private static final String POWER_SHELL_SCRIPT = CONFIGURATIONS_MAIN_FOLDER_PATH + "\\" + EXPORT_HARDWARE_MONITOR_FILE;
	
	public static int runPowerShellScript() {
		try {
			Process process = getProcess();
			return process.waitFor();
		} catch (Exception e) {
			printErrorMessage(e);
			return -1;
		}
	}
	
	private static @NotNull Process getProcess() throws IOException {
		String workingDir = CONFIGURATIONS_MAIN_FOLDER_PATH;
		String psCommand =
				"Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass; " +
						"cd '" + workingDir + "'; " +
						". '" + POWER_SHELL_SCRIPT + "'";
		
		ProcessBuilder builder = new ProcessBuilder(
				"powershell.exe",
				"-NoProfile",
				"-ExecutionPolicy", "Bypass",
				"-Command", psCommand
		);
		
		builder.directory(new File(workingDir));
		builder.redirectErrorStream(true);
		return builder.start();
	}
	
	public static Map<String, String> extractBatteryInfo() {
		int exitCode = runPowerShellScript();
		if (exitCode != 0) {
			Map<String, String> map = new LinkedHashMap<>();
			map.put("ERROR", "PowerShell script failed with exit code: " + exitCode);
			return map;
		}
		
		Map<String, String> cpuTemps = new LinkedHashMap<>();
		Map<String, String> tjMaxTemps = new LinkedHashMap<>();
		Map<String, String> gpuTemps = new LinkedHashMap<>();
		Map<String, String> powerMetrics = new LinkedHashMap<>();
		Map<String, String> degradationMetric = new LinkedHashMap<>();
		Map<String, String> spareMetric = new LinkedHashMap<>();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new File(JSON_FILE_PATH));
			
			if (root.has("Sensors")) {
				for (JsonNode sensor : root.get("Sensors")) {
					String type = sensor.get("Type").asText("");
					String name = sensor.get("Name").asText("");
					String value = sensor.get("Value").asText("");
					String nameLower = name.toLowerCase();
					
					if (type.equalsIgnoreCase("Temperature")) {
						if (nameLower.contains("cpu package")) {
							cpuTemps.put("CPU Package Temperature", value);
						} else if (nameLower.matches("cpu core #[1-9]+$")) {
							cpuTemps.put(name + " Temperature", value);
						} else if (nameLower.contains("distance to tjmax")) {
							tjMaxTemps.put(name.replaceAll("Distance to TjMax", "(to TjMax)"), value);
						} else if (nameLower.contains("gpu core")) {
							gpuTemps.put("GPU Core Temperature", value);
						} else if (nameLower.contains("hot spot")) {
							gpuTemps.put("GPU Hot Spot Temperature", value);
						}
					} else if (type.equalsIgnoreCase("Power")) {
						if (nameLower.contains("gpu power")) {
							powerMetrics.put("Power - GPU", value);
						} else if (nameLower.contains("discharge rate")) {
							powerMetrics.put("Power - Discharge Rate", value);
						} else if (nameLower.contains("charge rate")) {
							powerMetrics.put("Power - Charge Rate", value);
						}
					}
					
					if (nameLower.contains("degradation")) {
						degradationMetric.put("Degradation", value);
					} else if (nameLower.contains("available spare")) {
						spareMetric.put("Available Spare", value);
					}
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		
		Map<String, String> finalMap = new LinkedHashMap<>();
		finalMap.putAll(cpuTemps);
		finalMap.putAll(tjMaxTemps);
		finalMap.putAll(gpuTemps);
		finalMap.putAll(powerMetrics);
		finalMap.putAll(degradationMetric);
		finalMap.putAll(spareMetric);
		return finalMap;
	}
}