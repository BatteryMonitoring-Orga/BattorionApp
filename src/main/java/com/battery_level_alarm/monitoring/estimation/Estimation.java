package com.battery_level_alarm.monitoring.estimation;
import com.battery_level_alarm.monitoring.estimation.algoritms.EMA;
import com.battery_level_alarm.monitoring.estimation.algoritms.KalmanFilter;
import com.battery_level_alarm.monitoring.estimation.basics.*;

import java.io.IOException;
import java.util.LinkedList;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.RECORDED_DATA_FOLDER;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.RoamingConfigClass.ROAMING_CONFIG_PATH;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

public class Estimation {
	private static double finalEstimation = Double.NaN;
	private static String finalEstimationValue;
	private static BatteryMode currentMode = BatteryMode.UNKNOWN;
	private static final LinkedList<BatterySample> latestSamples = new LinkedList<>();
	private static final KalmanFilter kalman = new KalmanFilter(0.0, 1e-6, 1e-4);
	private static final EMA ema = new EMA(0.3);
	private static final double minAbsoluteRate = 1e-8;
	private static final long continuityTimeoutMs = 30_000;
	private static long lastTimestamp = -1;
	private static int lastPercent;
	
	/*
		85-25 = 60 >>> 65*60
		100-0 = 100 >>> X
		X = 6500 seconds
		
		if current level 77
		estimated time = ((100-77)/100)*6500 = 1495/60 = 25 minutes if it's charging up to full capacity,
		else if it's in discharging mode:
		(77/100)*3600 = 2772/60 = 46.2 minutes
	*/
	
	public static String getFinalEstimation() {
		return finalEstimationValue;
	}
	
	public static void main(String[] args) {
		estimate();
	}
	
	public static void estimate() {
		try {
			Estimation.startEstimatingFromExcelFolder(ROAMING_CONFIG_PATH + RECORDED_DATA_FOLDER);
			finalEstimationValue = formatSeconds(finalEstimation);
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	private static String formatSeconds(double s) {
		if (Double.isNaN(s) || s < 0) return "N/A";
		long sec = Math.round(s);
		long h = sec / 3600; sec %= 3600;
		long m = sec / 60; sec %= 60;
		if (h > 0) return String.format("%dh %dm %ds", h, m, sec);
		if (m > 0) return String.format("%dm %ds", m, sec);
		return String.format("%ds", sec);
	}
	
	private static void startEstimating(long timestampMillis, int percent) {
		if (percent < 0) percent = 0;
		if (percent > 100) percent = 100;
		if (lastTimestamp >= 0 && timestampMillis > lastTimestamp) {
			long dtMillis = timestampMillis - lastTimestamp;
			double dtSec = dtMillis / 1000.0;
			double rawRatePerSec = (percent - lastPercent) / dtSec;
			BatteryMode newMode = ModeDetector.deduceMode(percent, lastPercent);
			if (newMode == BatteryMode.UNKNOWN && percent != lastPercent) {
				newMode = percent > lastPercent ? BatteryMode.CHARGING : BatteryMode.DISCHARGING;
			}
			if (newMode == BatteryMode.UNKNOWN) newMode = currentMode;
			currentMode = newMode;
			double filteredRate = kalman.update(rawRatePerSec);
			double emaRate = ema.update(filteredRate);
			if (dtMillis > continuityTimeoutMs) kalman.reset(emaRate);
			lastPercent = percent;
			lastTimestamp = timestampMillis;
			finalEstimation = TimeEstimator.calculateEstimate(ema.getValue(), lastPercent, currentMode, minAbsoluteRate);
		} else {
			lastPercent = percent;
			lastTimestamp = timestampMillis;
			kalman.reset(0.0);
			ema.reset(0.0);
			currentMode = BatteryMode.UNKNOWN;
		}
		latestSamples.add(new BatterySample(timestampMillis, percent));
		if (latestSamples.size() > 10) latestSamples.removeFirst();
	}
	
	private static void startEstimatingFromExcelFolder(String folderPath) throws IOException {
		ExcelReader.readLatestExcelFiles(folderPath, Estimation::startEstimating);
		if (Double.isNaN(finalEstimation)) {
			System.out.println("⚠ No valid estimation could be calculated.");
		} else {
			System.out.println("⏳ Final estimation (seconds): " + finalEstimation);
		}
	}
}
