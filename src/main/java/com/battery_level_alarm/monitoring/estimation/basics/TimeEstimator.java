package com.battery_level_alarm.monitoring.estimation.basics;

public class TimeEstimator {
	public static double calculateEstimate(double ratePerSec, double lastPercent, BatteryMode currentMode, double minAbsoluteRate) {
		if (Double.isNaN(ratePerSec) || Math.abs(ratePerSec) < minAbsoluteRate) return Double.NaN;
		double percentLeft;
		
		if (currentMode == BatteryMode.DISCHARGING) {
			percentLeft = lastPercent;
			if (ratePerSec >= 0) return Double.NaN;
			return percentLeft / (-ratePerSec);
		} else if (currentMode == BatteryMode.CHARGING) {
			percentLeft = 100.0 - lastPercent;
			if (ratePerSec <= 0) return Double.NaN;
			return percentLeft / ratePerSec;
		}
		return Double.NaN;
	}
}