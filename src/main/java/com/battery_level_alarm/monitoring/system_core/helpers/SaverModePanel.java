package com.battery_level_alarm.monitoring.system_core.helpers;
import com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedButton;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.cleanup;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.setVisibleFalse;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.backgroundProcessMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.AS_SYSTEM;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.UIThemesGUI.customizationGradientBackground;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.DARK_GRADIENTS;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.LIGHT_GRADIENTS;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.*;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.getStartCustomColor;

public class SaverModePanel {
	public static void setupSaverModePanel() {
		saverModePanel = new JPanel();
		saverModePanel = applyGradientBackground(saverModePanel, isDarkMode, true, 25, false);
		saverModePanel.setPreferredSize(new Dimension(180, 140));
		saverModePanel.setMaximumSize(new Dimension(180, 140));
		saverModePanel.setLayout(new BoxLayout(saverModePanel, BoxLayout.Y_AXIS));
		saverModePanel.setOpaque(false);
		
		JLabel infoLabel = createSaverModePanelLabel();
		JButton toggleButton = createSaverModePanelButton(140);
		infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		saverModePanel.add(Box.createRigidArea(new Dimension(0, 15)));
		saverModePanel.add(infoLabel);
		saverModePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		saverModePanel.add(toggleButton);
	}
	
	private static JLabel createSaverModePanelLabel() {
		JLabel label = new JLabel("Power Saver Mode", SwingConstants.CENTER);
		label.setFont(new Font("Serif", Font.BOLD, 14));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setOpaque(false);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextArea textArea = getTextArea();
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(320, 160));
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				scrollPane.getViewport().setOpaque(false);
				scrollPane.setOpaque(false);
				
				JOptionPane.showMessageDialog(
						saverModePanel,
						scrollPane,
						"Power Saver Mode Info",
						JOptionPane.INFORMATION_MESSAGE
				);
			}
			
			private static @NotNull JTextArea getTextArea() {
				JTextArea textArea = new JTextArea("""
                    In Power Saver Mode, the application will minimize to the system tray\s
                    and continue running silently in the background.
        
                    This mode is ideal for battery monitoring without keeping the main window open,\s
                    helping to reduce resource usage and power consumption.
        
                    Use the button below to activate or deactivate Power Saver Mode.
                """);
				
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setEditable(false);
				textArea.setFocusable(false);
				textArea.setOpaque(false);
				textArea.setFont(new Font("Serif", Font.PLAIN, 14));
				return textArea;
			}
		});
		return label;
	}
	
	public static JButton createSaverModePanelButton(int buttonWidth) {
		Color backgroundColor;
		if(!customizationGradientBackground) {
			if(isDarkMode) {
				String dark = getGradientBackgroundDarkModeName();
				backgroundColor = DARK_GRADIENTS.get(dark)[0];
			} else {
				String light = getGradientBackgroundLightModeName();
				backgroundColor = LIGHT_GRADIENTS.get(light)[0];
			}
		} else {
			backgroundColor = getStartCustomColor();
		}
		return getButton(backgroundColor, buttonWidth);
	}
	
	private static @NotNull JButton getButton(Color backgroundColor, int buttonWidth) {
		Dimension dimension = new Dimension(buttonWidth, 30);
		JButton button = new RoundedButton("Run in Background", dimension, 1.5f, 30);
		button.setFont(new Font("Serif", Font.BOLD, 13));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBackground(backgroundColor);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
		
		button.addActionListener(_ -> {
			setVisibleFalse();
			
			boolean isFirstTime = Boolean.parseBoolean(prefs.get("IsFirstTimeRunningInBackground", String.valueOf(true)));
			if (isFirstTime) {
				final boolean[] result = {false};
				try {
					java.util.concurrent.FutureTask<Boolean> future = new java.util.concurrent.FutureTask<>(
							BattorionMainProcessHandler::showTrayModeConfirmationDialog);
					Platform.runLater(future);
					result[0] = future.get();
				} catch (Exception ex) {
					logger.severe("[EXCEPTION]: " + ex.getMessage());
					return;
				} if (!result[0]) {
					return;
				}
			}
			
			mainFrame.dispose();
			cleanup(true);
			Platform.setImplicitExit(false);
			Platform.runLater(() -> {
				new BattorionTrayUI().start(new Stage());
				backgroundProcessMonitoring(prefs.get("appTheme", String.valueOf(AS_SYSTEM)));
			});
		});
		return button;
	}
}
