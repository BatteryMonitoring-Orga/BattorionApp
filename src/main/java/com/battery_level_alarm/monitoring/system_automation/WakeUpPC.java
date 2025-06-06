package com.battery_level_alarm.monitoring.system_automation;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.visual_effects.DisplayMessages;
import java.awt.*;

import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

public class WakeUpPC {
	public static Thread wakeUpThread;
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

	public static void wakeUp() {
		if(!checkThread()){
			return;
		}
	    try {
	        java.awt.Robot robot = new java.awt.Robot();
			wakeUpThread = Thread.ofVirtual().start(() -> {
                while (ComputerSettings.isActivateTheAwakeningFeature() && !interruptRequest) {
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
	
	public static void wakeUpThreadInterruptRequest() {
		try {
			interruptRequest = true;
			if (wakeUpThread != null && wakeUpThread.isAlive()) {
				wakeUpThread.join(3000);
				
				if (wakeUpThread.isAlive()) {
					wakeUpThread.interrupt();
				}
			}
		} catch (Exception ex) {
			printErrorMessage(ex);
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