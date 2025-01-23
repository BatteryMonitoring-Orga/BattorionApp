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
        return """
                1. Minimum Battery Level:
                   Use this option to define the minimum battery level at which you want to receive alerts. For example, if set to 20%, you’ll be notified when the battery drops to this level.
                
                2. Maximum Battery Level:
                   This setting lets you define the maximum battery level at which you want to receive alerts. For instance, if set to 85%, you’ll get notified when the battery is fully charged to avoid overcharging.
                
                3. Repeat Interval Before Risk Phase (in seconds):
                   This option allows you to set the interval in seconds between each monitoring check before reaching the risk phase. For instance, you can configure it to check every 30 seconds before the primary alert sound is triggered at risk values.
                
                4. Sound Duration (in seconds):
                   Use this setting to define how long the alert sound should play when triggered. For example, setting it to 10 seconds will ensure the sound lasts for 10 seconds.
                
                5. Enable Automatic Monitoring:
                   Check this option to enable or disable automatic monitoring of battery levels. When enabled, the system will continuously monitor your battery and alert you based on the defined thresholds.
                
                6. Enable Primary Sound Alerts:
                   Turn this option on to enable the primary sound notifications. These alerts will sound only when the battery reaches a critical condition, such as Minimum Level or Maximum Level levels.
                
                7. Enable Secondary Sound Alerts:
                   Turn this option on to enable secondary sound notifications. These alerts will sound periodically before the battery reaches a critical condition, reminding you to take action proactively.
                
                8. Enable Charging/Discharging Sound:
                   Plays a sound notification when the device is connected to or disconnected from the charger.
                
                9. Enable Text Alerts:
                   Enable this option to receive text-based alerts on the screen. These notifications will provide details about the battery condition.
                
                10. Set the Default Sound:
                    Enable this option to reset the sound path to the default sound provided by the application.
                    When selected, the default sound file will be used for notifications, overriding any custom sound settings.
                
                11. Sound File Path:
                    This field shows the current path of the audio file used for alerts. It allows you to verify or update the sound being used.
                
                12. Choose Sound File:
                    Use this button to select a custom sound file from your device. The selected file will be used as the alert sound.
                
                13. Test Alarm Sound:
                    Press this button to simulate the alarm sound and verify if it works as expected.""";
	}
	
	public static String getTempFilesExplanation() {
        return """
                Temporary files (Temp files) are created by programs and the operating system to store data that is only needed for a limited period of time.
                
                These files are usually safe to delete, but in some cases, they might contain important data needed for running applications or updating the system.
                
                Here is why deleting Temp files is important:
                1. Free up disk space: Over time, Temp files can accumulate and consume disk space.
                
                2. Improve system performance: Clearing Temp files can help speed up your system by removing unnecessary files.
                
                3. Prevent errors: Sometimes, Temp files can cause application errors or conflicts. Removing them can prevent issues.
                
                However, be cautious while deleting Temp files, as some files might still be in use or needed by certain programs.""";
    }

    public static void aboutPC$DetailsDispatch() {
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Serif; font-size:11px;'>" +
                        "<p>This panel is designed to provide an interactive interface for managing your PC's automatic</p>" +
                        "<p>wake-up feature, password settings, and scheduling.</p>" +
                        "<ul>" +
                        "<li><b>Activate the Wake-Up Feature:</b> Toggle the automatic wake-up feature for your PC on or off</li>" +
                        "<p>using a switch button.</p>" +
                        "<li><b>Switch audio output to Speakers:</b> configures the system to route audio output to the speakers</li>" +
                        "<p>specifically when a critical battery level is detected, ensuring an alert is played through the </p>" +
                        "<p>appropriate output device.</p>" +
                        "<li><b>Set Your PC Password:</b> Enter your PC's password to ensure security and proper functionality</li>" +
                        "<p>of the wake-up feature.</p>" +
                        "<li><b>Schedule Wake-Up Intervals:</b> Specify the interval (in minutes) for automatically waking up </li>" +
                        "<p>your PC to perform scheduled tasks.</p>" +
                        "<li><b>Set Volume Level (%):</b> This allows you to set the default volume level for the system.</li>" +
                        "<p>You can choose a percentage value to control how loud the alerts will sound.</p>" +
                        "<li><b>Save Settings Automatically:</b> All changes are saved to a dedicated file, ensuring your </li>" +
                        "<p>preferences persist across sessions.</p>" +
                        "</ul>" +
                        "<p>This panel is designed to be user-friendly and flexible, leveraging Swing's robust</p>" +
                        "<p>components to deliver a seamless experience.</p>" +
                        "</body></html>",
                "About PC Details Panel",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aboutSoundSettingsGuide(){
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Segoe UI Emoji; font-size:10px;'>" +
                        "<p>This guide provides step-by-step instructions to access and configure sound settings on</p>" +
                        "<p>various operating systems.</p>" +
                        "<ul>" +
                        "<li><b>On Windows:</b></li>" +
                        "<p>Click on the Start button or press the Windows key on your keyboard.</p>" +
                        "<p>Navigate to Settings:</p>" +
                        "<ul>" +
                        "<li>If you're using Windows 10: Click on Settings (gear icon).</li>" +
                        "<li>If you're using Windows 11: Search for \"Settings\" in the search bar or select Settings from the menu.</li>" +
                        "</ul>" +
                        "<p>Select System and then click on Sound from the side menu.</p>" +
                        "<p>From here, you can:</p>" +
                        "<ul>" +
                        "<li>Choose the Output device.</li>" +
                        "<li>Adjust the volume level.</li>" +
                        "<li>Access advanced input and output settings.</li>" +
                        "</ul>" +
                        "<li><b>On macOS:</b></li>" +
                        "<p>Click on the Apple logo () at the top-left corner of the screen.</p>" +
                        "<p>Select System Preferences and then click on Sound.</p>" +
                        "<p>In the Output tab, you can select the audio output device, such as speakers, headphones, or USB devices.</p>" +
                        "<li><b>On Linux (Ubuntu example):</b></li>" +
                        "<p>Open Settings from the top toolbar.</p>" +
                        "<p>Select Sound from the side menu.</p>" +
                        "<p>From here, you can:</p>" +
                        "<ul>" +
                        "<li>Choose the Output device.</li>" +
                        "<li>Adjust the volume and configure other settings.</li>" +
                        "</ul>" +
                        "<li><b>Using Shortcuts:</b></li>" +
                        "<ul>" +
                        "<li><b>Windows:</b> Press Windows + I to open Settings directly, then go to System > Sound.</li>" +
                        "<li>Alternatively, right-click the sound icon in the taskbar and select Sounds or Open Sound settings.</li>" +
                        "<li><b>macOS:</b> Click the sound icon in the top menu bar and select Sound Preferences.</li>" +
                        "<li><b>Linux:</b> Click the sound icon in the top bar and choose Sound Settings.</li>" +
                        "</ul>" +
                        "</ul>" +
                        "<p>This guide is designed to simplify the process of managing sound settings across platforms,</p>" +
                        "<p>providing clear steps for users of all levels.</p>" +
                        "</body></html>",
                "Sound Settings Guide",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aboutBatteryStatisticsPanel() {
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Serif; font-size:11px;'>" +
                        "<p>This panel provides a comprehensive interface for monitoring and analyzing battery performance in real-time.</p>" +
                        "<ul>" +
                        "<li><b>Battery Statistics:</b> Displays detailed information including:" +
                        "<ul>" +
                        "<li>Current battery states (Charging or Discharging).</li>" +
                        "<li>The sharpest difference in battery levels during charging or discharging.</li>" +
                        "<li>The time required to fully charge or discharge the battery.</li>" +
                        "<li>Battery levels at the start and end of the charging/discharging process.</li>" +
                        "</ul>" +
                        "</li>" +
                        "<li><b>History:</b> Provides insights into past battery activity, such as:" +
                        "<ul>" +
                        "<li>Charging History: Analyzes the frequency of changes in charging levels.</li>" +
                        "<li>Discharging History: Offers a similar analysis for discharging activities.</li>" +
                        "</ul>" +
                        "</li>" +
                        "<li><b>Interactive Controls:</b> Includes buttons for viewing historical data and accessing PC-specific details.</li>" +
                        "</ul>" +
                        "<p>This panel is designed to enhance user understanding of battery behavior through detailed statistics and interactive features.</p>" +
                        "</body></html>",
                "About Battery Statistics Panel",
                JOptionPane.INFORMATION_MESSAGE);
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