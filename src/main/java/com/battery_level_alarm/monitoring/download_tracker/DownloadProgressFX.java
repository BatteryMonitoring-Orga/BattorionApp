package com.battery_level_alarm.monitoring.download_tracker;
import com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import java.util.Objects;

public class DownloadProgressFX {
    private static final String DOWNLOAD_PROGRESS_PNG = "/com/battery_level_alarm/monitoring/Images/Download.png";
    private static double progressValue = 0.0;
    private static ImageView downloadIcon;
    private static Arc progressArc;
    private final Group root;

    public DownloadProgressFX(Color color) {
        double radius = 12;
        double strokeWidth = 2;
        Tooltip tooltip = new Tooltip("Download the essential tools");

        progressArc = new Arc();
        progressArc.setRadiusX(radius);
        progressArc.setRadiusY(radius);
        progressArc.setCenterX(radius + 1);
        progressArc.setCenterY(radius + 1);
        progressArc.setStartAngle(90);
        progressArc.setLength(0);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(color);
        progressArc.setStrokeWidth(strokeWidth);
        progressArc.setType(ArcType.OPEN);
        progressArc.setStrokeType(StrokeType.CENTERED);
        progressArc.setCursor(Cursor.HAND);
        Tooltip.install(progressArc, tooltip);

        downloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(DOWNLOAD_PROGRESS_PNG)).toExternalForm()));
        downloadIcon.setFitWidth(23);
        downloadIcon.setFitHeight(23);
        downloadIcon.setOpacity(0.85);
        downloadIcon.setX(3);
        downloadIcon.setY(0);
        downloadIcon.setCursor(Cursor.HAND);
        Tooltip.install(downloadIcon, tooltip);

        DoubleProperty progress = new SimpleDoubleProperty(0);
        progress.addListener((_, _, newVal) -> progressArc.setLength(-360 * newVal.doubleValue()));

        EventHandler<MouseEvent> action = _ -> setAction(color);
        downloadIcon.setOnMouseClicked(action);

        EventHandler<MouseEvent> mouseEnteredHandler = _ -> setWhenMouseEntered();
        downloadIcon.setOnMouseEntered(mouseEnteredHandler);

        EventHandler<MouseEvent> mouseExitedHandler = _ -> setWhenMouseExited();
        downloadIcon.setOnMouseExited(mouseExitedHandler);

        root = new Group(progressArc, downloadIcon);
    }

    private static void startDownload(Color color) {
        progressValue = 0.0;
        progressArc.setVisible(true);
        progressArc.setLength(0);

        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() {
                EssentialToolsDownloader.Downloader((downloaded, total) -> {
                    if (total > 0) {
                        progressValue = (double) downloaded / total;
                        updateProgress(progressValue, 1.0);
                        Platform.runLater(() -> {
                            if (progressValue == 1.0) {
                                progressArc.setLength(-360);
                                progressArc.setStroke(Color.MEDIUMSPRINGGREEN);
                                PauseTransition pause = new PauseTransition(Duration.millis(2000));
                                pause.setOnFinished(_ -> {
                                    progressArc.setVisible(false);
                                    this.cancel();
                                });
                                pause.play();
                            } else {
                                progressArc.setLength(-360 * progressValue);
                                progressArc.setStroke(color);
                            }
                        });
                    }
                }, false);
                return null;
            }
        };
        new Thread(downloadTask).start();
    }

    public Scene createScene(Color color) {
        return new Scene(root, 30, 30, color);
    }

    public static void setWhenMouseEntered(){
        downloadIcon.setScaleX(1.3);
        downloadIcon.setScaleY(1.3);
        downloadIcon.setOpacity(1.0);
    }
    public static void setWhenMouseExited(){
        downloadIcon.setScaleX(1.0);
        downloadIcon.setScaleY(1.0);
        downloadIcon.setOpacity(0.85);
    }
    public static void setAction(Color color){
        startDownload(color);
    }
}