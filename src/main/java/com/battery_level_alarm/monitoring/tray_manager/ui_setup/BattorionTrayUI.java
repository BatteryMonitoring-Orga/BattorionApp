package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.Monitor.backgroundProcessMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.TrayIconManager.createTrayIcon;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.TrayTheme.applyTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.UITabs.createTabsPanel;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;
import java.util.prefs.Preferences;

public class BattorionTrayUI extends Application {
	static final String BATTORION_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/battorion_background.png";
	static final String DARK_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/dark-theme.css";
	static final String LIGHT_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/light-theme.css";
	static Stage primaryStage;
	static TabPane primaryTabPane;
	public static Preferences prefs;
	
	public enum DepartureModes {
		START_WITH_TRAY,
		START_WITH_APPLICATION
	}
	
	public static void main_fx( String[] args) {
		launch(args);
		backgroundProcessMonitoring();
	}
	
	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		setupPrimaryStage();
		createPopupWindow();
		createTrayIcon();
	}
	
	private void setupPrimaryStage() {
		primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BATTORION_ICON_PATH))));
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setX(Screen.getPrimary().getVisualBounds().getWidth() - 361);
		primaryStage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 485);
	}
	
	static void createPopupWindow() {
		primaryStage.setTitle("Battorion - In Background Process");
		primaryTabPane = createTabsPanel();
		Scene scene = new Scene(primaryTabPane, 350, 450);
		scene.getStylesheets().add(Objects.requireNonNull(BattorionTrayUI.class.getResource(LIGHT_THEME_FILE_PATH)).toExternalForm());
		primaryTabPane.setStyle("-fx-tab-min-width: 100 !important; -fx-tab-max-height: 24 !important; -fx-font-size: 10px !important;");
		primaryStage.setScene(scene);
		applyTheme(prefs.get("appTheme", "As System"));
		setupFocusAndCloseBehavior();
	}
	
	private static void setupFocusAndCloseBehavior() {
		primaryStage.setOnCloseRequest(event -> {
			event.consume();
			primaryStage.hide();
		});
		primaryStage.focusedProperty().addListener((_, _, newFocus) -> {
			if (!newFocus) {
				PauseTransition pause = new PauseTransition(Duration.millis(300));
				pause.setOnFinished(_ -> {
					if (!primaryStage.isFocused()) {
						primaryStage.hide();
					}
				});
				pause.play();
			}
		});
	}
	
	static void openSettingsWindow() {
		if (primaryTabPane == null) return;
		
		for (Tab tab : primaryTabPane.getTabs()) {
			if ("Settings".equals(tab.getText())) {
				primaryStage.show();
				primaryTabPane.getSelectionModel().select(tab);
				break;
			}
		}
	}
}