package com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.DropDownListPanelsArray;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.FOUR_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.ONE_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;

import javax.swing.*;
import java.awt.*;

public class ComputerSettingsFirstPartialPanel {
    public static final boolean[] COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY = {
            isActivateTheAwakeningFeature(),
            isEnableSystemNotificationSound(),
            isEnableUnmuteVolumeAutomatically()
    };

    public static JProgressBar firstProgressBar;
    public static JPanel prepareFirstPartialContainer(GridBagConstraints gbc){
        firstProgressBar = prepareProgressBar(COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY, 6);
        return firstPartialPanel(gbc);
    }

    private static JPanel firstPartialPanel(GridBagConstraints gbc){
        JPanel firstPartialPanel = new JPanel(new BorderLayout());
        firstPartialPanel.setOpaque(false);
        JPanel firstPartialPanelContent = new JPanel(new GridBagLayout());
        firstPartialPanelContent.setOpaque(false);

        int partialIndex = 0;
        String switched = isActivateTheAwakeningFeature()? "On":"Off";
        String toSNS = isEnableSystemNotificationSound()? "On":"Off";
        String toUnmuteVolume = isEnableUnmuteVolumeAutomatically()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Activate the awakening feature:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
                0,
                ComputerSettings::isActivateTheAwakeningFeature,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setActivateTheAwakeningFeature,
                ConfigurationFilesManager::saveComputerSettings, switched, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Enable System Notification Sound:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
                1,
                ComputerSettings::isEnableSystemNotificationSound,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setEnableSystemNotificationSound,
                ConfigurationFilesManager::saveComputerSettings, toSNS, 60, 30,
                secondProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Enable unmute volume automatically:", LABELS_FONT);
        ProgressBarValueUpdater thirdProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
                2,
                ComputerSettings::isEnableUnmuteVolumeAutomatically,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setEnableUnmuteVolumeAutomatically,
                ConfigurationFilesManager::saveComputerSettings, toUnmuteVolume, 60, 30,
                thirdProgressBarUpdater, true
        );

        DropDownListPanelsArray[0] = firstPartialPanelContent;
        JPanel firstPartialPanelFooter = createFirstPartialPanelFooter();
        firstPartialPanelFooter.setOpaque(false);
        firstPartialPanel.add(firstPartialPanelContent, BorderLayout.CENTER);
        firstPartialPanel.add(firstPartialPanelFooter, BorderLayout.SOUTH);
        return firstPartialPanel;
    }

    private static JPanel createFirstPartialPanelFooter(){
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