package com.battery_level_alarm.monitoring.system_core.helpers;
import static com.battery_level_alarm.monitoring.core_utilities.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PanelIdentifiers.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setButtonBackgroundColor;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.TopAssistPanel.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFirstPartialPanel.getFirstPCHeight;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsFourthPartialPanel.getFourthPCHeight;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsSecondPartialPanel.getSecondPCHeight;
import static com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper.ComputerSettingsThirdPartialPanel.getThirdPCHeight;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.*;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

import com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI;
import javax.swing.*;
import java.awt.*;

public class BattorionPanelHelper {
    public static void createHBoxPanel(){
        HBoxPanel = new JPanel();
        HBoxPanel = applyGradientBackground(
                HBoxPanel, isDarkMode, false, 0, false
        );
        HBoxPanel.setLayout(new BoxLayout(HBoxPanel, BoxLayout.X_AXIS));
        HBoxPanel.add(mainButtonsContainer);
        HBoxPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        HBoxPanel.add(motherPanel);
    }

    public static void refreshSettingsPanel() {
        setVisibleFalse();
        settingsButton.doClick();
    }

    public static void refreshBatteryStatisticsPanel() {
        BatteryStatisticsGUI.createGUI();
    }

    public static void refreshTopAssistantPartialPanelsShadow(Color color){
        firstTopAssistantPartialPanel.setRoundedBorderColor(color);
        secondTopAssistantPartialPanel.setRoundedBorderColor(color);
        thirdTopAssistantPartialPanel.setRoundedBorderColor(color);
        
        firstTopAssistantPartialPanel.repaint();
        secondTopAssistantPartialPanel.repaint();
        thirdTopAssistantPartialPanel.repaint();
        
        topAssistantPartialPanelsContainer.repaint();
        topAssistantPartialPanelsContainer.revalidate();
    }

    public static void ifPanelsNullCreate() {
        if(SettingsContainer == null) {
            SettingsContainer = new JTabbedPane();
            SettingsContainer.setVisible(false);
        } if(StatisticsContainer == null) {
            StatisticsContainer = new JTabbedPane();
            StatisticsContainer.setVisible(false);
        } if(SimulatorMainPanel == null){
            SimulatorMainPanel = new JPanel();
            SimulatorMainPanel.setVisible(false);
        }
    }
    
    public static void setVisibleFalse() {
        DashboardPanel.setVisible(false);
        SettingsContainer.setVisible(false);
        StatisticsContainer.setVisible(false);
        SimulatorMainPanel.setVisible(false);
    }

    public static void setVisibleTrue(String isA) {
        if (DashboardPanel != null && isA.equals(isA_DashboardPanel)) {
            DashboardPanel.setVisible(true);
        } else if (SettingsContainer != null && isA.equals(isA_SettingsContainer)) {
            settingsButton.doClick();
        } else if (StatisticsContainer != null && isA.equals(isA_StatisticsContainer)) {
            statisticsButton.doClick();
        } else if (SimulatorMainPanel != null && isA.equals(isA_SimulatorPanel)){
            simulatorButton.doClick();
        } else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
        }
    }

    public static void audioLabelMouseAction() {
        if (mainTabbedPanel == null) {
            createSettingsContainer();
        } if (mainTabbedPanel.getTabCount() < 2) {
            System.err.println("⚠️ Tabs not initialized yet. Tab count: " + mainTabbedPanel.getTabCount());
            return;
        }
        
        setVisibleFalse();
        setButtonBackgroundColor();
        settingsButton.setBackground(DARK_BLUE);
        setVisibleTrue(isA_SettingsContainer);
        SwingUtilities.invokeLater(() -> {
            if (mainTabbedPanel.getTabCount() > 1) {
                mainTabbedPanel.setSelectedIndex(1);
                pcSettingPanel.getVerticalScrollBar().setValue(250 + getDropHeight());
            } else {
                System.err.println("❌ Tab not available yet (TabCount = " + mainTabbedPanel.getTabCount() + ")");
            }
        });
    }
    
    private static int getDropHeight() {
        int total = 0;
        if(isCS_FirstDropDownListEnabled()) {
            total += getFirstPCHeight();
        } if(isCS_SecondDropDownListEnabled()) {
            total += getSecondPCHeight();
        } if(isCS_ThirdDropDownListEnabled()) {
            total += getThirdPCHeight();
        } if(isCS_FourthDropDownListEnabled()) {
            total += getFourthPCHeight();
        }
        return total;
    }

    public static String whatIsVisible() {
        if (DashboardPanel.isVisible()) {
            return isA_DashboardPanel;
        } else if (SettingsContainer.isVisible()) {
            return isA_SettingsContainer;
        } else if (StatisticsContainer.isVisible()) {
            return isA_StatisticsContainer;
        } else if (SimulatorMainPanel.isVisible()) {
            return isA_SimulatorPanel;
        } else {
            return "No panel is visible";
        }
    }
}