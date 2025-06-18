package com.battery_level_alarm.monitoring.user_interface.ui_config;

import org.jetbrains.annotations.NotNull;

public record SoundItem(String name) {
    @Override
    public @NotNull String toString() {
        return name;
    }
}