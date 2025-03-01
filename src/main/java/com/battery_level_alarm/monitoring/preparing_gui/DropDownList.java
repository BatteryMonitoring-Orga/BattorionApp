package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.basics.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.core.FileManager.saveDropDownListConfigurations;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.basics.DropDownListStatus;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.core.FileManager;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropDownList {
    private static final Font titleListsFont = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
    private static final Color DARK_GREEN = new Color(0, 140, 0);
    public static Color borderForegroundColor;
    private static DropShadowBorder listShadow;
    private static DropShadowBorder closedPanelShadow;
    private static DropShadowBorder openedPanelShadow;

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
        listShadow = new DropShadowBorder(
                DropDownList.borderForegroundColor, 5, 0.5f, 5,
            true, true, true, true
        );
        closedPanelShadow = new DropShadowBorder(
                DropDownList.borderForegroundColor, 5, 0.5f, 5,
                true, true, true, true
        );
        openedPanelShadow = new DropShadowBorder(
                DARK_GREEN, 5, 0.5f, 5,
                true, true, true, true
        );
    }

    public static JPanel prepareCheckLists(GridBagConstraints gbc) {
        createShadowObject();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createTitlePanel("   Do these procedures automatically:"));

        int firstTrueCount = countTrueValues(FirstPartialTrueArray);
        int firstPercentage = calculatePercentage(firstTrueCount, FirstPartialTrueArray.length);
        firstProgressBar = createProgressBar(borderForegroundColor, 6, firstPercentage);
        panel.add(createChecklistSection("General Options ",
                new Dimension(480, 170),
                openedPanelShadow, closedPanelShadow, isFirstEnabled(),
                firstProgressBar, () -> firstPartialPanel(gbc),
                DropDownList::createFirstPartialPanelFooter,
                DropDownListStatus::setFirstEnabled
        ));

        int secondTrueCount = countTrueValues(SecondPartialTrueArray);
        int secondPercentage = calculatePercentage(secondTrueCount, SecondPartialTrueArray.length);
        secondProgressBar = createProgressBar(borderForegroundColor, 6, secondPercentage);
        panel.add(createChecklistSection(
                "Audio Output " + ONE_SPACE,
                new Dimension(480, 110),
                openedPanelShadow, closedPanelShadow, isSecondEnabled(),
                secondProgressBar, () -> secondPartialPanel(gbc),
                DropDownList::createSecondPartialPanelFooter,
                DropDownListStatus::setSecondEnabled
        ));

        int thirdTrueCount = countTrueValues(ThirdPartialTrueArray);
        int thirdPercentage = calculatePercentage(thirdTrueCount, ThirdPartialTrueArray.length);
        thirdProgressBar = createProgressBar(borderForegroundColor, 6, thirdPercentage);
        panel.add(createChecklistSection("Sound Level" + TWO_SPACE,
                new Dimension(480, 110),
                openedPanelShadow, closedPanelShadow, isThirdEnabled(),
                thirdProgressBar, () -> thirdPartialPanel(gbc),
                DropDownList::createThirdPartialPanelFooter,
                DropDownListStatus::setThirdEnabled
        ));
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
            DropShadowBorder openedBorder, DropShadowBorder closedBorder,
            boolean initiallyVisible,
            JProgressBar progressBar,
            Supplier<JPanel> panelSupplier,
            Supplier<JPanel> footerSupplier,
            Consumer<Boolean> setStateConsumer
    ){
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel firstSpace = new JLabel(ONE_SPACE);
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setFont(titleListsFont);
        checkBox.setSelected(initiallyVisible);
        JLabel secondSpace = new JLabel(TEN_SPACE + TWO_SPACE + TWO_SPACE);
        JLabel arrowLabel = new JLabel(initiallyVisible ? "\u2003▲" : "\u2003▼");

        labelPanel.add(firstSpace);
        labelPanel.add(checkBox);
        labelPanel.add(secondSpace);
        labelPanel.add(progressBar);
        labelPanel.add(arrowLabel);
        labelPanel.setBorder(initiallyVisible ? openedBorder : closedBorder);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel(TWO_SPACE), BorderLayout.WEST);
        mainPanel.add(labelPanel, BorderLayout.CENTER);
        mainPanel.add(new JLabel(TWO_SPACE + " "), BorderLayout.EAST);

        JPanel contentPanel = panelSupplier.get();
        JPanel footerPanel = footerSupplier.get();
        contentPanel.setMaximumSize(listSize);
        contentPanel.setPreferredSize(listSize);
        contentPanel.setVisible(initiallyVisible);
        footerPanel.setVisible(initiallyVisible);

        Runnable toggleVisibilityRunnable = () -> {
            boolean isVisible = !contentPanel.isVisible();
            checkBox.setSelected(isVisible);
            contentPanel.setVisible(isVisible);
            footerPanel.setVisible(isVisible);
            arrowLabel.setText(isVisible ? "\u2003▲" : "\u2003▼");
            labelPanel.setBorder(isVisible ? openedBorder : closedBorder);
            setStateConsumer.accept(isVisible);
            saveDropDownListConfigurations();
        };
        checkBox.addActionListener(e -> SwingUtilities.invokeLater(toggleVisibilityRunnable));
        addMouseListenerToComponent(labelPanel, toggleVisibilityRunnable, false, labelPanel, checkBox, arrowLabel);
        addMouseListenerToComponent(checkBox, toggleVisibilityRunnable, true, labelPanel, checkBox, arrowLabel);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(mainPanel);
        container.add(contentPanel);
        container.add(footerPanel);
        return container;
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
        JPanel firstPartialPanel = new JPanel(new GridBagLayout());
        firstPartialPanel.setOpaque(false);
        firstPartialPanel.setBorder(listShadow);

        int partialIndex = 0;
        String switched = isActivateTheAwakeningFeature()? "On":"Off";
        String toSNS = isEnableSystemNotificationSound()? "On":"Off";
        String toUnmuteVolume = isEnableUnmuteVolumeAutomatically()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, firstPartialPanel, "Activate the awakening feature:");
        setColumn(1);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                0,
                ComputerSettings::isActivateTheAwakeningFeature
        );
        addToggleButton(
                gbc, firstPartialPanel, ComputerSettings::setActivateTheAwakeningFeature,
                FileManager::saveComputerSettings, switched, 120, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanel, "Enable System Notification Sound:");
        setColumn(1);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                1,
                ComputerSettings::isEnableSystemNotificationSound
        );
        addToggleButton(
                gbc, firstPartialPanel, ComputerSettings::setEnableSystemNotificationSound,
                FileManager::saveComputerSettings, toSNS, 120, 30,
                secondProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanel, "Enable unmute volume automatically:");
        setColumn(1);
        ProgressBarValueUpdater thirdProgressBarUpdater = new ProgressBarValueUpdater(
                firstProgressBar,
                FirstPartialTrueArray,
                2,
                ComputerSettings::isEnableUnmuteVolumeAutomatically
        );
        addToggleButton(
                gbc, firstPartialPanel, ComputerSettings::setEnableUnmuteVolumeAutomatically,
                FileManager::saveComputerSettings, toUnmuteVolume, 120, 30,
                thirdProgressBarUpdater, true
        );
        return firstPartialPanel;
    }

    private static JPanel createFirstPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getFirstPartialQuestionnaires)
                )
        );

        JPanel aboutPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.SOUTH);
        aboutPanel.add(aboutPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static JPanel secondPartialPanel(GridBagConstraints gbc){
        JPanel secondPartialPanel = new JPanel(new GridBagLayout());
        secondPartialPanel.setOpaque(false);
        secondPartialPanel.setBorder(listShadow);

        int partialIndex = 0;
        String toSpeaker = isEnableExchangeToSpeakerAudioOutput()? "On":"Off";
        String toUsed = isEnableExchangeToAudioOutputUsed()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, secondPartialPanel, "Exchange to speaker audio output:");
        setColumn(1);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                SecondPartialTrueArray,
                0,
                ComputerSettings::isEnableExchangeToSpeakerAudioOutput
        );
        addToggleButton(
                gbc, secondPartialPanel, ComputerSettings::setEnableExchangeToSpeakerAudioOutput,
                FileManager::saveComputerSettings, toSpeaker, 120, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, secondPartialPanel, "Restore audio output used after alert:");
        setColumn(1);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                secondProgressBar,
                SecondPartialTrueArray,
                1,
                ComputerSettings::isEnableExchangeToAudioOutputUsed
        );
        addToggleButton(
                gbc, secondPartialPanel, ComputerSettings::setEnableExchangeToAudioOutputUsed,
                FileManager::saveComputerSettings, toUsed, 120, 30,
                secondProgressBarUpdater, true
        );
        return secondPartialPanel;
    }

    private static JPanel createSecondPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getSecondPartialQuestionnaires)
                )
        );

        JPanel aboutPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.SOUTH);
        aboutPanel.add(aboutPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static JPanel thirdPartialPanel(GridBagConstraints gbc){
        JPanel thirdPartialPanel = new JPanel(new GridBagLayout());
        thirdPartialPanel.setOpaque(false);
        thirdPartialPanel.setBorder(listShadow);

        int partialIndex = 0;
        String toChangeSoundLevel = isEnablingSoundLevelChange()? "On":"Off";
        String toRestoreLevel = isRestoringSoundLevelAfterAlert()? "On":"Off";

        setDimension(partialIndex, 0);
        addLabel(gbc, thirdPartialPanel, "Enable sound level change:");
        setColumn(1);
        ProgressBarValueUpdater firstProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                ThirdPartialTrueArray,
                0,
                ComputerSettings::isEnablingSoundLevelChange
        );
        addToggleButton(
                gbc, thirdPartialPanel, ComputerSettings::setEnablingSoundLevelChange,
                FileManager::saveComputerSettings, toChangeSoundLevel, 120, 30,
                firstProgressBarUpdater, true
        );

        setDimension(++partialIndex, 0);
        addLabel(gbc, thirdPartialPanel, "Restore sound level after alert:");
        setColumn(1);
        ProgressBarValueUpdater secondProgressBarUpdater = new ProgressBarValueUpdater(
                thirdProgressBar,
                ThirdPartialTrueArray,
                1,
                ComputerSettings::isRestoringSoundLevelAfterAlert
        );
        addToggleButton(
                gbc, thirdPartialPanel, ComputerSettings::setRestoringSoundLevelAfterAlert,
                FileManager::saveComputerSettings, toRestoreLevel, 120, 30,
                secondProgressBarUpdater, true
        );
        return thirdPartialPanel;
    }

    private static JPanel createThirdPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getThirdPartialQuestionnaires)
                )
        );

        JPanel aboutPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.SOUTH);
        aboutPanel.add(aboutPackage, BorderLayout.CENTER);
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