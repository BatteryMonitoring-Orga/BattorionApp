package com.battery_level_alarm.monitoring.preparing_gui;
import com.battery_level_alarm.monitoring.basics.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.command.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.core.BattorionMain;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PrepareDiskInfoGUI {
	public static final Font textFont = new Font(Font.SERIF, Font.BOLD, 14);
	public static boolean isUnderTracking = false;
	private static Thread tracking;
	private static JPanel DiskInfoPanel;

	public static JPanel getDiskInfoPanel() {
		return DiskInfoPanel;
	}

    public static void createGUI() {
        JTable table = getJTable();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton cleanTempButton = new JButton("<html>Delete <u>Unnecessary</u> Temp Files</html>");
        setButtonFontAndSize(cleanTempButton, 250, 30);
        cleanTempButton.addActionListener(e -> {
            if(!isUnderTracking) {
                isUnderTracking = true;

                tracking = new Thread(() -> {
                    DiskSpaceInfo.cleanTempFiles();
                    BattorionMain.refreshDiskInfoPanel(BattorionMain.isA_DiskInfoPanel, true);
                    checkFlag();
                });
                tracking.start();
            }
        });

        JButton whatIsTempButton = new JButton("What Is Temp Files?");
        setButtonFontAndSize(whatIsTempButton, 150, 30);
        whatIsTempButton.addActionListener(e -> JOptionPane.showMessageDialog(null, StaticQuestionnaire.Dispatch(textFont,StaticQuestionnaire.getTempFilesExplanation()),
                "What Are Temp Files?", JOptionPane.INFORMATION_MESSAGE));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setPreferredSize(new Dimension(300, 40));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cleanTempButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(whatIsTempButton);

        DiskInfoPanel = new JPanel(new BorderLayout());
        DiskInfoPanel.add(scrollPane, BorderLayout.CENTER);
        DiskInfoPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private static JTable getJTable() {
        String[][] data = {
                {"Number of temporary files", DiskSpaceInfo.getFilesNumber()},
                {"Number of directories", DiskSpaceInfo.getDirNumber()},
                {"Total size of temporary files", DiskSpaceInfo.getFilesSize() + " Consumed"},
                {"Available disk space", DiskSpaceInfo.getDirSize() + " Free"}
        };

        String[] columns = {"\u2003Type\u2003", "\u2003Value\u2003"};

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        table.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
        return table;
    }

    public static void setButtonFontAndSize(JButton button, int width, int height) {
    	button.setPreferredSize(new Dimension(width, height));
    	button.setMaximumSize(new Dimension(width, height));
    	button.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
    }
    
    private static void checkFlag() {
    	if(!isUnderTracking) {
    		tracking.interrupt();
    	}
    }
}