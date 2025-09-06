package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.command_executors.SoundDevicesNamesFinder.updateAudioDevicesList;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.addItemToAudioList;
import static com.battery_level_alarm.monitoring.flow_chat.CallStepsFlow.handleUserFlows;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.scheduler;
import static com.battery_level_alarm.monitoring.registration_manager.AutoStartManager.enableAutoStart;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PanelIdentifiers.IS_A_LIFE_REPORT_PANEL;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.RoamingConfigClass.moveFileToRoamingFolder;
import static com.battery_level_alarm.monitoring.system_core.InitializeMainPanels.*;
import static com.battery_level_alarm.monitoring.system_core.handlers.BatteryModeHandler.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.*;
import static com.battery_level_alarm.monitoring.system_core.handlers.BattorionMainProcessHandler.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.system_core.handlers.BatteryLevelHandler.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.TopAssistPanel.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.LifeReportPanelUI.refreshReportPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.*;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.cleanupAfterInstallation;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.isReleaseInstallProcessRunning;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.mainPreviewFrame;
import static java.util.logging.Level.SEVERE;

import com.battery_level_alarm.monitoring.command_executors.SoundDevicesNamesFinder;
import com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph;
import com.battery_level_alarm.monitoring.modern_ui.AppNotificationToast;
import com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker;
import com.battery_level_alarm.monitoring.system_automation.WakeUpPC;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.PrepareDiskInfoGUI;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;
import javafx.application.Platform;

