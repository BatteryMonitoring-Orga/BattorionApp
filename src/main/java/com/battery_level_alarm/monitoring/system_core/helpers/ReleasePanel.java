package com.battery_level_alarm.monitoring.system_core.helpers;
import com.battery_level_alarm.monitoring.versions_manager.update_ui.AppReleaseNotify;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.latestVersion;
import static com.battery_level_alarm.monitoring.system_core.Battorion.releasePanel;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.versions_manager.update_ui.AppReleaseNotify.getNotifyBox;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

public class ReleasePanel {
	public static void setupReleasePanel() {
		releasePanel = new JPanel();
		releasePanel = applyGradientBackground(releasePanel, isDarkMode, true, 25, false);
		releasePanel.setPreferredSize(new Dimension(180, 140));
		releasePanel.setMaximumSize(new Dimension(180, 140));
		releasePanel.setLayout(new BoxLayout(releasePanel, BoxLayout.Y_AXIS));
		releasePanel.setOpaque(false);
		
		AppReleaseNotify.latestVersionApp = latestVersion;
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setOpaque(false);
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			VBox fxContent = getNotifyBox();
			fxContent.setBackground(Background.EMPTY);
			Scene scene = new Scene(fxContent);
			scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			
			SwingUtilities.invokeLater(() -> {
				jfxPanel.setScene(scene);
				releasePanel.add(jfxPanel);
				releasePanel.repaint();
				releasePanel.revalidate();
				
				jfxPanel.repaint();
				jfxPanel.requestFocusInWindow();
			});
		});
	}
}
