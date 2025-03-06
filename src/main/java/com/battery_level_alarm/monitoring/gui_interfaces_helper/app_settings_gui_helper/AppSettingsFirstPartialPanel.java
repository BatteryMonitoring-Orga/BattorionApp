package com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.addLabeledSpinner;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.getSpinnerValue;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;

import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.configuration_records.SpinnerConfig;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;

import javax.swing.*;
import java.awt.*;

public class AppSettingsFirstPartialPanel {
    public static final boolean[] APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY = {
            UserChoices.isAutoMonitoring()
    };

    public static JProgressBar ProgressBar;
    public static JPanel prepareAppSettingsFirstPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);

        int partialIndex = 0;
        String automatic = UserChoices.isAutoMonitoring()? "On":"Off";
        setDimension(partialIndex, 0);
        addLabel(gbc, partialPanelContent, "Enable Automatic Monitoring:", DEFAULT_FONT);
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isAutoMonitoring,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, partialPanelContent, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, partialPanelContent, UserChoices::setAutoMonitoring,
                ConfigurationFilesManager::saveSettings, automatic, 80, 30,
                progressBarUpdater, true
        );

        SpinnerConfig minBatteryConfig = new SpinnerConfig(
                "Minimum Battery Level:",
                UserChoices.getMinimumLevel(), 25, 15, 30, 1,
                ++partialIndex, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                    UserChoices.setMinimumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner minBatteryConfigSpinner = RelatedToSpinner.createSpinner(minBatteryConfig, false);
        addLabeledSpinner(gbc, partialPanelContent, minBatteryConfig, minBatteryConfigSpinner, true);

        SpinnerConfig maxBatteryConfig = new SpinnerConfig(
                "Maximum Battery Level:",
                UserChoices.getMaximumLevel(), 85, 80, 90, 1,
                ++partialIndex, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 80, 85);
                    UserChoices.setMaximumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner maxBatteryConfigSpinner = RelatedToSpinner.createSpinner(maxBatteryConfig, false);
        addLabeledSpinner(gbc, partialPanelContent, maxBatteryConfig, maxBatteryConfigSpinner, true);

        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[0] = partialPanelContent;
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