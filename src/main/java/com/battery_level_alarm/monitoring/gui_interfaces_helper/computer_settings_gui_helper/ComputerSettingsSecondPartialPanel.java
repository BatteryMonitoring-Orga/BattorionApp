package com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isEnableExchangeToAudioOutputUsed;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isEnableExchangeToSpeakerAudioOutput;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.FOUR_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.ONE_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.*;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;

import javax.swing.*;
import java.awt.*;

public class ComputerSettingsSecondPartialPanel {
    private static final boolean[] COMPUTER_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY = {
            isEnableExchangeToSpeakerAudioOutput(),
            isEnableExchangeToAudioOutputUsed()
    };

    public static JProgressBar secondProgressBar;
    public static JPanel prepareSecondPartialContainer(GridBagConstraints gbc){
        secondProgressBar = prepareProgressBar(COMPUTER_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY, 6);
        return secondPartialPanel(gbc);
    }

    private static JPanel secondPartialPanel(GridBagConstraints gbc){
        JPanel secondPartialPanel = new JPanel(new BorderLayout());
        secondPartialPanel.setOpaque(false);

        JPanel secondPartialPanelContent = new JPanel(new GridBagLayout());
        secondPartialPanelContent.setOpaque(false);
        int partialIndex = 0;
        String toSpeaker = isEnableExchangeToSpeakerAudioOutput()? "On":"Off";
        String toUsed = isEnableExchangeToAudioOutputUsed()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, secondPartialPanelContent, "Exchange to speaker audio output:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                COMPUTER_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY,
                0,
                ComputerSettings::isEnableExchangeToSpeakerAudioOutput,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, secondPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, secondPartialPanelContent, ComputerSettings::setEnableExchangeToSpeakerAudioOutput,
                ConfigurationFilesManager::saveComputerSettings, toSpeaker, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, secondPartialPanelContent, "Restore audio output used after alert:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                COMPUTER_SETTINGS_SECOND_PARTIAL_TRUE_ARRAY,
                1,
                ComputerSettings::isEnableExchangeToAudioOutputUsed,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, secondPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, secondPartialPanelContent, ComputerSettings::setEnableExchangeToAudioOutputUsed,
                ConfigurationFilesManager::saveComputerSettings, toUsed, 60, 30,
                secondProgressBarUpdater, true
        );

        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[1] = secondPartialPanelContent;
        JPanel secondPartialPanelFooter = createSecondPartialPanelFooter();
        secondPartialPanelFooter.setOpaque(false);
        secondPartialPanel.add(secondPartialPanelContent, BorderLayout.CENTER);
        secondPartialPanel.add(secondPartialPanelFooter, BorderLayout.SOUTH);
        return secondPartialPanel;
    }

    private static JPanel createSecondPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getSecondPartialQuestionnaires)
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