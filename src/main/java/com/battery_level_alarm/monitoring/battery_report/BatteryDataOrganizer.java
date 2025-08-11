package com.battery_level_alarm.monitoring.battery_report;
import java.util.LinkedHashMap;
import java.util.Map;

public class BatteryDataOrganizer {
	public static Map<String, Map<String, String>> groupBatteryData(
			Map<String, Map<String, String>> liveInfo, Map<String, String> batteryData) {
		if (liveInfo == null || liveInfo.isEmpty()) {
			return null;
		} else if (batteryData == null || batteryData.isEmpty()) {
			return null;
		}
		
		Map<String, Map<String, String>> grouped = new LinkedHashMap<>();
		String[] categories = {
				"Basic Info",
				"CPU Temperatures",
				"GPU Temperatures",
				"Power Metrics",
				"Degradation",
				"Spare",
				"Other"
		};
		
		for (String category : categories) {
			grouped.put(category, new LinkedHashMap<>());
		}
		
		for (Map.Entry<String, Map<String, String>> sectionEntry : liveInfo.entrySet()) {
			String sectionName = sectionEntry.getKey().toLowerCase();
			Map<String, String> sectionMap = sectionEntry.getValue();
			
			if (sectionName.contains("basic")) {
				grouped.get("Basic Info").putAll(sectionMap);
			} else {
				for (Map.Entry<String, String> entry : sectionMap.entrySet()) {
					String key = entry.getKey().toLowerCase();
					String value = entry.getValue();
					
					if (key.contains("cpu")) {
						grouped.get("CPU Temperatures").put(entry.getKey(), value + " C°");
					} else if (key.contains("gpu")) {
						grouped.get("GPU Temperatures").put(entry.getKey(), value + " C°");
					} else if (key.contains("power") || key.contains("watt") || value.contains("mW") || value.contains("V") || value.contains("mA")) {
						grouped.get("Power Metrics").put(entry.getKey(), value);
					} else if (key.contains("degradation") || key.contains("wear") || key.contains("cycle")) {
						grouped.get("Degradation").put(entry.getKey(), value);
					} else if (key.contains("spare")) {
						grouped.get("Spare").put(entry.getKey(), value);
					} else {
						grouped.get("Other").put(entry.getKey(), value);
					}
				}
			}
		}
		
		for (Map.Entry<String, String> entry : batteryData.entrySet()) {
			String key = entry.getKey().toLowerCase();
			String value = entry.getValue();
			
			if (key.contains("basic")) {
				grouped.get("Basic Info").put(entry.getKey(), value);
			} else if (key.contains("cpu")) {
				grouped.get("CPU Temperatures").put(entry.getKey(), value + " C°");
			} else if (key.contains("gpu")) {
				grouped.get("GPU Temperatures").put(entry.getKey(), value + " C°");
			} else if (key.contains("power") || key.contains("watt")) {
				grouped.get("Power Metrics").put(entry.getKey(), value + " W");
			} else if (key.contains("degradation")) {
				grouped.get("Degradation").put(entry.getKey(), value);
			} else if (key.contains("spare")) {
				grouped.get("Spare").put(entry.getKey(), value);
			} else {
				grouped.get("Other").put(entry.getKey(), value);
			}
		}
		
		grouped.entrySet().removeIf(e -> e.getValue().isEmpty());
		return grouped;
	}
}