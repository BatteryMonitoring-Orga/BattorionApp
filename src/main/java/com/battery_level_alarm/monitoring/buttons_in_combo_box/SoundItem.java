package com.battery_level_alarm.monitoring.buttons_in_combo_box;

public record SoundItem(String name) {
    @Override
    public String toString() {
        return name;
    }
}