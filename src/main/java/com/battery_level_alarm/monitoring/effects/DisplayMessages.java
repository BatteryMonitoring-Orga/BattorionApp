package com.battery_level_alarm.monitoring.effects;
import javax.swing.JOptionPane;

public class DisplayMessages {
	public static void printErrorMessage(Throwable e){
		JOptionPane.showMessageDialog(
				null,
				"Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
				"Battery Level Error",
				JOptionPane.ERROR_MESSAGE
		);
	}
}