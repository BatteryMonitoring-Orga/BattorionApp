package com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.addLabeledSpinner;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.getSpinnerValue;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.EffectDirection;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.configuration_records.*;
import com.battery_level_alarm.monitoring.core.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AppSettingsSecondPartialPanel {
    private static final boolean[] APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnablePrimarySound()
    };
    private static final JPanel[] APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY = {
            new JPanel()
    };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareAppSettingsSecondPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                BattorionPanelHelper::refreshSettingsPanel,
                0, mainFrame, motherPanel, SettingScrollPanel
        );

        Insets inset = gbc.insets;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel toggleButtonPanel = new JPanel(new GridBagLayout());
        addPrimarySoundToggle(gbc, toggleButtonPanel);

        SpinnerConfig soundDurationConfig = new SpinnerConfig(
                "Sound Duration (in Seconds):  " + FOUR_SPACE + ONE_SPACE,
                UserChoices.getSoundDuration(), 5, 1, 10, 1,
                0, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    UserChoices.setSoundDuration(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner spinner = RelatedToSpinner.createSpinner(soundDurationConfig, false);

        APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY[0] = new JPanel(new GridBagLayout());
        APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY[0].setVisible(UserChoices.isEnablePrimarySound());
        addLabeledSpinner(
                gbc, APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY[0],
                soundDurationConfig, spinner, true
        );

        gbc.insets = inset;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(toggleButtonPanel, gbc);
        gbc.gridy++;
        contentPanel.add(APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY[0], gbc);

        decideTheSizeDimension();
        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[1] = contentPanel;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(contentPanel, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
    }

    private static void addPrimarySoundToggle(GridBagConstraints gbc, JPanel panel) {
        setDimension(0, 0);
        addLabel(gbc, panel, "Enable Primary Sound Alerts:", DEFAULT_FONT);

        CompoundUpdaterRecord updaterRecord = getCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord toggleButton = createToggleButtonRecord();

        setColumn(1);
        addLabel(gbc, panel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButton, updaterRecord);
    }

    private static ToggleButtonRecord createToggleButtonRecord() {
        return new ToggleButtonRecord(
                UserChoices::setEnablePrimarySound,
                ConfigurationFilesManager::saveSettings,
                UserChoices.isEnablePrimarySound() ? "On" : "Off",
                new Dimension(80, 30)
        );
    }

    private static @NotNull CompoundUpdaterRecord getCompoundUpdaterRecord(
            ComponentHierarchy hierarchy
    ){
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isEnablePrimarySound,
                APP_SETTINGS_SECOND_PARTIAL_SPINNER_PANEL_ARRAY
        );
        return new CompoundUpdaterRecord(
                null, null, null,
                progressBarUpdater,
                EffectDirection.FORWARD,
                hierarchy,
                null,
                true,
                false
        );
    }

    private static JPanel createPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(ComputerSettingsGUI.TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getFirstPartialQuestionnaires)
                )
        );

        JPanel aboutLabelPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutLabelPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        //aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.NORTH);
        aboutPanel.add(aboutLabelPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static void decideTheSizeDimension(){
        if(UserChoices.isEnablePrimarySound()){
            partialPanelDimension = new Dimension(WIDTH, 140);
        } else {
            partialPanelDimension = new Dimension(WIDTH, 90);
        }
    }
}