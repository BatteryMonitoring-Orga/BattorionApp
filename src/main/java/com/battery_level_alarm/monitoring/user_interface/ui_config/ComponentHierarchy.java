package com.battery_level_alarm.monitoring.user_interface.ui_config;
import javax.swing.*;

public record ComponentHierarchy(
        Runnable renovated,
        int indexOfEditChild,
        JFrame frame,
        JPanel container,
        JComponent... children
){}