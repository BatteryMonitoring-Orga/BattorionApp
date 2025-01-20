package com.battery_level_alarm.monitoring.cybernate;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

public class AutoLogin {
	public static void loginToPC() {
		try {
	        Robot robot = new Robot();
	        
	        String password = "password";
	        for (char c : password.toCharArray()) {
	            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
	            if (KeyEvent.CHAR_UNDEFINED != keyCode) {
	                robot.keyPress(keyCode);
	                robot.keyRelease(keyCode);
	                System.out.print(keyCode);
	            }
	            Thread.sleep(50);
	        }
	        
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
		} catch(Exception e) {
			JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
                    "Battery Level Error",
                    JOptionPane.ERROR_MESSAGE
            );
		}
	}
}