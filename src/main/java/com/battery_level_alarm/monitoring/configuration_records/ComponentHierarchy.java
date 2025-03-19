package com.battery_level_alarm.monitoring.configuration_records;
import javax.swing.*;

public record ComponentHierarchy(
        Runnable renovated,
        int indexOfEditChild,
        JFrame frame,
        JPanel container,
        JComponent... children
){}