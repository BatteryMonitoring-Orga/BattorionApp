package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.addItemToAudioList;
import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.scheduler;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.main_browser;
import static com.battery_level_alarm.monitoring.system_core.handlers.BatteryModeHandler.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.system_core.handlers.BatteryLevelHandler.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionProgressBarHelper.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.ReleasePanel.setupReleasePanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.SaverModePanel.setupSaverModePanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.TopAssistPanel.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.addTextField;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.setMouseListener;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.textFieldFont;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.*;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.cleanupAfterInstallation;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.isReleaseInstallProcessRunning;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.mainPreviewFrame;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;
import static java.util.logging.Level.SEVERE;

import com.battery_level_alarm.monitoring.command_executors.DefaultSoundDeviceNameFinder;
import com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph;
import com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker;
import com.battery_level_alarm.monitoring.system_automation.WakeUpPC;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.PrepareDiskInfoGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Battorion {
    public static final Logger logger = Logger.getLogger(Battorion.class.getName());
    private static Thread monitoringThread;
    public static Preferences prefs;
    public static Color borderColor;
    public static Color panelBackgroundColor;
    public static AlarmSounds alarmSounds;
    static SystemTrayNotification stn;
    static Notifications notify;
    
    public static JFrame mainFrame;
    public static JTabbedPane SettingsContainer;
    public static JTabbedPane StatisticsContainer;
    public static JPanel motherFrameContainer;
    public static JPanel motherPanelContainer;
    public static JPanel motherPanel;
    public static JPanel mainButtonsContainer;
    public static JPanel HBoxPanel;
    public static JPanel DashboardPanel;
    public static JPanel SimulatorMainPanel;
    
    public static final JPanel progressPanel = new JPanel();
    public static JPanel soundControlPanel;
    public static JPanel saverModePanel = new JPanel();
    public static JPanel releasePanel = new JPanel();
    public static JProgressBar batteryBar;
    
    public static JTextField audioOutputDeviceDashTextField;
    private static JLabel monitoringStatus;
    public static JLabel alertLabel;
    public static JLabel statusLabel;
    public static JLabel ratioChargeLabel;

    private static final int duration = 1000;
    public static int batteryLevel = 0;
	public static String status = "";
    public static String lastMode = "";
    
    private static volatile boolean callFlag = false;
    private static volatile boolean interruptRequest = false;
    public static volatile boolean isMonitorRunning = false;
    public static boolean isApplicationMode = true;
    public static boolean isFromCriticalAlert = false;
    public static boolean isWasInCriticalPhase = false;
    public static boolean isCharging = false;
    public static boolean operationIsEnd = false;
    
    public static void main(String[] args) {
        setupUIFont();
        prefs = Preferences.userNodeForPackage(Battorion.class);
	    String modeToUse = prefs.get("StartBattorionWith", String.valueOf(DepartureModes.START_WITH_APPLICATION));
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
            logger.log(SEVERE, "🚨 Uncaught exception in thread: " + thread.getName(), throwable)
        );
        
        if(Boolean.parseBoolean(prefs.get("new-release", String.valueOf(false)))) {
            Thread.ofVirtual().start(() -> {
                prefs.put("new-release", String.valueOf(false));
                main_browser(new String[]{});
                cleanupAfterInstallation();
            });
        }
        loadGeneralConfigurations();
	    loadUpdateVersionConfigurations();
        Appearance.theme_setup();
        SingletonObject.singletonMethod(modeToUse, args);
    }
    
    private static void setupUIFont() {
        System.setProperty("flatlaf.useSystemFonts", "false");
        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));
    }
    
    public static void departure(String modeToUse, String[] args) {
        try {
            prepareStartup();
            DepartureModes mode = BattorionTrayUI.DepartureModes.valueOf(modeToUse);
            if (mode == DepartureModes.START_WITH_TRAY) {
                isApplicationMode = false;
                loadSettings();
                loadComputerSettings();
                main_fx(args);
            } else {
                isApplicationMode = true;
                build();
            }
        } catch (Exception e) {
            logger.severe("[EXCEPTION]: " + e.getMessage());
        }
    }
    
    public static void refreshMotherFrame() {
        mainFrame.repaint();
        mainFrame.validate();
    }
    
    public static void rebuild() {
        try {
            mainFrame.dispose();
            if (mainPreviewFrame != null) {
                mainPreviewFrame.dispose();
            }
            Thread.sleep(100);
        } catch (Exception e) {
            printErrorMessage(e);
        }
        
        setVisibleFalse();
        Appearance.started = true;
        loadGeneralConfigurations();
        Appearance.theme_setup();
        build();
    }
    
    public static void build() {
        setupToolTips();
        setupColors();
        setUIManagerPanelColor(panelBackgroundColor);
        configurationHistoryMap();
        
        loadSettings();
        loadComputerSettings();
        configurationSystemTrayNotifications();
        loadDropDownListConfigurations();
        loadGraphConfigurations();
        
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        BatteryStatisticsGUI.createGUI();
        SwingUtilities.invokeLater(Battorion::createAndShowGUI);
        Thread.ofVirtual().start(Battorion::setupDefaultAudioDeviceIfUnknown);
    }
    
    private static void setupToolTips() {
        UIManager.put("ToolTip.font", TEXT_FONT);
        ToolTipManager.sharedInstance().setEnabled(true);
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
    }
    
    private static void setupColors() {
        borderColor = UIManager.getColor("Label.foreground");
        panelBackgroundColor = UIManager.getColor("Button.background");
        DropDownList.borderForegroundColor = UIManager.getColor("Label.foreground");
        
        if (Appearance.getThemeName().equals("Dark")) {
            panelBackgroundColor = Color.BLACK;
        }
    }
    
    private static void setupDefaultAudioDeviceIfUnknown() {
        if (getDefaultSpeakerOutputDeviceName().equals(UNKNOWN_OUTPUT_DEVICE)) {
            String defaultDevice = DefaultSoundDeviceNameFinder.findFirstValidRenderDevice();
            setDefaultSpeakerOutputDeviceName(defaultDevice);
            
            String item = getItemFromAudioList(defaultDevice);
            if (item == null) {
                addItemToAudioList(defaultDevice);
            }
            saveComputerSettings();
        }
    }

    private static void configurationSystemTrayNotifications() {
        notify = new Notifications(
                APP_NAME,
                IMAGES_FOLDER_PATH + "/alert_stn.png",
                "Battery Reminder",
                "Battery is in risk!",
                duration,
                false
        );
        stn = new SystemTrayNotification();
        alarmSounds = new AlarmSounds(AlarmSounds.getIndexBySoundName(getNotificationSoundFileName()));
    }

    private static void callNotifier(String msg){
        notify.setAlarmMessage(msg + "\nBattery level is: " + batteryLevel);
        stn.setIsToShowPanel(false);
        SystemTrayNotification.CreateTrayIcon(notify, alarmSounds, null, true,true, true);
    }

    private static void createAndShowGUI() {
        initializeMainFrame();
        initializePanels();
        initializeDashboard(false);
        initializeButtonPanel();
        initializeStatusPanel();
        setupMainFrameListeners();
        handleAutoMonitoring();

        if(!isWestSidePartAppear){
            westSideButton.doClick();
        }
        mainFrame.setVisible(true);
        Appearance.started = false;
    }

    private static void initializeMainFrame() {
        mainFrame = new JFrame(APP_NAME);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setIconImage(CallResources.getImage(
                IMAGES_FOLDER_PATH, "13228401",
                new Dimension(20, 20), Image.SCALE_SMOOTH).getImage());
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getRootPane().putClientProperty("JRootPane.titleBarBackground", panelBackgroundColor);
    }
    
    private static void setupMainFrameListeners() {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isReleaseInstallProcessRunning) {
                    System.out.println("Cannot close: Release install process is running.");
                    return;
                }
                
                System.out.println("Window is closing...");
                isMonitorRunning = false;
                if (monitoringThread != null) {
                    BatteryLevelGraph.isRunning = false;
                    if(scheduler != null) {
                        scheduler.shutdown();
                    } if(monitoringThread != null) {
                        monitoringThread.interrupt();
                    }
                } if (isApplicationMode) {
                    cleanup(false);
                }
                Runtime.getRuntime().halt(0);
            }
            @Override
            public void windowClosed(WindowEvent e) {
                if(!isReleaseInstallProcessRunning) {
                    System.out.println("Window was closed...");
                }
            }
        });
    }
    
    private static void initializePanels() {
        motherFrameContainer = new JPanel(new BorderLayout());
        motherFrameContainer = applyGradientBackground(motherFrameContainer, isDarkMode, false, 0, false);
        motherPanelContainer = new JPanel();
        motherPanelContainer = applyGradientBackground(
                motherPanelContainer, isDarkMode, false, 0, false
        );
        motherPanelContainer.setLayout(new BoxLayout(motherPanelContainer, BoxLayout.Y_AXIS));
        
        motherPanel = new RoundedPanel(30, new BorderLayout());
        motherPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DashboardPanel = new JPanel(new BorderLayout());
        ifPanelsNullCreate();
    }
    
    public static void initializeDashboard(boolean isForRefreshDash) {
        if (isForRefreshDash) {
            prepareDashEastSidePanel();
            return;
        }
        monitoringStatus = createLabel("", 16, Font.ITALIC + Font.PLAIN);
        DashboardPanel.add(monitoringStatus, BorderLayout.NORTH);

        batteryLevel = getSafeBatteryLevel();
        Color color = getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel());
        createBatteryBar(color);
        ratioChargeLabel = createLabel("Battery Level: " + batteryLevel + "%", 15, Font.BOLD + Font.PLAIN);
        
        prepareDashEastSidePanel();
        setUpProgressPanel(progressBarInFirstMode);
        JPanel progressPanelContainer = new JPanel(new BorderLayout());
        progressPanelContainer.add(progressPanel, BorderLayout.CENTER);
        DashboardPanel.add(progressPanelContainer, BorderLayout.CENTER);
        
        alertLabel = createAlertLabel();
        JScrollPane scroll = createScrollPane(alertLabel);
        DashboardPanel.add(scroll, BorderLayout.SOUTH);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
    }
    
    private static void prepareDashEastSidePanel() {
        Component oldEast = ((BorderLayout)DashboardPanel.getLayout()).getLayoutComponent(BorderLayout.EAST);
        if (oldEast != null) {
            DashboardPanel.remove(oldEast);
        }
        
        JPanel eastSidePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        eastSidePanel.add(setupDashboardControlPanel());
        if(!isWaitingForInternet && thereIsNewVersion) {
            setupReleasePanel();
            eastSidePanel.add(releasePanel);
        } else {
            eastSidePanel.add(new JLabel(""));
        }
        
        setupSaverModePanel();
        eastSidePanel.add(saverModePanel);
        DashboardPanel.add(eastSidePanel, BorderLayout.EAST);
        DashboardPanel.revalidate();
        DashboardPanel.repaint();
    }
    
    private static void initializeButtonPanel() {
        mainButtonsContainer = createButtonPanel();
        createAndAddButtons(mainButtonsContainer);
        mainButtonsContainer.setPreferredSize(new Dimension(WEST_PANEL_OPEN_WIDTH, FRAME_HEIGHT));
        mainButtonsContainer.setMaximumSize(new Dimension(WEST_PANEL_OPEN_WIDTH, FRAME_HEIGHT));
    }

    private static void initializeStatusPanel() {
        JPanel statusLabelPanel = new JPanel(new BorderLayout());
        statusLabel = createLabel(ONE_SPACE + "Battery Status: " + status, 20, Font.PLAIN + Font.BOLD);
        statusLabelPanel.add(statusLabel, BorderLayout.CENTER);
        getBatteryMode(getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel()));
        lastMode = status;

        JPanel secondLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondLabelPanel.add(new JLabel(ONE_SPACE));
        JLabel audioOutputDeviceDashLabel = addLabel(
                new GridBagConstraints(), new JPanel(),
                "Audio Output: ", textFieldFont
        );
        
        audioOutputDeviceDashTextField = addTextField(
                new GridBagConstraints(), new JPanel(),
                getCurrentAudioDevice(),
                160, 20, null, false
        );
        setMouseListener(
                audioOutputDeviceDashTextField,
                BattorionPanelHelper::audioLabelMouseAction,
                UIManager.getColor("TextField.Foreground"),
                new Color(0, 134, 179),
                false, false, true
        );
        
        secondLabelPanel.add(audioOutputDeviceDashLabel);
        secondLabelPanel.add(audioOutputDeviceDashTextField);
        statusLabelPanel.add(secondLabelPanel, BorderLayout.SOUTH);

        JPanel topComponentsContainer = new RoundedPanel(30, new GridLayout(1, 2));
        topComponentsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topComponentsContainer.add(statusLabelPanel);
        topComponentsContainer.add(createTopAssistPanel(
                getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel())));
        motherPanelContainer.add(topComponentsContainer);
        motherPanelContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        createHBoxPanel();
        motherPanelContainer.add(HBoxPanel);

        motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.WEST);
        motherFrameContainer.add(new JLabel("\u2003 "), BorderLayout.EAST);
        motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.NORTH);
        motherFrameContainer.add(new JLabel("\u2003\u2003"), BorderLayout.SOUTH);
        motherFrameContainer.add(motherPanelContainer, BorderLayout.CENTER);
        mainFrame.add(motherFrameContainer);
    }

    private static void handleAutoMonitoring() {
        if (UserChoices.isAutoMonitoring()) {
            ImageIcon stopIcon = CallResources.getImage(
                    BUTTON_ICONS_PATH, "stop",
                    new Dimension(20, 20), Image.SCALE_SMOOTH);
            actionButton.setIcon(stopIcon);
            actionButton.doClick();
        }
    }

    private static JLabel createLabel(String text, int fontSize, int fontStyle) {
        int allowedStyles = Font.PLAIN | Font.BOLD | Font.ITALIC;
        fontStyle &= allowedStyles;

        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", fontStyle, fontSize));
        return label;
    }
    
    private static int getSafeBatteryLevel() {
        try {
            return getBatteryLevel();
        } catch (Exception e) {
            printErrorMessage(e);
            return 0;
        }
    }

    private static void createBatteryBar(Color color) {
        batteryBar = new JProgressBar(0, 100);
        setProgressBarMode();
        batteryBar.setForeground(color);
        batteryBar.setBorder(new LineBorder(Appearance.getBorderColor(), 3));
        batteryBar.setValue(batteryLevel);
        batteryBar.setStringPainted(false);
    }

    private static JLabel createAlertLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Serif", Font.PLAIN + Font.ITALIC, 16));
        label.setOpaque(true);
        label.setForeground(Color.RED);
        return label;
    }

    private static JScrollPane createScrollPane(JLabel label) {
        ScrollConfiguration configuration = new ScrollConfiguration(
                false, true, false,
                false, null,
                new Dimension(motherPanel.getWidth() - 100, 50)
        );
        JScrollPane scroll = new JScrollPane(label);
        applyScrollConfigurationDetails(scroll, configuration);
        return scroll;
    }
    
    private static JPanel createButtonPanel() {
        JPanel panel = new RoundedPanel(30, new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(110, 100));
        return panel;
    }

    public static void startMonitoring() {
        if (!isMonitorRunning) {
            isMonitorRunning = true;
            monitor();
        }
        monitoringStatus.setText(
                TWO_SPACE + "Battery is currently under monitoring."
        );

        actionButton.setText("");
        ImageIcon stopIcon = CallResources.getImage(
                BUTTON_ICONS_PATH, "stop",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        actionButton.setIcon(stopIcon);
        actionButton.setToolTipText("Click to stop monitoring");
        if(!westSideButton.getText().isEmpty()){
            actionButton.setText(STOP_BUTTON_TEXT);
        }
        AudioOutputDeviceNameChecker.threadStart();
    }

    public static void stopMonitoring() {
        isMonitorRunning = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }
        monitoringStatus.setText(
                TWO_SPACE + "Stopped!"
        );

        actionButton.setText("");
        ImageIcon stopIcon = CallResources.getImage(
                BUTTON_ICONS_PATH, "start",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        actionButton.setIcon(stopIcon);
        actionButton.setToolTipText("Click to start monitoring");
        if(!westSideButton.getText().isEmpty()){
            actionButton.setText(START_BUTTON_TEXT);
        }
    }
    
    public static void mainMonitorInterruptRequest() {
        try {
            interruptRequest = true;
            if (monitoringThread != null && monitoringThread.isAlive()) {
                monitoringThread.join(3000);
                
                if (monitoringThread.isAlive()) {
                    monitoringThread.interrupt();
                }
            }
        } catch (Exception ex) {
            printErrorMessage(ex);
        }
    }

    public static void checkAndReset(Color batteryColor){
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
        try {
            monitoringThread = new Thread(() -> {
                try {
                    while (isMonitorRunning) {
                        int maxValue = UserChoices.getMaximumLevel();
                        int minValue = UserChoices.getMinimumLevel();
                        
                        Color batteryColor = getBatteryColor(batteryLevel, minValue, maxValue);
                        checkAndReset(batteryColor);
                        batteryColor = getBatteryColor(batteryLevel, minValue, maxValue);
                        refreshTopAssistantPartialPanelsShadow(batteryColor);
                        
                        putNewItemInTheHistoryMap(status, batteryLevel);
                        isFromCriticalAlert = false;
                        operationIsEnd = false;
                        
                        try {
                            if (!isSilentMode && isCharging && (batteryLevel >= maxValue)) {
                                highLevelActions(batteryColor, "Battery is too high! Please unplug the charger...");
                            } else if (!isSilentMode && isCharging && (batteryLevel == (maxValue - 1))) {
                                highLevelActions(batteryColor, "Battery is high! Please unplug the charger...");
                            } else if (!isSilentMode && !isCharging && (batteryLevel <= minValue)) {
                                lowLevelActions(batteryColor);
                            } else if (
                                    !isSilentMode && isCharging &&
                                    (batteryLevel >= (maxValue - UserChoices.getAlertBeforeRiskPhaseBy()))
                            ){
                                handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                            } else if (
                                    !isSilentMode && !isCharging &&
                                    (batteryLevel <= (minValue + UserChoices.getAlertBeforeRiskPhaseBy()))
                            ) {
                                handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                            } else {
                                handleNormalBattery(batteryBar, alertLabel, batteryColor);
                            }
                        } catch (Exception e) {
                            logger.severe("[EXCEPTION]: " + e.getMessage());
                        }
                        
                        if(ComputerSettings.isActivateTheAwakeningFeature()){
                            WakeUpPC.wakeUp(ComputerSettings.getWakeUpEvery() * 60L);
                        } if(interruptRequest) {
                            isMonitorRunning = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    logger.severe("[EXCEPTION]: " + e.getMessage());
                    SwingUtilities.invokeLater(() -> alertLabel.setText(
                            TWO_SPACE + "Battery Monitoring was Stopped!"
                    ));
                }
            });
            monitoringThread.start();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.severe("[EXCEPTION]: " + e.getMessage());
        }
    }

    private static void highLevelActions(Color batteryColor, String msg) {
        try{
            if(isEnableSystemNotificationSound()){
                organizationOfRecallProcess(msg);
            }
            isFromCriticalAlert = true;
            handleHighBattery(batteryBar, alertLabel, batteryColor, msg);

            checkBrightnessControlMode(1);
            if(!operationIsEnd){
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            operationIsEnd = true;
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }

    private static void lowLevelActions(Color batteryColor) {
        try{
            String msg = "Battery is too low! Please plug the charger...";
            if(isEnableSystemNotificationSound()){
                organizationOfRecallProcess(msg);
            }
            isFromCriticalAlert = true;
            handleLowBattery(batteryBar, alertLabel, batteryColor, msg);

            checkBrightnessControlMode(2);
            if(!operationIsEnd){
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            operationIsEnd = true;
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }

    private static void checkBrightnessControlMode(int from){
        int mode = getBrightnessControlOption();

        if(mode == 0){
            setBrightnessLevel();
        } else if(mode == from){
            setBrightnessLevel();
        }
    }

    private static void setBrightnessLevel(){
        if (isAutomaticallyReduceAndRestoreBL()){
            brightnessLevelSetter();
        } else if(isAutomaticallyReduceBrightnessLevel()){
            brightnessLevelSetter();
        }
    }

    private static void brightnessLevelSetter(){
        isWasInCriticalPhase = true;
        Brightness.BrightnessProcess(0, true);
        Brightness.BrightnessProcess(getBrightnessLevel(), false);
    }
    
    private static void organizationOfRecallProcess(String msg){
        if(callFlag){
            return;
        }
        callFlag = true;
        callNotifier(msg);

        Timer organizer = new Timer(
                15000,
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
            if (charge > 60) return new Color(0, 140, 0);
            else if (charge > 30) return new Color(202, 88, 25);
            else return Color.RED;
        }
        else return Color.RED;
    }
}