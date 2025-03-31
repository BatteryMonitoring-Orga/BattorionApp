package com.battery_level_alarm.monitoring.visual_effects;
import javax.swing.*;
import java.awt.*;

public class PanelStyler {
    public static JPanel applyGradientBackground(
            JPanel panel, boolean isDarkMode, boolean isRoundedCorner, int cornerRadius
    ){
        Color startColor, endColor;

        if (isDarkMode) {
            startColor = new Color(0, 90, 145);
            endColor = new Color(0, 100, 10);
        } else {
            startColor = new Color(180, 220, 255);
            endColor = new Color(255, 180, 200);
        }

        panel.setOpaque(false);
        panel.paintComponents(panel.getGraphics());

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                g2d.setColor(getBackground());

                GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
                g2d.setPaint(gradient);
                if (isRoundedCorner) {
                    g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                } else {
                    g2d.fillRect(0, 0, width, height);
                }
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        return panel;
    }
}