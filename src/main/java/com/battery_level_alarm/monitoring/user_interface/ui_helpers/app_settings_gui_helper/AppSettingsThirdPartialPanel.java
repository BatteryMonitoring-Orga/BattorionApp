package com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.AppSettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.user_interface.ui_config.*;
import com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AppSettingsThirdPartialPanel {
    private static final boolean[] APP_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnableSecondarySound()
    };
    private static final JPanel[] APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY = {
            new JPanel(), new JPanel()
    };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareAppSettingsThirdPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                SettingsContainerClass::refreshAppSettingsTab,
                0, mainFrame, motherPanel, SettingsContainer
        );

        Insets inset = gbc.insets;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel toggleButtonPanel = new JPanel(new GridBagLayout());
        addPrimarySoundToggle(gbc, toggleButtonPanel);

        SpinnerConfig beforeRiskAlertConfig = new SpinnerConfig(
                "Alert me before risk phase by:" + FOUR_SPACE + TWO_SPACE,
                UserChoices.getAlertBeforeRiskPhaseBy(), 5, 1, 10, 1,
                0, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    UserChoices.setAlertBeforeRiskPhaseBy(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner firstSpinner = RelatedToSpinner.createSpinner(beforeRiskAlertConfig, false);

        APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[0] = new JPanel(new GridBagLayout());
        APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[0].setVisible(UserChoices.isEnableSecondarySound());
        addLabeledSpinner(
                gbc, APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[0],
                beforeRiskAlertConfig, firstSpinner, true
        );

        SpinnerConfig repeatIntervalConfig = new SpinnerConfig(
                "Start secondary sound alerting before: " + TWO_SPACE,
                UserChoices.getRepeatIntervalBeforeRiskPhase(), 1, 1, 60, 1,
                0, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 1);
                    UserChoices.setRepeatIntervalBeforeRiskPhase(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner secondSpinner = RelatedToSpinner.createSpinner(repeatIntervalConfig, false);

        APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[1] = new JPanel(new GridBagLayout());
        APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[1].setVisible(UserChoices.isEnableSecondarySound());
        addLabeledSpinner(
                gbc, APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[1],
                repeatIntervalConfig, secondSpinner, true
        );

        gbc.insets = inset;
        gbc.gridx = 0;
        gbc.gridy = 0;
        partialPanelContent.add(toggleButtonPanel, gbc);
        gbc.gridy++;
        partialPanelContent.add(APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[0], gbc);
        gbc.gridy++;
        partialPanelContent.add(APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY[1], gbc);

        decideTheSizeDimension();
        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[2] = partialPanelContent;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
    }

    private static void addPrimarySoundToggle(GridBagConstraints gbc, JPanel panel) {
        setDimension(0, 0);
        addLabel(gbc, panel, "Enable Secondary Sound Alerts:", DEFAULT_FONT);
        CompoundUpdaterRecord updaterRecord = getCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord toggleButton = createToggleButtonRecord();

        setColumn(1);
        addLabel(gbc, panel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButton, updaterRecord);
    }

    private static ToggleButtonRecord createToggleButtonRecord() {
        return new ToggleButtonRecord(
                UserChoices::setEnableSecondarySound,
                ConfigurationFilesManager::saveSettings,
                UserChoices.isEnableSecondarySound() ? "On" : "Off",
                new Dimension(80, 30)
        );
    }

    private static @NotNull CompoundUpdaterRecord getCompoundUpdaterRecord(
            ComponentHierarchy hierarchy
    ){
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_THIRD_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isEnableSecondarySound,
                APP_SETTINGS_THIRD_PARTIAL_SPINNER_PANEL_ARRAY
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
        if(UserChoices.isEnableSecondarySound()){
            partialPanelDimension = new Dimension(WIDTH, 190);
        } else {
            partialPanelDimension = new Dimension(WIDTH, 90);
        }
    }
}