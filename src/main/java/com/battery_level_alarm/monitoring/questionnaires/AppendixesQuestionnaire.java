package com.battery_level_alarm.monitoring.questionnaires;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.*;

public class AppendixesQuestionnaire {
	public static final Map<String, String> appendicesActionTitle = new HashMap<>();
	static {
		appendicesActionTitle.put("openTab-aboutSelectAudioDevice", OUTPUT_AUDIO_DEVICE);
		appendicesActionTitle.put("openTab-openSystemTrayNotification", SYSTEM_TRAY_NOTIFICATION);
		appendicesActionTitle.put("openTab-openNotificationSound", NOTIFICATION_SOUND);
	}
	
	public static @NotNull Map<String, String> getStringAppendicesMap() {
		Map<String, String> actionsMap = new HashMap<>();
		actionsMap.put("openTab-aboutSelectAudioDevice", AppendixesQuestionnaire.aboutSelectAudioDevice());
		actionsMap.put("openTab-openSystemTrayNotification", AppendixesQuestionnaire.aboutSystemTrayNotification());
		actionsMap.put("openTab-openNotificationSound", AppendixesQuestionnaire.aboutPlaySounds());
		return actionsMap;
	}
	
	public static String aboutSystemTrayNotification() {
		return """
        <html>
          <body style='font-family: Serif, sans-serif; padding: 10px; color:#222;'>
            <h2>About System Tray Notification</h2>

            <p><strong>Core Purpose:</strong> This application continuously monitors your computer’s battery health and status, delivering proactive alerts to help you maintain optimal battery performance and avoid unexpected power interruptions.</p>

            <p>The <strong>System Tray Notification</strong> serves as the central communication hub for the app, offering real-time updates and alerts through the notification area of your desktop. It works silently in the background, keeping you informed without disrupting your work.</p>

            <h3>Key Features:</h3>
            <ul>
              <li><strong>Persistent Tray Icon:</strong><br>
                  An always-visible icon that visually indicates battery status and app activity at a glance.</li>
              <li><strong>Contextual Popup Menu:</strong><br>
                  Right-click the icon to quickly access functions such as opening the main window, generating battery reports, or exiting the program.</li>
              <li><strong>Customizable Notifications:</strong><br>
                  Receive timely alerts with personalized messages and sounds when battery levels cross critical or user-defined thresholds.</li>
              <li><strong>Configurable Alert Timing:</strong><br>
                  Control how frequently notifications are repeated during critical battery conditions, balancing awareness and interruption.</li>
              <li><strong>Interactive Dialogs:</strong><br>
                  Click the tray icon to open detailed panels or messages that provide insights and management tools for your battery.</li>
            </ul>

            <h3>How to Use This Feature Effectively</h3>
            <ul>
              <li><strong>Set Alert Thresholds Wisely:</strong> Customize minimum and maximum battery levels in settings to receive alerts tailored to your usage habits and battery health.</li>
              <li><strong>Enable Both Visual and Sound Alerts:</strong> Combining notifications ensures you won’t miss critical battery warnings, even if you’re away from the screen.</li>
              <li><strong>Use the Tray Icon Menu for Quick Actions:</strong> Access essential controls like report generation or app exit without opening the main interface.</li>
              <li><strong>Adjust Notification Repeat Intervals:</strong> Set intervals that keep you informed without overwhelming you with frequent alerts.</li>
              <li><strong>Regularly Review Battery Reports:</strong> Use the notifications as a prompt to check detailed battery health reports to maintain optimal performance.</li>
            </ul>

            <p>By integrating this feature into your daily routine, you can proactively manage your device's battery health, prevent unexpected shutdowns, and prolong overall battery lifespan.</p>
          </body>
        </html>
        """;
	}
	
	public static String aboutPlaySounds() {
		return """
            <html><body style='font-family: Serif, sans-serif; padding: 10px;'>
            <h2>About Play Sounds</h2>
            <p>This feature allows your application to play notification sounds when an alert is triggered.</p>
    
            <h3>Key Features:</h3>
            <ul>
                <li><b>Sound Playback:</b><br>
                    Plays alarm sounds from the resources folder.</li>
                <li><b>Error Handling:</b><br>
                    Displays an error message if the sound file is missing or unsupported.</li>
                <li><b>Audio Format Support:</b><br>
                    Works with various sound file types.</li>
            </ul>
    
            <p>This feature ensures that users are alerted with an audio notification whenever needed.</p>
            </body></html>
            """;
	}
	
