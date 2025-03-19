package com.battery_level_alarm.monitoring.configuration_records;
import java.awt.*;
import java.util.function.Consumer;

public record ToggleButtonRecord(
        Consumer<Boolean> stateChangeHandler,
        Runnable saveAction,
        String value,
        Dimension dimension
){}