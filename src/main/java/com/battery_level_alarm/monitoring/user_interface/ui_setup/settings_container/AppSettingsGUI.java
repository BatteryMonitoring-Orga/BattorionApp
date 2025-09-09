package com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.SETTINGS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.HYPERLINK_HOVER_COLOR;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabelWithMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.TITLE_LISTS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList.prepareListsContainer;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.addTextInScroll;
import static com.battery_level_alarm.monitoring.notifications.alerts.AlertSound.DEFAULT_PRIMARY_SOUND_PATH;
import static com.battery_level_alarm.monitoring.notifications.alerts.AlertSound.DEFAULT_SECONDARY_SOUND_PATH;

import com.battery_level_alarm.monitoring.user_interface.ui_config.DropDownListsContainerRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SingleDropDownListRecord;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.notifications.alerts.AlertSound;
import org.jdesktop.swingx.border.DropShadowBorder;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AppSettingsGUI {
    private static String primarySoundPath = DEFAULT_PRIMARY_SOUND_PATH;
    private static String secondarySoundPath = DEFAULT_SECONDARY_SOUND_PATH;
    private static boolean soundPlayed = false;
    
    private static JScrollPane CreatedGUI;
    private static final ScrollConfiguration SCROLL_TEXT_FIELD_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            false,
            false,
            null,
            new Dimension(550, 45)
    );
    private static final ScrollConfiguration SCROLL_PANEL_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            true,
            false,
            null,
            new Dimension(600, 350)
    );

    public static final JPanel[] APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY = {
            new JPanel(), new JPanel(), new JPanel(), new JPanel()
    };

    static JScrollPane getApplicationSettingsGUI() {
    	return CreatedGUI;
    }

    static void createAndShowGUI() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
        
        JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setButtonDefaultSize();
        setDimension(0, 0);
        addLabelWithMouseListener(
                gbc, aboutPanel, "About App Settings Panel ", HYPERLINK_HOVER_COLOR,
                () -> Thread.ofVirtual().start(() -> launchAndOpenTopic(SETTINGS_QUESTIONNAIRE, 0)), DEFAULT_FONT
        );
        
        createAppSettingsDropDownListConfigurations(gbc);
        DropDownListsContainerRecord containerRecord = new DropDownListsContainerRecord(
                "   Do these procedures automatically:",
                TITLE_LISTS_FONT,
                computerSettingsContainerListShadow,
                APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY,
                new SingleDropDownListRecord[]{
                        APP_SETTINGS_FIRST_DDL,
                        APP_SETTINGS_SECOND_DDL,
                        APP_SETTINGS_THIRD_DDL,
                        APP_SETTINGS_FOURTH_DDL
                }
        );
        
        int index = 0;
        JPanel firstPartPanel = prepareListsContainer(containerRecord);
        JPanel secondPartPanel = getSecondPartPanel(gbc, index);
        JPanel thirdPartPanel = getThirdPartPanel(gbc, index);
        returnGBC$ToDefault(gbc);
        settingsPanel.add(aboutPanel);
        settingsPanel.add(firstPartPanel);
        settingsPanel.add(secondPartPanel);
        settingsPanel.add(thirdPartPanel);
        
        CreatedGUI = new JScrollPane(settingsPanel);
        applyScrollConfigurationDetails(CreatedGUI, SCROLL_PANEL_CONFIGURATION);
    }

    private static JPanel getSecondPartPanel(GridBagConstraints gbc, int index){
        JPanel secondPartPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        pathField.setOpaque(false);
        southPanel.add(
                addTextInScroll(
                        pathField, UserChoices.getPrimarySoundPath(),
                        DEFAULT_FONT, false, false,
                        SCROLL_TEXT_FIELD_CONFIGURATION
                )
        );
        pathField.setBorder(new DropShadowBorder(
                DropDownList.borderForegroundColor, 2, 0.2f, 2,
                false, false, true, false
        ));

        setDimension(index, 0);
        addSeparator(gbc, centerPanel, 150);
        returnGBC$ToDefault(gbc);
        boolean isSelected = DEFAULT_PRIMARY_SOUND_PATH.equals(UserChoices.getPrimarySoundPath());
        setDimension(++index, 0);
        addLabel(gbc,centerPanel, "Primary Sound Configuration ", DEFAULT_FONT);
        
        setColumn(2);
        JCheckBox defaultSoundCheckBox = addCheckbox(
                gbc, centerPanel, "Select the default sound", isSelected,
                _ -> {
                    UserChoices.setPrimarySoundPath(DEFAULT_PRIMARY_SOUND_PATH);
                    pathField.setText(DEFAULT_PRIMARY_SOUND_PATH);
                    ConfigurationFilesManager.saveSettings();
                    BattorionPanelHelper.refreshSettingsPanel();
                });
        
        setDimension(++index, 0);
        addLabel(gbc, centerPanel, "Sound File Path: ", DEFAULT_FONT);
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003", DEFAULT_FONT);
        setDimension(index, 2);
        setButtonSize(200, 30);
        addButton(gbc, centerPanel, "Choose Sound",
                _ -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Audio Files", "wav", "mp3"));
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        primarySoundPath = fileChooser.getSelectedFile().getAbsolutePath();
                        if(DEFAULT_PRIMARY_SOUND_PATH.equals(primarySoundPath)) {
                            defaultSoundCheckBox.setSelected(true);
                            pathField.setText(DEFAULT_PRIMARY_SOUND_PATH);
                        } else {
                            defaultSoundCheckBox.setSelected(false);
                            pathField.setText(primarySoundPath);
                        }

                        UserChoices.setPrimarySoundPath(primarySoundPath);
                        ConfigurationFilesManager.saveSettings();
                        BattorionPanelHelper.refreshSettingsPanel();
                    }
                });

        setDimension(++index, 0);
        addLabel(gbc, centerPanel, "Simulation of alarm sound: ", DEFAULT_FONT);
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003", DEFAULT_FONT);
        setButtonDefaultSize();
        setDimension(index, 2);
        addButton(gbc, centerPanel, "  ⏯  ",
                _ -> {
                    if (soundPlayed) {
                        return;
                    }

                    soundPlayed = true;
                    Thread playThread = new Thread(() -> {
                        AlertSound.playSound(UserChoices.getPrimarySoundPath());
                        soundPlayed = false;
                    });
                    playThread.start();
                });

        setButtonDefaultSize();
        secondPartPanel.add(centerPanel, BorderLayout.CENTER);
        secondPartPanel.add(southPanel, BorderLayout.SOUTH);
        return secondPartPanel;
    }
    
    private static JPanel getThirdPartPanel(GridBagConstraints gbc, int index){
        JPanel thirdPartPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        pathField.setOpaque(false);
        southPanel.add(
                addTextInScroll(
                        pathField, UserChoices.getSecondarySoundPath(),
                        DEFAULT_FONT, false, false,
                        SCROLL_TEXT_FIELD_CONFIGURATION
                )
        );
        pathField.setBorder(new DropShadowBorder(
                DropDownList.borderForegroundColor, 2, 0.2f, 2,
                false, false, true, false
        ));
        
        setDimension(index, 0);
        addSeparator(gbc, centerPanel, 150);
        returnGBC$ToDefault(gbc);
        boolean isSelected = DEFAULT_SECONDARY_SOUND_PATH.equals(UserChoices.getSecondarySoundPath());
        setDimension(++index, 0);
        addLabel(gbc,centerPanel, "Secondary Sound Configuration ", DEFAULT_FONT);
        
        setColumn(2);
        JCheckBox defaultSoundCheckBox = addCheckbox(
                gbc, centerPanel, "Select the default sound", isSelected,
                _ -> {
                    UserChoices.setSecondarySoundPath(DEFAULT_SECONDARY_SOUND_PATH);
                    pathField.setText(DEFAULT_SECONDARY_SOUND_PATH);
                    ConfigurationFilesManager.saveSettings();
                    BattorionPanelHelper.refreshSettingsPanel();
                });
        
        setDimension(++index, 0);
        addLabel(gbc, centerPanel, "Sound File Path: ", DEFAULT_FONT);
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003", DEFAULT_FONT);
        setDimension(index, 2);
        setButtonSize(200, 30);
        addButton(gbc, centerPanel, "Choose Sound",
                _ -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Audio Files", "wav", "mp3"));
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        secondarySoundPath = fileChooser.getSelectedFile().getAbsolutePath();
                        if(DEFAULT_SECONDARY_SOUND_PATH.equals(secondarySoundPath)) {
                            defaultSoundCheckBox.setSelected(true);
                            pathField.setText(DEFAULT_SECONDARY_SOUND_PATH);
                        } else {
                            defaultSoundCheckBox.setSelected(false);
                            pathField.setText(secondarySoundPath);
                        }
                        
                        UserChoices.setSecondarySoundPath(secondarySoundPath);
                        ConfigurationFilesManager.saveSettings();
                        BattorionPanelHelper.refreshSettingsPanel();
                    }
                });
        
        setDimension(++index, 0);
        addLabel(gbc, centerPanel, "Simulation of alarm sound: ", DEFAULT_FONT);
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003", DEFAULT_FONT);
        setButtonDefaultSize();
        setDimension(index, 2);
        addButton(gbc, centerPanel, "  ⏯  ",
                _ -> {
                    if (soundPlayed) {
                        return;
                    }
                    
                    soundPlayed = true;
                    Thread playThread = new Thread(() -> {
                        if(UserChoices.getSecondarySoundPath().equals(DEFAULT_SECONDARY_SOUND_PATH)){
                            java.awt.Toolkit.getDefaultToolkit().beep();
                        } else {
                            AlertSound.useDefaultDuration = true;
                            AlertSound.playSound(UserChoices.getSecondarySoundPath());
                            AlertSound.useDefaultDuration = false;
                        }
                        soundPlayed = false;
                    });
                    playThread.start();
                });
        
        setButtonDefaultSize();
        thirdPartPanel.add(centerPanel, BorderLayout.CENTER);
        thirdPartPanel.add(southPanel, BorderLayout.SOUTH);
        return thirdPartPanel;
    }
}