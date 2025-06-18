package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.tray_manager.modern_component.JavaFXSoundComboBox.setVBoxThemeMode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.notificationUI;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.scene.Scene;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class TrayTheme {
	public enum SystemTheme {DARK, LIGHT, GRAY}
	public static void applyTheme(String selectedTheme) {
		Scene scene = primaryStage.getScene();
		scene.getStylesheets().clear();
		
		String themePath = switch (selectedTheme) {
			case "Dark" -> DARK_THEME_FILE_PATH;
			case "Gray" -> GRAY_THEME_FILE_PATH;
			case "As System" -> {
				SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
				yield theme == SystemTheme.DARK ? DARK_THEME_FILE_PATH : LIGHT_THEME_FILE_PATH;
			}
			default -> LIGHT_THEME_FILE_PATH;
		};
		
		scene.getStylesheets().add(Objects.requireNonNull(BattorionTrayUI.class.getResource(themePath)).toExternalForm());
		if(notificationUI != null) {
			notificationUI.getStylesheets().clear();
			setVBoxThemeMode(notificationUI, selectedTheme);
		}
	}
	
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
			logger.severe("[EXCEPTION]: " + e.getMessage());
			return SystemTheme.LIGHT;
		}
	}
}
