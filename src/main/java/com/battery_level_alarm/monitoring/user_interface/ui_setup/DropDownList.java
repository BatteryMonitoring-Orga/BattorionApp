package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveDropDownListConfigurations;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.user_interface.ui_config.DropDownListsContainerRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SingleDropDownListRecord;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropDownList {
    public static Color borderForegroundColor;
    private static Font titleListFont;
    private static DropShadowBorder containerListShadow;
    private static JPanel[] PartialPanelsArray;

    public static JPanel prepareListsContainer(DropDownListsContainerRecord containerRecord){
        titleListFont = containerRecord.titleListFont();
        containerListShadow = containerRecord.containerListShadow();
        PartialPanelsArray = containerRecord.PartialPanelsArray();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        panel.add(createTitlePanel(containerRecord.title()));

        for(SingleDropDownListRecord record : containerRecord.SingleDropDownListRecordsArray()){
            JPanel dropDownList = createDropDownList(record);
            dropDownList.setAlignmentY(Component.TOP_ALIGNMENT);
            panel.add(dropDownList);
        }
        return panel;
    }

    private static JPanel createTitlePanel(String titleText) {
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Serif", Font.BOLD, 16));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(title);
        return titlePanel;
    }

    private static JPanel createDropDownList(SingleDropDownListRecord record){
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boolean initiallyVisible = record.initiallyVisible();
        Dimension listSize = record.listSize();
        DropShadowBorder openPanelShadow = record.openPanelShadow();
        DropShadowBorder closedBorder = record.closedPanelShadow();
        Consumer<Boolean> setStateConsumer = record.setStateConsumer();
        int index = record.index();

        JCheckBox checkBox = new JCheckBox(record.title());
        checkBox.setSelected(initiallyVisible);
        checkBox.setFont(titleListFont);
        JLabel firstSpace = new JLabel(TEN_SPACE + FOUR_SPACE);
        JLabel arrowLabel = new JLabel(initiallyVisible ? "\u2003▲" : "\u2003▼");
        JLabel secondSpace = new JLabel(ONE_SPACE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        labelPanel.add(checkBox);
        labelPanel.add(firstSpace);
        labelPanel.add(record.progressBar());
        labelPanel.add(arrowLabel);
        labelPanel.add(secondSpace);
        labelPanel.setBorder(initiallyVisible ? openPanelShadow : closedBorder);
        mainPanel.add(labelPanel, BorderLayout.CENTER);

        JPanel contentPanel = record.dropDownPanel();
        contentPanel.setMaximumSize(listSize);
        contentPanel.setPreferredSize(listSize);
        contentPanel.setVisible(initiallyVisible);
        PartialPanelsArray[index].setBorder(initiallyVisible ? openPanelShadow : null);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.add(new JLabel(TWO_SPACE), BorderLayout.CENTER);
        footerPanel.setVisible(initiallyVisible);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        setContainerSpecifications(container, (int) listSize.getHeight(), initiallyVisible);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(mainPanel);
        container.add(contentPanel);

        Runnable toggleVisibilityRunnable = () -> {
            boolean isVisible = !contentPanel.isVisible();
            checkBox.setSelected(isVisible);
            contentPanel.setVisible(isVisible);
            footerPanel.setVisible(isVisible);
            arrowLabel.setText(isVisible ? "\u2003▲" : "\u2003▼");
            labelPanel.setBorder(isVisible ? openPanelShadow : closedBorder);
            PartialPanelsArray[index].setBorder(isVisible ? openPanelShadow : null);
            setContainerSpecifications(container, (int) listSize.getHeight(), isVisible);
            setStateConsumer.accept(isVisible);
            saveDropDownListConfigurations();
        };
        checkBox.addActionListener(_ -> SwingUtilities.invokeLater(toggleVisibilityRunnable));
        addMouseListenerToComponent(labelPanel, toggleVisibilityRunnable, false, labelPanel, checkBox, arrowLabel);
        addMouseListenerToComponent(checkBox, toggleVisibilityRunnable, true, labelPanel, checkBox, arrowLabel);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(new JLabel(TWO_SPACE + " "), BorderLayout.WEST);
        mainContainer.add(container, BorderLayout.CENTER);
        mainContainer.add(new JLabel(TWO_SPACE + " "), BorderLayout.EAST);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);
        return mainContainer;
    }

    private static void addMouseListenerToComponent(
            JComponent component, Runnable runnable, boolean isCheckBox,
            JPanel labelPanel, JCheckBox checkBox, JLabel arrowLabel
    ){
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!isCheckBox){
                    SwingUtilities.invokeLater(runnable);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                checkBox.setForeground(Color.LIGHT_GRAY);
                arrowLabel.setForeground(Color.LIGHT_GRAY);
                labelPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                checkBox.setForeground(UIManager.getColor("CheckBox.foreground"));
                arrowLabel.setForeground(UIManager.getColor("Label.Foreground"));
                labelPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private static void setContainerSpecifications(JPanel container, int height, boolean isOpened){
        container.setMaximumSize(new Dimension(WIDTH, isOpened ? height + 50 : 50));
        container.setPreferredSize(new Dimension(WIDTH, isOpened ? height + 50 : 50));
        container.setBorder(isOpened ? containerListShadow : null);
    }

    public static void updateProgressBars(ProgressBarValueUpdater progressBarValueUpdater){
        try {
            progressBarValueUpdater.partialTrueArray()[progressBarValueUpdater.index()] = progressBarValueUpdater.callable().call();
            int trueCount = countTrueValues(progressBarValueUpdater.partialTrueArray());
            int percentage = calculatePercentage(trueCount, progressBarValueUpdater.partialTrueArray().length);
            progressBarValueUpdater.progressBar().setValue(percentage);
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }

    public static JProgressBar prepareProgressBar(boolean[] array, int thickness){
        int count = countTrueValues(array);
        int percentage = calculatePercentage(count, array.length);
        return prepareProgressBar(borderForegroundColor, thickness, percentage);
    }

    public static JProgressBar prepareProgressBar(Color separatorColor, int thickness, int Value) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(Value);
        progressBar.setPreferredSize(new Dimension(100, thickness));
        progressBar.setBackground(separatorColor);
        progressBar.setStringPainted(false);
        return progressBar;
    }

    private static int countTrueValues(boolean[] array) {
        int count = 0;
        for (boolean value : array) {
            if (value) {
                count++;
            }
        }
        return count;
    }

    private static int calculatePercentage(int trueCount, int totalCount) {
        return (int) (((double) trueCount / totalCount) * 100);
    }

    public static void displayPopUpMenu(JLabel about, JPopupMenu popupMenu){
        popupMenu.show(about, 0, about.getHeight());
    }

    public static JPopupMenu createFooterPopupMenu(Supplier<String> textSourceGetter) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        editorPane.setContentType("text/html");
        editorPane.setText(textSourceGetter.get());
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setFocusable(false);
        editorPane.setHighlighter(null);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(340, 180));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(scrollPane);
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
            editorPane.setCaretPosition(0);
        });
        return popupMenu;
    }
}