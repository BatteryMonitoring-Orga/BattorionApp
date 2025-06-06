package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GUI_ComponentConstraints.setTableConstraints;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.setButtonFontAndSize;

import com.battery_level_alarm.monitoring.core_utilities.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PrepareDiskInfoGUI {
	public static final Font textFont = new Font(Font.SERIF, Font.BOLD, 14);
	public static boolean isUnderTracking = false;
	private static Thread tracking;

	private static JPanel DiskInfoPanel = new JPanel();
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
        setButtonFontAndSize(
                cleanTempButton, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 13),
                250, 30,  SwingConstants.CENTER, SwingConstants.CENTER);
        cleanTempButton.addActionListener(_ -> Thread.ofVirtual().start(() -> {
            if(!isUnderTracking) {
                isUnderTracking = true;
                DiskSpaceInfo.cleanTempFiles();
                StatisticsContainerClass.refreshDiskInfoTab();
            }
        }));

        JButton whatIsTempButton = new JButton("What Is Temp Files?");
        setButtonFontAndSize(
                whatIsTempButton, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 13),
                150, 30,  SwingConstants.CENTER, SwingConstants.CENTER);
        whatIsTempButton.addActionListener(_ ->
                JOptionPane.showMessageDialog(
                        mainFrame,
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
}