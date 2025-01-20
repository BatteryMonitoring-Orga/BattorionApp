package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.basics.HandleLevel.*;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.*;

import com.battery_level_alarm.monitoring.basics.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.command.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.effects.call_resources;
import com.battery_level_alarm.monitoring.preparing_gui.PrepareDiskInfoGUI;
import com.battery_level_alarm.monitoring.preparing_gui.Settings;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;

public class BatteryLevelAlarm {
	public static final Font textFont = new Font(Font.SERIF, Font.BOLD + Font.ITALIC, 14);
	public static final String isA_DashboardPanel = "DashboardPanel";
	public static final String isA_SettingScrollPanel = "SettingScrollPanel";
	public static final String isA_DiskInfoPanel = "DiskInfoPanel";
    private static Thread monitoringThread;
    
    public static JFrame mainFrame;
    private static JPanel motherPanel;
    private static JPanel DashboardPanel;
    private static JPanel DiskInfoPanel;
    private static JScrollPane SettingScrollPanel;
    
    private static JProgressBar batteryBar;
    private static JButton actionButton;
    private static JButton settingsButton;
    private static JButton diskButton;
    private static JButton aboutSettingsButton;
    
    private static JLabel monitoringStatus;
    private static JLabel ratioChargeLabel;
    private static JLabel alertLabel;
    private static JLabel statusLabel;
    
    private static String status = "";
    private static int batteryLevel = 0;
    private static boolean running = false;
    private static boolean isCharging = false;
    
	public static void main(String[] args) {
		FlatMacLightLaf.setup();
		UIManager.put("ToolTip.font", textFont);
		FileManager.loadSettings();
		Settings.createAndShowGUI();
		DiskSpaceInfo.DiskSpace();
		PrepareDiskInfoGUI.createGUI();
		SwingUtilities.invokeLater(BatteryLevelAlarm::createAndShowGUI);
	}
	
