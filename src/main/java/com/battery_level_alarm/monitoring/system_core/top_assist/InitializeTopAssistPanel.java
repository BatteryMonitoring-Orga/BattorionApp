package com.battery_level_alarm.monitoring.system_core.top_assist;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.getCurrentAudioDevice;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.getLayoutModeID;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.MainComponentsCreator.createLabel;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.createHBoxPanel;
import static com.battery_level_alarm.monitoring.system_core.top_assist.TopAssistPanel.createTopAssistPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.addTextField;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.setMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.ONE_SPACE;

public class InitializeTopAssistPanel {
	private static JButton topAssistMenuButton = new JButton();
	static JPopupMenu assistMenu;
	
	/** Initializes the entire top assist section */
	public static void initializeStatusPanel() {
		rebuildTopAssistContainer();
		mainFrame.add(motherFrameContainer);
	}
	
	/** Clears and rebuilds the motherFrameContainer without removing it from the mainFrame */
	public static void rebuildTopAssistContainer() {
		motherFrameContainer.removeAll();
		boolean popupLayout = getLayoutModeID() == 3;
		boolean toggleLayout = getLayoutModeID() == 4;
		
		JPanel topAssistStatusPanel = createStatusPanel(toggleLayout);
		JPanel topComponentsContainer = createTopComponentsContainer(popupLayout, toggleLayout, topAssistStatusPanel);
		
		motherPanelContainer.removeAll();
		motherPanelContainer.add(topComponentsContainer);
		motherPanelContainer.add(Box.createRigidArea(new Dimension(0, 15)));
		createHBoxPanel();
		motherPanelContainer.add(HBoxPanel);
		
		addFramePadding();
		motherFrameContainer.add(motherPanelContainer, BorderLayout.CENTER);
		motherFrameContainer.revalidate();
		motherFrameContainer.repaint();
	}
	
	/** Creates the status panel containing battery status and audio info */
	private static JPanel createStatusPanel(boolean toggleLayout) {
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, toggleLayout ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
		
		statusLabel = createLabel(
				ONE_SPACE + "Battery Status: (" + batteryLevel + "%), " + status + " ",
				18, Font.PLAIN + Font.BOLD
		);
		getBatteryMode(getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel()));
		lastMode = status;
		
		JLabel audioLabel = addLabel(
				new GridBagConstraints(), new JPanel(),
				(toggleLayout ? ONE_SPACE : "| ") + "Audio Output: ",
				new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 18)
		);
		
		audioOutputDeviceDashTextField = addTextField(new GridBagConstraints(), new JPanel(),
				getCurrentAudioDevice(), 200, 25, null, false);
		audioOutputDeviceDashTextField.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
		setMouseListener(
				audioOutputDeviceDashTextField,
				BattorionPanelHelper::audioLabelMouseAction,
				UIManager.getColor("TextField.Foreground"),
				new Color(0, 134, 179),
				false, false, true
		);
		
		JPanel audioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		audioPanel.add(audioLabel);
		audioPanel.add(audioOutputDeviceDashTextField);
		
		if (toggleLayout) {
			JPanel statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			statusLabelPanel.add(statusLabel);
			statusPanel.add(statusLabelPanel);
		} else {
			statusPanel.add(statusLabel);
		}
		
		statusPanel.add(audioPanel);
		return statusPanel;
	}
	
	/** Creates the top components container including the assist panel or popup/toggle menu */
	private static JPanel createTopComponentsContainer(boolean popupLayout, boolean toggleLayout, JPanel statusPanel) {
		JPanel container = new RoundedPanel(30, new BorderLayout());
		container.setLayout(new BoxLayout(container, (toggleLayout || popupLayout) ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
		container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		container.add(statusPanel);
		
		JPanel topAssistPanel = createTopAssistPanel(
				getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel())
		);
		
		if (popupLayout) {
			createTopAssistMenuButton(topAssistPanel);
			container.add(topAssistMenuButton);
		} else if (toggleLayout) {
			createTopAssistMenuButton(topAssistPanel);
			JPanel externalContainer = new JPanel(new BorderLayout());
			JPanel internalContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			internalContainer.add(topAssistMenuButton);
			externalContainer.add(internalContainer, BorderLayout.SOUTH);
			container.add(externalContainer);
		} else {
			container.add(topAssistPanel);
		}
		
		return container;
	}
	
	/** Creates the menu button for assist options */
	private static void createTopAssistMenuButton(JPanel topAssistPanel) {
		topAssistMenuButton = BattorionButtonHelper.createButton(
				"Open quick assist menu",
				ASSETS_FOLDER_PATH,
				"menu.png",
				_ -> {
					assistMenu = new JPopupMenu();
					assistMenu.setBorder(BorderFactory.createEmptyBorder());
					assistMenu.add(topAssistPanel);
					assistMenu.show(topAssistMenuButton, -365, topAssistMenuButton.getHeight());
				}
		);
	}
	
	/** Adds padding to the motherFrameContainer */
	private static void addFramePadding() {
		motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.WEST);
		motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.EAST);
		motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.NORTH);
		motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.SOUTH);
	}
}
