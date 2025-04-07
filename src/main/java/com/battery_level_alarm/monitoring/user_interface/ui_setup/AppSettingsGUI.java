package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.TITLE_LISTS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList.prepareListsContainer;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.addTextInScroll;

import com.battery_level_alarm.monitoring.user_interface.ui_config.DropDownListsContainerRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SingleDropDownListRecord;
import com.battery_level_alarm.monitoring.system_core.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.visual_effects.AlertSound;
import org.jdesktop.swingx.border.DropShadowBorder;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AppSettingsGUI {
    private static final String DEFAULT_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav";
    private static String soundPath = DEFAULT_SOUND_PATH;
    private static boolean soundPlayed = false;

    private static JScrollPane CreatedGUI;
    private static final ScrollConfiguration SCROLL_TEXT_FIELD_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            false,
            false,
            null,
            new Dimension(520, 50)
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

    public static JScrollPane getCreatedGUI() {
    	return CreatedGUI;
    }

    public static void createAndShowGUI() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);

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
        returnGBC$ToDefault(gbc);
        settingsPanel.add(firstPartPanel);
        settingsPanel.add(secondPartPanel);

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
                        pathField, UserChoices.getSoundPath(),
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
        boolean isSelected = DEFAULT_SOUND_PATH.equals(UserChoices.getSoundPath());
        setDimension(++index, 0);
        JCheckBox defaultSoundCheckBox = addCheckbox(
                gbc, centerPanel, "Select the default sound", isSelected,
                _ -> {
                    UserChoices.setSoundPath(DEFAULT_SOUND_PATH);
                    pathField.setText(DEFAULT_SOUND_PATH);
                    ConfigurationFilesManager.saveSettings();
                    BattorionPanelHelper.refreshSettingsPanel();
                });

        setRow(++index);
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
                        soundPath = fileChooser.getSelectedFile().getAbsolutePath();
                        if(DEFAULT_SOUND_PATH.equals(soundPath)) {
                            defaultSoundCheckBox.setSelected(true);
                            pathField.setText(DEFAULT_SOUND_PATH);
                        } else {
                            defaultSoundCheckBox.setSelected(false);
                            pathField.setText(soundPath);
                        }

                        UserChoices.setSoundPath(soundPath);
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
                        AlertSound.playSound(UserChoices.getSoundPath());
                        soundPlayed = false;
                    });
                    playThread.start();
                });

        setButtonDefaultSize();
        secondPartPanel.add(centerPanel, BorderLayout.CENTER);
        secondPartPanel.add(southPanel, BorderLayout.SOUTH);
        return secondPartPanel;
    }
}