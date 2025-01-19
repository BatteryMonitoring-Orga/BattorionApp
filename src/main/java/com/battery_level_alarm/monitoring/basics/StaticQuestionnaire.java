package com.battery_level_alarm.monitoring.basics;

import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StaticQuestionnaire {
	public static void aboutDispatch() {
		JOptionPane.showMessageDialog(null, 
                "<html><body style='font-family:Serif; font-size:11px;'>"
                + "<b>About Battery Level Alarm</b><br><br>"
                + "This application monitors your battery level and provides alerts in the following cases:<br>"
                + "- <b>High Battery</b>: If the level reaches <b>85% or more</b>, you will be asked to unplug the charger.<br>"
                + "- <b>Low Battery</b>: If the level drops to <b>25% or less</b>, you will be reminded to charge the battery.<br><br>"
                + "The program operates in the background, periodically checking the battery level.<br>"
                + "It is compatible with Windows, Linux, and macOS.<br><br>"
                + "To use the application:<br>"
                + "- Click '<b>Start</b>' to begin monitoring.<br>"
                + "- Click '<b>Stop</b>' to halt monitoring.<br><br>"
                + "Thank you for using Battery Level Alarm!</body></html>", 
                "About Battery Level Alarm", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static String getHowToUseSettings(){
		String message = 
                "1. Set Volume Level (%):\n" +
                "   This allows you to set the default volume level for the system. You can choose a percentage value to control how loud the alerts will sound.\n\n" +
                "2. Minimum Battery Level:\n" +
                "   Use this option to define the minimum battery level at which you want to receive alerts. For example, if set to 20%, you’ll be notified when the battery drops to this level.\n\n" +
                "3. Maximum Battery Level:\n" +
                "   This setting lets you define the maximum battery level at which you want to receive alerts. For instance, if set to 85%, you’ll get notified when the battery is fully charged to avoid overcharging.\n\n" +
                "4. Repeat Interval For General Monitoring (in minutes):\n" +
                "   Set the interval in minutes for how often the alert should repeat. For example, you can configure it to remind you every 5 minutes until the condition is resolved.\n\n" +
                "5. Repeat Interval Before Risk Phase (in seconds):\n" +
                "   This option allows you to set the interval in seconds between each monitoring check before reaching the risk phase. For instance, you can configure it to check every 30 seconds before the primary alert sound is triggered at risk values.\n\n" +
                "6. Sound Duration (in seconds):\n" +
                "   Use this setting to define how long the alert sound should play when triggered. For example, setting it to 10 seconds will ensure the sound lasts for 10 seconds.\n\n" +
                "7. Enable Automatic Monitoring:\n" +
                "   Check this option to enable or disable automatic monitoring of battery levels. When enabled, the system will continuously monitor your battery and alert you based on the defined thresholds.\n\n" +
                "8. Enable Primary Sound Alerts:\n" +
                "   Turn this option on to enable the primary sound notifications. These alerts will sound only when the battery reaches a critical condition, such as Minimum Level or Maximum Level levels.\n\n" +
                "9. Enable Secondary Sound Alerts:\n" +
                "   Turn this option on to enable secondary sound notifications. These alerts will sound periodically before the battery reaches a critical condition, reminding you to take action proactively.\n\n" +
                "10. Enable Text Alerts:\n" +
                "    Enable this option to receive text-based alerts on the screen. These notifications will provide details about the battery condition.\n\n" +
                "11. Set the Default Sound:\n" +
                "    Enable this option to reset the sound path to the default sound provided by the application.\n" +
                "    When selected, the default sound file will be used for notifications, overriding any custom sound settings.\n\n" +
                "12. Sound File Path:\n" +
                "    This field shows the current path of the audio file used for alerts. It allows you to verify or update the sound being used.\n\n" +
                "13. Choose Sound File:\n" +
                "    Use this button to select a custom sound file from your device. The selected file will be used as the alert sound.\n\n" +
                "14. Test Alarm Sound:\n" +
                "    Press this button to simulate the alarm sound and verify if it works as expected.";
                
        return message;
	}
	
	public static String getTempFilesExplanation() {
        String message = "Temporary files (Temp files) are created by programs and the operating system to store data that is only needed for a limited period of time.\n\n" +
                         "These files are usually safe to delete, but in some cases, they might contain important data needed for running applications or updating the system.\n\n" +
                         "Here is why deleting Temp files is important:\n" +
                         "1. Free up disk space: Over time, Temp files can accumulate and consume disk space.\n\n" +
                         "2. Improve system performance: Clearing Temp files can help speed up your system by removing unnecessary files.\n\n" +
                         "3. Prevent errors: Sometimes, Temp files can cause application errors or conflicts. Removing them can prevent issues.\n\n" +
                         "However, be cautious while deleting Temp files, as some files might still be in use or needed by certain programs.";
        
        return message;
    }
	
	public static JScrollPane Dispatch(Font textFont, String message) {
    	JTextArea textArea = new JTextArea(message);
    	textArea.setFont(textFont);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        return scrollPane;
	}
}