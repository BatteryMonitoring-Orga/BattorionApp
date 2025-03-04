package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isEnableSystemNotificationSound;
import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.aboutNotificationsIcon;
import static com.battery_level_alarm.monitoring.core.BatteryMode.*;
import static com.battery_level_alarm.monitoring.core.BattorionButtonsHelper.*;
import static com.battery_level_alarm.monitoring.core.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager.*;
import static com.battery_level_alarm.monitoring.core.HandleLevel.*;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.*;
import static com.battery_level_alarm.monitoring.core.BattorionProgressBarHelper.*;
import static com.battery_level_alarm.monitoring.effects.Appearance.getPopupMenu;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.cybernate.Timing.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.applyScrollConfigurationDetails;

import com.battery_level_alarm.monitoring.configuration_records.ScrollConfiguration;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.main_folder_manager.EssentialToolsDownloader;
import com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.battery_simulation.BatteryIcon;
import com.battery_level_alarm.monitoring.command.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.cybernate.WakeUpPC;
import com.battery_level_alarm.monitoring.effects.Appearance;
import com.battery_level_alarm.monitoring.effects.CallResources;
import com.battery_level_alarm.monitoring.preparing_gui.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.preparing_gui.PrepareDiskInfoGUI;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattorionMain {
    private static final Font textFont = new Font(Font.SERIF, Font.BOLD + Font.ITALIC, 14);
    static final String ChargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-start-2574.wav";
    static final String DischargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-back-2575.wav";
    static final String isA_DashboardPanel = "DashboardPanel";
    static final String isA_SettingScrollPanel = "SettingScrollPanel";
    static final String isA_PC_SettingScrollPanel = "PC_SettingScrollPanel";
    static final String isA_BatteryStatisticsPanel = "BatteryStatisticsPanel";
    public static final String isA_DiskInfoPanel = "DiskInfoPanel";
    public static final String isIn_ChargingMode = "Charging";
    public static final String isIn_DisChargingMode = "Not Charging";
    private static final String APP_NAME = "Battorion";

    private static Thread monitoringThread;
    static SystemTrayNotification stn;
    static Notifications notify;
    static AlarmSounds alarmSounds;

    static JFrame mainFrame;
    static JPanel motherPanel;
    static JPanel DashboardPanel;
    static JPanel DiskInfoPanel;
    static JPanel BatteryStatisticsPanel;
    static JScrollPane SettingScrollPanel;
    static JScrollPane pcSettingScrollPanel;

    static final JPanel progressPanel = new JPanel();
    static JProgressBar batteryBar;
    static JButton actionButton;
    static JButton settingsButton;
    static JButton pcSettingsButton;
    static JButton diskButton;
    static JButton batteryStatisticsButton;
    static JButton aboutSettingsButton;

    private static JLabel monitoringStatus;
    private static JLabel alertLabel;
    static JLabel ratioChargeLabel;
    static JLabel statusLabel;

    private static final int duration = 1000;
    private static boolean callFlag = false;
    private static boolean running = false;
    static boolean isCharging = false;
    static boolean operationIsEnd = false;
    static String status = "";
    static String lastMode = "";

    public static boolean progressBarInVerticalMode;
    public static int batteryLevel = 0;
    public static boolean isFromCriticalAlert = false;
    public static boolean simulatorMode;
    
	public static void main(String[] args) {
        loadGeneralConfigurations();
        Appearance.theme_setup();
        EssentialToolsDownloader.Downloader();
        SingletonObject.singletonMethod();
	}

    private static void refreshMotherFrame() {
        mainFrame.setLocationRelativeTo(null);
        mainFrame.repaint();
        mainFrame.validate();
    }

    public static void rebuild(){
        mainFrame.dispose();
        Appearance.started = true;
        loadGeneralConfigurations();
        Appearance.theme_setup();
        build();
    }

    public static void build(){
        UIManager.put("ToolTip.font", textFont);
        configurationHistoryMap();
        configurationSystemTrayNotifications();

        loadSettings();
        loadComputerSettings();
        loadDropDownListConfigurations();
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        BatteryStatisticsGUI.createGUI();
        SwingUtilities.invokeLater(BattorionMain::createAndShowGUI);
    }

    private static void configurationSystemTrayNotifications() {
        notify = new Notifications(
                APP_NAME,
                "/resources/com/battery_level_alarm/monitoring/BattIco/13228401.png",
                "Battery Reminder",
                "Battery is in risk!",
                duration,
                false
        );
        stn = new SystemTrayNotification();
        alarmSounds = new AlarmSounds(1);
    }

    private static void callNotifier(){
        notify.setAlarmMessage("Battery level is: " + batteryLevel);
        stn.setIsToShowPanel(false);
        stn.CreateTrayIcon(notify, alarmSounds);
    }
	
    private static void createAndShowGUI() {
        mainFrame = new JFrame(APP_NAME);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = CallResources.getImage("BattIco/13228401");
        mainFrame.setIconImage(icon.getImage());
        mainFrame.setSize(width, height);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        
        ifPanelsNullCreate();
        motherPanel = new JPanel(new BorderLayout());
        DashboardPanel = new JPanel(new BorderLayout());

		monitoringStatus = new JLabel("");
		monitoringStatus.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
		DashboardPanel.add(monitoringStatus, BorderLayout.NORTH);
		
		try {
			batteryLevel = getBatteryLevel();
		} catch (Exception e) {
			batteryLevel = 0;
            printErrorMessage(e);
        }
        Color color = getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel());

        batteryBar = new JProgressBar(0, 100);
        setProgressBarMode();
        batteryBar.setForeground(color);
        batteryBar.setBorder(new LineBorder(Appearance.getBorderColor(), 3));
        batteryBar.setValue(batteryLevel);
        batteryBar.setStringPainted(false);
        batteryBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		ratioChargeLabel = new JLabel("Battery Level: " + batteryLevel + "%");
		ratioChargeLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
		ratioChargeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setUpProgressPanel();
		DashboardPanel.add(progressPanel, BorderLayout.CENTER);
		
		alertLabel = new JLabel("");
		alertLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
		alertLabel.setOpaque(true);
		alertLabel.setForeground(Color.RED);
        ScrollConfiguration configuration = new ScrollConfiguration(
                false,
                true,
                false,
                false,
                null,
                new Dimension(motherPanel.getWidth() - 100, 50)
        );
		JScrollPane scroll = new JScrollPane(alertLabel);
        applyScrollConfigurationDetails(scroll, configuration);
		DashboardPanel.add(scroll, BorderLayout.SOUTH);
		motherPanel.add(DashboardPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(110, 100));

        createMainButtons();
        JButton reportButton = createButton("Life Report", "Generate battery life report", _ -> batteryReport());
        JButton aboutButton = createButton("About", "About the application", _ -> StaticQuestionnaire.aboutDispatch());

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(settingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(pcSettingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(diskButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(batteryStatisticsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(aboutButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(aboutSettingsButton);
        aboutSettingsButton.setVisible(false);
        motherPanel.add(buttonPanel, BorderLayout.WEST);

        JPanel statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Battery Status: " + status + " ");
        statusLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        statusLabelPanel.add(statusLabel);
        getBatteryMode(color);
        lastMode = status;

        JPanel labelsPanel = new JPanel(new GridLayout(1, 2));
        labelsPanel.add(statusLabelPanel);
        labelsPanel.add(createTopAssistPanel(color));
        motherPanel.add(labelsPanel, BorderLayout.NORTH);
        
        mainFrame.add(motherPanel);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	running = false;
            	if(monitoringThread != null) {
                	monitoringThread.interrupt();
            	}
            }
        });
        
        if(UserChoices.isAutoMonitoring()) {
        	actionButton.doClick();
        }
        mainFrame.setVisible(true);
        Appearance.started = false;
    }

    private static void createMainButtons(){
        actionButton = createButton("Start", "Click to start monitoring", _ -> {
            if(actionButton.getText().equals("Start")) {
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });
        settingsButton = createButton("Settings", "Open settings menu", _ -> {
            if(settingsButton.getText().equals("Settings")) {
                setUpSettingScrollPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        pcSettingsButton = createButton("PC - Settings", "Open PC settings", _ -> {
            if(pcSettingsButton.getText().equals("PC - Settings")) {
                setUpPCSettingsScrollPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        diskButton = createButton("Disk Info.", "View disk information", _ -> {
            if(diskButton.getText().equals("Disk Info.")) {
                setUpDiskInfoPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        batteryStatisticsButton = createButton("Statistics", "View battery statistics", _ -> {
            if(batteryStatisticsButton.getText().equals("Statistics")) {
                setUpBatteryStatisticsPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        aboutSettingsButton = createButton("What is that?", "Explanation of settings", _ ->
            JOptionPane.showMessageDialog(null,
                    StaticQuestionnaire.Dispatch(textFont, StaticQuestionnaire.getHowToUseSettings()),
                    "Settings Explanation",
                    JOptionPane.INFORMATION_MESSAGE
            )
        );
    }

    private static JPanel createTopAssistPanel(Color color){
        JButton resetButton = new JButton();
        resetButton.setToolTipText("Reset the alert statement and update 'Disk Info.' panel");
        ImageIcon newIcon = CallResources.getImage("BattIco/3808356");
        resetButton.setIcon(new ImageIcon(newIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        resetButton.addActionListener(_ -> {
            alertLabel.setText("");
            checkAndReset(color);
            String isA = whatIsVisible();
            refreshDiskInfoPanel(isA, DiskInfoPanel.isVisible());
        });

        JButton progressBarModeButton = new JButton();
        progressBarModeButton.setToolTipText("Convert to the other mode");
        ImageIcon progressBarModeIcon = CallResources.getImage("BattIco/9213472");
        progressBarModeButton.setIcon(new ImageIcon(progressBarModeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        progressBarModeButton.addActionListener(_ -> {
            progressBarInVerticalMode = !progressBarInVerticalMode;
            setProgressBarMode();
            setUpProgressPanel();

            DashboardPanel.add(progressPanel, BorderLayout.CENTER);
            DashboardPanel.repaint();
            DashboardPanel.revalidate();

            setVisibleFalse();
            reviewButtonsName();
            motherPanel.add(DashboardPanel, BorderLayout.CENTER);
            setVisibleTrue(isA_DashboardPanel);
            motherPanel.repaint();
            motherPanel.revalidate();
            saveGeneralConfigurations();
        });

        JButton themeButton = new JButton();
        themeButton.setToolTipText("Switch the theme, right-click to open the context menu");
        ImageIcon themeIcon = CallResources.getImage(Appearance.iconName);
        themeButton.setIcon(new ImageIcon(themeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        themeButton.addActionListener(_ -> {
            Appearance.switchToOtherMode();
            ConfigurationFilesManager.saveGeneralConfigurations();
            rebuild();
        });
        themeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == (MouseEvent.BUTTON3)){
                    JPopupMenu menu = getPopupMenu();
                    menu.show(themeButton, 0, themeButton.getHeight());
                }
            }
        });

        JButton simulatorButton = new JButton();
        simulatorButton.setToolTipText("Open the Battery Simulator");
        ImageIcon simulatorIcon = CallResources.getImage("BattIco/5550932");
        simulatorButton.setIcon(new ImageIcon(simulatorIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        simulatorButton.addActionListener(_ -> BatteryIcon.BatterySimulationStart());

        JButton notificationAboutButton = new JButton();
        ImageIcon notificationIcon = CallResources.getImage("BattIco/9783934");
        notificationAboutButton.setIcon(new ImageIcon(notificationIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        notificationAboutButton.addActionListener(_ -> aboutNotificationsIcon());

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonsPanel.add(resetButton);
        actionButtonsPanel.add(progressBarModeButton);
        actionButtonsPanel.add(themeButton);
        actionButtonsPanel.add(simulatorButton);
        actionButtonsPanel.add(notificationAboutButton);
        return actionButtonsPanel;
    }

    private static void startMonitoring() {
        if (!running) {
            running = true;
            monitor();
        }

        monitoringStatus.setText(
                com.battery_level_alarm.monitoring.core.HandleLevel.SPACE
                        + "The battery under monitoring."
        );
        actionButton.setText("Stop");
        actionButton.setToolTipText("Click to stop monitoring");
    }

    private static void stopMonitoring() {
        running = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }

        monitoringStatus.setText(
                com.battery_level_alarm.monitoring.core.HandleLevel.SPACE + "Stopped!"
        );
        actionButton.setText("Start");
        actionButton.setToolTipText("Click to start monitoring");
    }

    private static void checkAndReset(Color batteryColor){
    	try {
    		getBatteryMode(batteryColor);
    		batteryLevel = getBatteryLevel();
			batteryBar.setValue(batteryLevel);
			SwingUtilities.invokeLater(() -> ratioChargeLabel.setText("Battery Level: " + batteryLevel + "%"));
		} catch (Exception e1) {
            printErrorMessage(e1);
		}
    }
    
    private static void monitor() {
        monitoringThread = new Thread(() -> {
            try {
                while (running) {
                    int maxValue = UserChoices.getMaximumLevel();
                    int minValue = UserChoices.getMinimumLevel();
                    Color batteryColor = getBatteryColor(batteryLevel, minValue, maxValue);

                	checkAndReset(batteryColor);
                    batteryColor = getBatteryColor(batteryLevel, minValue, maxValue);
                    putNewItemInTheHistoryMap(status, batteryLevel);
                    isFromCriticalAlert = false;
                    operationIsEnd = false;

                    if ((batteryLevel >= maxValue) && isCharging) {
                        highLevelActions(batteryColor);
                    } else if ((batteryLevel == (maxValue - 1)) && isCharging) {
                        highLevelActions(batteryColor);
                    } else if ((batteryLevel <= minValue) && !isCharging) {
                        lowLevelActions(batteryColor);
                    } else if (
                            (batteryLevel >= (maxValue - UserChoices.getAlertBeforeRiskPhaseBy()))
                                    && (batteryLevel < maxValue) && isCharging
                    ){
                        handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                    } else if (
                            (batteryLevel <= (minValue + UserChoices.getAlertBeforeRiskPhaseBy())) &&
                                    (batteryLevel > minValue) && !isCharging
                    ){
                        handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                    } else {
                        handleNormalBattery(batteryBar, alertLabel, batteryColor);
                    }

                    if(ComputerSettings.isActivateTheAwakeningFeature()){
                        WakeUpPC.wakeUp();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                SwingUtilities.invokeLater(() ->
                        alertLabel.setText(
                                com.battery_level_alarm.monitoring.core.HandleLevel.SPACE
                                        + "The Monitoring was Stopped!"
                        )
                );
            }
        });
        monitoringThread.start();
    }

    private static void highLevelActions(Color batteryColor) {
        try{
            if(isEnableSystemNotificationSound()){
                organizationOfRecallProcess();
            }
            isFromCriticalAlert = true;
            handleHighBattery(batteryBar, alertLabel, batteryColor);
            if(!operationIsEnd){
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            operationIsEnd = true;
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
    }

    private static void lowLevelActions(Color batteryColor) {
        try{
            if(isEnableSystemNotificationSound()){
                organizationOfRecallProcess();
            }
            isFromCriticalAlert = true;
            handleLowBattery(batteryBar, alertLabel, batteryColor);
            if(!operationIsEnd){
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            operationIsEnd = true;
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
    }

    private static void organizationOfRecallProcess(){
        if(callFlag){
            return;
        }
        callFlag = true;
        callNotifier();

        Timer organizer = new Timer(
                60000,
                _ -> callFlag = false
        );
        organizer.setRepeats(false);
        organizer.start();
    }
    
    private static void getBatteryMode(Color batteryColor) {
    	try {
    		isCharging = getBatteryStatus();
		} catch (Exception e) {
            printErrorMessage(e);
		}
        exchangeBatteryMode(batteryColor);
        track();
    }

    private static Color getBatteryColor(int charge, int min, int max) {
        if(isCharging) return Color.CYAN;
        else if(charge >= (max-1)) return Color.darkGray;
        else if(charge > min){
            if (charge > 60) return Color.GREEN;
            else if (charge > 30) return Color.ORANGE;
            else return Color.RED;
        }
        else return Color.RED;
    }
}