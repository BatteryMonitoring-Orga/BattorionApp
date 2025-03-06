package com.battery_level_alarm.monitoring.configuration_records;
import javax.swing.*;
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