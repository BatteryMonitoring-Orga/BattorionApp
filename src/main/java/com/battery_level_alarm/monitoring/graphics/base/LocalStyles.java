package com.battery_level_alarm.monitoring.graphics.base;

public class LocalStyles {
	public static String chartLegendItem(String color) {
		return """
				-fx-background-color:
				""" +
				color
				+ """
				, white;
				-fx-background-insets: 0, 2;
				-fx-background-radius: 5px;
				-fx-padding: 5px;
				-fx-shape: "M5,0 A5,5 0 1,1 4.999,0 Z";
				""";
	}
}
