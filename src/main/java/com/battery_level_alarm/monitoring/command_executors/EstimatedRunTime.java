package com.battery_level_alarm.monitoring.command_executors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

public class EstimatedRunTime {
	private static String getOS() {
		return System.getProperty("os.name").toLowerCase();
	}
	
	private static double parseValue(String line) {
		try {
			if (line == null || line.trim().isEmpty()) return -1;
			Pattern timePattern = Pattern.compile("(\\d+):(\\d+)");
			Matcher timeMatcher = timePattern.matcher(line);
			if (timeMatcher.find()) {
				int hours = Integer.parseInt(timeMatcher.group(1));
				int minutes = Integer.parseInt(timeMatcher.group(2));
				return hours * 60 + minutes;
			}
			
			Pattern hoursPattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*hours?");
			Matcher hoursMatcher = hoursPattern.matcher(line);
			if (hoursMatcher.find()) {
				double hours = Double.parseDouble(hoursMatcher.group(1));
				return hours * 60;
			}
			
			String digits = line.replaceAll("[^0-9]", "").trim();
			if (!digits.isEmpty()) {
				return Double.parseDouble(digits);
			}
		} catch (Exception ex) {
			printErrorMessage(ex);
		}
		return -1;
	}
	
	public static double[] getEstimatedRunTimes() {
		String os = getOS();
		double[] estimatedRunTimes = new double[3];
		estimatedRunTimes[0] = getFirstEstimatedRunTime(os);
		estimatedRunTimes[1] = getSecondEstimatedRunTime(os);
		estimatedRunTimes[2] = getThirdEstimatedRunTime(os);
		return estimatedRunTimes;
	}
	
	private static double getFirstEstimatedRunTime(String os) {
		return runAndParse(getProcessBuilderForFirstEstimatedRunTime(os));
	}
	
	private static double getSecondEstimatedRunTime(String os) {
		return runAndParse(getProcessBuilderForSecondEstimatedRunTime(os));
	}
	
	private static double getThirdEstimatedRunTime(String os) {
		return runAndParse(getProcessBuilderForThirdEstimatedRunTime(os));
	}
	
	private static double runAndParse(ProcessBuilder builder) {
		try {
			Process process = builder.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					double value = parseValue(line);
					if (value != -1) return value;
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return -1;
	}
	
	private static ProcessBuilder getProcessBuilderForFirstEstimatedRunTime(String os) {
		if (os.contains("win")) {
			return new ProcessBuilder("C:\\Windows\\System32\\wbem\\WMIC.exe", "Path", "Win32_Battery", "Get", "EstimatedRunTime");
		} else if (os.contains("nix") || os.contains("nux")) {
			return new ProcessBuilder("upower", "-i", "/org/freedesktop/UPower/devices/battery_BAT0");
		} else if (os.contains("mac")) {
			return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
		} else {
			throw new UnsupportedOperationException("Unsupported OS: " + os);
		}
	}
	
	private static ProcessBuilder getProcessBuilderForSecondEstimatedRunTime(String os) {
		if (os.contains("win")) {
			return new ProcessBuilder("powershell", "-Command",
					"Get-WmiObject -Class Win32_Battery | Select-Object -ExpandProperty EstimatedRunTime");
		} else if (os.contains("nix") || os.contains("nux")) {
			return new ProcessBuilder("upower", "-i", "/org/freedesktop/UPower/devices/battery_BAT0");
		} else if (os.contains("mac")) {
			return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
		} else {
			throw new UnsupportedOperationException("Unsupported OS: " + os);
		}
	}
	
	private static ProcessBuilder getProcessBuilderForThirdEstimatedRunTime(String os) {
		if (os.contains("win")) {
			String powershellScript =
					"$status = Get-WmiObject -Class Win32_Battery; " +
							"if ($status.BatteryLifeTime -ge 0) { " +
							"[Math]::Round($status.BatteryLifeTime / 60) " +
							"} else { -1 }";
			return new ProcessBuilder("powershell", "-Command", powershellScript);
		} else if (os.contains("nix") || os.contains("nux")) {
			return new ProcessBuilder("upower", "-i", "/org/freedesktop/UPower/devices/battery_BAT0");
		} else if (os.contains("mac")) {
			return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
		} else {
			throw new UnsupportedOperationException("Unsupported OS: " + os);
		}
	}
}