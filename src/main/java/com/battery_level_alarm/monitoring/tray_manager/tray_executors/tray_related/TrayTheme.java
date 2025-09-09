package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import static com.battery_level_alarm.monitoring.system_core.Battorion.isApplicationMode;
import static com.battery_level_alarm.monitoring.tray_manager.modern_component.JavaFXSoundComboBox.setVBoxThemeMode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.notificationUI;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.scene.Scene;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrayTheme {
	public enum SystemTheme { DARK, DARK_BLUE, LIGHT, LAVENDER, GRAY, CREAM, AS_SYSTEM }
	private static SystemTheme currentTheme;
	private static ScheduledExecutorService scheduler;
	
	public static void monitorSystemTheme() {
		if (scheduler != null && !scheduler.isShutdown() && !scheduler.isTerminated()) {
			return;
		}
		currentTheme = getCurrentSystemTheme();
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			SystemTheme newTheme = getCurrentSystemTheme();
			if (newTheme != currentTheme) {
				currentTheme = newTheme;
				applyTheme(SystemTheme.AS_SYSTEM);
			}
		}, 0, 5, TimeUnit.MINUTES);
	}
	
	public static void stopSystemThemeMonitoring() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
			try {
				if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
					scheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public static void applyTheme(SystemTheme selectedTheme) {
		Scene scene = primaryStage.getScene();
		if(scene == null) {
			if(isApplicationMode) {
				scene = primaryScene;
			} else {
				return;
			}
		}
		
		scene.getStylesheets().clear();
		String themePath = switch (selectedTheme) {
			case DARK -> DARK_THEME_FILE_PATH;
			case DARK_BLUE -> DARK_BLUE_THEME_FILE_PATH;
			case LAVENDER -> LAVENDER_THEME_FILE_PATH;
			case GRAY -> GRAY_THEME_FILE_PATH;
			case CREAM -> CREAM_THEME_FILE_PATH;
			case AS_SYSTEM -> {
				SystemTheme theme = getCurrentSystemTheme();
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
	
	private static SystemTheme getCurrentSystemTheme() {
		return System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
	}
	
	public static SystemTheme getSystemTheme() {
		try {
			String[] command = {
					"reg", "query",
					"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
					"/v", "AppsUseLightTheme"
			};
			Process process = Runtime.getRuntime().exec(command);
			
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
			String[] command = {"defaults", "read", "-g", "AppleInterfaceStyle"};
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return (reader.readLine() != null) ? SystemTheme.DARK : SystemTheme.LIGHT;
		} catch (Exception e) {
			printErrorMessage(e);
			return SystemTheme.LIGHT;
		}
	}
}
