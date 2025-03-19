package com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.core.BattorionMain.mainFrame;
import static com.battery_level_alarm.monitoring.core.BattorionMain.motherPanel;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.EffectDirection;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.configuration_records.ComponentHierarchy;
import com.battery_level_alarm.monitoring.configuration_records.CompoundUpdaterRecord;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.configuration_records.ToggleButtonRecord;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class AppSettingsFourthPartialPanel {
    private static final boolean[] APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnableChargeAndDischargeSound(),
            UserChoices.isEnableText()
    };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareAppSettingsFourthPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc) {
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                null, 0, mainFrame, motherPanel, mainPartialPanel
        );

        int partialIndex = 0;
        partialIndex = addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable Charging/Discharging Sound:",
                UserChoices::setEnableChargeAndDischargeSound,
                UserChoices.isEnableChargeAndDischargeSound()
        );

        addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable Text Alerts:",
                UserChoices::setEnableText,
                UserChoices.isEnableText()
        );

        decideTheSizeDimension();
        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[3] = partialPanelContent;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
    }

    private static int addToggleFeature(GridBagConstraints gbc, JPanel panel, int index,
                                        String label, Consumer<Boolean> setter, boolean currentState) {
        setDimension(index, 0);
        addLabel(gbc, panel, label, LABELS_FONT);
        CompoundUpdaterRecord compoundUpdaterRecord = getCompoundUpdaterRecord(hierarchy, index);
        ToggleButtonRecord toggleButtonRecord = new ToggleButtonRecord(
                setter,
                ConfigurationFilesManager::saveSettings,
                currentState ? "On" : "Off",
                new Dimension(80, 30)
        );

        setColumn(1);
        addLabel(gbc, panel, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButtonRecord, compoundUpdaterRecord);
        return index + 1;
    }

    private static CompoundUpdaterRecord getCompoundUpdaterRecord(ComponentHierarchy hierarchy, int index) {
        return switch (index) {
            case 0 -> getFirstCompoundUpdaterRecord(hierarchy);
            case 1 -> getSecondCompoundUpdaterRecord(hierarchy);
            default -> throw new IllegalArgumentException("Invalid index for CompoundUpdaterRecord");
        };
    }

    private static @NotNull CompoundUpdaterRecord getFirstCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                0,
                UserChoices::isEnableChargeAndDischargeSound,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord getSecondCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                1,
                UserChoices::isEnableText,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord createCompoundUpdaterRecord(
            ComponentHierarchy hierarchy,
            int index,
            Callable<Boolean> conditionSupplier,
            JComponent[] components
    ){
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                index,
                conditionSupplier,
                components
        );
        return new CompoundUpdaterRecord(
                null, null, null,
                progressBarUpdater,
                EffectDirection.NONE,
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
        partialPanelDimension = new Dimension(WIDTH, 140);
    }
}