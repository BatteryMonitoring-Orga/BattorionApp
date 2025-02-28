package com.battery_level_alarm.monitoring.cybernate;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.effects.DisplayMessages;
import java.awt.*;

public class WakeUpPC {
	private static Thread wakeUpThread;
    private static int x;
	private static int y;

	public static void wakeUp() {
		if(!checkThread()){
			return;
		}
	    try {
	        java.awt.Robot robot = new java.awt.Robot();
			wakeUpThread = Thread.ofVirtual().start(() -> {
                while (ComputerSettings.isActivateTheAwakeningFeature()) {
                    getMousePosition();
					doRobotAction(robot);
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

	private static void getMousePosition(){
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point currentMousePosition = pointerInfo.getLocation();
		x = currentMousePosition.x;
		y = currentMousePosition.y;
	}

	private static void doRobotAction(Robot robot){
		robot.mouseMove(x, y);
		robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
		robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
	}
}