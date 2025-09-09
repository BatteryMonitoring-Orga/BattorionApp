package com.battery_level_alarm.monitoring.core_utilities;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

public class VersionReader {
	public static String version(String path) {
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(path)) {
			props.load(fis);
			String version = props.getProperty("app.version");
			
			if (version != null) {
				return version;
			} else {
				return "0.0.0";
			}
		} catch (IOException e) {
			printErrorMessage(e);
		}
		return "0.0.0";
	}
}
