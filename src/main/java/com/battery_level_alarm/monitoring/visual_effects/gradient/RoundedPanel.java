package com.battery_level_alarm.monitoring.visual_effects.gradient;
import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int cornerRadius;
    private Color borderColor = Color.CYAN;
    private int borderThickness = 0;
    private boolean showBorder = true;
    private Color glowColor = Color.CYAN;
    private int glowSize = 10;
    private boolean isGlowing = false;
    
    public RoundedPanel(int radius, LayoutManager layout) {
        super(layout);
        this.cornerRadius = radius;
        setOpaque(false);
    }
    
    public RoundedPanel(
            LayoutManager layout, boolean showBorder,
            Color color, int radius, int thickness, boolean isGlowing
    ) {
        super(layout);
        this.cornerRadius = radius;
        this.showBorder = showBorder;
        this.borderColor = color;
        this.glowColor = color;
        this.borderThickness = thickness;
        this.glowSize = thickness;
        this.isGlowing = isGlowing;
        setOpaque(false);
    }
    
    public void setRoundedBorderColor(Color color) {
        this.borderColor = color;
        this.glowColor = color;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = cornerRadius;
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        
        if (showBorder && borderColor != null && borderThickness > 0) {
            if(isGlowing && glowColor != null && glowSize > 0){
                for (int i = glowSize; i > 0; i--) {
                    float alpha = (float)(glowSize - i + 1) / glowSize;
                    int maxAlpha = 15;
                    
                    g2.setColor(new Color(
                            glowColor.getRed(),
                            glowColor.getGreen(),
                            glowColor.getBlue(),
                            Math.min(255, (int)(maxAlpha * alpha))
                    ));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, arc, arc);
                }
            }
            
            Color color = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 100);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(borderThickness));
            
            int offset = borderThickness / 2;
            g2.drawRoundRect(
                    offset, offset,
                    getWidth() - borderThickness,
                    getHeight() - borderThickness,
                    arc, arc
            );
        }
        g2.dispose();
        super.paintComponent(g);
    }
}