import javax.swing.*;
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
    private static SystemTrayNotification stn;
    private static Notifications notify;
    public static AlarmSounds alarmSounds;
    
    public static JFrame mainFrame;
    public static JTabbedPane SettingsContainer;
    public static JTabbedPane StatisticsContainer;
    public static JPanel motherFrameContainer;
    public static JPanel motherPanelContainer;
    public static JPanel motherPanel;
    public static JPanel mainButtonsContainer;
    public static JPanel HBoxPanel;
    public static JPanel DashboardPanel;
    public static JPanel LifeReportPanel;
    public static JPanel SimulatorMainPanel;
    public static JPanel FeedbackMainPanel;
    
    public static final JPanel progressPanel = new JPanel();
    public static JPanel soundControlPanel;
    public static JPanel saverModePanel = new JPanel();
    public static JPanel releasePanel = new JPanel();
    public static JProgressBar batteryBar;
    
    public static JTextField audioOutputDeviceDashTextField;
    static JLabel monitoringStatusLabel;
    public static JLabel estimatedRemainingTime;
    public static JLabel alertLabel;
    public static JLabel statusLabel;
    public static JLabel ratioChargeLabel;
    
    private static final int duration = 1000;
    private static int lastBatteryLevel = 0;
    public static int batteryLevel = 0;
    private static String msg = "";
	public static String status = "";
    public static String lastMode = "";
    
    private static volatile boolean callFlag = false;
    private static volatile boolean interruptRequest = false;
    public static volatile boolean isMonitorRunning = false;
    public static volatile boolean isFXLaunched = false;
    public static volatile boolean isAppToastNotifyEnabled = true;
    public static volatile boolean isProgramCurrentlyStarted = true;
    public static boolean isApplicationMode = true;
    public static boolean isFromCriticalAlert = false;
    public static boolean isWasInCriticalPhase = false;
    public static boolean isNextActiveMonitorMode = true;
    public static boolean isCharging = false;
    public static boolean operationIsEnd = false;
    public static boolean isMiniBrowserLaunched = false;
    
    public static void main(String[] args) {
        setupUIFont();
        prefs = Preferences.userNodeForPackage(Battorion.class);
        String modeToUse = prefs.get(START_BATTORION_WITH, String.valueOf(DepartureModes.START_WITH_APPLICATION));
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(SEVERE, "🚨 Uncaught exception in thread: " + thread.getName(), throwable);
            printErrorMessage(throwable);
        });
        
        if(prefs.getBoolean(NEW_RELEASE, false)) {
            Thread.ofVirtual().start(() -> Platform.startup(() -> {
                prefs.putBoolean(NEW_RELEASE, false);
                prefs.putBoolean(NEW_TRAY_TAB, true);
                moveFileToRoamingFolder(getVersionFileParentPath(), VERSION_FILE_NAME);
                cleanupAfterInstallation();
            }));
        } if(!prefs.getBoolean(START_ON_BOOT, false)) {
            prefs.putBoolean(START_ON_BOOT, true);
            prefs.put(VERSION_FILE_PATH, getVersionFileParentPath());
            moveFileToRoamingFolder(getVersionFileParentPath(), VERSION_FILE_NAME);
            enableAutoStart(APP_NAME, getCurrentExePath());
        }
        
        loadGeneralConfigurations();
        loadUpdateVersionConfigurations();
        Appearance.theme_setup();
        SingletonObject.singletonMethod(modeToUse, args);
    }
    
    public static void departure(String modeToUse, String[] args) {
        try {
            prepareStartup();
            Thread.ofVirtual().start(() -> {
                loadComputerSettings();
                setupAudioDevicesNames();
            });
            
            DepartureModes mode = BattorionTrayUI.DepartureModes.valueOf(modeToUse);
            if (mode == DepartureModes.START_WITH_TRAY) {
                isApplicationMode = false;
                isMiniBrowserLaunched = true;
                loadSettings();
                main_fx(args);
            } else {
                isApplicationMode = true;
                build();
                handleUserFlows();
            }
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }
    
    public static void build() {
        setupToolTips();
        setupColors();
        setUIManagerPanelColor(panelBackgroundColor);
        configurationHistoryMap();
        
        loadSettings();
        configurationSystemTrayNotifications();
        loadDropDownListConfigurations();
        loadGraphConfigurations();
        
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        BatteryStatisticsGUI.createGUI();
        SwingUtilities.invokeLater(Battorion::createAndShowGUI);
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
        isNextActiveMonitorMode = true;
        Appearance.started = true;
        loadGeneralConfigurations();
        Appearance.theme_setup();
        build();
    }
    
    private static void createAndShowGUI() {
        initializeMainFrame();
        initializePanels();
        initializeButtonPanel();
        initializeDashboard(false);
        initializeStatusPanel();
        setupMainFrameListeners();
        handleAutoMonitoring();
        
        Platform.setImplicitExit(false);
        Platform.runLater(() -> AppNotificationToast.showNotification("\uD83D\uDC4B Hey there! Welcome to Battorion — Where innovation meets simplicity!", batteryLevel, true));
        if(!isWestSidePartAppear){
            westSideButton.doClick();
        }
        mainFrame.setVisible(true);
        Appearance.started = false;
    }
    
    private static void initializeMainFrame() {
        mainFrame = new JFrame(APP_FRAME_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setIconImage(CallResources.getImage(
                IMAGES_FOLDER_PATH, "13228401",
                new Dimension(20, 20), Image.SCALE_SMOOTH).getImage());
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getRootPane().putClientProperty("JRootPane.titleBarBackground", panelBackgroundColor);
    }
    
    public static void refreshMasterFrame() {
        mainFrame.repaint();
        mainFrame.validate();
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
    
    private static void setupUIFont() {
        System.setProperty("flatlaf.useSystemFonts", "false");
        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));
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
    
    private static void setupAudioDevicesNames() {
        updateAudioDevicesList();
        if(getDefaultSpeakerOutputDeviceName() == null || getDefaultSpeakerOutputDeviceName().isEmpty() ||
                getDefaultSpeakerOutputDeviceName().equals(UNKNOWN_OUTPUT_DEVICE)) {
            String defaultDevice = SoundDevicesNamesFinder.findFirstDefaultValidRenderDevice();
            setDefaultSpeakerOutputDeviceName(defaultDevice);
            
            String item = getItemFromAudioList(defaultDevice);
            if (item == null) {
                addItemToAudioList(defaultDevice);
            } if (getCurrentAudioDevice().equals(UNKNOWN_OUTPUT_DEVICE)) {
                setCurrentAudioDevice(defaultDevice);
            }
        }
        saveComputerSettings();
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
    
    private static void handleAutoMonitoring() {
        if (UserChoices.isAutoMonitoring()) {
            ImageIcon stopIcon = CallResources.getImage(
                    BUTTON_ICONS_PATH, "stop",
                    new Dimension(20, 20), Image.SCALE_SMOOTH);
            actionButton.setIcon(stopIcon);
            actionButton.doClick();
        }
    }

    public static void startMonitoring() {
        if (!isMonitorRunning) {
            isMonitorRunning = true;
            monitor();
        }
        
        monitoringStatusLabel.setText("Battery is currently under monitoring.");
        ImageIcon stopIcon = CallResources.getImage(
                BUTTON_ICONS_PATH, "stop",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        actionButton.setIcon(stopIcon);
        actionButton.setToolTipText("Click to stop monitoring");
        AudioOutputDeviceNameChecker.threadStart();
    }

    public static void stopMonitoring() {
        isMonitorRunning = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }
        
        monitoringStatusLabel.setText("Stopped!");
        ImageIcon stopIcon = CallResources.getImage(
                BUTTON_ICONS_PATH, "start",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        actionButton.setIcon(stopIcon);
        actionButton.setToolTipText("Click to start monitoring");
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
                                highLevelActions(batteryColor);
                            } else if (!isSilentMode && !isCharging && (batteryLevel <= minValue)) {
                                lowLevelActions(batteryColor);
                            } else if (isCharging && (batteryLevel >= maxValue)) {
                                msg = "Battery is too high! Please unplug the charger...";
                                toastNotification();
                            } else if (!isCharging && (batteryLevel <= minValue)) {
                                msg = "Battery is too low! Please plug the charger...";
                                toastNotification();
                            } else if (
                                    !isSilentMode && isCharging &&
                                    (batteryLevel >= (maxValue - UserChoices.getAlertBeforeRiskPhaseBy()))
                            ) {
                                handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                                Platform.setImplicitExit(false);
                                Platform.runLater(() -> AppNotificationToast.showNotification("Battery level is high! Please unplug the charger.", batteryLevel, false));
                            } else if (
                                    !isSilentMode && !isCharging &&
                                    (batteryLevel <= (minValue + UserChoices.getAlertBeforeRiskPhaseBy()))
                            ) {
                                handleBatteryWarning(batteryBar, alertLabel, "", batteryColor);
                                Platform.setImplicitExit(false);
                                Platform.runLater(() -> AppNotificationToast.showNotification("Battery level is low! Please connect the charger.", batteryLevel, false));
                            } else {
                                handleNormalBattery(batteryBar, alertLabel, batteryColor);
                            }
                        } catch (Exception e) {
                            printErrorMessage(e);
                        }
                        
                        if (ComputerSettings.isActivateTheAwakeningFeature()) {
                            WakeUpPC.wakeUp(ComputerSettings.getWakeUpEvery() * 60L);
                        } if (interruptRequest) {
                            isMonitorRunning = false;
                            break;
                        } if ((lastBatteryLevel != batteryLevel) || !status.equals(lastMode)) {
                            lastBatteryLevel = batteryLevel;
                            Thread.ofVirtual().start(() -> refreshReportPanel(IS_A_LIFE_REPORT_PANEL.equals(whatIsVisible())));
                        }
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    printErrorMessage(e);
                    SwingUtilities.invokeLater(() -> alertLabel.setText(
                            TWO_SPACE + "Battery Monitoring was Stopped!"
                    ));
                }
            });
            monitoringThread.start();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            printErrorMessage(e);
        }
    }
    
    public static void checkAndReset(Color batteryColor) {
        try {
            getBatteryMode(batteryColor);
            batteryLevel = (int) getBatteryLevel();
            batteryBar.setValue(batteryLevel);
            SwingUtilities.invokeLater(() -> ratioChargeLabel.setText("Battery Level: " + batteryLevel + "%"));
        } catch (Exception e1) {
            printErrorMessage(e1);
        }
    }
    
    static int getSafeBatteryLevel() {
        try {
            return (int) getBatteryLevel();
        } catch (Exception e) {
            printErrorMessage(e);
            return 0;
        }
    }
    
    static void getBatteryMode(Color batteryColor) {
    	try {
            boolean previousCharging = isCharging;
    		isCharging = getBatteryStatus();
            if(isCharging != previousCharging) {
                isAppToastNotifyEnabled = true;
            }
		} catch (Exception e) {
            printErrorMessage(e);
		}
        exchangeBatteryMode(batteryColor);
        track();
    }
    
    private static void highLevelActions(Color batteryColor) {
        try {
            msg = "Battery is too high! Please unplug the charger...";
            if(isEnableSystemNotificationSound()) {
                organizationOfRecallProcess(msg);
            } if(!operationIsEnd) {
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            
            isFromCriticalAlert = true;
            handleHighBattery(batteryBar, alertLabel, batteryColor, msg);
            toastNotification();
            checkBrightnessControlMode(1);
            operationIsEnd = true;
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }
    
    private static void lowLevelActions(Color batteryColor) {
        try {
            msg = "Battery is too low! Please plug the charger...";
            if(isEnableSystemNotificationSound()) {
                organizationOfRecallProcess(msg);
            } if(!operationIsEnd) {
                howLongBatteryNeedToFullOrDump(status, "End");
            }
            
            isFromCriticalAlert = true;
            handleLowBattery(batteryBar, alertLabel, batteryColor, msg);
            toastNotification();
            checkBrightnessControlMode(2);
            operationIsEnd = true;
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }
    
    private static void organizationOfRecallProcess(String msg) {
        if(callFlag) {
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
    
    private static void toastNotification() {
        Platform.setImplicitExit(false);
        Platform.runLater(() -> AppNotificationToast.showNotification(msg, batteryLevel, false));
    }
    
    private static void checkBrightnessControlMode(int from) {
        int mode = getBrightnessControlOption();
        if ((mode == 0) || (mode == from)) {
            setBrightnessLevel();
        }
    }
    
    private static void setBrightnessLevel() {
        if (isAutomaticallyReduceAndRestoreBL() ||
                isAutomaticallyReduceBrightnessLevel()) {
            brightnessLevelSetter();
        }
    }
    
    private static void brightnessLevelSetter() {
        isWasInCriticalPhase = true;
        Brightness.BrightnessProcess(0, true);
        Brightness.BrightnessProcess(getBrightnessLevel(), false);
    }

    static Color getBatteryColor(int charge, int min, int max) {
        if(isCharging) return Color.CYAN;
        else if(charge >= (max-1)) return Color.darkGray;
        else if(charge > min) {
            if (charge > 60) return new Color(0, 140, 0);
            else if (charge > 30) return new Color(202, 88, 25);
            else return Color.RED;
        }
        else return Color.RED;
    }
}