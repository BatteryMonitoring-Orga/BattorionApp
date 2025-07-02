package com.battery_level_alarm.monitoring.visual_effects;
import javafx.scene.paint.Color;

public class ColorUtils {
	public static String javafxColor;
	public static String javaAwtColor;
	public static String originalColorString;
	public enum ColorType {
		HEX, RGB, RGBA, HEX_0X, NAMED, UNKNOWN
	}
	
	public static ColorType detectColorType(String colorStr) {
		if (colorStr == null) return ColorType.UNKNOWN;
		String c = colorStr.trim().toLowerCase();
		if (c.startsWith("#") && (c.length() == 7 || c.length() == 9)) return ColorType.HEX;
		if (c.startsWith("0x") && c.length() == 10) return ColorType.HEX_0X;
		if (c.startsWith("rgb(")) return ColorType.RGB;
		if (c.startsWith("rgba(")) return ColorType.RGBA;
		try {
			Color.web(c);
			return ColorType.NAMED;
		} catch (IllegalArgumentException e) {
			return ColorType.UNKNOWN;
		}
	}
	
	public static String toCssCompatibleColor(Color c) {
		return String.format("#%02X%02X%02X",
				(int) (c.getRed() * 255),
				(int) (c.getGreen() * 255),
				(int) (c.getBlue() * 255));
	}
	
	public static String toCssCompatibleAlphaColor(Color fx) {
		return String.format("rgba(%d, %d, %d, %.2f)",
				(int) (fx.getRed() * 255),
				(int) (fx.getGreen() * 255),
				(int) (fx.getBlue() * 255),
				0.5);
	}
	
	public static Color toJavaFXColor(String colorStr) {
		if (colorStr == null) {
			originalColorString = "null";
			javafxColor = "#000000";
			return Color.BLACK;
		}
		try {
			Color fx;
			switch (detectColorType(colorStr)) {
				case HEX_0X: {
					long val = Long.decode(colorStr);
					int a = (int) ((val >> 24) & 0xFF);
					int r = (int) ((val >> 16) & 0xFF);
					int g = (int) ((val >> 8) & 0xFF);
					int b = (int) (val & 0xFF);
					fx = Color.rgb(r, g, b, a / 255.0);
					break;
				}
				case RGB: {
					String[] parts = colorStr.substring(colorStr.indexOf('(') + 1, colorStr.indexOf(')')).split(",");
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					fx = Color.rgb(r, g, b);
					break;
				}
				case HEX:
				case NAMED:
					fx = Color.web(colorStr);
					break;
				default:
					fx = Color.BLACK;
			}
			javafxColor = String.format("#%02X%02X%02X",
					(int) (fx.getRed() * 255),
					(int) (fx.getGreen() * 255),
					(int) (fx.getBlue() * 255));
			originalColorString = colorStr;
			return fx;
		} catch (Exception e) {
			originalColorString = "exception";
			javafxColor = "#000000";
			return Color.BLACK;
		}
	}
	
	public static java.awt.Color toAWTColor(String colorStr) {
		if (colorStr == null) {
			originalColorString = "null";
			javaAwtColor = "rgb(0, 0, 0)";
			return java.awt.Color.BLACK;
		}
		try {
			Color fx;
			switch (detectColorType(colorStr)) {
				case HEX_0X: {
					long val = Long.decode(colorStr);
					int a = (int) ((val >> 24) & 0xFF);
					int r = (int) ((val >> 16) & 0xFF);
					int g = (int) ((val >> 8) & 0xFF);
					int b = (int) (val & 0xFF);
					javaAwtColor = String.format("rgb(%d, %d, %d)", r, g, b);
					originalColorString = colorStr;
					return new java.awt.Color(r, g, b, a);
				}
				case RGB: {
					String[] parts = colorStr.substring(colorStr.indexOf('(') + 1, colorStr.indexOf(')')).split(",");
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					javaAwtColor = String.format("rgb(%d, %d, %d)", r, g, b);
					originalColorString = colorStr;
					return new java.awt.Color(r, g, b);
				}
				case HEX:
				case NAMED: {
					fx = Color.web(colorStr);
					int r = (int) (fx.getRed() * 255);
					int g = (int) (fx.getGreen() * 255);
					int b = (int) (fx.getBlue() * 255);
					javaAwtColor = String.format("rgb(%d, %d, %d)", r, g, b);
					originalColorString = colorStr;
					return new java.awt.Color(r, g, b, (int)(fx.getOpacity() * 255));
				}
				default:
					javaAwtColor = "rgb(0, 0, 0)";
					originalColorString = "invalid";
					return java.awt.Color.BLACK;
			}
		} catch (Exception e) {
			originalColorString = "exception";
			javaAwtColor = "rgb(0, 0, 0)";
			return java.awt.Color.BLACK;
		}
	}
}