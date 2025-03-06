package com.battery_level_alarm.monitoring.skeleton_constraints;
import static com.battery_level_alarm.monitoring.basics.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsFirstPartialPanel.prepareAppSettingsFirstPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsFourthPartialPanel.prepareAppSettingsFourthPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsSecondPartialPanel.prepareAppSettingsSecondPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsThirdPartialPanel.prepareAppSettingsThirdPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsFirstPartialPanel.prepareFirstPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsSecondPartialPanel.prepareSecondPartialContainer;
import static com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsThirdPartialPanel.prepareThirdPartialContainer;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.ONE_SPACE;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.TWO_SPACE;

import com.battery_level_alarm.monitoring.basics.DropDownListStatus;
import com.battery_level_alarm.monitoring.configuration_records.GridBagConstraintsConfiguration;
import com.battery_level_alarm.monitoring.configuration_records.SingleDropDownListRecord;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsFirstPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsFourthPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsSecondPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.app_settings_gui_helper.AppSettingsThirdPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsFirstPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsSecondPartialPanel;
import com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper.ComputerSettingsThirdPartialPanel;
import com.battery_level_alarm.monitoring.preparing_gui.DropDownList;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;

public class RecordConfigurations {
    private static final Color DARK_GREEN = new Color(0, 140, 0);
    private static final Color DARK_BROWN = new Color(139, 69, 19);
    public static DropShadowBorder computerSettingsContainerListShadow;
    private static DropShadowBorder computerSettingsClosedPanelShadow;
    private static DropShadowBorder computerSettingsOpenPanelShadow;

    public static final GridBagConstraintsConfiguration GRID_BAG_CONSTRAINTS_CONFIGURATION = new GridBagConstraintsConfiguration(
            0,
            0,
            1,
            1,
            1.0,
            1.0,
            GridBagConstraints.WEST,
            GridBagConstraints.BOTH,
            new Insets(10, 10, 10, 10),
            0,
            0
    );

    public static SingleDropDownListRecord APP_SETTINGS_FIRST_DDL;
    public static SingleDropDownListRecord APP_SETTINGS_SECOND_DDL;
    public static SingleDropDownListRecord APP_SETTINGS_THIRD_DDL;
    public static SingleDropDownListRecord APP_SETTINGS_FOURTH_DDL;

    public static SingleDropDownListRecord COMPUTER_SETTINGS_FIRST_DDL;
    public static SingleDropDownListRecord COMPUTER_SETTINGS_SECOND_DDL;
    public static SingleDropDownListRecord COMPUTER_SETTINGS_THIRD_DDL;

    private static void createShadowObject(){
        computerSettingsContainerListShadow = new DropShadowBorder(
                DARK_GREEN, 8, 0.8f, 8,
                true, true, true, true
        );
        computerSettingsClosedPanelShadow = new DropShadowBorder(
                DropDownList.borderForegroundColor, 8, 0.8f, 8,
                true, true, true, true
        );
        computerSettingsOpenPanelShadow = new DropShadowBorder(
                DARK_BROWN, 4, 0.4f, 4,
                false, false, true, false
        );
    }

    public static void createAppSettingsDropDownListConfigurations(GridBagConstraints gbc){
        createShadowObject();
        JPanel firstPartialPanel = prepareAppSettingsFirstPartialContainer(gbc);
        JPanel secondPartialPanel = prepareAppSettingsSecondPartialContainer(gbc);
        JPanel thirdPartialPanel = prepareAppSettingsThirdPartialContainer(gbc);
        JPanel fourthPartialPanel = prepareAppSettingsFourthPartialContainer(gbc);

        APP_SETTINGS_FIRST_DDL = new SingleDropDownListRecord(
                "General Options ",
                new Dimension(480, 190),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsFirstDropDownListEnabled(), 0,
                AppSettingsFirstPartialPanel.ProgressBar,
                firstPartialPanel,
                DropDownListStatus::setAppSettingsFirstDropDownListEnabled
        );
        APP_SETTINGS_SECOND_DDL = new SingleDropDownListRecord(
                "Primary Sound   ",
                new Dimension(480, 140),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsSecondDropDownListEnabled(), 1,
                AppSettingsSecondPartialPanel.ProgressBar,
                secondPartialPanel,
                DropDownListStatus::setAppSettingsSecondDropDownListEnabled
        );
        APP_SETTINGS_THIRD_DDL = new SingleDropDownListRecord(
                "Secondary Sound",
                new Dimension(480, 190),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsThirdDropDownListEnabled(), 2,
                AppSettingsThirdPartialPanel.ProgressBar,
                thirdPartialPanel,
                DropDownListStatus::setAppSettingsThirdDropDownListEnabled
        );
        APP_SETTINGS_FOURTH_DDL = new SingleDropDownListRecord(
                "Other Alerts " + TWO_SPACE,
                new Dimension(480, 140),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsFourthDropDownListEnabled(), 3,
                AppSettingsFourthPartialPanel.ProgressBar,
                fourthPartialPanel,
                DropDownListStatus::setAppSettingsFourthDropDownListEnabled
        );
    }

    public static void createComputerSettingsDropDownListConfigurations(GridBagConstraints gbc){
        createShadowObject();
        JPanel firstPartialPanel = prepareFirstPartialContainer(gbc);
        JPanel secondPartialPanel = prepareSecondPartialContainer(gbc);
        JPanel thirdPartialPanel = prepareThirdPartialContainer(gbc);

        COMPUTER_SETTINGS_FIRST_DDL = new SingleDropDownListRecord(
                "General Options ",
                new Dimension(480, 190),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_FirstDropDownListEnabled(), 0,
                ComputerSettingsFirstPartialPanel.firstProgressBar,
                firstPartialPanel,
                DropDownListStatus::setCS_FirstDropDownListEnabled
        );
        COMPUTER_SETTINGS_SECOND_DDL = new SingleDropDownListRecord(
                "Audio Output " + ONE_SPACE,
                new Dimension(480, 140),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_SecondDropDownListEnabled(), 1,
                ComputerSettingsSecondPartialPanel.secondProgressBar,
                secondPartialPanel,
                DropDownListStatus::setCS_SecondDropDownListEnabled
        );
        COMPUTER_SETTINGS_THIRD_DDL = new SingleDropDownListRecord(
                "Sound Level" + TWO_SPACE,
                new Dimension(480, 140),
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_ThirdDropDownListEnabled(), 2,
                ComputerSettingsThirdPartialPanel.thirdProgressBar,
                thirdPartialPanel,
                DropDownListStatus::setCS_ThirdDropDownListEnabled
        );
    }
}