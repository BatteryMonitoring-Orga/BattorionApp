package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import static com.battery_level_alarm.monitoring.battery_report.BatteryJsonAnalyzer.getMainPackagesTemp;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.SHOW_BATTERY_ICON;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.DashboardTab.batteryEstimatedTime;

public class BatteryIconWindow {
	private static Stage invisibleOwner;
	private static Stage primaryIconStage;
	private static BatteryIcon batteryIcon;
	public static boolean isIconHidden = false;
	
	public static Stage getInvisibleOwner() {
		return invisibleOwner;
	}
	
	public static Stage getPrimaryIconStage() {
		return primaryIconStage;
	}
	
	public static void show(double level, boolean isCharging) {
		Platform.runLater(() -> {
			if (primaryIconStage == null || isIconHidden) {
				isIconHidden = false;
				batteryIcon = new BatteryIcon();
				batteryIcon.setStatus(level, isCharging);
				
				StackPane root = new StackPane(batteryIcon);
				root.setStyle("-fx-background-color: transparent;");
				Scene scene = new Scene(root, 130, 40);
				scene.setFill(Color.TRANSPARENT);
				
				invisibleOwner = new Stage(StageStyle.UTILITY);
				invisibleOwner.setOpacity(0);
				invisibleOwner.show();
				
				primaryIconStage = new Stage(StageStyle.TRANSPARENT);
				primaryIconStage.initOwner(invisibleOwner);
				primaryIconStage.setAlwaysOnTop(true);
				primaryIconStage.setResizable(false);
				primaryIconStage.setScene(scene);
				primaryIconStage.setX(2);
				primaryIconStage.setY(2);
				primaryIconStage.show();
				primaryIconStage.toFront();
			} else if(prefs.getBoolean(SHOW_BATTERY_ICON, false)) {
				updateStatus(level, isCharging);
				if (!primaryIconStage.isShowing()) {
					primaryIconStage.show();
					primaryIconStage.toFront();
				}
			}
		});
	}
	
	public static void updateStatus(double level, boolean isCharging) {
		if (batteryIcon != null) {
			Platform.runLater(() -> batteryIcon.setStatus(level, isCharging));
		}
	}
	
	private static class BatteryIcon extends Canvas {
		private final Popup infoPopup;
		private static Text cpuTempText;
		private static Text gpuTempText;
		
		private double level = -1;
		private boolean isCharging = false;
		private static String lastCPUTemp = "...";
		private static String lastGPUTemp = "...";
		
		public BatteryIcon() {
			super(130, 40);
			setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
			SystemInfo systemInfo = new SystemInfo();
			HardwareAbstractionLayer hal = systemInfo.getHardware();
			infoPopup = new Popup();
			
			setOnMouseEntered(e -> {
				VBox box = new VBox(5);
				box.setPadding(new Insets(10));
				box.setAlignment(Pos.CENTER_LEFT);
				box.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40, 0.9), new CornerRadii(8), Insets.EMPTY)));
				
				Text title = createInfoText("🔋 Battery Info", "", Color.WHITE);
				title.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
				cpuTempText = createInfoText("CPU Temperature: ", lastCPUTemp, Color.LIGHTCORAL);
				gpuTempText = createInfoText("GPU Temperature: ", lastGPUTemp, Color.LIGHTCORAL);
				
				box.getChildren().addAll(
						title,
						createInfoText("Level: ", (int)(level * 100) + "%", Color.LIGHTGREEN),
						createInfoText("Status: ", isCharging ? "Charging" : "Discharging", Color.LIGHTBLUE),
						createInfoText("Time Left: ", batteryEstimatedTime.getText(), Color.ORANGE),
						cpuTempText,
						gpuTempText,
						createInfoText("Voltage: ", hal.getPowerSources().getFirst().getVoltage() + "V", Color.LIGHTYELLOW)
				);
				infoPopup.getContent().clear();
				infoPopup.getContent().add(box);
				infoPopup.show(getScene().getWindow(), e.getScreenX() + 5, e.getScreenY() + 5);
				getTemps();
			});
			setOnMouseExited(_ -> infoPopup.hide());
		}
		
		private Text createInfoText(String label, String value, Color color) {
			Text t = new Text(label + value);
			t.setFill(color);
			t.setFont(Font.font("Times New Roman", 14));
			return t;
		}
		
		private static void getTemps() {
			Thread.ofVirtual().start(() -> {
				String[] temps = getMainPackagesTemp();
				if (temps != null) {
					Platform.runLater(() -> {
						lastCPUTemp = temps[0] + "°C";
						lastGPUTemp = temps[1] + "°C";
						cpuTempText.setText("CPU Temperature: " + temps[0] + "°C");
						gpuTempText.setText("GPU Temperature: " + temps[1] + "°C");
					});
				}
			});
		}
		
		public void setStatus(double level, boolean isCharging) {
			double clamped = Math.max(0, Math.min(1, level));
			if (this.level != clamped || this.isCharging != isCharging) {
				this.level = clamped;
				this.isCharging = isCharging;
				draw();
			}
		}
		
		private void draw() {
			GraphicsContext gc = getGraphicsContext2D();
			double width = 100;
			double height = 25;
			gc.clearRect(0, 0, width + 6, height);
			
			gc.setFill(Color.rgb(0, 0, 0, 0.5));
			gc.fillRoundRect(0, 0, width, height, 10, 10);
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(3);
			gc.strokeRoundRect(1.5, 1.5, width - 3, height - 3, 12, 12);
			gc.setFill(getColor(level, isCharging));
			gc.fillRoundRect(3, 1.5, (width - 8) * level, height - 3, 10, 10);
			
			double headWidth = 6;
			double headHeight = height * 0.45;
			double headX = width + 1;
			double headY = (height - headHeight) / 2;

			LinearGradient gradient = new LinearGradient(
					0, headY, headWidth, headY + headHeight, false, CycleMethod.NO_CYCLE,
					new Stop(0, Color.rgb(80, 80, 80)),
					new Stop(1, Color.rgb(40, 40, 40))
			);
			gc.setFill(gradient);
			gc.fillRoundRect(headX, headY, headWidth, headHeight, 3, 3);

			gc.setStroke(Color.rgb(30, 30, 30));
			gc.setLineWidth(1.2);
			gc.strokeRoundRect(headX, headY, headWidth, headHeight, 3, 3);
			
			gc.setFill(Color.WHITE);
			gc.setFont(Font.font("Arial", height * 0.5));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText(String.format("%.0f%%", level * 100), width / 2, height * 0.68);
		}
		
		private Color getColor(double level, boolean isCharging) {
			if (isCharging) return Color.STEELBLUE;
			if (level >= 0.8) return Color.LIMEGREEN;
			if (level >= 0.6) return Color.DARKORANGE;
			if (level >= 0.3) return Color.GOLDENROD;
			return Color.CRIMSON;
		}
	}
}