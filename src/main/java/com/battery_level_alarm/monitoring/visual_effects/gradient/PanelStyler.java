package com.battery_level_alarm.monitoring.visual_effects.gradient;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.UIThemesGUI.customizationGradientBackground;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.getEndPreviewColor;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.getStartPreviewColor;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.DARK_GRADIENTS;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.LIGHT_GRADIENTS;

import javax.swing.*;
import java.awt.*;

public class PanelStyler {
    private static String gradientBackgroundDarkModeName;
    private static String gradientBackgroundLightModeName;
    private static Color startColor;
    private static Color endColor;

    public static String getGradientBackgroundDarkModeName(){
        return gradientBackgroundDarkModeName;
    }
    public static void setGradientBackgroundDarkModeName(String gradientBackgroundDarkModeName) {
        PanelStyler.gradientBackgroundDarkModeName = gradientBackgroundDarkModeName;
    }
    public static String getGradientBackgroundLightModeName(){
        return gradientBackgroundLightModeName;
    }
    public static void setGradientBackgroundLightModeName(String gradientBackgroundLightModeName) {
        PanelStyler.gradientBackgroundLightModeName = gradientBackgroundLightModeName;
    }
    public static Color getStartColor() {
        return startColor;
    }
    public static Color getEndColor() {
        return endColor;
    }

    public static JPanel applyGradientBackground(
            JPanel panel, boolean isDarkMode, boolean isRoundedCorner, int cornerRadius, boolean isForPreview
    ){
        if (isDarkMode && !isForPreview && !customizationGradientBackground) {
            Color[] colors = DARK_GRADIENTS.get(gradientBackgroundDarkModeName);
            if(colors == null){
                colors = DARK_GRADIENTS.get("BloodEmber");
            }
            startColor = colors[0];
            endColor = colors[1];
        } else if (!isForPreview && !customizationGradientBackground){
            Color[] colors = LIGHT_GRADIENTS.get(gradientBackgroundLightModeName);
            if(colors == null){
                colors = LIGHT_GRADIENTS.get("FrozenRose");
            }
            startColor = colors[0];
            endColor = colors[1];
        } else if (!isForPreview){
            startColor = getStartPreviewColor();
            endColor = getEndPreviewColor();
        } else {
            startColor = getStartPreviewColor();
            endColor = getEndPreviewColor();
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