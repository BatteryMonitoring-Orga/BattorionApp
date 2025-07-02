package com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static com.battery_level_alarm.monitoring.graphics.ui.BatteryGraphController.buildGraphController;
import static com.battery_level_alarm.monitoring.graphics.ui.BatteryGraphController.toCssColor;
import static com.battery_level_alarm.monitoring.system_core.Battorion.DashboardPanel;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.CSS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;

public class GraphSettingsGUI {
	private static final ScrollConfiguration GRAPH_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
			false, true, true, false, null,
			new Dimension(600, 350)
	);
	
	static JScrollPane createGraphSettingsGUI() {
		Color awtColor = DashboardPanel.getBackground();
		String cssColor = toCssColor(awtColor);
		String backgroundStyle = "-fx-background-color: " + cssColor + " !important;";
		
		JPanel graphSettingsPanel = new JPanel();
		graphSettingsPanel.setBackground(awtColor);
		
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setBackground(awtColor);
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			BorderPane fxContent = buildGraphController();
			fxContent.setBackground(Background.EMPTY);
			fxContent.setStyle(backgroundStyle);
			
			Scene scene = new Scene(fxContent);
			if(isDarkMode) {
				scene.getStylesheets().add(Objects.requireNonNull(GraphSettingsGUI.class.getResource(
						CSS_FOLDER_PATH + "graph-controller-dark-theme.css")).toExternalForm());
			} else {
				scene.getStylesheets().add(Objects.requireNonNull(GraphSettingsGUI.class.getResource(
						CSS_FOLDER_PATH + "graph-controller-light-theme.css")).toExternalForm());
			}
			
			scene.setFill(javafx.scene.paint.Color.web(cssColor));
			SwingUtilities.invokeLater(() -> {
				jfxPanel.setScene(scene);
				jfxPanel.repaint();
				jfxPanel.requestFocusInWindow();
				
				graphSettingsPanel.add(jfxPanel);
				graphSettingsPanel.repaint();
				graphSettingsPanel.revalidate();
			});
		});
		
		JScrollPane graphSettingsScroll = new JScrollPane(graphSettingsPanel);
		applyScrollConfigurationDetails(graphSettingsScroll, GRAPH_SET_SCROLL_CONFIGURATION);
		graphSettingsScroll.setBackground(awtColor);
		return graphSettingsScroll;
	}
}
