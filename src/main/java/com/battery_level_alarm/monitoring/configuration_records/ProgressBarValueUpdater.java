package com.battery_level_alarm.monitoring.configuration_records;
import javax.swing.*;
import java.util.concurrent.Callable;

public record ProgressBarValueUpdater(
        JProgressBar progressBar,
        boolean[] partialTrueArray,
        int index,
        Callable<Boolean> callable
){}