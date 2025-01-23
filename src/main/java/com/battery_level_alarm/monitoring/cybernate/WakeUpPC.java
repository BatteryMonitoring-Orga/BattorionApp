package com.battery_level_alarm.monitoring.cybernate;
import com.battery_level_alarm.monitoring.basics.PC_Details;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                while (PC_Details.getActivateTheAwakeningFeature()) {
                    getMousePosition();
					doRobotAction(robot);
                    try {
                        Thread.sleep(PC_Details.getWakeUpEvery() * 1000L);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(callCommandToWakeUp()));
                        System.out.println("Wake-up Status:");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
			wakeUpThread.start();
	    } catch (Exception e) {
			AutoLogin.printErrorMessage(e);
	    }
	}

	private static boolean checkThread(){
		if(wakeUpThread != null){
			if(!wakeUpThread.isAlive()){
				wakeUpThread.start();
				return true;
			}
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

	private static InputStream callCommandToWakeUp() throws IOException {
		Process process = new ProcessBuilder("cmd.exe", "/c", "powercfg /lastwake").start();
		return process.getInputStream();
	}

	private static void getPC$Status(){

	}
}