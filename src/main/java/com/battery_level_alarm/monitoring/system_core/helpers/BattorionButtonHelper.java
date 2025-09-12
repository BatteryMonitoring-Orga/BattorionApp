package com.battery_level_alarm.monitoring.system_core.helpers;
import static com.battery_level_alarm.monitoring.server_side.feedback.FeedbackPanel.createFeedbackPanel;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.NULL_VALUE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.battery_emulator.BatteryIcon.mainSimulatorPanel;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.battery_emulator.BatteryIcon.BatterySimulationStart;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.LifeReportPanelUI.lifeReportPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.setButtonFontAndSize;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.createSettingsContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.mainTabbedPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.StatisticsContainerClass.createStatisticsContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.StatisticsContainerClass.statisticsMainTabbedPanel;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.LifeReportPanelUI;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattorionButtonHelper {
    public static JButton createButton(
            String title, String toolTip, String imageIconPath,
            String iconName, ActionListener actionListener
    ) {
        ImageIcon icon = null;
        try {
            if (iconName != null && !iconName.equalsIgnoreCase(NULL_VALUE))
                icon = CallResources.getImage(imageIconPath, iconName, new Dimension(20, 20), Image.SCALE_SMOOTH);
        } catch (Exception e) {
            printErrorMessage(e);
        }
        
        JButton button;
        if (icon != null) button = new JButton(title, icon);
        else button = new JButton(title);
        
        setButtonFontAndSize(
                button, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 14),
                WEST_PANEL_OPEN_WIDTH - 10, 70, SwingConstants.LEFT, SwingConstants.RIGHT);
        button.setToolTipText(toolTip);
        button.addActionListener(actionListener);
        return button;
    }
    
    public static JButton createButton(
            String toolTip, String imageIconPath, String iconName,
            ActionListener actionListener
    ) {
        if(iconName == null) return new JButton();
        ImageIcon icon = CallResources.getImage(
                imageIconPath, iconName, new Dimension(20, 20), Image.SCALE_SMOOTH);
        
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setToolTipText(toolTip);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hyalineButton(button, false);
        button.addActionListener(actionListener);
        return button;
    }
    
    public static void hyalineButton(JButton button, boolean isColoredAble, boolean... values) {
        if (values.length == 0) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
        } else {
            button.setOpaque(values[0]);
            if (values.length >= 2) button.setContentAreaFilled(values[1]);
            if (values.length >= 3) button.setBorderPainted(values[2]);
        }
        
        boolean isBorderPainted = values.length < 3 || values[2];
        addHandMouseListener(button, isColoredAble, isBorderPainted);
    }
    
    private static void addHandMouseListener(JButton button, boolean isColoredAble, boolean isBorderPainted) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setBorderPainted(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                button.setBorderPainted(isBorderPainted);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isColoredAble) {
                    setButtonBackgroundColor();
                    button.setBackground(DARK_BLUE);
                }
            }
        });
    }
    
    public static JButton createGraphButton() {
        BatteryLevelGraph.initialize();
        return createButton(
                GRAPH_PAINTER_TEXT, "Display a graph of battery level",
                BUTTON_ICONS_PATH, "graph", _ -> BatteryLevelGraph.display()
        );
    }

    public static void setupWestSideButton() {
        int width = WEST_PANEL_OPEN_WIDTH;
        if(westSideButton.getText().equals(WEST_SIDE_BUTTON_TEXT)) {
            setButtonNamesEmpty();
            isWestSidePartAppear = false;
            width = WEST_PANEL_CLOSED_WIDTH;
        } else {
            returnButtonNames();
            isWestSidePartAppear = true;
        }
        refreshWestSide(width);
    }

    public static void refreshWestSide(int width) {
        mainButtonsContainer.setPreferredSize(new Dimension(width, FRAME_HEIGHT));
        mainButtonsContainer.setMaximumSize(new Dimension(width, FRAME_HEIGHT));
        refreshMasterFrame();
        saveGeneralConfigurations();
    }

    private static void setButtonNamesEmpty() {
        westSideButton.setText("");
        dashboardButton.setText("");
        settingsButton.setText("");
        statisticsButton.setText("");
        graphPainter.setText("");
        guideButton.setText("");
        simulatorButton.setText("");
        reportButton.setText("");
        aboutButton.setText("");
        feedbackButton.setText("");
    }

    private static void returnButtonNames() {
        westSideButton.setText(WEST_SIDE_BUTTON_TEXT);
        dashboardButton.setText(DASHBOARD_BUTTON_TEXT);
        settingsButton.setText(SETTINGS_BUTTON_TEXT);
        feedbackButton.setText(FEEDBACK_BUTTON_TEXT);
        statisticsButton.setText(STATISTICS_BUTTON_TEXT);
        graphPainter.setText(GRAPH_PAINTER_TEXT);
        guideButton.setText(GUIDE_BUTTON_TEXT);
        simulatorButton.setText(SIMULATOR_BUTTON_TEXT);
        reportButton.setText(REPORT_BUTTON_TEXT);
        aboutButton.setText(ABOUT_BUTTON_TEXT);
    }
    
    public static void setButtonBackgroundColor() {
        westSideButton.setBackground(panelBackgroundColor);
        dashboardButton.setBackground(panelBackgroundColor);
        statisticsButton.setBackground(panelBackgroundColor);
        feedbackButton.setBackground(panelBackgroundColor);
        reportButton.setBackground(panelBackgroundColor);
        graphPainter.setBackground(panelBackgroundColor);
        guideButton.setBackground(panelBackgroundColor);
        simulatorButton.setBackground(panelBackgroundColor);
        actionButton.setBackground(panelBackgroundColor);
        aboutButton.setBackground(panelBackgroundColor);
        settingsButton.setBackground(panelBackgroundColor);
    }
    
    public static void createPopup(String msg, Component parent) {
        JPopupMenu popup = new JPopupMenu();
        JLabel label = new JLabel(msg);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        popup.add(label);
        popup.show(parent, 0, parent.getHeight());
        new Timer(2000, _ -> popup.setVisible(false)).start();
    }
    
    public static void setupDashboardPanel() {
        setVisibleFalse();
        DashboardPanel.setVisible(true);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
    }
    
    public static void setupLifeReportPanel() {
        ifPanelsNullCreate();
        setVisibleFalse();
        LifeReportPanel = lifeReportPanel();
        LifeReportPanel.setVisible(true);
        motherPanel.add(LifeReportPanel, BorderLayout.CENTER);
        Thread.ofVirtual().start(LifeReportPanelUI::updateBatteryLiveInfo);
    }
    
    public static void setupSettingPanel() {
        ifPanelsNullCreate();
        setVisibleFalse();
        createSettingsContainer();
        SettingsContainer = mainTabbedPanel;
        SettingsContainer.setVisible(true);
        motherPanel.add(SettingsContainer, BorderLayout.CENTER);
    }
    
    public static void setupStatisticsPanel() {
        createStatisticsContainer();
        StatisticsContainer = statisticsMainTabbedPanel;
        
        ifPanelsNullCreate();
        setVisibleFalse();
        StatisticsContainer.setVisible(true);
        motherPanel.add(StatisticsContainer, BorderLayout.CENTER);
    }
    
    public static void setupSimulatorPanel() {
        BatterySimulationStart();
        SimulatorMainPanel = new JPanel(new BorderLayout());
        SimulatorMainPanel.add(new JScrollPane(mainSimulatorPanel), BorderLayout.CENTER);
        
        ifPanelsNullCreate();
        setVisibleFalse();
        SimulatorMainPanel.setVisible(true);
        motherPanel.add(SimulatorMainPanel, BorderLayout.CENTER);
    }
    
    public static void setupFeedbackPanel() {
        createFeedbackPanel();
        ifPanelsNullCreate();
        setVisibleFalse();
        FeedbackMainPanel.setVisible(true);
        motherPanel.add(FeedbackMainPanel, BorderLayout.CENTER);
    }
}