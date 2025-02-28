package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.gui_constraints.GUI_ComponentConstraints.setTableConstraints;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import com.battery_level_alarm.monitoring.basics.StaticQuestionnaire;

import com.battery_level_alarm.monitoring.core.BattorionMain;
import com.battery_level_alarm.monitoring.core.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.command.DiskSpaceInfo;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PrepareDiskInfoGUI {
	public static final Font textFont = new Font(Font.SERIF, Font.BOLD, 14);
	public static boolean isUnderTracking = false;
	private static Thread tracking;

	private static JPanel DiskInfoPanel = new JPanel();;
	public static JPanel getDiskInfoPanel() {
		return DiskInfoPanel;
	}

    public static void createGUI() {
        DiskInfoPanel.setLayout(new BoxLayout(DiskInfoPanel, BoxLayout.Y_AXIS));
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
        cleanTempButton.addActionListener(_ -> {
            if(!isUnderTracking) {
                isUnderTracking = true;

                tracking = new Thread(() -> {
                    DiskSpaceInfo.cleanTempFiles();
                    BattorionPanelHelper.refreshDiskInfoPanel(BattorionMain.isA_DiskInfoPanel, true);
                    checkFlag();
                });
                tracking.start();
            }
        });

        JButton whatIsTempButton = new JButton("What Is Temp Files?");
        setButtonFontAndSize(whatIsTempButton, 150, 30);
        whatIsTempButton.addActionListener(_ ->
                JOptionPane.showMessageDialog(
                        null,
                        StaticQuestionnaire.Dispatch(
                                textFont, StaticQuestionnaire.getTempFilesExplanation()
                        ), "What Are Temp Files?", JOptionPane.INFORMATION_MESSAGE
                )
        );

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(300, 40));
        buttonPanel.add(cleanTempButton);
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
        return setTableConstraints(data, columns);
    }
    
    private static void checkFlag() {
    	if(!isUnderTracking) {
    		tracking.interrupt();
    	}
    }
}