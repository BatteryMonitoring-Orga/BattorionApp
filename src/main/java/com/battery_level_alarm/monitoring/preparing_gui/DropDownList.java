package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.basics.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager.saveDropDownListConfigurations;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.DropDownListStatus;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropDownList {
    private static final Font TITLE_LISTS_FONT = new Font("Serif", Font.BOLD + Font.ITALIC, 15);
    public static final Font LABELS_FONT = new Font("Serif", Font.BOLD, 15);
    private static final Color DARK_GREEN = new Color(0, 140, 0);
    private static final Color DARK_BROWN = new Color(139, 69, 19);
    public static Color borderForegroundColor;
    private static DropShadowBorder containerListShadow;
    private static DropShadowBorder closedPanelShadow;
    private static DropShadowBorder openPanelShadow;

    private static final boolean[] FirstPartialTrueArray = {
            isActivateTheAwakeningFeature(),
            isEnableSystemNotificationSound(),
            isEnableUnmuteVolumeAutomatically()
    };
    private static final boolean[] SecondPartialTrueArray = {
            isEnableExchangeToSpeakerAudioOutput(),
            isEnableExchangeToAudioOutputUsed()
    };
    private static final boolean[] ThirdPartialTrueArray = {
            isEnablingSoundLevelChange(),
            isRestoringSoundLevelAfterAlert()
    };

    private static final JPanel[] PartialPanelsArray = {
            new JPanel(), new JPanel(), new JPanel()
    };

    private static JProgressBar firstProgressBar;
    private static JProgressBar secondProgressBar;
    private static JProgressBar thirdProgressBar;
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

    private static void createShadowObject(){
        containerListShadow = new DropShadowBorder(
                DARK_GREEN, 8, 0.8f, 8,
            true, true, true, true
        );
        closedPanelShadow = new DropShadowBorder(
                DropDownList.borderForegroundColor, 8, 0.8f, 8,
                true, true, true, true
        );
        openPanelShadow = new DropShadowBorder(
                DARK_BROWN, 4, 0.4f, 4,
                false, false, true, false
        );
    }

    public static JPanel prepareCheckLists(GridBagConstraints gbc) {
        createShadowObject();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        panel.add(createTitlePanel("   Do these procedures automatically:"));

        int firstTrueCount = countTrueValues(FirstPartialTrueArray);
        int firstPercentage = calculatePercentage(firstTrueCount, FirstPartialTrueArray.length);
        firstProgressBar = createProgressBar(borderForegroundColor, 6, firstPercentage);
        JPanel checklist1 = createChecklistSection("General Options ",
                new Dimension(480, 190),
                openPanelShadow, closedPanelShadow, isFirstEnabled(), 0,
                firstProgressBar, () -> firstPartialPanel(gbc),
                DropDownListStatus::setFirstEnabled);
        checklist1.setAlignmentY(Component.TOP_ALIGNMENT);

        int secondTrueCount = countTrueValues(SecondPartialTrueArray);
        int secondPercentage = calculatePercentage(secondTrueCount, SecondPartialTrueArray.length);
        secondProgressBar = createProgressBar(borderForegroundColor, 6, secondPercentage);
        JPanel checklist2 = createChecklistSection("Audio Output " + ONE_SPACE,
                new Dimension(480, 140),
                openPanelShadow, closedPanelShadow, isSecondEnabled(), 1,
                secondProgressBar, () -> secondPartialPanel(gbc),
                DropDownListStatus::setSecondEnabled);
        checklist2.setAlignmentY(Component.TOP_ALIGNMENT);

        int thirdTrueCount = countTrueValues(ThirdPartialTrueArray);
        int thirdPercentage = calculatePercentage(thirdTrueCount, ThirdPartialTrueArray.length);
        thirdProgressBar = createProgressBar(borderForegroundColor, 6, thirdPercentage);
        JPanel checklist3 = createChecklistSection("Sound Level" + TWO_SPACE,
                new Dimension(480, 140),
                openPanelShadow, closedPanelShadow, isThirdEnabled(), 2,
                thirdProgressBar, () -> thirdPartialPanel(gbc),
                DropDownListStatus::setThirdEnabled);
        checklist3.setAlignmentY(Component.TOP_ALIGNMENT);

        panel.add(checklist1);
        panel.add(checklist2);
        panel.add(checklist3);
        return panel;
    }

    private static JPanel createTitlePanel(String titleText) {
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 16));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(title);
        return titlePanel;
    }

    private static JPanel createChecklistSection(
            String title, Dimension listSize,
            DropShadowBorder openPanelShadow,
            DropShadowBorder closedBorder,
            boolean initiallyVisible, int index,
            JProgressBar progressBar,
            Supplier<JPanel> panelSupplier,
            Consumer<Boolean> setStateConsumer
    ){
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setFont(TITLE_LISTS_FONT);
        checkBox.setSelected(initiallyVisible);
        JLabel firstSpace = new JLabel(TEN_SPACE + FOUR_SPACE);
        JLabel arrowLabel = new JLabel(initiallyVisible ? "\u2003▲" : "\u2003▼");
        JLabel secondSpace = new JLabel(ONE_SPACE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        labelPanel.add(checkBox);
        labelPanel.add(firstSpace);
        labelPanel.add(progressBar);
        labelPanel.add(arrowLabel);
        labelPanel.add(secondSpace);
        labelPanel.setBorder(initiallyVisible ? openPanelShadow : closedBorder);
        mainPanel.add(labelPanel, BorderLayout.CENTER);

        JPanel contentPanel = panelSupplier.get();
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

    private static JPanel firstPartialPanel(GridBagConstraints gbc){
        JPanel firstPartialPanel = new JPanel(new BorderLayout());
        firstPartialPanel.setOpaque(false);
        JPanel firstPartialPanelContent = new JPanel(new GridBagLayout());
        firstPartialPanelContent.setOpaque(false);

        int partialIndex = 0;
        String switched = isActivateTheAwakeningFeature()? "On":"Off";
        String toSNS = isEnableSystemNotificationSound()? "On":"Off";
        String toUnmuteVolume = isEnableUnmuteVolumeAutomatically()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Activate the awakening feature:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                0,
                ComputerSettings::isActivateTheAwakeningFeature
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setActivateTheAwakeningFeature,
                ConfigurationFilesManager::saveComputerSettings, switched, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Enable System Notification Sound:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                1,
                ComputerSettings::isEnableSystemNotificationSound
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setEnableSystemNotificationSound,
                ConfigurationFilesManager::saveComputerSettings, toSNS, 60, 30,
                secondProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanelContent, " Enable unmute volume automatically:", LABELS_FONT);
        ProgressBarValueUpdater thirdProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                2,
                ComputerSettings::isEnableUnmuteVolumeAutomatically
        );
        setColumn(1);
        addLabel(gbc, firstPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, firstPartialPanelContent, ComputerSettings::setEnableUnmuteVolumeAutomatically,
                ConfigurationFilesManager::saveComputerSettings, toUnmuteVolume, 60, 30,
                thirdProgressBarUpdater, true
        );

        PartialPanelsArray[0] = firstPartialPanelContent;
        JPanel firstPartialPanelFooter = createFirstPartialPanelFooter();
        firstPartialPanelFooter.setOpaque(false);
        firstPartialPanel.add(firstPartialPanelContent, BorderLayout.CENTER);
        firstPartialPanel.add(firstPartialPanelFooter, BorderLayout.SOUTH);
        return firstPartialPanel;
    }

    private static JPanel createFirstPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getFirstPartialQuestionnaires)
                )
        );

        JPanel aboutLabelPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutLabelPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        //aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.NORTH);
        aboutPanel.add(aboutLabelPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static JPanel secondPartialPanel(GridBagConstraints gbc){
        JPanel secondPartialPanel = new JPanel(new BorderLayout());
        secondPartialPanel.setOpaque(false);

        JPanel secondPartialPanelContent = new JPanel(new GridBagLayout());
        secondPartialPanelContent.setOpaque(false);
        int partialIndex = 0;
        String toSpeaker = isEnableExchangeToSpeakerAudioOutput()? "On":"Off";
        String toUsed = isEnableExchangeToAudioOutputUsed()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, secondPartialPanelContent, "Exchange to speaker audio output:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                SecondPartialTrueArray,
                0,
                ComputerSettings::isEnableExchangeToSpeakerAudioOutput
        );
        setColumn(1);
        addLabel(gbc, secondPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, secondPartialPanelContent, ComputerSettings::setEnableExchangeToSpeakerAudioOutput,
                ConfigurationFilesManager::saveComputerSettings, toSpeaker, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, secondPartialPanelContent, "Restore audio output used after alert:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                SecondPartialTrueArray,
                1,
                ComputerSettings::isEnableExchangeToAudioOutputUsed
        );
        setColumn(1);
        addLabel(gbc, secondPartialPanelContent, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, secondPartialPanelContent, ComputerSettings::setEnableExchangeToAudioOutputUsed,
                ConfigurationFilesManager::saveComputerSettings, toUsed, 60, 30,
                secondProgressBarUpdater, true
        );

        PartialPanelsArray[1] = secondPartialPanelContent;
        JPanel secondPartialPanelFooter = createSecondPartialPanelFooter();
        secondPartialPanelFooter.setOpaque(false);
        secondPartialPanel.add(secondPartialPanelContent, BorderLayout.CENTER);
        secondPartialPanel.add(secondPartialPanelFooter, BorderLayout.SOUTH);
        return secondPartialPanel;
    }

    private static JPanel createSecondPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getSecondPartialQuestionnaires)
                )
        );

        JPanel aboutLabelPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutLabelPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        //aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.NORTH);
        aboutPanel.add(aboutLabelPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static JPanel thirdPartialPanel(GridBagConstraints gbc){
        JPanel thirdPartialPanel = new JPanel(new BorderLayout());
        thirdPartialPanel.setOpaque(false);

        JPanel thirdPartialPanelContent = new JPanel(new GridBagLayout());
        thirdPartialPanelContent.setOpaque(false);
        int partialIndex = 0;
        String toChangeSoundLevel = isEnablingSoundLevelChange()? "On":"Off";
        String toRestoreLevel = isRestoringSoundLevelAfterAlert()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, thirdPartialPanelContent, "Enable sound level change:", LABELS_FONT);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                ThirdPartialTrueArray,
                0,
                ComputerSettings::isEnablingSoundLevelChange
        );
        setColumn(1);
        addLabel(gbc, thirdPartialPanelContent, FOUR_SPACE + TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, thirdPartialPanelContent, ComputerSettings::setEnablingSoundLevelChange,
                ConfigurationFilesManager::saveComputerSettings, toChangeSoundLevel, 60, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, thirdPartialPanelContent, "Restore sound level after alert:", LABELS_FONT);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                ThirdPartialTrueArray,
                1,
                ComputerSettings::isRestoringSoundLevelAfterAlert
        );
        setColumn(1);
        addLabel(gbc, thirdPartialPanelContent, FOUR_SPACE + TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, thirdPartialPanelContent, ComputerSettings::setRestoringSoundLevelAfterAlert,
                ConfigurationFilesManager::saveComputerSettings, toRestoreLevel, 60, 30,
                secondProgressBarUpdater, true
        );

        PartialPanelsArray[2] = thirdPartialPanelContent;
        JPanel thirdPartialPanelFooter = createThirdPartialPanelFooter();
        thirdPartialPanelFooter.setOpaque(false);
        thirdPartialPanel.add(thirdPartialPanelContent, BorderLayout.CENTER);
        thirdPartialPanel.add(thirdPartialPanelFooter, BorderLayout.SOUTH);
        return thirdPartialPanel;
    }

    private static JPanel createThirdPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getThirdPartialQuestionnaires)
                )
        );

        JPanel aboutLabelPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutLabelPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        //aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.NORTH);
        aboutPanel.add(aboutLabelPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static JPopupMenu createFooterPopupMenu(Supplier<String> textSourceGetter) {
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

    private static void setContainerSpecifications(JPanel container, int height, boolean isOpened){
        container.setMaximumSize(new Dimension(480, isOpened ? height + 50 : 50));
        container.setPreferredSize(new Dimension(480, isOpened ? height + 50 : 50));
        container.setBorder(isOpened ? containerListShadow : null);
    }

    public static JProgressBar createProgressBar(Color separatorColor, int thickness, int Value) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(Value);
        progressBar.setPreferredSize(new Dimension(100, thickness));
        progressBar.setBackground(separatorColor);
        progressBar.setStringPainted(false);
        return progressBar;
    }

    private static void displayPopUpMenu(JLabel about, JPopupMenu popupMenu){
        popupMenu.show(about, 0, about.getHeight());
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
}