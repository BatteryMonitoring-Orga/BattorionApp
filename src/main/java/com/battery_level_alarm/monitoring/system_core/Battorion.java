package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.BatteryMode.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PanelIdentifiers.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import static com.battery_level_alarm.monitoring.system_core.BattorionButtonsHelper.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionButtonsHelper.createButton;
import static com.battery_level_alarm.monitoring.system_core.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.system_core.BatteryLevelHandler.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionProgressBarHelper.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.core_utilities.StaticQuestionnaire.aboutNotificationsIcon;
import static com.battery_level_alarm.monitoring.battery_report.ChooseAction.choose;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabelWithMouseListener;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.*;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance.getPopupMenu;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeIcons.THEME_ICON_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

import com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph;
import com.battery_level_alarm.monitoring.download_tracker.DownloadProgressSwingWithFX;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader;
import com.battery_level_alarm.monitoring.file_manager.AudioDeviceToolChecker;
import com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker;
import com.battery_level_alarm.monitoring.system_automation.WakeUpPC;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.PrepareDiskInfoGUI;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.StatisticsContainerClass;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.notifications.system_tray_notifications.basics.Notifications;
import com.notifications.system_tray_notifications.system_tray.SystemTrayNotification;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Battorion {
    private static Thread monitoringThread;
    public static Color borderColor;
    public static Color panelBackgroundColor;
    public static AlarmSounds alarmSounds;
    static SystemTrayNotification stn;
    static Notifications notify;
    static DropShadowBorder dropShadowBorder;
    
    public static JFrame mainFrame;
    public static JPanel motherFrameContainer;
    public static JPanel motherPanelContainer;
    public static JPanel motherPanel;
    public static JTabbedPane SettingsContainer;
    public static JTabbedPane StatisticsContainer;
    static JPanel mainButtonsContainer;
    static JPanel HBoxPanel;
    static JPanel DashboardPanel;
    static JPanel SimulatorMainPanel;
    static JPanel downloaderPanel;

    static JPanel topAssistantPartialPanelsContainer;
    static JPanel firstTopAssistantPartialPanel;
    static JPanel secondTopAssistantPartialPanel;
    static JPanel thirdTopAssistantPartialPanel;

    static final JPanel progressPanel = new JPanel();
    static JPanel safeModePanel = new JPanel();
    static JProgressBar batteryBar;
    static JButton westSideButton;
    static JButton dashboardButton;
    static JButton actionButton;
    static JButton settingsButton;
    static JButton statisticsButton;
    static JButton simulatorButton;
    static JButton graphPainter;
    static JButton reportButton;
    static JButton aboutButton;

    private static JLabel monitoringStatus;
    private static JLabel alertLabel;
    public static JLabel audioOutputDeviceDashLabel;
    static JLabel ratioChargeLabel;
    static JLabel statusLabel;

    private static final int duration = 1000;
    public static int batteryLevel = 0;
    private static int clickCount = 0;
    public static String status = "";
    static String lastMode = "";

    private static boolean callFlag = false;
    public static boolean isFromCriticalAlert = false;
    public static boolean isWasInCriticalPhase = false;
    public static boolean isMonitorRunning = false;
    static boolean isCharging = false;
    static boolean operationIsEnd = false;

	public static void main(String[] args) {
        dropShadowBorder = new DropShadowBorder(
                Color.WHITE, 5, 0.8f,
                4, true, true,
                true, true
        );

        loadGeneralConfigurations();
        Appearance.theme_setup();
        EssentialToolsDownloader.Downloader((_, _) -> {}, true);
        AudioDeviceToolChecker.startCheckingThread();
        SingletonObject.singletonMethod();
	}

    static void refreshMotherFrame() {
        mainFrame.repaint();
        mainFrame.validate();
    }

    public static void rebuild() {
        mainFrame.dispose();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }

        setVisibleFalse();
        Appearance.started = true;
        loadGeneralConfigurations();
        Appearance.theme_setup();
        build();
    }

    public static void build(){
        UIManager.put("ToolTip.font", TEXT_FONT);
        ToolTipManager.sharedInstance().setEnabled(true);
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(5000);

        borderColor = UIManager.getColor("Label.foreground");
        panelBackgroundColor = UIManager.getColor("Button.background");
        DropDownList.borderForegroundColor = UIManager.getColor("Label.foreground");
        if(Appearance.getThemeName().equals("Dark")){
            panelBackgroundColor = Color.BLACK;
        }
        setUIManagerPanelColor(panelBackgroundColor);
        configurationHistoryMap();
        configurationSystemTrayNotifications();

        loadSettings();
        loadComputerSettings();
        loadDropDownListConfigurations();
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        BatteryStatisticsGUI.createGUI();
        SwingUtilities.invokeLater(Battorion::createAndShowGUI);
    }

    private static void configurationSystemTrayNotifications() {
        notify = new Notifications(
                APP_NAME,
                IMAGES_FOLDER_PATH + "/13228401.png",
                "Battery Reminder",
                "Battery is in risk!",
                duration,
                false
        );
        stn = new SystemTrayNotification();
        alarmSounds = new AlarmSounds(1);
    }

    private static void callNotifier(String msg){
        notify.setAlarmMessage(msg + "\nBattery level is: " + batteryLevel);
        stn.setIsToShowPanel(false);
        stn.CreateTrayIcon(notify, alarmSounds);
    }

    private static void createAndShowGUI() {
        initializeMainFrame();
        initializePanels();
        initializeDashboard();
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
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setIconImage(CallResources.getImage(
                IMAGES_FOLDER_PATH, "13228401",
                new Dimension(20, 20), Image.SCALE_SMOOTH).getImage());
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getRootPane().putClientProperty("JRootPane.titleBarBackground", panelBackgroundColor);
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

    private static void initializeDashboard() {
        monitoringStatus = createLabel("", 15, Font.BOLD + Font.ITALIC);
        DashboardPanel.add(monitoringStatus, BorderLayout.NORTH);

        batteryLevel = getSafeBatteryLevel();
        Color color = getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel());

        createBatteryBar(color);
        ratioChargeLabel = createLabel("Battery Level: " + batteryLevel + "%", 15, Font.BOLD + Font.PLAIN);

        setUpSafeModePanel();
        JPanel eastSidePanel = new JPanel(new GridLayout(3, 1));
        eastSidePanel.add(new JLabel(""));
        eastSidePanel.add(new JLabel(""));
        eastSidePanel.add(safeModePanel);
        DashboardPanel.add(eastSidePanel, BorderLayout.EAST);

        setUpProgressPanel(progressBarInFirstMode);
        JPanel progressPanelContainer = new JPanel(new BorderLayout());
        progressPanelContainer.add(progressPanel, BorderLayout.CENTER);
        DashboardPanel.add(progressPanelContainer, BorderLayout.CENTER);

        alertLabel = createAlertLabel();
        JScrollPane scroll = createScrollPane(alertLabel);
        DashboardPanel.add(scroll, BorderLayout.SOUTH);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
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
        audioOutputDeviceDashLabel = addLabelWithMouseListener(
                new GridBagConstraints(), new JPanel(),
                "Audio Output: " + getCurrentAudioDevice(),
                new Color(0, 134, 179), BattorionPanelHelper::audioLabelMouseAction,
                new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 12)
        );
        secondLabelPanel.add(audioOutputDeviceDashLabel);
        statusLabelPanel.add(secondLabelPanel, BorderLayout.SOUTH);

        JPanel topComponentsContainer = new RoundedPanel(30, new GridLayout(1, 2));
        topComponentsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topComponentsContainer.add(statusLabelPanel);
        topComponentsContainer.add(createTopAssistPanel(getBatteryColor(batteryLevel, UserChoices.getMinimumLevel(), UserChoices.getMaximumLevel())));
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

    private static void setupMainFrameListeners() {
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isMonitorRunning = false;
                if (monitoringThread != null) {
                    BatteryLevelGraph.isRunning = false;
                    BatteryLevelGraph.scheduler.shutdown();
                    monitoringThread.interrupt();
                }
            }
        });
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
        label.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
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

    private static void createAndAddButtons(JPanel panel) {
        createMainButtons();
        hyalineButton(westSideButton, true, false, true, false);
        hyalineButton(dashboardButton, true, false, true, false);
        hyalineButton(statisticsButton, true, false, true, false);
        hyalineButton(reportButton, true, false, true, false);
        hyalineButton(graphPainter, true, false, true, false);
        hyalineButton(simulatorButton, true, false, true, false);
        hyalineButton(actionButton, true, false, true, false);
        hyalineButton(aboutButton, true, false, true, false);
        hyalineButton(settingsButton, true, false, true, false);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(westSideButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(dashboardButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(statisticsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(reportButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(graphPainter);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(simulatorButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(actionButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        for(int i=0; i<5; i++){
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        panel.add(aboutButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(settingsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        setButtonBackgroundColor();
    }

    private static void createMainButtons(){
        graphPainter = createGraphButton();
        westSideButton = createButton(WEST_SIDE_BUTTON_TEXT, "Disappear the west side",
                BUTTON_ICONS_PATH, "side_bar", _ -> setUpWestSideButton());
        dashboardButton = createButton(DASHBOARD_BUTTON_TEXT, "Go to dashboard panel",
                BUTTON_ICONS_PATH, "dashboard", _ ->{
                    if(!DashboardPanel.isVisible()){
                        setUpDashboardPanel();
                        refreshMotherFrame();
                    }
                });
        statisticsButton = createButton(STATISTICS_BUTTON_TEXT, "Go to statistics panel",
                BUTTON_ICONS_PATH, "statistics", _ -> {
                    if (!StatisticsContainer.isVisible()) {
                        setUpStatisticsPanel();
                        refreshMotherFrame();
                    }
                });
        simulatorButton = createButton(SIMULATOR_BUTTON_TEXT, "View simulator",
                BUTTON_ICONS_PATH, "simulator", _ -> {
                    if (!SimulatorMainPanel.isVisible()) {
                        setUpSimulatorPanel();
                        refreshMotherFrame();
                    }
                });
        actionButton = createButton(START_BUTTON_TEXT, "Click to start monitoring",
                BUTTON_ICONS_PATH, "start", _ -> {
                    if (actionButton.getText().contains("Start") || !isMonitorRunning) {
                        startMonitoring();
                    } else {
                        stopMonitoring();
                    }
                    refreshMotherFrame();
                });
        settingsButton = createButton(SETTINGS_BUTTON_TEXT, "Open settings tabbed panel",
                BUTTON_ICONS_PATH, "settings", _ -> {
                    if (!SettingsContainer.isVisible()) {
                        setUpSettingPanel();
                        refreshMotherFrame();
                    }
                });
        reportButton = createButton(
                REPORT_BUTTON_TEXT, "Generate battery life report",
                BUTTON_ICONS_PATH, "report", _ -> choose());
        aboutButton = createButton(
                ABOUT_BUTTON_TEXT, "About the application",
                BUTTON_ICONS_PATH, "about",
                _ -> StaticQuestionnaire.aboutDispatch());
    }

    private static JPanel createTopAssistPanel(Color color){
        Color downloaderBackgroundColor = UIManager.getColor("Panel.background");
        DownloadProgressSwingWithFX createDownloaderPanel = new DownloadProgressSwingWithFX(downloaderBackgroundColor);
        downloaderPanel = createDownloaderPanel.getDownloadProgressPanel();
        downloaderPanel.setPreferredSize(new Dimension(30, 30));
        downloaderPanel.setMaximumSize(new Dimension(30, 30));

        JButton updateButton = new JButton();
        updateButton.setToolTipText("Install new updates/versions of app");
        ImageIcon updateIcon = CallResources.getImage(
                IMAGES_FOLDER_PATH, "2878768",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        updateButton.setIcon(updateIcon);
        updateButton.addActionListener(_ -> System.out.println("new"));

        JButton resetButton = new JButton();
        resetButton.setToolTipText("Reset the alert statement then update Disk \nInformation tab and audio output device name");
        ImageIcon resetButtonIcon = CallResources.getImage(
                IMAGES_FOLDER_PATH, "3808356",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        resetButton.setIcon(resetButtonIcon);
        resetButton.addActionListener(_ -> {
            alertLabel.setText("");
            checkAndReset(color);
            StatisticsContainerClass.refreshDiskInfoTab();
            AudioOutputDeviceNameChecker.doExecutionSingleton();
        });

        JButton themeButton = new JButton();
        themeButton.setToolTipText("Switch the theme, right-click to open the context menu");
        ImageIcon themeIcon = CallResources.getImage(
                THEME_ICON_FOLDER_PATH, Appearance.iconName,
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        themeButton.setIcon(themeIcon);
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

        JButton brightnessButton = new JButton();
        brightnessButton.setToolTipText("Change the brightness of the screen");
        ImageIcon brightnessIcon = CallResources.getImage(
                IMAGES_FOLDER_PATH, "brightness",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        brightnessButton.setIcon(brightnessIcon);
        brightnessButton.addActionListener(_ -> {
            Brightness.BrightnessProcess(0, true);
            JPopupMenu menu = Brightness.createBrightnessMenu(Brightness.getCurrentBrightness());
            menu.show(brightnessButton, -100, brightnessButton.getHeight() + 12);
        });

        JButton progressBarModeButton = new JButton();
        progressBarModeButton.setToolTipText("Convert to the other mode");
        ImageIcon progressBarModeIcon = CallResources.getImage(
                IMAGES_FOLDER_PATH, "9213472",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        progressBarModeButton.setIcon(progressBarModeIcon);
        progressBarModeButton.addActionListener(_ -> {
            progressBarInVerticalMode = !progressBarInVerticalMode;
            clickCount++;
            if (clickCount % 2 == 0) {
                progressBarInFirstMode = !progressBarInFirstMode;
            }

            setProgressBarMode();
            setUpProgressPanel(progressBarInFirstMode);
            setVisibleFalse();
            motherPanel.add(DashboardPanel, BorderLayout.CENTER);
            setVisibleTrue(isA_DashboardPanel);
            motherPanel.repaint();
            motherPanel.revalidate();
            saveGeneralConfigurations();
        });

        JButton notificationAboutButton = new JButton();
        ImageIcon notificationIcon = CallResources.getImage(
                IMAGES_FOLDER_PATH, "9783934",
                new Dimension(20, 20), Image.SCALE_SMOOTH);
        notificationAboutButton.setIcon(notificationIcon);
        notificationAboutButton.addActionListener(_ -> aboutNotificationsIcon());

        hyalineButton(resetButton, false);
        hyalineButton(updateButton, false);
        hyalineButton(themeButton, false);
        hyalineButton(brightnessButton, false);
        hyalineButton(progressBarModeButton, false);
        hyalineButton(notificationAboutButton, false);

        topAssistantPartialPanelsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        firstTopAssistantPartialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        secondTopAssistantPartialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        thirdTopAssistantPartialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        firstTopAssistantPartialPanel.add(themeButton);
        firstTopAssistantPartialPanel.add(brightnessButton);
        secondTopAssistantPartialPanel.add(updateButton);
        secondTopAssistantPartialPanel.add(downloaderPanel);
        secondTopAssistantPartialPanel.add(resetButton);
        thirdTopAssistantPartialPanel.add(progressBarModeButton);
        thirdTopAssistantPartialPanel.add(notificationAboutButton);

        topAssistantPartialPanelsContainer.add(firstTopAssistantPartialPanel);
        topAssistantPartialPanelsContainer.add(secondTopAssistantPartialPanel);
        topAssistantPartialPanelsContainer.add(thirdTopAssistantPartialPanel);
        refreshTopAssistantPartialPanelsShadow(color);
        return topAssistantPartialPanelsContainer;
    }

    private static void hyalineButton(JButton button, boolean isColoredAble, boolean... values) {
        if (values.length == 0) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
        } else {
            if (values.length >= 1) {
                button.setOpaque(values[0]);
            } if (values.length >= 2) {
                button.setContentAreaFilled(values[1]);
            } if (values.length >= 3) {
                button.setBorderPainted(values[2]);
            }
        }

        boolean isBorderPainted = values.length < 3 || values[2];
        addHandMouseListener(button, isColoredAble, isBorderPainted);
    }

    private static void addHandMouseListener(JButton button, boolean isColoredAble, boolean isBorderPainted){
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
                if(isColoredAble){
                    setButtonBackgroundColor();
                    button.setBackground(DARK_BLUE);
                }
            }
        });
    }

    private static void startMonitoring() {
        if (!isMonitorRunning) {
            isMonitorRunning = true;
            monitor();
        }
        monitoringStatus.setText(
                TWO_SPACE + "The battery under monitoring."
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

    private static void stopMonitoring() {
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

                    if ((batteryLevel >= maxValue) && isCharging) {
                        highLevelActions(batteryColor, "Battery is too high! Please unplug the charger...");
                    } else if ((batteryLevel == (maxValue - 1)) && isCharging) {
                        highLevelActions(batteryColor, "Battery is high! Please unplug the charger...");
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
                                TWO_SPACE + "The Monitoring was Stopped!"
                        )
                );
            }
        });
        monitoringThread.start();
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
        } catch (InterruptedException e) {
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
        } catch (InterruptedException e) {
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