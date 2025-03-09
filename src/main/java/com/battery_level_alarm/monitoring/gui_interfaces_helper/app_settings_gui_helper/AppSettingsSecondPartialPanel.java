package com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper;
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

import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.configuration_records.SpinnerConfig;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;

import javax.swing.*;
import java.awt.*;

public class AppSettingsSecondPartialPanel {
    public static final boolean[] APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnablePrimarySound()
    };
    public static final JSpinner[] APP_SETTINGS_SECOND_PARTIAL_SPINNER_ARRAY = {
            new JSpinner()
    };

    public static JProgressBar ProgressBar;
    public static JPanel prepareAppSettingsSecondPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);

        int partialIndex = 0;
        boolean isEnablePrimarySound = UserChoices.isEnablePrimarySound();
        String primarySound = isEnablePrimarySound? "On":"Off";
        setDimension(partialIndex, 0);
        addLabel(gbc, partialPanelContent, "Enable Primary Sound Alerts:", DEFAULT_FONT);
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isEnablePrimarySound,
                APP_SETTINGS_SECOND_PARTIAL_SPINNER_ARRAY
        );
        setColumn(1);
        addLabel(gbc, partialPanelContent, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, partialPanelContent, UserChoices::setEnablePrimarySound,
                ConfigurationFilesManager::saveSettings, primarySound, 80, 30,
                progressBarUpdater, true
        );

        SpinnerConfig soundDurationConfig = new SpinnerConfig(
                "Sound Duration (in Seconds):",
                UserChoices.getSoundDuration(), 5, 1, 10, 1,
                ++partialIndex, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    UserChoices.setSoundDuration(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        APP_SETTINGS_SECOND_PARTIAL_SPINNER_ARRAY[0] = RelatedToSpinner.createSpinner(soundDurationConfig, false);
        APP_SETTINGS_SECOND_PARTIAL_SPINNER_ARRAY[0].setEnabled(isEnablePrimarySound);
        addLabeledSpinner(
                gbc, partialPanelContent, soundDurationConfig,
                APP_SETTINGS_SECOND_PARTIAL_SPINNER_ARRAY[0],
                true
        );

        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[1] = partialPanelContent;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
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
}