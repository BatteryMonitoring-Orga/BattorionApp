package com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.SETTINGS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.TRAY_INTEGRATION;
import static com.battery_level_alarm.monitoring.system_core.Battorion.DashboardPanel;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.GRID_BAG_CONSTRAINTS_CONFIGURATION;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.START_BATTORION_WITH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.HYPERLINK_HOVER_COLOR;
import static com.battery_level_alarm.monitoring.system_core.helpers.SaverModePanel.createSaverModePanelButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.DepartureModes.START_WITH_APPLICATION;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.DepartureModes.START_WITH_TRAY;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.getTrayUI;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.createGridBagConstraints;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.addCheckbox;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.setButtonDefaultSize;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabelWithMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.DEFAULT_FONT;

public class TraySettingsGUI {
	public static final Font DEFAULT_TITLE_FONT = new Font("Serif", Font.BOLD, 25);
	private static final ScrollConfiguration TRAY_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
			false, true, true, false, null,
			new Dimension(600, 350)
	);
	private static final ScrollConfiguration CARDS_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
			false, true, true, true, null,
			new Dimension(550, 280)
	);
	
	private static CardLayout cardLayout;
	private static JPanel cardsPanel;
	
	static JScrollPane createTraySettingsGUI() {
		JPanel mainTrayPanel = new JPanel();
		mainTrayPanel.setLayout(new BoxLayout(mainTrayPanel, BoxLayout.Y_AXIS));
		mainTrayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainTrayPanel.add(createCardSelectorButtons());
		
		cardLayout = new CardLayout();
		cardsPanel = new JPanel(cardLayout);
		cardsPanel.setOpaque(true);
		
		cardsPanel.add(createFirstCardPanel(), "header");
		cardsPanel.add(createTrayPreviewPanel(), "preview");
		mainTrayPanel.add(cardsPanel);
		
		JScrollPane scrollPaneContainer = new JScrollPane(mainTrayPanel);
		applyScrollConfigurationDetails(scrollPaneContainer, TRAY_SET_SCROLL_CONFIGURATION);
		return scrollPaneContainer;
	}
	
	private static JPanel createCardSelectorButtons() {
		JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		switchPanel.setOpaque(true);
		
		JButton settingsBtn = new JButton("Header");
		JButton previewBtn = new JButton("Preview");
		settingsBtn.setFont(DEFAULT_FONT);
		previewBtn.setFont(DEFAULT_FONT);
		settingsBtn.addActionListener(_ -> cardLayout.show(cardsPanel, "header"));
		previewBtn.addActionListener(_ -> cardLayout.show(cardsPanel, "preview"));
		
		switchPanel.add(settingsBtn);
		switchPanel.add(previewBtn);
		return switchPanel;
	}
	
	private static JScrollPane createFirstCardPanel() {
		JPanel trayMainNorthPanel = new JPanel();
		trayMainNorthPanel.setLayout(new BoxLayout(trayMainNorthPanel, BoxLayout.Y_AXIS));
		trayMainNorthPanel.setOpaque(true);
		trayMainNorthPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		trayMainNorthPanel.setPreferredSize(new Dimension(350, 200));
		
		JLabel titleLabel = new JLabel("Tray Icon Preview Settings");
		titleLabel.setFont(DEFAULT_TITLE_FONT);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		trayMainNorthPanel.add(titleLabel);
		trayMainNorthPanel.add(Box.createVerticalStrut(15));
		
		JButton saverButton = createSaverModePanelButton(200);
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttonContainer.setOpaque(false);
		buttonContainer.add(saverButton);
		trayMainNorthPanel.add(buttonContainer);
		trayMainNorthPanel.add(Box.createVerticalStrut(15));
		
		JPanel checkBoxContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		checkBoxContainer.setOpaque(false);
		checkBoxContainer.add(createCheckBoxesPanel());
		trayMainNorthPanel.add(checkBoxContainer);
		trayMainNorthPanel.add(Box.createVerticalStrut(10));
		
		JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		aboutPanel.setOpaque(false);
		addLabelWithMouseListener(
				new GridBagConstraints(),
				aboutPanel,
				"About Tray Panel ",
				HYPERLINK_HOVER_COLOR,
				() -> Thread.ofVirtual().start(() -> launchAndOpenTopic(SETTINGS_QUESTIONNAIRE, 950)),
				DEFAULT_FONT
		);
		trayMainNorthPanel.add(aboutPanel);
		trayMainNorthPanel.add(Box.createVerticalStrut(5));
		
		JPanel aboutTrayIntegration = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		aboutTrayIntegration.setOpaque(false);
		addLabelWithMouseListener(
				new GridBagConstraints(),
				aboutTrayIntegration,
				"About Tray Integration ",
				HYPERLINK_HOVER_COLOR,
				() -> Thread.ofVirtual().start(() -> launchAndOpenTopic(TRAY_INTEGRATION, 0)),
				DEFAULT_FONT
		);
		trayMainNorthPanel.add(aboutTrayIntegration);
		
		JScrollPane trayMainSettingsPanel = new JScrollPane(trayMainNorthPanel);
		applyScrollConfigurationDetails(trayMainSettingsPanel, CARDS_SET_SCROLL_CONFIGURATION);
		trayMainSettingsPanel.setBackground(DashboardPanel.getBackground());
		return trayMainSettingsPanel;
	}
	
	private static JPanel createCheckBoxesPanel() {
		JPanel checkBoxesPanel = new JPanel(new GridBagLayout());
		checkBoxesPanel.setOpaque(true);
		GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
		
		String modeToUse = prefs.get(START_BATTORION_WITH, String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_APPLICATION));
		boolean isChecked = modeToUse.equals(String.valueOf(START_WITH_TRAY));
		addCheckbox(
				gbc, checkBoxesPanel, "Start with Tray window", isChecked,
				e -> {
					JCheckBox source = (JCheckBox) e.getSource();
					boolean selected = source.isSelected();
					String newMode = selected ? String.valueOf(START_WITH_TRAY) : String.valueOf(START_WITH_APPLICATION);
					prefs.put(START_BATTORION_WITH, newMode);
				}
		);
		return checkBoxesPanel;
	}
	
	private static JScrollPane createTrayPreviewPanel() {
		JPanel trayPreviewPanel = new JPanel();
		trayPreviewPanel.setOpaque(true);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(true);
		
		JFXPanel jfxPanel = new JFXPanel();
		jfxPanel.setOpaque(true);
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			Scene scene = getTrayUI();
			scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			
			SwingUtilities.invokeLater(() -> {
				jfxPanel.setScene(scene);
				contentPanel.add(jfxPanel, BorderLayout.CENTER);
				trayPreviewPanel.add(contentPanel);
				trayPreviewPanel.revalidate();
				trayPreviewPanel.repaint();
			});
		});
		
		JScrollPane trayPreviewScroll = new JScrollPane(trayPreviewPanel);
		applyScrollConfigurationDetails(trayPreviewScroll, CARDS_SET_SCROLL_CONFIGURATION);
		return trayPreviewScroll;
	}
}