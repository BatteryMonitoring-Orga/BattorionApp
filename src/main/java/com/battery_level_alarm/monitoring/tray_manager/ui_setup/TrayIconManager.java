package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.AutoStartManager.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.Monitor;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.TrayAlerts;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Objects;

public class TrayIconManager {
	static void createTrayIcon() {
		if (!SystemTray.isSupported()) return;
		try {
			InputStream is = TrayIconManager.class.getResourceAsStream(BATTORION_ICON_PATH);
			if (is == null) return;
			java.awt.Image image = ImageIO.read(is);
			TrayIcon trayIcon = new TrayIcon(image, "Battorion - Battery Monitor");
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(_ -> showApp());
			
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.getClickCount() == 1) {
							showApp();
						}
					} else if(e.getButton() == MouseEvent.BUTTON3) {
						Platform.setImplicitExit(false);
						Platform.runLater(() -> {
							ContextMenu contextMenu = getContextMenu(trayIcon);
							Pane fakePane = new Pane();
							fakePane.setBackground(Background.EMPTY);
							Scene scene = new Scene(fakePane);
							scene.setFill(Color.TRANSPARENT);
							
							Stage stage = new Stage(StageStyle.TRANSPARENT);
							stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(
									TrayIconManager.class.getResource(
											"/com/battery_level_alarm/monitoring/Tray/menu.png"
									)).toExternalForm()));
							stage.setAlwaysOnTop(true);
							stage.setScene(scene);
							stage.setX(Screen.getPrimary().getVisualBounds().getWidth() - 400);
							stage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 230);
							stage.show();
							
							contextMenu.show(fakePane, stage.getX(), stage.getY());
							contextMenu.setOnHidden(_ -> Platform.runLater(() -> {
								if (stage.isShowing()) {
									stage.close();
								}
							}));
						});
					}
				}
			});
			
			SystemTray.getSystemTray().add(trayIcon);
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
	
	static ContextMenu getContextMenu(TrayIcon trayIcon) {
		ContextMenu menu = new ContextMenu();
		CustomMenuItem openItem = createItem("Open", "Open the application window", TrayIconManager::showApp);
		CustomMenuItem pauseItem = createItem("Pause", "Pause the application threads", Monitor::pushAndResume);
		CustomMenuItem settingsItem = createItem("Settings", "Open the settings window", BattorionTrayUI::openSettingsWindow);
		CustomMenuItem aboutItem = createItem("About", "About this application", TrayAlerts::showAboutDialog);
		
		CustomMenuItem pinIconItem = createItem(
				"Pin to Tray",
				"Show instructions to pin icon",
				TrayAlerts::showTrayPinInstructionsFX);
		CustomMenuItem exitItem = createItem("Exit (Stop Battorion)", "Exit the application", () -> {
			SystemTray.getSystemTray().remove(trayIcon);
			System.exit(0);
		});
		exitItem.getContent().setStyle("-fx-padding: 0 26 0 26; -fx-font-family:\"Times New Roman\", Serif; -fx-text-fill: red; -fx-font-size: 14px;");
		
		CheckBox checkBox = new CheckBox("Start on Boot");
		checkBox.setSelected(isAutoStartEnabled(APP_NAME));
		checkBox.setStyle("-fx-text-fill: white; -fx-font-family:\"Times New Roman\", Serif; -fx-font-size: 14px;");
		Tooltip.install(checkBox, new Tooltip("Enable or disable start on system boot"));
		checkBox.setOnAction(_ -> {
			if (checkBox.isSelected()) {
				enableAutoStart(APP_NAME, getCurrentExeDirectory());
			} else {
				disableAutoStart(APP_NAME);
			}
		});
		CustomMenuItem autoStartItem = new CustomMenuItem(checkBox);
		autoStartItem.setHideOnClick(false);
		
		menu.getItems().addAll(
				openItem,
				pauseItem,
				settingsItem,
				aboutItem,
				pinIconItem,
				exitItem,
				new SeparatorMenuItem(),
				autoStartItem
		);
		menu.setStyle("-fx-pref-width: 175px; -fx-background-color: #2b2b2b;");
		return menu;
	}
	
	static CustomMenuItem createItem(String text, String tooltipText, Runnable action) {
		Label label = new Label(text);
		label.setStyle("-fx-padding: 0 26 0 26; -fx-font-family:\"Times New Roman\", Serif; -fx-text-fill: white; -fx-font-size: 14px;");
		Tooltip tooltip = new Tooltip(tooltipText);
		Tooltip.install(label, tooltip);
		
		CustomMenuItem item = new CustomMenuItem(label);
		item.setOnAction(_ -> action.run());
		item.setHideOnClick(true);
		return item;
	}
	
	private static void showApp() {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			if (primaryStage == null) createPopupWindow();
			if (!primaryStage.isShowing()) {
				if (primaryTabPane == null) return;
				
				for (Tab tab : primaryTabPane.getTabs()) {
					if ("Dashboard".equals(tab.getText())) {
						primaryStage.show();
						primaryTabPane.getSelectionModel().select(tab);
						break;
					}
				}
				primaryStage.setAlwaysOnTop(true);
			} else {
				primaryStage.toFront();
			}
		});
	}
}
