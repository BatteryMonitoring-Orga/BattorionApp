package com.battery_level_alarm.monitoring.visual_effects.messages;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggedMessage {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private LoggedMessage() {
	}
	
	public static void info(String message) {
		print("[INFO]", message, false);
	}
	
	public static void warn(String message) {
		print("[WARN]", message, false);
	}
	
	public static void error(String message) {
		print("[ERROR]", message, true);
	}
	
	public static void error(String message, Throwable throwable) {
		print("[ERROR]", message, true);
		throwable.printStackTrace(System.err);
	}
	
	private static void print(String level, String message, boolean isError) {
		String timestamp = LocalDateTime.now().format(formatter);
		String formatted = level + " " + timestamp + " - " + message;
		
		if (isError) {
			System.err.println(formatted);
		} else {
			System.out.println(formatted);
		}
	}
}
