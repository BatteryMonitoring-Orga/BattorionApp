package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BatteryTrayIcon {
	public static TrayIcon trayIcon;
	
	public static void showBatteryTrayIcon(int value, Color color) {
		Image iconImage = createProgressBarIcon(value, color);
		String tooltipText = "Battery Level: " + value + "%";
		
		if (trayIcon == null) {
			trayIcon = new TrayIcon(iconImage, tooltipText);
			trayIcon.setImageAutoSize(true);
			try {
				boolean isAllowToAdd = Boolean.parseBoolean(
						prefs.get("showBatteryIcon", String.valueOf(false))
				);
				if(isAllowToAdd) {
					SystemTray.getSystemTray().add(trayIcon);
				}
			} catch (AWTException e) {
				logger.severe("[EXCEPTION]: " + e.getMessage());
			}
		} else {
			trayIcon.setImage(iconImage);
			trayIcon.setToolTip(tooltipText);
		}
	}
	
	private static Image createProgressBarIcon(int value, Color fillColor) {
		int barWidth = 10;
		int barHeight = 70;
		
		int margin = 2;
		int totalWidth = barWidth + margin * 2;
		int totalHeight = barHeight + margin * 2;
		
		BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(margin, margin, barWidth, barHeight, 4, 4);
		int filledHeight = (int) (barHeight * (value / 100.0));
		int y = margin + barHeight - filledHeight;
		
		g.setColor(fillColor);
		g.fillRoundRect(margin, y, barWidth, filledHeight, 4, 4);
		
		g.setColor(Color.DARK_GRAY);
		g.drawRoundRect(margin, margin, barWidth - 1, barHeight - 1, 4, 4);
		g.dispose();
		return image;
	}
}
