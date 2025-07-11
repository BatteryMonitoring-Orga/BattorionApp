package com.battery_level_alarm.monitoring.visual_effects.gradient;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

import javax.swing.*;
import java.awt.*;

public class GradientPreview {
    public static JFrame mainPreviewFrame;
    private static JPanel gradientPanel;

    private static Color startPreviewColor = new Color(5, 56, 89);
    private static Color endPreviewColor = new Color(0, 67, 8);
    private static boolean isChanged = false;

    public static Color getStartPreviewColor() {
        return startPreviewColor;
    }
    public static void setStartPreviewColor(Color startPreviewColor) {
        GradientPreview.startPreviewColor = startPreviewColor;
        isChanged = true;
    }
    public static Color getEndPreviewColor() {
        return endPreviewColor;
    }
    public static void setEndPreviewColor(Color endPreviewColor) {
        GradientPreview.endPreviewColor = endPreviewColor;
        isChanged = true;
    }

    public static void newGradientPreview() {
        if(mainPreviewFrame != null){
            mainPreviewFrame.dispose();
        }
        mainPreviewFrame = new JFrame();
        gradientPanel = new JPanel();
        gradientPanel = applyGradientBackground(
                gradientPanel, false, false, 0, true);

        mainPreviewFrame.add(gradientPanel);
        mainPreviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPreviewFrame.setSize(new Dimension(500, 500));
        mainPreviewFrame.setLocation(20, 20);
        mainPreviewFrame.setResizable(false);
        mainPreviewFrame.setVisible(true);
        startPreviewUpdater();
    }

    private static void startPreviewUpdater(){
        Thread.startVirtualThread(() -> {
            while (mainPreviewFrame.isVisible()) {
                if (isChanged) {
                    previewNewColors();
                    isChanged = false;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    printErrorMessage(e);
                }
            }
        });
    }

    private static void previewNewColors(){
        gradientPanel = new JPanel();
        gradientPanel = applyGradientBackground(
                gradientPanel, false, false, 0, true);

        mainPreviewFrame.add(gradientPanel);
        mainPreviewFrame.repaint();
        mainPreviewFrame.revalidate();
    }
}