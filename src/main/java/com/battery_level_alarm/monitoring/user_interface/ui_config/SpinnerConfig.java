package com.battery_level_alarm.monitoring.user_interface.ui_config;
import javax.swing.event.ChangeListener;

public record SpinnerConfig(
        String label,
        int currentValue,
        int defaultValue,
        int min,
        int max,
        int step,
        int row,
        int column,
        int width,
        int height,
        ChangeListener listener
){}