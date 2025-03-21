package com.battery_level_alarm.monitoring.user_interface.ui_config;
import javax.swing.*;
import java.util.concurrent.Callable;

public record ProgressBarValueUpdater(
        JProgressBar progressBar,
        boolean[] partialTrueArray,
        int index,
        Callable<Boolean> callable,
        JComponent[] components
){}