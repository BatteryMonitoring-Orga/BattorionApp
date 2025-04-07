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
    private static Color startCustomColor;
    private static Color endCustomColor;
    private static Color appliedStartColor;
    private static Color appliedEndColor;

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
    public static Color getAppliedStartColor() {
        return appliedStartColor;
    }
    public static Color getAppliedEndColor() {
        return appliedEndColor;
    }

    public static void setStartCustomColor(Color startCustomColor) {
        PanelStyler.startCustomColor = startCustomColor;
    }
    public static void setEndCustomColor(Color endCustomColor) {
        PanelStyler.endCustomColor = endCustomColor;
    }

    public static JPanel applyGradientBackground(
            JPanel panel, boolean isDarkMode, boolean isRoundedCorner, int cornerRadius, boolean isForPreview
    ){
        panel.setOpaque(false);
        panel.paintComponents(panel.getGraphics());
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                getGradientBackgroundColors(isDarkMode, isForPreview);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                g2d.setColor(getBackground());

                GradientPaint gradient = new GradientPaint(0, 0, appliedStartColor, width, height, appliedEndColor);
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

    private static void getGradientBackgroundColors(boolean isDarkMode, boolean isForPreview){
        if (isDarkMode && !isForPreview && !customizationGradientBackground) {
            Color[] colors = DARK_GRADIENTS.get(gradientBackgroundDarkModeName);
            if(colors == null){
                colors = DARK_GRADIENTS.get("BloodEmber");
            }
            appliedStartColor = colors[0];
            appliedEndColor = colors[1];
        } else if (!isForPreview && !customizationGradientBackground){
            Color[] colors = LIGHT_GRADIENTS.get(gradientBackgroundLightModeName);
            if(colors == null){
                colors = LIGHT_GRADIENTS.get("FrozenRose");
            }
            appliedStartColor = colors[0];
            appliedEndColor = colors[1];
        } else if (!isForPreview){
            appliedStartColor = startCustomColor;
            appliedEndColor = endCustomColor;
        } else {
            appliedStartColor = getStartPreviewColor();
            appliedEndColor = getEndPreviewColor();
        }
    }
}