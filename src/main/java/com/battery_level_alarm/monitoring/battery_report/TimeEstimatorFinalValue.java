package com.battery_level_alarm.monitoring.battery_report;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;
import static com.battery_level_alarm.monitoring.command_executors.EstimatedRunTime.getEstimatedRunTimes;

public class TimeEstimatorFinalValue {
	public static String getEstimatedTimeText(
			boolean charging,
			boolean hasValidRate,
			double percent,
			double currentCapacity,
			double maxCapacity,
			double rawRate
	) {
		if (charging) {
			if (hasValidRate && (maxCapacity - currentCapacity) > 0) {
				double toFull = ((maxCapacity - currentCapacity) / Math.abs(rawRate)) * 60.0;
				return formatMinutes(toFull) + " (to full)";
			}
			return "Charging";
		}
		
		double[] externalEstimates = getEstimatedRunTimes();
		double liveEst = -1;
		double historicalEst = -1;
		if (hasValidRate && currentCapacity > 0) {
			liveEst = (currentCapacity / Math.abs(rawRate)) * 60.0;
			liveEst = Math.min(liveEst, 5 * 60);
		} if (BatteryInfo.getEstimatedRuntimeMinutes() > 0) {
			historicalEst = BatteryInfo.getEstimatedRuntimeMinutes() * (percent / 100.0);
		}
		
		double finalEst = computeWeightedAverage(new double[]{
				externalEstimates[0],
				externalEstimates[1],
				externalEstimates[2],
				historicalEst,
				liveEst
		}, new int[]{
				3, 3, 3,
				1,
				1
		});
		
		if (finalEst > 0) {
			return formatMinutes(finalEst) + " (to empty)";
		}
		return "Calculating...";
	}
	
	private static double computeWeightedAverage(double[] values, int[] weights) {
		double total = 0;
		int weightSum = 0;
		for (int i = 0; i < values.length; i++) {
			double v = values[i];
			if (v > 0 && v < 1000) {
				total += v * weights[i];
				weightSum += weights[i];
			}
		}
		return weightSum > 0 ? (total / weightSum) : -1;
	}
	
	private static String formatMinutes(double minutes) {
		int total = (int) Math.round(minutes);
		int hours = total / 60;
		int mins = total % 60;
		if (hours > 0) {
			return String.format("%d hr %02d min", hours, mins);
		}
		return String.format("%d min", mins);
	}
}