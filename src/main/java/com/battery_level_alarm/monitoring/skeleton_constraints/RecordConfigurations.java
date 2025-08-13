package com.battery_level_alarm.monitoring.skeleton_constraints;
import static com.battery_level_alarm.monitoring.core_utilities.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsFirstPartialPanel.prepareAppSettingsFirstPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsFourthPartialPanel.prepareAppSettingsFourthPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsSecondPartialPanel.prepareAppSettingsSecondPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsThirdPartialPanel.prepareAppSettingsThirdPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFirstPartialPanel.prepareFirstPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsSecondPartialPanel.prepareSecondPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsThirdPartialPanel.prepareThirdPartialContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFourthPartialPanel.prepareFourthPartialContainer;

import com.battery_level_alarm.monitoring.core_utilities.DropDownListStatus;
import com.battery_level_alarm.monitoring.user_interface.ui_config.GridBagConstraintsConfiguration;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SingleDropDownListRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsFirstPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsFourthPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsSecondPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper.AppSettingsThirdPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFirstPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFourthPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsSecondPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsThirdPartialPanel;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;

public class RecordConfigurations {
    private static final Color DARK_GREEN = new Color(0, 140, 0);
    private static final Color DARK_BROWN = new Color(139, 69, 19);
    public static DropShadowBorder computerSettingsContainerListShadow;
    private static DropShadowBorder computerSettingsClosedPanelShadow;
    private static DropShadowBorder computerSettingsOpenPanelShadow;
    public static final int WIDTH = 500;

    public static final GridBagConstraintsConfiguration GRID_BAG_CONSTRAINTS_CONFIGURATION = new GridBagConstraintsConfiguration(
            0,
            0,
            1,
            1,
            1.0,
            1.0,
            GridBagConstraints.WEST,
            GridBagConstraints.BOTH,
            new Insets(5, 10, 5, 10),
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
    public static SingleDropDownListRecord COMPUTER_SETTINGS_FOURTH_DDL;

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
                "General Options",
                AppSettingsFirstPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsFirstDropDownListEnabled(), 0,
                AppSettingsFirstPartialPanel.ProgressBar,
                firstPartialPanel,
                DropDownListStatus::setAppSettingsFirstDropDownListEnabled
        );
        APP_SETTINGS_SECOND_DDL = new SingleDropDownListRecord(
                "Primary Sound",
                AppSettingsSecondPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsSecondDropDownListEnabled(), 1,
                AppSettingsSecondPartialPanel.ProgressBar,
                secondPartialPanel,
                DropDownListStatus::setAppSettingsSecondDropDownListEnabled
        );
        APP_SETTINGS_THIRD_DDL = new SingleDropDownListRecord(
                "Secondary Sound",
                AppSettingsThirdPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isAppSettingsThirdDropDownListEnabled(), 2,
                AppSettingsThirdPartialPanel.ProgressBar,
                thirdPartialPanel,
                DropDownListStatus::setAppSettingsThirdDropDownListEnabled
        );
        APP_SETTINGS_FOURTH_DDL = new SingleDropDownListRecord(
                "Other Alerts",
                AppSettingsFourthPartialPanel.partialPanelDimension,
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
        JPanel fourthPartialPanel = prepareFourthPartialContainer(gbc);

        COMPUTER_SETTINGS_FIRST_DDL = new SingleDropDownListRecord(
                "General Options",
                ComputerSettingsFirstPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_FirstDropDownListEnabled(), 0,
                ComputerSettingsFirstPartialPanel.ProgressBar,
                firstPartialPanel,
                DropDownListStatus::setCS_FirstDropDownListEnabled
        );
        COMPUTER_SETTINGS_SECOND_DDL = new SingleDropDownListRecord(
                "Audio Output -AO",
                ComputerSettingsSecondPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_SecondDropDownListEnabled(), 1,
                ComputerSettingsSecondPartialPanel.ProgressBar,
                secondPartialPanel,
                DropDownListStatus::setCS_SecondDropDownListEnabled
        );
        COMPUTER_SETTINGS_THIRD_DDL = new SingleDropDownListRecord(
                "Sound Level -SL",
                ComputerSettingsThirdPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_ThirdDropDownListEnabled(), 2,
                ComputerSettingsThirdPartialPanel.ProgressBar,
                thirdPartialPanel,
                DropDownListStatus::setCS_ThirdDropDownListEnabled
        );
        COMPUTER_SETTINGS_FOURTH_DDL = new SingleDropDownListRecord(
                "Brightness -BL",
                ComputerSettingsFourthPartialPanel.partialPanelDimension,
                computerSettingsOpenPanelShadow,
                computerSettingsClosedPanelShadow,
                isCS_FourthDropDownListEnabled(), 3,
                ComputerSettingsFourthPartialPanel.ProgressBar,
                fourthPartialPanel,
                DropDownListStatus::setCS_FourthDropDownListEnabled
        );
    }
}