package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BatteryIconWindow {
	private static Stage primaryIconStage;
	private static BatteryIcon batteryIcon;
	
	public static void show(double level, boolean isCharging) {
		Platform.runLater(() -> {
			if (primaryIconStage == null) {
				batteryIcon = new BatteryIcon();
				batteryIcon.setStatus(level, isCharging);
				
				StackPane root = new StackPane(batteryIcon);
				root.setStyle("-fx-background-color: transparent;");
				Scene scene = new Scene(root, 130, 40);
				scene.setFill(Color.TRANSPARENT);
				
				primaryIconStage = new Stage(StageStyle.TRANSPARENT);
				primaryIconStage.setAlwaysOnTop(true);
				primaryIconStage.setScene(scene);
				primaryIconStage.setX(2);
				primaryIconStage.setY(2);
				primaryIconStage.show();
				primaryIconStage.toFront();
			} else {
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
	
	public static Stage getStage() {
		return primaryIconStage;
	}
	
	private static class BatteryIcon extends Canvas {
		private double level = -1;
		private boolean isCharging = false;
		private final double width = 100;
		private final double height = 25;
		
		public BatteryIcon() {
			super(130, 40);
			setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
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
			gc.clearRect(0, 0, width + 6, height);
			
			gc.setFill(Color.rgb(0, 0, 0, 0.5));
			gc.fillRoundRect(0, 0, width, height, 10, 10);
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(2);
			gc.strokeRoundRect(0, 0, width, height, 10, 10);
			gc.setFill(getColor(level, isCharging));
			gc.fillRoundRect(1.5, 1.5, (width - 8) * level, height - 3, 10, 10);
			
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