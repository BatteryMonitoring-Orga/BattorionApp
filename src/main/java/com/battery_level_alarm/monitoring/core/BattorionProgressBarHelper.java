package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.core.BattorionMain.*;

import javax.swing.*;
import java.awt.*;

public class BattorionProgressBarHelper {
    public static void setProgressBarMode(){
        batteryBar.setMinimum(0);
        batteryBar.setMaximum(100);

        if(progressBarInVerticalMode){
            configureProgressBarVertical();
        } else {
            configureProgressBarHorizontal();
        }
    }

    private static void configureProgressBarVertical() {
        batteryBar.setOrientation(JProgressBar.VERTICAL);
        Dimension size = new Dimension(85, 200);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    private static void configureProgressBarHorizontal() {
        batteryBar.setOrientation(JProgressBar.HORIZONTAL);
        Dimension size = new Dimension(225, 85);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    public static void setUpProgressPanel(){
        if(progressBarInVerticalMode){
            progressPanelForVerticalMode();
        } else {
            progressPanelForHorizontalMode();
        }
    }

    private static void progressPanelForVerticalMode(){
        progressPanel.removeAll();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
        progressPanel.add(Box.createHorizontalGlue());
        progressPanel.add(ratioChargeLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        progressPanel.add(batteryBar);
        progressPanel.add(Box.createHorizontalGlue());
        progressPanel.setBackground(UIManager.getColor("Panel.Background"));
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private static void progressPanelForHorizontalMode(){
        progressPanel.removeAll();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(Box.createVerticalGlue());
        progressPanel.add(batteryBar);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        progressPanel.add(ratioChargeLabel);
        progressPanel.add(Box.createVerticalGlue());
        progressPanel.setBackground(UIManager.getColor("Panel.Background"));
        progressPanel.revalidate();
        progressPanel.repaint();
    }
}