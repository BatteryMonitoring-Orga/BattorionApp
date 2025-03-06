package com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;

import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;

import javax.swing.*;
import java.awt.*;

public class AppSettingsFourthPartialPanel {
    public static final boolean[] APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnableChargeAndDischargeSound(),
            UserChoices.isEnableText()
    };

    public static JProgressBar ProgressBar;
    public static JPanel prepareAppSettingsFourthPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);

        int partialIndex = 0;
        String chargingSound = UserChoices.isEnableChargeAndDischargeSound()? "On":"Off";
        String textAlert = UserChoices.isEnableText()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, partialPanelContent, "Enable Charging/Discharging Sound:", DEFAULT_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isEnableChargeAndDischargeSound,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, partialPanelContent, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, partialPanelContent, UserChoices::setEnableChargeAndDischargeSound,
                ConfigurationFilesManager::saveSettings, chargingSound, 80, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, partialPanelContent, "Enable Text Alerts:", DEFAULT_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                1,
                UserChoices::isEnableText,
                new JSpinner[]{}
        );
        setColumn(1);
        addLabel(gbc, partialPanelContent, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, partialPanelContent, UserChoices::setEnableText,
                ConfigurationFilesManager::saveSettings, textAlert, 80, 30,
                secondProgressBarUpdater, true
        );

        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[3] = partialPanelContent;
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