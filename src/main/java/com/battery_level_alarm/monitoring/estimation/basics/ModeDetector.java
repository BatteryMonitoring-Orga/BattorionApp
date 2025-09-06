package com.battery_level_alarm.monitoring.estimation.basics;

public class ModeDetector {
	public static BatteryMode deduceMode(double currentPercent, double previousPercent) {
		if (Double.isNaN(previousPercent)) return BatteryMode.UNKNOWN;
		if (currentPercent > previousPercent + 0.01) return BatteryMode.CHARGING;
		if (currentPercent < previousPercent - 0.01) return BatteryMode.DISCHARGING;
		return BatteryMode.UNKNOWN;
	}
}