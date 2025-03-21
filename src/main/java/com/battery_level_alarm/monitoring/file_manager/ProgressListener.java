package com.battery_level_alarm.monitoring.file_manager;

public interface ProgressListener {
    void onProgress(long downloaded, long total);
}