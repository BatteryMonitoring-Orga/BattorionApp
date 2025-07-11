package com.battery_level_alarm.monitoring.core_utilities;

public class BatteryInfo {
	private static long designedCapacity;
	private static long fullChargeCapacity;
	private static double healthPercentage;
	
	private static long lastMeasuredCapacity = -1;
	private static long estimatedRuntimeMinutes = -1;
	private static String estimatedTimeRemaining;
	
	public static long getDesignedCapacity() {
		return designedCapacity;
	}
	
	public static void setDesignedCapacity(long designedCapacity) {
		BatteryInfo.designedCapacity = designedCapacity;
	}
	
	public static long getFullChargeCapacity() {
		return fullChargeCapacity;
	}
	
	public static void setFullChargeCapacity(long fullChargeCapacity) {
		BatteryInfo.fullChargeCapacity = fullChargeCapacity;
	}
	
	public static double getHealthPercentage() {
		return healthPercentage;
	}
	
	public static void setHealthPercentage(double healthPercentage) {
		BatteryInfo.healthPercentage = healthPercentage;
	}
	
	public static long getLastMeasuredCapacity() {
		return lastMeasuredCapacity;
	}
	
	public static void setLastMeasuredCapacity(long lastMeasuredCapacity) {
		BatteryInfo.lastMeasuredCapacity = lastMeasuredCapacity;
	}
	
	public static long getEstimatedRuntimeMinutes() {
		return estimatedRuntimeMinutes;
	}
	
	public static void setEstimatedRuntimeMinutes(long estimatedRuntimeMinutes) {
		BatteryInfo.estimatedRuntimeMinutes = estimatedRuntimeMinutes;
	}
	
	public static String getEstimatedTimeRemaining() {
		return estimatedTimeRemaining;
	}
	
	public static void setEstimatedTimeRemaining(String estimatedTimeRemaining) {
		BatteryInfo.estimatedTimeRemaining = estimatedTimeRemaining;
	}
}