package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.TRAY_NOTIFICATION_NAME;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.backgroundProcessMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.AS_SYSTEM;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayUsageTutorial.showTrayUsageTutorial;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.TrayIconManager.createTrayIcon;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.applyTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTabsPanel;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.MiniToast;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
	public static final String BATTORION_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/Icons/battorion_background.png";
	public static final String STYLES_FILES_DIR_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles";
	public static final String DARK_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/dark-theme.css";
	public static final String DARK_BLUE_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/dark-blue-theme.css";
	public static final String GRAY_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/gray-theme.css";
	public static final String CREAM_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/cream-theme.css";
	public static final String LIGHT_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/light-theme.css";
	public static final String LAVENDER_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/interface/lavender-theme.css";
	private static String trayTheme;
	
	public static Stage primaryStage;
	public static Scene primaryScene;
	public static TabPane primaryTabPane;
	public static Insets insets;
	
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
	
	public static void main_fx(String[] args) {
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
			backgroundProcessMonitoring(trayTheme);
		} if(Boolean.parseBoolean(prefs.get(TIME_RUNNING_IN_BACKGROUND, "true"))) {
			showTrayUsageTutorial(new Stage());
			showApp();
			prefs.put(TIME_RUNNING_IN_BACKGROUND, "false");
		}
	}
	
	public static Scene getTrayUI() {
		primaryStage = new Stage();
		setupPrimaryStage();
		createPopupWindow();
		return primaryScene;
	}
	
	private static void setupPrimaryStage() {
		primaryStage.getIcons().add(new Image(Objects.requireNonNull(BattorionTrayUI.class.getResourceAsStream(BATTORION_ICON_PATH))));
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setX(Screen.getPrimary().getVisualBounds().getWidth() - 361);
		primaryStage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 485);
	}
	
	static void createPopupWindow() {
		primaryStage.setTitle(TRAY_NOTIFICATION_NAME);
		String position = prefs.get(TAB_HEADER_POSITION, "Bottom");
		setUpdateBoxPadding(position);
		primaryTabPane = createTabsPanel();
		setUpdateTabHeaderPosition(position);
		
		primaryScene = new Scene(primaryTabPane, 350, 450);
		primaryScene.getStylesheets().add(Objects.requireNonNull(BattorionTrayUI.class.getResource(LIGHT_THEME_FILE_PATH)).toExternalForm());
		primaryTabPane.setStyle("-fx-tab-min-width: 100 !important; -fx-tab-max-height: 24 !important; -fx-font-size: 10px !important;");
		primaryStage.setScene(primaryScene);
		
		trayTheme = prefs.get(APP_THEME, String.valueOf(AS_SYSTEM));
		applyTheme(TrayTheme.SystemTheme.valueOf(trayTheme));
		setupFocusAndCloseBehavior();
	}
	
	public static void rebuildTabPanels() {
		if (primaryTabPane != null) {
			primaryTabPane.getTabs().clear();
			primaryTabPane.getTabs().addAll(createTabsPanel().getTabs());
		}
	}
	
	private static void setupFocusAndCloseBehavior() {
		primaryStage.setOnCloseRequest(event -> {
			event.consume();
			primaryStage.hide();
		});
		
		primaryStage.iconifiedProperty().addListener((_, _, newVal) -> {
			if (newVal) {
				primaryStage.setIconified(false);
				primaryStage.hide();
			}
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
	
	public static void showNewTabHint(String newTabTitle) {
		String mode = prefs.get(TAB_HEADER_POSITION, "Bottom");
		Point2D stagePos = new Point2D(primaryStage.getX(), primaryStage.getY());
		double stageWidth = primaryStage.getWidth();
		double stageHeight = primaryStage.getHeight();
		
		double x = stagePos.getX() + (stageWidth / 2) + 100;
		double y = mode.equals("Top")
				? stagePos.getY() + 90
				: stagePos.getY() + stageHeight - 60;
		
		MiniToast.show(
				new Point2D(x, y),
				"🎉 We've added a new tab: '" + newTabTitle + "' – take a look!",
				5,
				true,
				() -> {
					if (primaryTabPane == null) return;
					for (Tab tab : primaryTabPane.getTabs()) {
						if (newTabTitle.equals(tab.getText())) {
							primaryTabPane.getSelectionModel().select(tab);
							prefs.putBoolean(NEW_TRAY_TAB, true);
							break;
						}
					}
				}
		);
	}
	
	public static void setUpdateTabHeaderPosition(String position) {
		if(position.equals("Top")) {
			primaryTabPane.setSide(Side.TOP);
		} else {
			primaryTabPane.setSide(Side.BOTTOM);
		}
	}
	
	public static void setUpdateBoxPadding(String position) {
		if(position.equals("Top")) {
			insets = new Insets(20);
		} else {
			insets = new Insets(0, 20, 35, 20);
		}
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
	
	static void showApp() {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			if (primaryStage == null) createPopupWindow();
			if (!primaryStage.isShowing()) {
				if (primaryTabPane == null) return;
				primaryStage.show();
				primaryStage.setAlwaysOnTop(true);
				primaryStage.setX(Screen.getPrimary().getVisualBounds().getWidth() - 361);
				primaryStage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 485);
				if(!prefs.getBoolean(NEW_TRAY_TAB, false)) {
					showNewTabHint("Feedback");
				}
			} else {
				primaryStage.toFront();
			}
		});
	}
}