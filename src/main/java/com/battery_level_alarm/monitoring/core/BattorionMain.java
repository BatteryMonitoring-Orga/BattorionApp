package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.aboutNotificationsIcon;
import static com.battery_level_alarm.monitoring.core.BatteryMode.*;
import static com.battery_level_alarm.monitoring.core.FileManager.*;
import static com.battery_level_alarm.monitoring.core.HandleLevel.*;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.*;
import static com.battery_level_alarm.monitoring.effects.Appearance.getPopupMenu;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.cybernate.Timing.*;
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
import com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattorionMain {
    public static final String ChargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-start-2574.wav";
    public static final String DischargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-back-2575.wav";
    public static final String isIn_ChargingMode = "Charging";
    public static final String isIn_DisChargingMode = "Not Charging";
    public static final String isA_DashboardPanel = "DashboardPanel";
    public static final String isA_SettingScrollPanel = "SettingScrollPanel";
    public static final String isA_DiskInfoPanel = "DiskInfoPanel";
    public static final String isA_BatteryStatisticsPanel = "BatteryStatisticsPanel";
    private static final String APP_NAME = "Battorion";

    public static final Font textFont = new Font(Font.SERIF, Font.BOLD + Font.ITALIC, 14);
    private static Thread monitoringThread;
    private static SystemTrayNotification stn;
    private static Notifications notify;
    private static AlarmSounds alarmSounds;
    
    public static JFrame mainFrame;
    private static JPanel motherPanel;
    private static JPanel DashboardPanel;
    private static JPanel DiskInfoPanel;
    private static JPanel BatteryStatisticsPanel;
    private static JScrollPane SettingScrollPanel;

    private static final JPanel progressPanel = new JPanel();
    public static JProgressBar batteryBar;
    private static JButton actionButton;
    private static JButton settingsButton;
    private static JButton diskButton;
    private static JButton batteryStatisticsButton;
    private static JButton aboutSettingsButton;
    
    private static JLabel monitoringStatus;
    private static JLabel ratioChargeLabel;
    private static JLabel alertLabel;
    public static JLabel statusLabel;

    public static boolean progressBarInVerticalMode;
    public static String status = "";
    public static String lastMode = "";
    public static int batteryLevel = 0;
    private static final int duration = 1000;
    private static boolean callFlag = false;
    private static boolean running = false;
    public static boolean isFromCriticalAlert = false;
    public static boolean isCharging = false;
    public static boolean isExchanged = false;
    public static boolean operationIsEnd = false;
    public static boolean simulatorMode;
    
	public static void main(String[] args) {
        build();
	}

    private static void refreshMotherFrame() {
        mainFrame.setLocationRelativeTo(null);
        mainFrame.repaint();
        mainFrame.validate();
    }

    public static void rebuild(){
        mainFrame.dispose();
        Appearance.started = true;
        build();
    }

    public static void build(){
        loadGeneralConfigurations();
        Appearance.theme_setup();
        UIManager.put("ToolTip.font", textFont);
        configurationHistoryMap();
        configurationSystemTrayNotifications();

        loadSettings();
        loadComputerSettings();
        SettingsGUI.createAndShowGUI();
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        BatteryStatisticsGUI.createGUI(alarmSounds);
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
        mainFrame.setSize(550, 320);
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
		JScrollPane scroll = new JScrollPane(alertLabel);
		scroll.setPreferredSize(new Dimension(300, 40));
		DashboardPanel.add(scroll, BorderLayout.SOUTH);
		motherPanel.add(DashboardPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(110, 100));
        
        actionButton = new JButton("Start");
        setButtonFontAndSize(actionButton, 120, 70);
        actionButton.setToolTipText("Click to start monitoring");
        
        settingsButton = new JButton("Settings");
        setButtonFontAndSize(settingsButton, 120, 70);

        diskButton = new JButton("Disk Info.");
        setButtonFontAndSize(diskButton, 120, 70);

        batteryStatisticsButton = new JButton("Statistics");
        setButtonFontAndSize(batteryStatisticsButton, 120, 70);
        
        JButton reportButton = new JButton("Life Report");
        setButtonFontAndSize(reportButton, 120, 70);
        
        JButton aboutButton = new JButton("About");
        setButtonFontAndSize(aboutButton, 120, 70);
        
        aboutSettingsButton = new JButton("What is that?");
        setButtonFontAndSize(aboutSettingsButton, 120, 70);
        
        actionButton.addActionListener(e -> {
            if(actionButton.getText().equals("Start")) {
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });
        settingsButton.addActionListener(e -> {
            if(settingsButton.getText().equals("Settings")) {
                setUpSettingScrollPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        diskButton.addActionListener(e -> {
            if(diskButton.getText().equals("Disk Info.")) {
                setUpDiskInfoPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        batteryStatisticsButton.addActionListener(e -> {
            if(batteryStatisticsButton.getText().equals("Statistics")) {
                setUpBatteryStatisticsPanel();
            } else {
                setUpDashboardPanel();
            }
            refreshMotherFrame();
        });
        reportButton.addActionListener(e -> batteryReport());
        aboutButton.addActionListener(e -> StaticQuestionnaire.aboutDispatch());
        aboutSettingsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null, StaticQuestionnaire.Dispatch(textFont, StaticQuestionnaire.getHowToUseSettings())
                , "Settings Explanation", JOptionPane.INFORMATION_MESSAGE));
        
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(settingsButton);
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
        
        JButton resetButton = new JButton();
        resetButton.setToolTipText("Reset the alert statement and update 'Disk Info.' panel");
	    ImageIcon newIcon = CallResources.getImage("BattIco/3808356");
	    resetButton.setIcon(new ImageIcon(newIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        resetButton.addActionListener(e -> {
            alertLabel.setText("");
            checkAndReset(color);
            String isA = whatIsVisible();
            refreshDiskInfoPanel(isA, DiskInfoPanel.isVisible());
        });

        JButton progressBarModeButton = new JButton();
        progressBarModeButton.setToolTipText("Convert to the other mode");
        ImageIcon progressBarModeIcon = CallResources.getImage("BattIco/9213472");
        progressBarModeButton.setIcon(new ImageIcon(progressBarModeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        progressBarModeButton.addActionListener(e -> {
            progressBarInVerticalMode = !progressBarInVerticalMode;
            setProgressBarMode();
            setUpProgressPanel();

            DashboardPanel.add(progressPanel, BorderLayout.CENTER);
            DashboardPanel.repaint();
            DashboardPanel.revalidate();

            setVisibleFalse();
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
        themeButton.addActionListener(e -> {
            Appearance.switchToOtherMode();
            FileManager.saveGeneralConfigurations();
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
        simulatorButton.addActionListener(e -> {
            BatteryIcon.BatterySimulationStart();
        });

        JButton notificationAboutButton = new JButton();
        ImageIcon notificationIcon = CallResources.getImage("BattIco/9783934");
        notificationAboutButton.setIcon(new ImageIcon(notificationIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        notificationAboutButton.addActionListener(e -> {
            aboutNotificationsIcon();
        });

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonsPanel.add(resetButton);
        actionButtonsPanel.add(progressBarModeButton);
        actionButtonsPanel.add(themeButton);
        actionButtonsPanel.add(simulatorButton);
        actionButtonsPanel.add(notificationAboutButton);

        JPanel labelsPanel = new JPanel(new GridLayout(1, 2));
        labelsPanel.add(statusLabelPanel);
        labelsPanel.add(actionButtonsPanel);
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

    private static void setProgressBarMode(){
        batteryBar.setMinimum(0);
        batteryBar.setMaximum(100);

        if(progressBarInVerticalMode){
            configureProgressBarVertical();
        } else {
            configureProgressBarHorizontal();
        }
    }

    private static void configureProgressBarVertical() {
        batteryBar.setOrientation(JProgressBar.VERTICAL);
        batteryBar.setPreferredSize(new Dimension(83, 180));
        batteryBar.setMaximumSize(new Dimension(83, 180));
    }

    private static void configureProgressBarHorizontal() {
        batteryBar.setOrientation(JProgressBar.HORIZONTAL);
        batteryBar.setPreferredSize(new Dimension(230, 80));
        batteryBar.setMaximumSize(new Dimension(230, 80));
    }

    private static void setUpProgressPanel(){
        if(progressBarInVerticalMode){
            progressPanelForVerticalMode();
        } else {
            progressPanelForHorizontalMode();
        }
    }

    private static void progressPanelForVerticalMode(){
        progressPanel.removeAll();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
        progressPanel.add(Box.createHorizontalGlue());
        progressPanel.add(ratioChargeLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        progressPanel.add(batteryBar);
        progressPanel.add(Box.createHorizontalGlue());
        progressPanel.setBackground(UIManager.getColor("Panel.Background"));
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private static void progressPanelForHorizontalMode(){
        progressPanel.removeAll();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(Box.createVerticalGlue());
        progressPanel.add(batteryBar);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        progressPanel.add(ratioChargeLabel);
        progressPanel.add(Box.createVerticalGlue());
        progressPanel.setBackground(UIManager.getColor("Panel.Background"));
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private static void setButtonFontAndSize(JButton button, int width, int height) {
    	button.setPreferredSize(new Dimension(width, height));
    	button.setMaximumSize(new Dimension(width, height));
    	button.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
    }
    
    private static void startMonitoring() {
        if (!running) {
            running = true;
            monitor();
        }
        
        monitoringStatus.setText("The battery under monitoring.");
        actionButton.setText("Stop");
        actionButton.setToolTipText("Click to stop monitoring");
    }
    
    private static void stopMonitoring() {
        running = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }
        
        monitoringStatus.setText("Stopped!");
        actionButton.setText("Start");
        actionButton.setToolTipText("Click to start monitoring");
    }
    
    private static void reviewButtonsName() {
    	if(diskButton.getText().equals("Dashboard")) {
    		diskButton.setText("Disk Info.");
    	} else if(settingsButton.getText().equals("Dashboard")) {
    		settingsButton.setText("Settings");
    	} else if(batteryStatisticsButton.getText().equals("Dashboard")){
            batteryStatisticsButton.setText("Statistics");
        }
    }
    
    private static void ifPanelsNullCreate() {
    	if(SettingScrollPanel == null) {
    		SettingScrollPanel = new JScrollPane();
            SettingScrollPanel.setVisible(false);
    	} if(DiskInfoPanel == null) {
    		DiskInfoPanel = new JPanel();
            DiskInfoPanel.setVisible(false);
    	} if(BatteryStatisticsPanel == null) {
            BatteryStatisticsPanel = new JPanel();
            BatteryStatisticsPanel.setVisible(false);
        }
    }
    
    private static void setVisibleFalse() {
    	aboutSettingsButton.setVisible(false);
    	DashboardPanel.setVisible(false);
    	SettingScrollPanel.setVisible(false);
    	DiskInfoPanel.setVisible(false);
        BatteryStatisticsPanel.setVisible(false);
    }
    
    private static void setVisibleTrue(String isA) {
    	if (DashboardPanel != null && isA.equals(isA_DashboardPanel)) {
    		DashboardPanel.setVisible(true);
    	} else if (DiskInfoPanel != null && isA.equals(isA_DiskInfoPanel)) {
    		diskButton.setText("Disk Info.");
    		diskButton.doClick();
    	} else if (SettingScrollPanel != null && isA.equals(isA_SettingScrollPanel)) {
    		settingsButton.setText("Settings");
    		settingsButton.doClick();
    	} else if (BatteryStatisticsPanel != null && isA.equals(isA_BatteryStatisticsPanel)) {
            batteryStatisticsButton.setText("Statistics");
            batteryStatisticsButton.doClick();
        } else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
    	}
    }
    
    private static String whatIsVisible() {
    	if (DashboardPanel.isVisible()) {
    		return isA_DashboardPanel;
    	} else if (DiskInfoPanel.isVisible()) {
    		return isA_DiskInfoPanel;
    	} else if (SettingScrollPanel.isVisible()) {
    		return isA_SettingScrollPanel;
    	} else if (BatteryStatisticsPanel.isVisible()) {
            return isA_BatteryStatisticsPanel;
        } else {
    	    return "No panel is visible";
    	}
    }

    private static void setUpDashboardPanel() {
        batteryStatisticsButton.setText("Statistics");
        diskButton.setText("Disk Info.");
        settingsButton.setText("Settings");
        settingsButton.setToolTipText("Go To Settings Page");

        setVisibleFalse();
        DashboardPanel.setVisible(true);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
        mainFrame.setSize(550, 320);
    }
    
    private static void setUpSettingScrollPanel() {
    	reviewButtonsName();
    	SettingsGUI.createAndShowGUI();
    	SettingScrollPanel = SettingsGUI.getCreatedGUI();
    	settingsButton.setText("Dashboard");
    	settingsButton.setToolTipText("Go To Dashboard Page");
    	
    	ifPanelsNullCreate();
    	setVisibleFalse();
    	SettingScrollPanel.setVisible(true);
    	aboutSettingsButton.setVisible(true);
    	motherPanel.add(SettingScrollPanel, BorderLayout.CENTER);
    	mainFrame.setSize(550, 350);
    }
    
    private static void setUpDiskInfoPanel() {
    	reviewButtonsName();
    	DiskInfoPanel = PrepareDiskInfoGUI.getDiskInfoPanel();
    	diskButton.setText("Dashboard");
    	
    	ifPanelsNullCreate();
    	setVisibleFalse();
    	DiskInfoPanel.setVisible(true);
    	motherPanel.add(DiskInfoPanel, BorderLayout.CENTER);
    	mainFrame.setSize(550, 320);
    }

    private static void setUpBatteryStatisticsPanel() {
        reviewButtonsName();
        BatteryStatisticsPanel = BatteryStatisticsGUI.getBatteryStatisticsPanel();
        batteryStatisticsButton.setText("Dashboard");

        ifPanelsNullCreate();
        setVisibleFalse();
        BatteryStatisticsPanel.setVisible(true);
        motherPanel.add(BatteryStatisticsPanel, BorderLayout.CENTER);
        mainFrame.setSize(550, 320);
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
    
    public static void refreshSettingsPanel() {
    	settingsButton.setText("Settings");
    	setVisibleFalse();
    	settingsButton.doClick();
    }
    
    public static void refreshDiskInfoPanel(String isA, boolean fromDiskPanel) {
    	DiskSpaceInfo.DiskSpace();
		PrepareDiskInfoGUI.createGUI();
		
		diskButton.setText("Disk Info.");
		setVisibleFalse();
		if(fromDiskPanel) {
			diskButton.doClick();
			return;
		}
		setVisibleTrue(isA);
    }

    public static void refreshBatteryStatisticsPanel() {
        BatteryStatisticsGUI.createGUI(alarmSounds);
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
                    } else if ((batteryLevel >= (maxValue - 5)) && (batteryLevel < maxValue) && isCharging) {
                        handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                    } else if ((batteryLevel > minValue) && (batteryLevel <= (minValue + 5)) && !isCharging) {
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
                SwingUtilities.invokeLater(() -> alertLabel.setText("The Monitoring was Stopped!"));
            }
        });
        monitoringThread.start();
    }

    private static void highLevelActions(Color batteryColor) {
        try{
            organizationOfRecallProcess();
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
            organizationOfRecallProcess();
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
                e -> {
                    callFlag = false;
                }
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