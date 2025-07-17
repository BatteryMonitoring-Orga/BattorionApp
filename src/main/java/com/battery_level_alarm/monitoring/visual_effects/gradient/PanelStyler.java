package com.battery_level_alarm.monitoring.visual_effects.gradient;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.DEFAULT_DARK_GRADIENT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.DEFAULT_LIGHT_GRADIENT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.UIThemesGUI.customizationGradientBackground;
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
    private static Color[] currentGradientColors = new Color[]{
            new Color(0x0f2027),
            new Color(0x203a43),
            new Color(0x2c5364)
    };
    
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

    public static Color getStartCustomColor() {
        return startCustomColor;
    }
    public static Color getEndCustomColor() {
        return endCustomColor;
    }
    public static void setStartCustomColor(Color startCustomColor) {
        PanelStyler.startCustomColor = startCustomColor;
    }
    public static void setEndCustomColor(Color endCustomColor) {
        PanelStyler.endCustomColor = endCustomColor;
    }

    public static JPanel applyGradientBackground(
            JPanel panel, boolean isDarkMode, boolean isRoundedCorner, int cornerRadius, boolean isForPreview
    ) {
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
                
                float[] fractions = new float[currentGradientColors.length];
                for (int i = 0; i < fractions.length; i++) {
                    fractions[i] = (float) i / (fractions.length - 1);
                }
                
                LinearGradientPaint paint = new LinearGradientPaint(
                        0, 0, width, height,
                        fractions,
                        currentGradientColors
                );
                
                g2d.setPaint(paint);
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
                colors = DARK_GRADIENTS.get(DEFAULT_DARK_GRADIENT);
            }
            currentGradientColors = colors;
        } else if (!isForPreview && !customizationGradientBackground){
            Color[] colors = LIGHT_GRADIENTS.get(gradientBackgroundLightModeName);
            if(colors == null){
                colors = LIGHT_GRADIENTS.get(DEFAULT_LIGHT_GRADIENT);
            }
            currentGradientColors = colors;
        } else if (!isForPreview){
            currentGradientColors = new Color[]{ startCustomColor, endCustomColor };
        } else {
            currentGradientColors = new Color[]{ getStartPreviewColor(), getEndPreviewColor() };
        }
    }
}