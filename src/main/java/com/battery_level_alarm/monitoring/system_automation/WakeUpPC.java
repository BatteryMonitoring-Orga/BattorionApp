package com.battery_level_alarm.monitoring.system_automation;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.visual_effects.DisplayMessages;
import java.awt.*;

public class WakeUpPC {
	private static Thread wakeUpThread;
	private static int shiftInY_axis = 0;
	private static int shiftInX_axis = 0;

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

	public static void wakeUp() {
		if(!checkThread()){
			return;
		}
	    try {
	        java.awt.Robot robot = new java.awt.Robot();
			wakeUpThread = Thread.ofVirtual().start(() -> {
                while (ComputerSettings.isActivateTheAwakeningFeature()) {
					Point position = getMousePosition();
					doRobotAction(robot, position, false, 0, 0);
					try{
						Thread.sleep(ComputerSettings.getWakeUpEvery() * 60000L);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						break;
					}
                }
            });
	    } catch (Exception e) {
			DisplayMessages.printErrorMessage(e);
	    }
	}

	private static boolean checkThread(){
		if(wakeUpThread == null){
			return true;
		}

		if(wakeUpThread.isInterrupted() || !wakeUpThread.isAlive()){
			wakeUpThread.start();
			return true;
		}
		return false;
	}

	public static Point getMousePosition(){
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point currentMousePosition = pointerInfo.getLocation();
		int xLocal = currentMousePosition.x;
		int yLocal = currentMousePosition.y;
		return new Point(xLocal, yLocal);
	}

	public static void doRobotAction(Robot robot, Point position, boolean shift, int shiftInY_axis, int shiftInX_axis){
		if(shift){
			robot.mouseMove(position.x + shiftInX_axis, position.y + shiftInY_axis);
		} else {
			robot.mouseMove(position.x, position.y);
		}
		robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
		robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
	}
}