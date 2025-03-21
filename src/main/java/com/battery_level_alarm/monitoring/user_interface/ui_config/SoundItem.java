package com.battery_level_alarm.monitoring.user_interface.ui_config;

public record SoundItem(String name) {
    @Override
    public String toString() {
        return name;
    }
}