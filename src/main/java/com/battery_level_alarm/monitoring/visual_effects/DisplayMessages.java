package com.battery_level_alarm.monitoring.visual_effects;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import javax.swing.JOptionPane;

public class DisplayMessages {
	public static void printErrorMessage(Throwable e){
		JOptionPane.showMessageDialog(
				mainFrame,
				"Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
				"Battery Level Error",
				JOptionPane.ERROR_MESSAGE
		);
	}
}