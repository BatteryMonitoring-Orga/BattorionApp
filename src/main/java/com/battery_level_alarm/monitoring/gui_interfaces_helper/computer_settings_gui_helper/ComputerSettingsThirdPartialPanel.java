package com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;

import javax.swing.*;
import java.awt.*;

import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isEnablingSoundLevelChange;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isRestoringSoundLevelAfterAlert;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.ONE_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.*;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;

public class ComputerSettingsThirdPartialPanel {
    private static final boolean[] COMPUTER_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY = {
            isEnablingSoundLevelChange(),
            isRestoringSoundLevelAfterAlert()
    };

    public static JProgressBar thirdProgressBar;
    public static JPanel prepareThirdPartialContainer(GridBagConstraints gbc){
        thirdProgressBar = prepareProgressBar(COMPUTER_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY, 6);
        return thirdPartialPanel(gbc);
    }

    private static JPanel thirdPartialPanel(GridBagConstraints gbc){
        JPanel thirdPartialPanel = new JPanel(new BorderLayout());
        thirdPartialPanel.setOpaque(false);

        JPanel thirdPartialPanelContent = new JPanel(new GridBagLayout());
        thirdPartialPanelContent.setOpaque(false);
        int partialIndex = 0;
        String toChangeSoundLevel = isEnablingSoundLevelChange()? "On":"Off";
        String toRestoreLevel = isRestoringSoundLevelAfterAlert()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, thirdPartialPanelContent, "Enable sound level change:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                COMPUTER_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY,
                0,
                ComputerSettings::isEnablingSoundLevelChange,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, thirdPartialPanelContent, FOUR_SPACE + TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, thirdPartialPanelContent, ComputerSettings::setEnablingSoundLevelChange,
                ConfigurationFilesManager::saveComputerSettings, toChangeSoundLevel, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, thirdPartialPanelContent, "Restore sound level after alert:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                COMPUTER_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY,
                1,
                ComputerSettings::isRestoringSoundLevelAfterAlert,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, thirdPartialPanelContent, FOUR_SPACE + TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, thirdPartialPanelContent, ComputerSettings::setRestoringSoundLevelAfterAlert,
                ConfigurationFilesManager::saveComputerSettings, toRestoreLevel, 60, 30,
                secondProgressBarUpdater, true
        );

        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[2] = thirdPartialPanelContent;
        JPanel thirdPartialPanelFooter = createThirdPartialPanelFooter();
        thirdPartialPanelFooter.setOpaque(false);
        thirdPartialPanel.add(thirdPartialPanelContent, BorderLayout.CENTER);
        thirdPartialPanel.add(thirdPartialPanelFooter, BorderLayout.SOUTH);
        return thirdPartialPanel;
    }

    private static JPanel createThirdPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getThirdPartialQuestionnaires)
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