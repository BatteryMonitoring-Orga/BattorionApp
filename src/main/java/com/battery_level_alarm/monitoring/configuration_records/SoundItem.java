package com.battery_level_alarm.monitoring.configuration_records;

public record SoundItem(String name) {
    @Override
    public String toString() {
        return name;
    }
}