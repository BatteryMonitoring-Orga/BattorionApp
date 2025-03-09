package com.battery_level_alarm.monitoring.battery_report;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.RESOURCES_PATH;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.batteryReport;
import javax.swing.*;

public class ChooseAction {
    public static void choose() {
        String[] actions = {"open", "create"};
        int action = JOptionPane.showOptionDialog(
                null,
                "Choose action",
                "Choose action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                actions,
                actions[0]
        );

        switch (action) {
            case 0:
                HTMLOpener.open(RESOURCES_PATH);
                break;
            case 1:
                batteryReport();
                break;
        }
    }
}