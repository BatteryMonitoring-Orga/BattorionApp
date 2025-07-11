package com.battery_level_alarm.monitoring.core_utilities;
import javafx.scene.paint.Color;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.CSS_FOLDER_PATH;

public class GraphSettings {
	public enum GRAPH_THEME {
		FlatAtomOneLightTheme("Atom One Light", CSS_FOLDER_PATH + "FlatAtomOneLightIJTheme.css"),
		FlatAtomOneDarkTheme("Atom One Dark", CSS_FOLDER_PATH + "FlatAtomOneDarkIJTheme.css"),
		FlatHiberbeeDarkTheme("Hiberbee Dark", CSS_FOLDER_PATH + "FlatHiberbeeDarkIJTheme.css"),
		FlatMacLightLaf("Mac Light", CSS_FOLDER_PATH + "FlatMacLightLaf.css"),
		FlatMacDarkLaf("Mac Dark", CSS_FOLDER_PATH + "FlatMacDarkLaf.css"),
		FlatIntellijLightLaf("Intellij Light", CSS_FOLDER_PATH + "FlatIntelliJLaf.css");
		
		private final String displayName;
		private final String cssPath;
		GRAPH_THEME(String displayName, String cssPath) {
			this.displayName = displayName;
			this.cssPath = cssPath;
		}
		
		public static GRAPH_THEME fromDisplayName(String displayName) {
			for (GRAPH_THEME theme : values()) {
				if (theme.displayName.equalsIgnoreCase(displayName)) {
					return theme;
				}
			}
			throw new IllegalArgumentException("No enum constant with display name: " + displayName);
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public String getCssPath() {
			return cssPath;
		}
		
		@Override
		public String toString() {
			return displayName;
		}
	}
	
	private static String painterTheme;
	private static String chartType;
	private static String saveFormat;
	
	private static Color sketchColor;
	private static Color backgroundColor;
	private static Color axisColor;
	private static Color alertColor;
	
	private static boolean showDataPoints;
	private static boolean showValuesOnHover;
	private static boolean showGridLines;
	private static boolean showXAxisLabels;
	private static boolean showYAxisLabels;
	private static boolean autoUpdate;
	private static boolean zoomEnabled;
	private static boolean autoSave;
	
	private static double zoomMin;
	private static double zoomMax;
	private static int alertThreshold;
	private static int saveAfterNumOfRecords;
	
	public static String getPainterTheme() {
		return painterTheme;
	}
	
	public static void setPainterTheme(String value) {
		painterTheme = value;
	}
	
	public static Color getSketchColor() {
		return sketchColor;
	}
	
	public static void setSketchColor(Color value) {
		sketchColor = value;
	}
	
	public static Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public static void setBackgroundColor(Color value) {
		backgroundColor = value;
	}
	
	public static Color getAxisColor() {
		return axisColor;
	}
	
	public static void setAxisColor(Color value) {
		axisColor = value;
	}
	
	public static String getChartType() {
		return chartType;
	}
	
	public static void setChartType(String value) {
		chartType = value;
	}
	
	public static boolean isShowDataPoints() {
		return showDataPoints;
	}
	
	public static void setShowDataPoints(boolean value) {
		showDataPoints = value;
	}
	
	public static boolean isShowValuesOnHover() {
		return showValuesOnHover;
	}
	
	public static void setShowValuesOnHover(boolean value) {
		showValuesOnHover = value;
	}
	
	public static boolean isShowGridLines() {
		return showGridLines;
	}
	
	public static void setShowGridLines(boolean value) {
		showGridLines = value;
	}
	
	public static boolean isShowXAxisLabels() {
		return showXAxisLabels;
	}
	
	public static void setShowXAxisLabels(boolean value) {
		showXAxisLabels = value;
	}
	
	public static boolean isShowYAxisLabels() {
		return showYAxisLabels;
	}
	
	public static void setShowYAxisLabels(boolean value) {
		showYAxisLabels = value;
	}
	
	public static boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	public static void setAutoUpdate(boolean value) {
		autoUpdate = value;
	}
	
	public static boolean isZoomEnabled() {
		return zoomEnabled;
	}
	
	public static void setZoomEnabled(boolean value) {
		zoomEnabled = value;
	}
	
	public static double getZoomMin() {
		return zoomMin;
	}
	
	public static void setZoomMin(double value) {
		zoomMin = value;
	}
	
	public static double getZoomMax() {
		return zoomMax;
	}
	
	public static void setZoomMax(double value) {
		zoomMax = value;
	}
	
	public static int getAlertThreshold() {
		return alertThreshold;
	}
	
	public static void setAlertThreshold(int value) {
		alertThreshold = value;
	}
	
	public static Color getAlertColor() {
		return alertColor;
	}
	
	public static void setAlertColor(Color value) {
		alertColor = value;
	}
	
	public static String getSaveFormat() {
		return saveFormat;
	}
	
	public static void setSaveFormat(String value) {
		saveFormat = value;
	}
	
	public static boolean isAutoSave() {
		return autoSave;
	}
	
	public static void setAutoSave(boolean value) {
		autoSave = value;
	}
	
	public static int getSaveAfterNumOfRecords() {
		return saveAfterNumOfRecords;
	}
	
	public static void setSaveAfterNumOfRecords(int value) {
		saveAfterNumOfRecords = value;
	}
}
