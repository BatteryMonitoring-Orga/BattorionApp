package com.battery_level_alarm.monitoring.basics;

public class DropDownListStaticQuestionnaires {
    public static String getFirstPartialQuestionnaires() {
        return """
                <h2>About General Settings:</h2>
                <p>This list includes general system behaviors to enhance battery monitoring efficiency. You can activate or deactivate essential features such as:</p>
                <ul>
                <li><b>Awakening Feature:</b><br>
                 Prevents your computer from going to sleep, keeping it awake to ensure constant monitoring.</li>
                <li><b>System Notification Sound:</b><br>
                 Enables system notifications, providing auditory alerts for critical battery events.</li>
                <li><b>Automatic Unmute Volume:</b><br>
                 Ensures your system volume is unmuted when an alert is triggered, so you never miss an important warning.</li>
                </ul>
                """;
    }

    public static String getSecondPartialQuestionnaires() {
        return """
                <h2>About Audio Output:</h2>
                <p>This list controls how audio output is managed during and after alerts. You can toggle options to control your sound devices dynamically:</p>
                <ul>
                <li><b>Exchange to Speaker Output:</b><br>
                 Automatically switches audio to the speaker when an alert is fired, ensuring it's loud and clear.</li>
                <li><b>Restore Audio Output Used:</b><br>
                 After the alert is dismissed, the system restores the previously selected audio output, returning to your normal configuration.</li>
                </ul>
                """;
    }

    public static String getThirdPartialQuestionnaires() {
        return """
                <h2>About Sound Level:</h2>
                <p>This list manages sound level adjustments to ensure alerts are noticeable without permanently affecting your settings:</p>
                <ul>
                <li><b>Enable Sound Level Change:</b><br>
                 Adjusts the sound level automatically when an alert is triggered, making sure notifications are heard even if the volume was low.</li>
                <li><b>Restore Sound Level After Alert:</b><br>
                 Once the alert ends, the system reverts the volume to its previous level, maintaining your preferred sound environment.</li>
                </ul>
                """;
    }
}