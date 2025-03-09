package com.battery_level_alarm.monitoring.basics;
import static com.battery_level_alarm.monitoring.core.BattorionMain.MAIN_FOLDER_NAME;
import com.battery_level_alarm.monitoring.battery_report.HTMLOpener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class StaticQuestionnaire {
	public static void aboutDispatch() {
        JEditorPane aboutEditor = new JEditorPane("text/html",
                "<html><body style='font-family:Serif; font-size:11px;'>"
                        + "<b>About Battorion</b><br><br>"
                        + "<b>Version:</b> 1.0.0<br>"
                        + "<b>Author:</b> Muath Hassoun<br><br>"
                        + "This application monitors your battery level and provides alerts in the following cases:<br>"
                        + "- <b>High Battery</b>: If the level reaches <b>85% or more</b>, you will be asked to unplug the charger.<br>"
                        + "- <b>Low Battery</b>: If the level drops to <b>25% or less</b>, you will be reminded to charge the battery.<br><br>"
                        + "The program operates in the background, periodically checking the battery level.<br>"
                        + "It is compatible with Windows, Linux, and macOS.<br><br>"
                        + "To use the application:<br>"
                        + "- Click '<b>Start</b>' to begin monitoring.<br>"
                        + "- Click '<b>Stop</b>' to halt monitoring.<br><br>"
                        + "Thank you for using Battorion!"
                        + "<p><a href='action:openComprehensiveBatteryGuideInArabic'><b>Comprehensive guide in Arabic</b></a></p>"
                        + "<p><a href='action:openComprehensiveBatteryGuideInEnglish'><b>Comprehensive guide in English</b></a></p>"
                        + "</body></html>"
        );

        Map<String, Runnable> actionsMap = getStringRunnableMap();
        aboutEditorPanelDispatch(
                "About Battorion",
                aboutEditor,
                actionsMap, 600, 400
        );
    }

    private static @NotNull Map<String, Runnable> getStringRunnableMap() {
        Map<String, Runnable> actionsMap = new HashMap<>();
        actionsMap.put("action:openComprehensiveBatteryGuideInArabic",
                () -> HTMLOpener.open(System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - Arabic.html")
        );
        actionsMap.put("action:openComprehensiveBatteryGuideInEnglish",
                () -> HTMLOpener.open(System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - English.html")
        );
        return actionsMap;
    }

    public static String getHowToUseSettings(){
        return """
            <html><body style='font-family:Serif; font-size:11px;'>
            
            <p>This panel allows you to configure various settings related to battery monitoring and alerts:</p>

            <ol>
                <li><b>Minimum Battery Level:</b><br>
                    Define the minimum battery level at which you want to receive alerts. Example: If set to 20%, you'll be notified when the battery reaches this level.</li><br>

                <li><b>Maximum Battery Level:</b><br>
                    Set the maximum battery level for alerts. Example: If set to 85%, you'll get notified to avoid overcharging.</li><br>

                <li><b>Repeat Interval Before Risk Phase (in seconds):</b><br>
                    Set how often the system should check battery status before reaching a critical level. Example: Every 30 seconds.</li><br>

                <li><b>Sound Duration (in seconds):</b><br>
                    Define how long the alert sound should play. Example: Setting it to 10 seconds ensures the sound lasts for 10 seconds.</li><br>

                <li><b>Enable Automatic Monitoring:</b><br>
                    Turn on/off automatic monitoring of battery levels. The system will notify you based on the thresholds set.</li><br>

                <li><b>Enable Primary Sound Alerts:</b><br>
                    Enable sound alerts when battery reaches a critical level (Minimum/Maximum levels).</li><br>

                <li><b>Enable Secondary Sound Alerts:</b><br>
                    Enable periodic sound alerts before reaching a critical condition, reminding you in advance.</li><br>

                <li><b>Enable Charging/Discharging Sound:</b><br>
                    Play a sound when the device is connected/disconnected from the charger.</li><br>

                <li><b>Enable Text Alerts:</b><br>
                    Show text-based alerts on the screen with battery status details.</li><br>

                <li><b>Set the Default Sound:</b><br>
                    Reset the sound path to the default application sound, overriding custom settings.</li><br>

                <li><b>Sound File Path:</b><br>
                    Displays the current alert sound file path, allowing verification or updates.</li><br>

                <li><b>Choose Sound File:</b><br>
                    Select a custom sound file from your device for alerts.</li><br>

                <li><b>Test Alarm Sound:</b><br>
                    Click this button to simulate the alarm sound and check if it works properly.</li>
            </ol>

            <p>Adjust these settings as needed to ensure a seamless battery monitoring experience.</p>

            </body></html>""";
    }

    public static String getTempFilesExplanation() {
        return """
            <html>
                <body style="font-family: Serif, sans-serif; padding: 10px;">
                    <h2>What Are Temporary Files?</h2>
                    <p>
                        Temporary files (<b>Temp files</b>) are created by programs and the operating system\s
                        to store data that is only needed for a limited period of time.
                    </p>
                    <p>
                        These files are usually safe to delete, but in some cases, they might contain\s
                        important data needed for running applications or updating the system.
                    </p><br>
                    <h3>Why Should You Delete Temp Files?</h3>
                    <ul>
                        <li><b>Free up disk space:</b> Over time, Temp files can accumulate and consume disk space.</li>
                        <li><b>Improve system performance:</b> Clearing Temp files can help speed up your system by removing unnecessary files.</li>
                        <li><b>Prevent errors:</b> Sometimes, Temp files can cause application errors or conflicts. Removing them can prevent issues.</li>
                    </ul><br>
                    <p style="color: red; font-weight: bold;">
                        However, be cautious while deleting Temp files, as some files might still be in use or needed by certain programs.
                    </p>
                </body>
            </html>
           \s""";
    }

    public static JEditorPane getComputerSettingsEditorText(){
        return new JEditorPane("text/html",
                "<html><body style='font-family:Serif; font-size:11px;'>" +
                        "<p>This panel provides an intuitive interface to manage automated system behaviors, including:</p>" +

                        "<ol>" +
                        "    <li><b>Wake-Up Features</b>" +
                        "        <ul>" +
                        "            <li><b>Activate Auto Wake-Up:</b><br>" +
                        "                   Enable or disable the automatic wake-up function for your PC.</li>" +
                        "            <li><b>Schedule Wake-Up Intervals:</b><br>" +
                        "                   Set the frequency (in minutes) for automatically waking up your PC.</li>" +
                        "        </ul>" +
                        "    </li>" +
                        "    <li><b>Audio Settings</b>" +
                        "        <ul>" +
                        "            <li><b>Exchange Output to Speaker:</b><br>" +
                        "                   Switch audio output to speakers when a critical battery level is detected.</li>" +
                        "            <li><b>Exchange Output to Last Used Device:</b><br>" +
                        "                   Restore audio output to the previously used device after an event.</li>" +
                        "        </ul>" +
                        "    </li>" +
                        "    <li><b>Additional Settings</b>" +
                        "        <ul>" +
                        "            <li><b>Select Audio Output Device:</b><br>" +
                        "                   Choose the preferred audio output device from the available options.</li>" +
                        "            <li><b>Set Volume Level (%):</b><br>" +
                        "                   Adjust the default system volume to control alert sound intensity.</li>" +
                        "            <li><b>Customize Notification Sounds:</b><br>" +
                        "                   Select and configure system notification sounds to match your preference.</li>" +
                        "        </ul>" +
                        "    </li>" +
                        "</ol>" +

                        "<p>All changes are saved automatically to ensure your settings persist across sessions.</p>" +
                        "<p><a href='action:openSystemTrayNotification'>Click here to open 'About Notification'</a></p>" +
                        "<p><a href='action:openNotificationSound'>Click here to open 'About Notification Sounds'</a></p>" +
                        "</body></html>"
        );
    }

    public static void aboutSystemTrayNotification() {
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Serif; font-size:11px;'>" +
                        "<h2>About System Tray Notification</h2>" +
                        "<p>This feature allows your application to display notifications in the system tray.</p>" +
                        "<p>It includes options for showing alerts, playing sounds, and customizing notification messages.</p>" +

                        "<h3>Key Features:</h3>" +
                        "<ul>" +
                        "    <li><b>Tray Icon:</b><br>" +
                        "           Displays a notification icon in the system tray.</li>" +
                        "    <li><b>Popup Menu:</b><br>" +
                        "           Provides a right-click menu with options (e.g., stop program).</li>" +
                        "    <li><b>Notifications:</b><br>" +
                        "           Shows alerts with custom messages and sound.</li>" +
                        "    <li><b>Timer Control:</b><br>" +
                        "           Allows notifications to be repeated at set intervals.</li>" +
                        "    <li><b>Custom Dialog:</b><br>" +
                        "           Displays either a text message or a JPanel when the icon is clicked.</li>" +
                        "</ul>" +

                        "<p>This feature ensures that important notifications are delivered efficiently without interrupting workflow.</p>" +
                        "</body></html>",
                "System Tray Notification",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aboutPlaySounds() {
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Serif; font-size:11px;'>" +
                        "<h2>About Play Sounds</h2>" +
                        "<p>This feature allows your application to play notification sounds when an alert is triggered.</p>" +

                        "<h3>Key Features:</h3>" +
                        "<ul>" +
                        "    <li><b>Sound Playback:</b><br>" +
                        "           Plays alarm sounds from the resources folder.</li>" +
                        "    <li><b>Error Handling:</b><br>" +
                        "           Displays an error message if the sound file is missing or unsupported.</li>" +
                        "    <li><b>Audio Format Support:</b><br>" +
                        "           Works with various sound file types.</li>" +
                        "</ul>" +

                        "<p>This feature ensures that users are alerted with an audio notification whenever needed.</p>" +
                        "</body></html>",
                "Play Sounds Feature",
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

    public static void aboutNotificationsIcon(){
        JOptionPane.showMessageDialog(null,
                "<html><body style='font-family:Segoe UI Emoji; font-size:10px;'>" +
                        "<p>If a notification is enabled but the notification icon does not appear, notifications may be disabled in system settings.</p>" +
                        "<ul>" +
                        "<li><b>Windows:</b>" +
                        "<ul>" +
                        "<li>Open <b>Settings</b> using <b>Win + I</b>.</li>" +
                        "<li>Go to <b>System</b> and select <b>Notifications</b>.</li>" +
                        "<li>Enable <b>Get notifications from apps and other senders</b>.</li>" +
                        "<li>Customize notifications for each app as needed.</li>" +
                        "</ul>" +
                        "<b>Path:</b> Settings ➡️ System ➡️ Notifications" +
                        "</li>" +
                        "</ul>" +
                        "<li><b>macOS:</b>" +
                        "<ul>" +
                        "<li>Click on the <b>Apple logo</b>  and select <b>System Settings</b>.</li>" +
                        "<li>Navigate to <b>Notifications</b> from the sidebar.</li>" +
                        "<li>Select the desired app and enable notifications.</li>" +
                        "<li>Adjust notification style (Banners, Alerts, or None).</li>" +
                        "</ul>" +
                        "<b>Path:</b> Apple logo ➡️ System Settings ➡️ Notifications" +
                        "</li>" +
                        "</ul>" +
                        "<li><b>Linux (GNOME):</b>" +
                        "<ul>" +
                        "<li>Open <b>Settings</b> from the main menu.</li>" +
                        "<li>Go to <b>Notifications</b>.</li>" +
                        "<li>Enable notifications globally or per application.</li>" +
                        "</ul>" +
                        "<b>Path:</b> Settings ➡️ Notifications" +
                        "</li>" +
                        "</ul>" +
                        "<li><b>Linux (KDE Plasma):</b>" +
                        "<ul>" +
                        "<li>Open <b>System Settings</b> from the application menu.</li>" +
                        "<li>Navigate to <b>Notifications</b>.</li>" +
                        "<li>Enable and configure notifications.</li>" +
                        "</ul>" +
                        "<b>Path:</b> System Settings ➡️ Notifications" +
                        "</li>" +
                        "</ul>" +
                        "<p>By following these steps, notifications can be enabled on different operating systems, ensuring alerts appear when needed.</p>" +
                        "</body></html>",
                "How to Enable Notifications",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static JScrollPane Dispatch(Font textFont, String message) {
        JEditorPane editorPane = new JEditorPane("text/html", message);
        editorPane.setFont(textFont);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setFocusable(false);
        editorPane.setHighlighter(null);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        return scrollPane;
    }

    public static void aboutEditorPanelDispatch(
            String title, JEditorPane messageSupplier,
            Map<String, Runnable> actionsMap, int... dimensions
    ){
        messageSupplier.setEditable(false);
        messageSupplier.setOpaque(false);
        messageSupplier.setFocusable(false);
        messageSupplier.setHighlighter(null);
        messageSupplier.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Runnable action = actionsMap.get(e.getDescription());
                if (action != null) {
                    action.run();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(messageSupplier);
        scrollPane.setPreferredSize(new Dimension(dimensions[0], dimensions[1]));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
            messageSupplier.setCaretPosition(0);
        });
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}