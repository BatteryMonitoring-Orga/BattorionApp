package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.backgroundProcessMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.TrayIconManager.createTrayIcon;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.applyTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTabsPanel;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Objects;

public class BattorionTrayUI extends Application {
	static final String BATTORION_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/Icons/battorion_background.png";
	public static final String DARK_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/dark-theme.css";
	public static final String GRAY_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/gray-theme.css";
	public static final String LIGHT_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/light-theme.css";
	public static Stage primaryStage;
	public static TabPane primaryTabPane;
	
	private static boolean isLaunched = false;
	public enum DepartureModes {
		START_WITH_TRAY,
		START_WITH_APPLICATION
	}
	
	public enum UpdateSpeed {
		FAST(1000, "Fast (1 second)"),
		MEDIUM(5000, "Medium (5 seconds)"),
		SLOW(10000, "Slow (10 seconds)");
		
		private final int intervalMs;
		private final String label;
		UpdateSpeed(int intervalMs, String label) {
			this.intervalMs = intervalMs;
			this.label = label;
		}
		
		public int getIntervalMs() {
			return intervalMs;
		}
		public String getLabel() {
			return label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	public static void main_fx( String[] args) {
		isLaunched = true;
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		setupPrimaryStage();
		createPopupWindow();
		createTrayIcon();
		if(isLaunched) {
			backgroundProcessMonitoring();
		}
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
		primaryTabPane.setSide(Side.BOTTOM);
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