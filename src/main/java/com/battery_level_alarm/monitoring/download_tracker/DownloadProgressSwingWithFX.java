package com.battery_level_alarm.monitoring.download_tracker;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.*;
import java.awt.*;

public class DownloadProgressSwingWithFX {
    private static JFXPanel fxPanel;
    private static JPanel downloadProgressPanel;

    public DownloadProgressSwingWithFX(Color color) {
        downloadProgressPanel = new JPanel();
        downloadProgressPanel.setOpaque(false);

        fxPanel = new JFXPanel();
        downloadProgressPanel.add(fxPanel);
        downloadProgressPanel.setVisible(true);
        javafx.scene.paint.Color jFxColor = javafx.scene.paint.Color.color(
                color.getRed() / 255.0,
                color.getGreen() / 255.0,
                color.getBlue() / 255.0
        );

        javafx.scene.paint.Color jFxColorReverse = javafx.scene.paint.Color.color(
                1.0 - jFxColor.getRed(),
                1.0 - jFxColor.getGreen(),
                1.0 - jFxColor.getBlue()
        );

        Platform.setImplicitExit(false);
        Platform.runLater(() -> updateJavaFXScene(jFxColor, jFxColorReverse));
    }

    private void updateJavaFXScene(javafx.scene.paint.Color jFxColor, javafx.scene.paint.Color jFxColorReverse) {
        DownloadProgressFX progressFX = new DownloadProgressFX(jFxColorReverse);
        Scene newScene = progressFX.createScene(jFxColor);

        if (fxPanel.getScene() != null) {
            fxPanel.setScene(null);
        }
        fxPanel.setScene(newScene);
    }
    
    public JPanel getDownloadProgressPanel() {
        return downloadProgressPanel;
    }
}