    private static void createAndShowGUI() {
        mainFrame = new JFrame("Battery Level Alarm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = call_resources.getImage("13228401");
        mainFrame.setIconImage(icon.getImage());
        mainFrame.setSize(420, 300);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        
        ifPanelsNullCreate();
        motherPanel = new JPanel(new BorderLayout());
        DashboardPanel = new JPanel(new BorderLayout());
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		
		monitoringStatus = new JLabel("");
		monitoringStatus.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
		monitoringStatus.setForeground(Color.BLACK);
		DashboardPanel.add(monitoringStatus, BorderLayout.NORTH);
		
		try {
			batteryLevel = getBatteryLevel();
		} catch (Exception e) {
			batteryLevel = 0;
            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
                    "Battery Level Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
		
		batteryBar = new JProgressBar(0, 100);
		batteryBar.setPreferredSize(new Dimension(200, 60));
		batteryBar.setMaximumSize(new Dimension(200, 60));
		batteryBar.setForeground(Color.GREEN);
		batteryBar.setValue(batteryLevel);
		batteryBar.setStringPainted(false);
		batteryBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		ratioChargeLabel = new JLabel("Battery Level: " + batteryLevel + "%");
		ratioChargeLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
		ratioChargeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		progressPanel.add(Box.createVerticalGlue());
		progressPanel.add(batteryBar);
		progressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		progressPanel.add(ratioChargeLabel);
		progressPanel.add(Box.createVerticalGlue());
		DashboardPanel.add(progressPanel, BorderLayout.CENTER);
		
		alertLabel = new JLabel("");
		alertLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
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
        reportButton.addActionListener(e -> batteryReport());
        aboutButton.addActionListener(e -> StaticQuestionnaire.aboutDispatch());
        aboutSettingsButton.addActionListener(e -> JOptionPane.showMessageDialog(null, StaticQuestionnaire.Dispatch(textFont, StaticQuestionnaire.getHowToUseSettings())
                , "Settings Explanation", JOptionPane.INFORMATION_MESSAGE));
        
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(settingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(diskButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(reportButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(aboutButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(aboutSettingsButton);
        aboutSettingsButton.setVisible(false);
        motherPanel.add(buttonPanel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Battery Status: " + status + " ");
        statusLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
        getBatteryMode();
        
        JButton resetButton = new JButton();
        resetButton.setToolTipText("Reset the alert and counter to zero");
	    ImageIcon newIcon = call_resources.getImage("3808356");
	    resetButton.setIcon(new ImageIcon(newIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
        resetButton.addActionListener(e -> {
            alertLabel.setText("");
            checkAndReset();
            String isA = whatIsVisible();
            refreshDiskInfoPanel(isA, DiskInfoPanel.isVisible());
        });
        
        JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelsPanel.add(statusLabel);
        labelsPanel.add(resetButton);
        motherPanel.add(labelsPanel, BorderLayout.SOUTH);
        
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
    	} else if (settingsButton.getText().equals("Dashboard")) {
    		settingsButton.setText("Settings");
    	}
    }
    
    private static void ifPanelsNullCreate() {
    	if(SettingScrollPanel == null) {
    		SettingScrollPanel = new JScrollPane();
    	}
    	
    	if(DiskInfoPanel == null) {
    		DiskInfoPanel = new JPanel();
    	}
    }
    
    private static void setVisibleFalse() {
    	aboutSettingsButton.setVisible(false);
    	DashboardPanel.setVisible(false);
    	SettingScrollPanel.setVisible(false);
    	DiskInfoPanel.setVisible(false);
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
    	} else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
    	}
    }
    
    private static String whatIsVisible() {
    	if (DashboardPanel != null && DashboardPanel.isVisible()) {
    		return isA_DashboardPanel;
    	} else if (DiskInfoPanel.isVisible()) {
    		return isA_DiskInfoPanel;
    	} else if (SettingScrollPanel.isVisible()) {
    		return isA_SettingScrollPanel;
    	} else {
    	    return "No panel is visible";
    	}
    }
    
    private static void setUpSettingScrollPanel() {
    	reviewButtonsName();
    	Settings.createAndShowGUI();
    	SettingScrollPanel = Settings.getCreatedGUI();
    	settingsButton.setText("Dashboard");
    	settingsButton.setToolTipText("Go To Dashboard Page");
    	
    	ifPanelsNullCreate();
    	setVisibleFalse();
    	SettingScrollPanel.setVisible(true);
    	aboutSettingsButton.setVisible(true);
    	
    	motherPanel.add(SettingScrollPanel, BorderLayout.CENTER);
    	mainFrame.setSize(550, 320);
    }
    
    private static void setUpDashboardPanel() {
    	diskButton.setText("Disk Info.");
    	settingsButton.setText("Settings");
    	settingsButton.setToolTipText("Go To Settings Page");
    	
    	setVisibleFalse();
    	DashboardPanel.setVisible(true);
    	
    	motherPanel.add(DashboardPanel, BorderLayout.CENTER);
    	mainFrame.setSize(420, 300);
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
    
    private static void refreshMotherFrame() {
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.repaint();
    	mainFrame.validate();
    }
    
    private static void checkAndReset(){
    	try {
    		getBatteryMode();
    		batteryLevel = getBatteryLevel();
			batteryBar.setValue(batteryLevel);
			SwingUtilities.invokeLater(() -> ratioChargeLabel.setText("Battery Level: " + batteryLevel + "%"));
		} catch (Exception e1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e1.getClass().getName() + "\nMessage: " + e1.getMessage(),
                    "Battery Level Error",
                    JOptionPane.ERROR_MESSAGE
            );
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
    
    private static void monitor() {
        monitoringThread = new Thread(() -> {
            try {
                while (running) {
                	checkAndReset();
                    
                    int maxValue = UserChoices.getMaximumLevel();
                    int minValue = UserChoices.getMinimumLevel();
                    
                    if ((batteryLevel >= maxValue) && isCharging) {
                        handleHighBattery(batteryBar, alertLabel);
                    } else if ((batteryLevel == (maxValue - 1)) && isCharging) {
                        handleHighBattery(batteryBar, alertLabel);
                    } else if ((batteryLevel <= minValue) && !isCharging) {
                        handleLowBattery(batteryBar, alertLabel);
                    } else if ((batteryLevel >= (maxValue - 5)) && (batteryLevel < maxValue) && isCharging) {
                        handleBatteryWarning(batteryBar, alertLabel, "", Color.DARK_GRAY);
                    } else if ((batteryLevel > minValue) && (batteryLevel <= (minValue + 5)) && !isCharging) {
                        handleBatteryWarning(batteryBar, alertLabel, "", Color.RED);
                    } else {
                        handleNormalBattery(alertLabel);
                    }
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> alertLabel.setText("The Monitoring was Stopped!"));
            }
        });
        monitoringThread.start();
    }
    
    private static void getBatteryMode() {
    	try {
    		isCharging = getBatteryStatus();
		} catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
                    "Battery Level Error",
                    JOptionPane.ERROR_MESSAGE
            );
		}
    	
    	if(isCharging) {
    		batteryBar.setForeground(Color.CYAN);
    		status = "Charging";
    		statusLabel.setText("Battery Status: " + status + " ");
    	} else {
    		batteryBar.setForeground(Color.GREEN);
    		status = "Not Charging";
    		statusLabel.setText("Battery Status: " + status + " ");
    	}
    }
}