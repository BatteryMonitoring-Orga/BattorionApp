package com.battery_level_alarm.monitoring.main_folder_manager;

public interface ProgressListener {
    void onProgress(long downloaded, long total);
}