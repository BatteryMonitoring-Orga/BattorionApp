package com.battery_level_alarm.monitoring.tray_manager.utils;
import javafx.scene.control.Tooltip;
import java.lang.reflect.Field;
import javafx.util.Duration;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

/*
Tooltip tooltip = new Tooltip("My fast tooltip");
TooltipDelayFixer.setTooltipShowDelay(tooltip, Duration.millis(100));
myNode.setTooltip(tooltip);
 */
public class TooltipDelayFixer {
	public static void setTooltipShowDelay(Tooltip tooltip, Duration openDelay) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);
			
			Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			fieldTimer.setAccessible(true);
			javafx.animation.Timeline objTimer = (javafx.animation.Timeline) fieldTimer.get(objBehavior);
			
			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new javafx.animation.KeyFrame(openDelay));
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
}
