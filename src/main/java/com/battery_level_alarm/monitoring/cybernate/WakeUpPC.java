package com.battery_level_alarm.monitoring.cybernate;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WakeUpPC {
	public static void wakeUp() {
	    try {
	        java.awt.Robot robot = new java.awt.Robot();
	        robot.mouseMove(100, 100);
	        robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
	        robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
	        
	        Thread.sleep(2000);
	        
	        Process process = new ProcessBuilder("cmd.exe", "/c", "powercfg /lastwake").start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
	        System.out.println("Wake-up Status:");
	        String line;
	        while ((line = reader.readLine()) != null) {
	            System.out.println(line);
	        }
	    } catch (Exception e) {
	        System.err.println("Error waking up the PC: " + e.getMessage());
	    }
	}
}