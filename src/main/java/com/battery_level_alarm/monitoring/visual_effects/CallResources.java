package com.battery_level_alarm.monitoring.visual_effects;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;

import com.battery_level_alarm.monitoring.system_core.Battorion;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;

import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;

public class CallResources {
    private static final String DEFAULT_EXTEND = ".png";
	public static ImageIcon getImage(
            String parentFolder, String imageName,
            Dimension dimension, int hints
    ) {
        String iconName = imageName;
        if (!imageName.contains(".")) {
            iconName = imageName + DEFAULT_EXTEND;
        }
        
		URL resource = CallResources.class.getResource( parentFolder + iconName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found: " + parentFolder + imageName + ".png");
        } if (iconName.endsWith(".svg")) {
            try {
                TranscoderInput input = new TranscoderInput(resource.toString());
                BufferedImageTranscoder t = new BufferedImageTranscoder();
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) dimension.getWidth());
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) dimension.getHeight());
                t.transcode(input, null);
                BufferedImage rawImg = t.getBufferedImage();
                
                Image finalImage = getScaledImage(rawImg, (int) dimension.getWidth(), (int) dimension.getHeight());
                return new ImageIcon(finalImage);
            } catch (Exception e) {
                logger.severe("[EXCEPTION]: " + e.getMessage());
            }
        }
        
        ImageIcon icon = new ImageIcon(resource);
        return new ImageIcon(icon.getImage().getScaledInstance(
                (int) dimension.getWidth(), (int) dimension.getHeight(), hints));
	}

    public static Image getScaledImage(Image srcImg, int w, int h) {
        if (srcImg == null) {
            throw new IllegalArgumentException("Source image does not exist!");
        }
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    public static ImageIcon getGif(String parentFolder, String gifName) {
        String path = parentFolder + gifName + ".gif";
        URL resource = Battorion.class.getResource(path);

        if (resource == null) {
            System.err.println("Error: File not found - " + path);
            throw new IllegalArgumentException("File not found: " + path);
        }

        return new ImageIcon(resource);
    }
}