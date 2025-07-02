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
            <html><body style='font-family: Serif, sans-serif; padding: 10px;'>
            <h2>About System Tray Notification</h2>
            <p>This feature allows your application to display notifications in the system tray.</p>
            <p>It includes options for showing alerts, playing sounds, and customizing notification messages.</p>
    
            <h3>Key Features:</h3>
            <ul>
                <li><b>Tray Icon:</b><br>
                    Displays a notification icon in the system tray.</li>
                <li><b>Popup Menu:</b><br>
                    Provides a right-click menu with options (e.g., stop program).</li>
                <li><b>Notifications:</b><br>
                    Shows alerts with custom messages and sound.</li>
                <li><b>Timer Control:</b><br>
                    Allows notifications to be repeated at set intervals.</li>
                <li><b>Custom Dialog:</b><br>
                    Displays either a text message or a JPanel when the icon is clicked.</li>
            </ul>
    
            <p>This feature ensures that important notifications are delivered efficiently without interrupting workflow.</p>
            </body></html>
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
