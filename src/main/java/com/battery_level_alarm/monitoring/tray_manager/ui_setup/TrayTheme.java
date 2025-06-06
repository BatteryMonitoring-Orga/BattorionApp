package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class TrayTheme {
	static void applyTheme(String selectedTheme) {
		Scene scene = primaryStage.getScene();
		scene.getStylesheets().clear();
		String themePath = switch (selectedTheme) {
			case "Dark" -> DARK_THEME_FILE_PATH;
			case "As System" -> {
				SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
				yield theme == SystemTheme.DARK ? DARK_THEME_FILE_PATH : LIGHT_THEME_FILE_PATH;
			}
			default -> LIGHT_THEME_FILE_PATH;
		};
		scene.getStylesheets().add(Objects.requireNonNull(BattorionTrayUI.class.getResource(themePath)).toExternalForm());
	}
	
	public enum SystemTheme { DARK, LIGHT }
	public static SystemTheme getSystemTheme() {
		try {
			Process process = Runtime.getRuntime().exec(
					"reg query HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize /v AppsUseLightTheme"
			);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("AppsUseLightTheme")) {
					String[] parts = line.trim().split("\\s+");
					int value = Integer.decode(parts[parts.length - 1]);
					return value == 0 ? SystemTheme.DARK : SystemTheme.LIGHT;
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return SystemTheme.LIGHT;
	}
	
	public static SystemTheme getMacTheme() {
		try {
			Process process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return (reader.readLine() != null) ? SystemTheme.DARK : SystemTheme.LIGHT;
		} catch (Exception e) {
			return SystemTheme.LIGHT;
		}
	}
}
