package com.battery_level_alarm.monitoring.system_core.helpers;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.APP_THEME;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.TIME_RUNNING_IN_BACKGROUND;
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
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

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
			boolean isFirstTime = Boolean.parseBoolean(prefs.get(TIME_RUNNING_IN_BACKGROUND, String.valueOf(true)));
			if (isFirstTime) {
				final boolean[] result = {false};
				try {
					java.util.concurrent.FutureTask<Boolean> future = new java.util.concurrent.FutureTask<>(
							SaverModePanel::showTrayModeConfirmationDialog);
					Platform.runLater(future);
					result[0] = future.get();
				} catch (Exception ex) {
					printErrorMessage(ex);
					return;
				} if (!result[0]) {
					return;
				}
			}
			
			mainFrame.dispose();
			cleanup(true);
			Platform.setImplicitExit(false);
			Platform.runLater(() -> {
				Monitor.isShouldUpdateTrayDashboard = true;
				new BattorionTrayUI().start(new Stage());
				backgroundProcessMonitoring(prefs.get(APP_THEME, String.valueOf(AS_SYSTEM)));
			});
		});
		return button;
	}
	
	private static boolean showTrayModeConfirmationDialog() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("PSM Mode Information");
		alert.setHeaderText("You are now switching to Power Saver Mode (PSM)");
		
		Label contentLabel = getContentLabel();
		contentLabel.setWrapText(true);
		CheckBox confirmBox = new CheckBox("I have read and understood the information");
		VBox dialogContent = new VBox(10, contentLabel, confirmBox);
		dialogContent.setPadding(new Insets(10));
		alert.getDialogPane().setContent(dialogContent);
		
		ButtonType okButtonType = new ButtonType("Switch to PSM", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButtonType, cancelButtonType);
		
		Button okButton = (Button) alert.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);
		confirmBox.selectedProperty().addListener((_, _, isChecked) ->
				okButton.setDisable(!isChecked));
		
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == okButtonType;
	}
	
	private static @NotNull Label getContentLabel() {
		String content = """
		In Tray Mode:
		• Some alerts may not be available.
		• Notification efficiency is reduced compared to normal mode.
		• Value changes and updates may take slightly longer to reflect.
		
		However, this mode is designed to:
		✓ Greatly reduce CPU usage.
		✓ Minimize power consumption.
		✓ Run silently in the background.
		
		Note:
		The program already runs efficiently and consumes minimal resources.
		But with PSM Mode, power and CPU usage become almost zero —
		making it the most energy-saving mode available.
		""";
		return new Label(content);
	}
}
