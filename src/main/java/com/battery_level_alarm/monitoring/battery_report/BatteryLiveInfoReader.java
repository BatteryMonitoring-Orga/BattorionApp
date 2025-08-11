package com.battery_level_alarm.monitoring.battery_report;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryLevel;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getBatteryStatus;
import static com.battery_level_alarm.monitoring.core_utilities.BatteryInfo.setEstimatedTimeRemaining;

public class BatteryLiveInfoReader {
	public static Map<String, Map<String, String>> getBatteryInfoAsMap() {
		Map<String, Map<String, String>> nestedMap = new LinkedHashMap<>();
		Map<String, String> basicInfo = new LinkedHashMap<>();
		Map<String, String> powerMetrics = new LinkedHashMap<>();
		Map<String, String> degradation = new LinkedHashMap<>();
		Map<String, String> warnings = new LinkedHashMap<>();
		
		SystemInfo systemInfo = new SystemInfo();
		HardwareAbstractionLayer hal = systemInfo.getHardware();
		
		for (PowerSource battery : hal.getPowerSources()) {
			String name = battery.getName();
			boolean charging = getBatteryStatus();
			
			double percent;
			try {
				percent = getBatteryLevel();
			} catch (Exception e) {
				percent = 0.0;
			}
			
			double maxCapacity = battery.getMaxCapacity();
			double designCapacity = battery.getDesignCapacity();
			if (BatteryInfo.getFullChargeCapacity() > 0) {
				maxCapacity = BatteryInfo.getFullChargeCapacity();
			}
			if (BatteryInfo.getDesignedCapacity() > 0) {
				designCapacity = BatteryInfo.getDesignedCapacity();
			}
			
			double currentCapacity = (percent / 100.0) * maxCapacity;
			double rawRate = battery.getPowerUsageRate();
			double absRate = Math.abs(rawRate);
			boolean hasValidRate = absRate > 0;
			
			String timeText = TimeEstimator.getEstimatedTimeText(
					charging, hasValidRate, percent, currentCapacity, maxCapacity, rawRate
			);
			setEstimatedTimeRemaining(timeText);
			
			double voltage = battery.getVoltage();
			double amperage = battery.getAmperage();
			String chemistry = battery.getChemistry();
			long cycleCount = battery.getCycleCount();
			double wear = (designCapacity > 0 && maxCapacity >= 0)
					? (1 - maxCapacity / designCapacity) * 100 : -1;
			
			basicInfo.put("Battery Name", name);
			basicInfo.put("Chemistry", chemistry != null && !chemistry.isBlank() ? chemistry : "N/A");
			basicInfo.put("Charge Level", percent >= 0 ? String.format("%.1f%%", percent) : "N/A");
			basicInfo.put("Charging", charging ? "Yes" : "No");
			basicInfo.put("Estimated Remaining Time", timeText);
			basicInfo.put("Current Capacity", currentCapacity >= 0 ? String.format("%.0f mWh", currentCapacity) : "N/A");
			basicInfo.put("Max Capacity", maxCapacity >= 0 ? String.format("%.0f mWh", maxCapacity) : "N/A");
			basicInfo.put("Design Capacity", designCapacity >= 0 ? String.format("%.0f mWh", designCapacity) : "N/A");
			
			degradation.put("Battery Wear", wear >= 0 ? String.format("%.2f%%", wear) : "N/A");
			degradation.put("Cycle Count", cycleCount >= 0 ? String.valueOf(cycleCount) : "N/A");
			
			String direction = rawRate > 0 ? "Charging" : "Discharging";
			String rateText = hasValidRate ? String.format("%.0f mW (%s)", absRate, direction) : "N/A";
			
			powerMetrics.put("Usage Rate", rateText);
			powerMetrics.put("Voltage", voltage > 0 ? String.format("%.2f V", voltage) : "N/A");
			powerMetrics.put("Amperage", amperage != 0 ? String.format("%.0f mA", amperage) : "N/A");
			if (maxCapacity > designCapacity && designCapacity > 0) {
				warnings.put("Warning", "⚠ Max Capacity > Design Capacity! Possible calibration issue.");
			}
		}
		
		if (!basicInfo.isEmpty()) nestedMap.put("Basic", basicInfo);
		if (!powerMetrics.isEmpty()) nestedMap.put("Power Metrics", powerMetrics);
		if (!degradation.isEmpty()) nestedMap.put("Degradation", degradation);
		if (!warnings.isEmpty()) nestedMap.put("Warnings", warnings);
		return nestedMap;
	}
}