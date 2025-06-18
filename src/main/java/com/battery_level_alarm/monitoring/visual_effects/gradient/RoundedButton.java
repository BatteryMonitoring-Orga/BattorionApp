package com.battery_level_alarm.monitoring.visual_effects.gradient;
import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
	private final int radius;
	private final Dimension dimension;
	private final float strokeWidth;
	
	public RoundedButton(String label, Dimension dimension, float strokeWidth, int radius) {
		super(label);
		this.radius = radius;
		this.dimension = dimension;
		this.strokeWidth = strokeWidth;
		setContentAreaFilled(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		super.paintComponent(g2);
		g2.dispose();
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color background = getBackground();
		Color darkerBorder = background.darker();
//		Color darkerBorder = getDarkerColor(getBackground(), 0.8f);
		g2.setColor(darkerBorder);
		g2.setStroke(new BasicStroke(strokeWidth));
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
		g2.dispose();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}
	
	private Color getDarkerColor(Color color, float factor) {
		int r = Math.max((int)(color.getRed() * factor), 0);
		int g = Math.max((int)(color.getGreen() * factor), 0);
		int b = Math.max((int)(color.getBlue() * factor), 0);
		return new Color(r, g, b);
	}
}