package com.battery_level_alarm.monitoring.graphics;
import static com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.status;
import com.battery_level_alarm.monitoring.system_core.Battorion;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalScheduledExecutorService {
    public static Timeline changeTimer;
    static int counter = 1;

    static void createMainScheduledExecutor(){
        createTheTimer();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try{
                batteryLevelForGraphics = Battorion.batteryLevel;
                if(batteryLevelForGraphics != previousLevel){
                    isValueChanged = true;
                }
            } catch (Exception e) {
                batteryLevelForGraphics = previousLevel;
            }

            if (!isRunning) {
                scheduler.shutdown();
                return;
            }

            Platform.runLater(() -> {
                if (isRunning && isValueChanged) {
                    stopTimer();
                    toMinute();
                    addDataPoint(totalTime, elapsedTime, batteryLevelForGraphics);
                    batteryStatusLabel.setText("Battery Status: " + status);
                    startTimer();
                }
                previousLevel = batteryLevelForGraphics;
                isValueChanged = false;
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    static void createTheTimer() {
        changeTimer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (!isRunning) {
                stopTimer();
                return;
            }
            elapsedTime++;
        }));
        changeTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private static void startTimer() {
        elapsedTime = 0;
        if (changeTimer != null) {
            changeTimer.play();
        }
    }

    private static void stopTimer() {
        if (changeTimer != null) {
            changeTimer.stop();
        }
    }

    private static void toMinute(){
        double value = elapsedTime / 60.0;
        totalTime += value;
    }
}