	public static String aboutSelectAudioDevice() {
		return """
            <html>
              <body style='font-family: Serif, sans-serif; padding: 10px; color:#222;'>
                <p>This guide provides step-by-step instructions to access and configure sound settings on various operating systems.</p>
               \s
                <ul>
                  <li><b>On Windows:</b>
                    <p>Click on the Start button or press the Windows key on your keyboard.</p>
                    <p>Navigate to Settings:</p>
                    <ul>
                      <li>If you're using Windows 10: Click on Settings (gear icon).</li>
                      <li>If you're using Windows 11: Search for "Settings" in the search bar or select Settings from the menu.</li>
                    </ul>
                    <p>Select <b>System</b> and then click on <b>Sound</b> from the side menu.</p>
                    <p>From here, you can:</p>
                    <ul>
                      <li>Choose the Output device.</li>
                      <li>Adjust the volume level.</li>
                      <li>Access advanced input and output settings.</li>
                    </ul>
                  </li>
                 \s
                  <br>
                  <li><b>On macOS:</b>
                    <p>Click on the Apple logo () at the top-left corner of the screen.</p>
                    <p>Select <b>System Preferences</b> and then click on <b>Sound</b>.</p>
                    <p>In the <b>Output</b> tab, you can select the audio output device, such as speakers, headphones, or USB devices.</p>
                  </li>
                 \s
                  <br>
                  <li><b>On Linux (Ubuntu example):</b>
                    <p>Open <b>Settings</b> from the top toolbar.</p>
                    <p>Select <b>Sound</b> from the side menu.</p>
                    <p>From here, you can:</p>
                    <ul>
                      <li>Choose the Output device.</li>
                      <li>Adjust the volume and configure other settings.</li>
                    </ul>
                  </li>
                 \s
                  <li><b>Using Shortcuts:</b>
                    <ul>
                      <li><b>Windows:</b> Press <b>Windows + I</b> to open Settings directly, then go to <b>System &gt; Sound</b>.</li>
                      <li>Alternatively, right-click the sound icon in the taskbar and select <b>Sounds</b> or <b>Open Sound settings</b>.</li>
                      <li><b>macOS:</b> Click the sound icon in the top menu bar and select <b>Sound Preferences</b>.</li>
                      <li><b>Linux:</b> Click the sound icon in the top bar and choose <b>Sound Settings</b>.</li>
                    </ul>
                  </li>
                </ul>
               \s
                <p>This guide is designed to simplify the process of managing sound settings across platforms, providing clear steps for users of all levels.</p>
              </body>
            </html>
           \s""";
	}
	
	public static String aboutNotificationsIcon() {
		return """
            <html>
                <body style='font-family: Serif, sans-serif; padding: 10px; color:#222;'>
                    <p>
                        If a notification is enabled but the notification icon does not appear, notifications may be disabled in system settings.
                    </p>
                   \s
                    <ul>
                        <li><b>Windows:</b>
                            <ul>
                                <li>Open <b>Settings</b> using <b>Win + I</b>.</li>
                                <li>Go to <b>System</b> and select <b>Notifications</b>.</li>
                                <li>Enable <b>Get notifications from apps and other senders</b>.</li>
                                <li>Customize notifications for each app as needed.</li>
                            </ul>
                            <p><b>Path:</b> Settings ➡️ System ➡️ Notifications</p>
                        </li>
                       \s
                        <li><b>macOS:</b>
                            <ul>
                                <li>Click on the <b>Apple logo</b>  and select <b>System Settings</b>.</li>
                                <li>Navigate to <b>Notifications</b> from the sidebar.</li>
                                <li>Select the desired app and enable notifications.</li>
                                <li>Adjust notification style (Banners, Alerts, or None).</li>
                            </ul>
                            <p><b>Path:</b> Apple logo ➡️ System Settings ➡️ Notifications</p>
                        </li>
                       \s
                        <li><b>Linux (GNOME):</b>
                            <ul>
                                <li>Open <b>Settings</b> from the main menu.</li>
                                <li>Go to <b>Notifications</b>.</li>
                                <li>Enable notifications globally or per application.</li>
                            </ul>
                            <p><b>Path:</b> Settings ➡️ Notifications</p>
                        </li>
                       \s
                        <li><b>Linux (KDE Plasma):</b>
                            <ul>
                                <li>Open <b>System Settings</b> from the application menu.</li>
                                <li>Navigate to <b>Notifications</b>.</li>
                                <li>Enable and configure notifications.</li>
                            </ul>
                            <p><b>Path:</b> System Settings ➡️ Notifications</p>
                        </li>
                    </ul>
                   \s
                    <p>
                        By following these steps, notifications can be enabled on different operating systems, ensuring alerts appear when needed.
                    </p>
                </body>
            </html>
           \s""";
	}
}
