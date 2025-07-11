package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.wakeUp;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.wakeUpThreadInterruptRequest;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.actions.AutoStartManager.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.DARK;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.GRAY;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getMacTheme;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.getSystemTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.isReleaseInstallProcessRunning;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.NotificationToast;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.BatteryTrayIcon;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.TrayAlerts;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingActions;
import com.battery_level_alarm.monitoring.visual_effects.messages.LoggedMessage;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Objects;

public class TrayIconManager {
	private static TrayIcon mainTrayIcon;
	
	static void createTrayIcon() {
		if (!SystemTray.isSupported()) return;
		try {
			InputStream is = TrayIconManager.class.getResourceAsStream(BATTORION_ICON_PATH);
			if (is == null) return;
			java.awt.Image image = ImageIO.read(is);
			mainTrayIcon = new TrayIcon(image, "Battorion (Battery Monitor)");
			mainTrayIcon.setImageAutoSize(true);
			mainTrayIcon.addActionListener(_ -> showApp());
			
			mainTrayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.getClickCount() == 1) {
							showApp();
						}
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						Platform.setImplicitExit(false);
						Platform.runLater(() -> {
							String cssFile = getCssFile();
							ContextMenu contextMenu = getContextMenu(mainTrayIcon);
							contextMenu.setId("tray-context-menu");
							
							Pane fakePane = new Pane();
							fakePane.setBackground(Background.EMPTY);
							Scene scene = new Scene(fakePane);
							scene.setFill(Color.TRANSPARENT);
							scene.getStylesheets().add(Objects.requireNonNull(NotificationToast.class.getResource(cssFile)).toExternalForm());
							
							Stage stage = new Stage(StageStyle.TRANSPARENT);
							stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(
									TrayIconManager.class.getResource("/com/battery_level_alarm/monitoring/Tray/Icons/menu.png")
							).toExternalForm()));
							stage.setAlwaysOnTop(true);
							stage.setScene(scene);
							stage.setX(Screen.getPrimary().getVisualBounds().getWidth() - 400);
							stage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 312);
							stage.show();
							
