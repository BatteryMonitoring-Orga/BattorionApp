package com.battery_level_alarm.monitoring.system_automation;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages;

import java.awt.*;
import java.util.concurrent.*;

public class WakeUpPC {
	private static ScheduledExecutorService keepAwakeExecutor;
	private static int shiftInY_axis = 0;
	private static int shiftInX_axis = 0;
	private static volatile boolean interruptRequest = false;
	
	public static int getShiftInY_axis() {
		return shiftInY_axis;
	}
	
	public static void setShiftInY_axis(int shiftInY_axis) {
		WakeUpPC.shiftInY_axis = shiftInY_axis;
	}
	
	public static int getShiftInX_axis() {
		return shiftInX_axis;
	}
	
	public static void setShiftInX_axis(int shiftInX_axis) {
		WakeUpPC.shiftInX_axis = shiftInX_axis;
	}
	
	public static void wakeUp(long wakeUpEverySeconds) {
		if (keepAwakeExecutor != null && !keepAwakeExecutor.isShutdown()) {
			return;
		}
		
		interruptRequest = false;
		try {
			Robot robot = new Robot();
			keepAwakeExecutor = Executors.newSingleThreadScheduledExecutor();
			keepAwakeExecutor.scheduleAtFixedRate(() -> {
				if (ComputerSettings.isActivateTheAwakeningFeature() && !interruptRequest) {
					try {
						Point position = getMousePosition();
						doRobotAction(robot, position, false, 0, 0);
					} catch (Exception e) {
						logger.severe("[EXCEPTION]: " + e.getMessage());
					}
				} else {
					shutdownScheduler();
				}
			}, 0, wakeUpEverySeconds, TimeUnit.SECONDS);
		} catch (AWTException e) {
			DisplayMessages.printErrorMessage(e);
		}
	}
	
	public static void wakeUpThreadInterruptRequest() {
		interruptRequest = true;
		shutdownScheduler();
	}
	
	private static void shutdownScheduler() {
		if (keepAwakeExecutor != null && !keepAwakeExecutor.isShutdown()) {
			keepAwakeExecutor.shutdownNow();
			try {
				if (!keepAwakeExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
					logger.warning("Scheduler did not terminate in time.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.severe("[EXCEPTION]: " + e.getMessage());
			}
			keepAwakeExecutor = null;
		}
	}
	
	public static Point getMousePosition() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point currentMousePosition = pointerInfo.getLocation();
		return new Point(currentMousePosition.x, currentMousePosition.y);
	}
	
	public static void doRobotAction(Robot robot, Point position, boolean shift, int shiftInY_axis, int shiftInX_axis) {
		if (shift) {
			robot.mouseMove(position.x + shiftInX_axis, position.y + shiftInY_axis);
		} else {
			robot.mouseMove(position.x, position.y);
		}
		robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
		robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
	}
}