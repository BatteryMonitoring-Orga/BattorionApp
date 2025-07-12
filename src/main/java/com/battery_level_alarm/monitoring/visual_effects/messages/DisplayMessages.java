package com.battery_level_alarm.monitoring.visual_effects.messages;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DisplayMessages {
	public static void printErrorMessage(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logger.severe("[EXCEPTION]: " + sw);
	}
}