							contextMenu.show(fakePane, stage.getX(), stage.getY());
							contextMenu.setOnHidden(_ -> Platform.runLater(() -> {
								if (stage.isShowing()) stage.close();
							}));
						});
					}
				}
			});
			
			SystemTray.getSystemTray().add(mainTrayIcon);
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	public static boolean removeMainTrayIcon() {
		try {
			SystemTray.getSystemTray().remove(mainTrayIcon);
			return true;
		} catch (Exception e) {
			logger.severe("[EXCEPTION]: " + e.getMessage());
		}
		return false;
	}
	
	static ContextMenu getContextMenu(TrayIcon trayIcon) {
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(createStandardItems(trayIcon));
		menu.getItems().addAll(
				new SeparatorMenuItem(),
				createAutoStartItem(),
				createWakeUpItem(),
				createBatteryIconItem()
		);
		return menu;
	}
	
	private static List<MenuItem> createStandardItems(TrayIcon trayIcon) {
		return List.of(
				createItem("Open", "Open the application window", BattorionTrayUI::showApp, "item-open"),
				createItem("Pause", "Pause the application threads", Monitor::pushAndResume, "item-pause"),
				createItem("Settings", "Open the settings window", BattorionTrayUI::openSettingsWindow, "item-settings"),
				createItem("About", "About this application", TrayAlerts::showAboutDialog, "item-about"),
				createItem("Pin to Tray", "Show instructions to pin icon", TrayAlerts::showTrayPinInstructionsFX, "item-pin-to-tray"),
				createItem("Display App Interface", "Restore the application window", SettingActions.AppInterface::displayAppInterface, "item-switch-mode"),
				createItem("Exit (Stop Battorion)", "Exit the application", () -> {
					if(!isReleaseInstallProcessRunning) {
						SystemTray.getSystemTray().remove(trayIcon);
						Runtime.getRuntime().halt(0);
					}
				}, "item-exit")
		);
	}
	
	private static CustomMenuItem createAutoStartItem() {
		CheckBox startOnBoot = new CheckBox("Start on Boot");
		startOnBoot.setId("checkbox-startup");
		startOnBoot.setSelected(isAutoStartEnabled(APP_NAME));
		Tooltip.install(startOnBoot, new Tooltip("Enable or disable start on system boot"));
		startOnBoot.setOnAction(_ -> {
			if (startOnBoot.isSelected()) {
				enableAutoStart(APP_NAME, getCurrentExeDirectory());
			} else {
				disableAutoStart(APP_NAME);
			}
		});
		CustomMenuItem item = new CustomMenuItem(startOnBoot);
		item.setHideOnClick(false);
		return item;
	}
	
	private static CustomMenuItem createBatteryIconItem() {
		boolean isAllowToAdd = Boolean.parseBoolean(prefs.get(SHOW_BATTERY_ICON, "false"));
		CheckBox showBatteryIcon = new CheckBox("Show Battery Icon");
		showBatteryIcon.setId("checkbox-battery-icon");
		showBatteryIcon.setSelected(isAllowToAdd);
		Tooltip.install(showBatteryIcon, new Tooltip("Enable or disable displaying the battery icon"));
		showBatteryIcon.setOnAction(_ -> {
			boolean selected = showBatteryIcon.isSelected();
			prefs.put(SHOW_BATTERY_ICON, String.valueOf(selected));
			try {
				if (selected) {
					if (BatteryTrayIcon.trayIcon != null) {
						SystemTray.getSystemTray().add(BatteryTrayIcon.trayIcon);
					} else {
						java.awt.Color color = new java.awt.Color(
								(int) (batteryLevelColor.getRed() * 255),
								(int) (batteryLevelColor.getGreen() * 255),
								(int) (batteryLevelColor.getBlue() * 255)
						);
						BatteryTrayIcon.showBatteryTrayIcon(chargeLevel, color);
					}
				} else {
					SystemTray.getSystemTray().remove(BatteryTrayIcon.trayIcon);
				}
				Monitor.changeFlag = true;
			} catch (Exception e) {
				LoggedMessage.error("Failed to update tray icon.", e);
			}
		});
		CustomMenuItem item = new CustomMenuItem(showBatteryIcon);
		item.setHideOnClick(false);
		return item;
	}
	
	private static CustomMenuItem createWakeUpItem() {
		boolean isWakeUpAuto = Boolean.parseBoolean(prefs.get(WAKE_UP_PC_AUTO, "false"));
		long wakeUpIntervalSeconds = 90L;
		if (isWakeUpAuto) {
			wakeUp(wakeUpIntervalSeconds);
		}
		
		CheckBox wakeUpPC = new CheckBox("Keep PC Awake");
		wakeUpPC.setId("checkbox-wake-up");
		wakeUpPC.setSelected(isWakeUpAuto);
		Tooltip.install(wakeUpPC, new Tooltip("Keep the PC awake automatically"));
		wakeUpPC.setOnAction(_ -> {
			boolean enabled = wakeUpPC.isSelected();
			prefs.put(WAKE_UP_PC_AUTO, String.valueOf(enabled));
			if (enabled) {
				wakeUp(wakeUpIntervalSeconds);
			} else {
				wakeUpThreadInterruptRequest();
			}
		});
		CustomMenuItem item = new CustomMenuItem(wakeUpPC);
		item.setHideOnClick(false);
		return item;
	}
	
	static CustomMenuItem createItem(String text, String tooltipText, Runnable action, String id) {
		Label label = new Label(text);
		label.setId(id);
		Tooltip tooltip = new Tooltip(tooltipText);
		Tooltip.install(label, tooltip);
		CustomMenuItem item = new CustomMenuItem(label);
		item.setOnAction(_ -> action.run());
		item.setHideOnClick(true);
		return item;
	}
	
	private static String getCssFile() {
		String mode = prefs.get(APP_THEME, AS_SYSTEM.toString());
		String cssFile;
		if (mode != null && mode.equalsIgnoreCase(String.valueOf(LIGHT))) {
			cssFile = STYLES_FILES_DIR_PATH + "/tray-light.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(DARK))) {
			cssFile = STYLES_FILES_DIR_PATH + "/tray-dark.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(GRAY))) {
			cssFile = STYLES_FILES_DIR_PATH + "/tray-gray.css";
		} else if (mode != null && mode.equalsIgnoreCase(String.valueOf(CREAM))) {
			cssFile = STYLES_FILES_DIR_PATH + "/tray-cream.css";
		} else {
			TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
			cssFile = (theme == DARK) ? STYLES_FILES_DIR_PATH + "/tray-dark.css" : STYLES_FILES_DIR_PATH + "/tray-light.css";
		}
		return cssFile;
	}
}