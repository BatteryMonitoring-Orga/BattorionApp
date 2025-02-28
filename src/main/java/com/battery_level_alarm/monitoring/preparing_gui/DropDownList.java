package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.core.FileManager;

import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

public class DropDownList {
    private static final Font titleListsFont = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
    private static final Color DARK_GREEN = new Color(0, 140, 0);
    public static Color borderForegroundColor;
    public static Color panelBackgroundColor;
    private static DropShadowBorder listShadow;
    private static DropShadowBorder closedPanelShadow;
    private static DropShadowBorder openedPanelShadow;

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
        panel.add(createTitlePanel("\u2003Do these procedures automatically:"));

        panel.add(createChecklistSection("General Options ",
                new Dimension(480, 170),
                openedPanelShadow, closedPanelShadow, true,
                () -> firstPartialPanel(gbc),
                DropDownList::createFirstPartialPanelFooter)
        );
        panel.add(createChecklistSection("Audio Output " + ONE_SPACE,
                new Dimension(480, 110),
                openedPanelShadow, closedPanelShadow, false,
                () -> secondPartialPanel(gbc),
                DropDownList::createSecondPartialPanelFooter)
        );
        panel.add(createChecklistSection("Sound Level" + TWO_SPACE,
                new Dimension(480, 110),
                openedPanelShadow, closedPanelShadow, false,
                () -> thirdPartialPanel(gbc),
                DropDownList::createThirdPartialPanelFooter)
        );
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
            Supplier<JPanel> panelSupplier,
            Supplier<JPanel> footerSupplier
    ){
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel separator = createSeparatorPanel(initiallyVisible? DARK_GREEN : panelBackgroundColor, 5);
        JLabel titleLabel = new JLabel(ONE_SPACE + title + TEN_SPACE + TWO_SPACE + TWO_SPACE);
        titleLabel.setFont(titleListsFont);
        JLabel arrowLabel = new JLabel(initiallyVisible ? "▲" : "▼");

        labelPanel.add(titleLabel);
        labelPanel.add(separator);
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

        labelPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = !contentPanel.isVisible();
                contentPanel.setVisible(isVisible);
                footerPanel.setVisible(isVisible);
                arrowLabel.setText(isVisible ? "▲" : "▼");
                separator.setBackground(isVisible ? DARK_GREEN : panelBackgroundColor);
                labelPanel.setBorder(isVisible ? openedBorder : closedBorder);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(Color.LIGHT_GRAY);
                arrowLabel.setForeground(Color.LIGHT_GRAY);
                labelPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(UIManager.getColor("Label.Foreground"));
                arrowLabel.setForeground(UIManager.getColor("Label.Foreground"));
                labelPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(mainPanel);
        container.add(contentPanel);
        container.add(footerPanel);
        return container;
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
        addToggleButton(gbc, firstPartialPanel, ComputerSettings::setActivateTheAwakeningFeature, FileManager::saveComputerSettings, switched, 120, 30);

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanel, "Enable System Notification Sound:");
        setColumn(1);
        addToggleButton(gbc, firstPartialPanel, ComputerSettings::setEnableSystemNotificationSound, FileManager::saveComputerSettings, toSNS, 120, 30);

        setDimension(++partialIndex, 0);
        addLabel(gbc, firstPartialPanel, "Enable unmute volume automatically:");
        setColumn(1);
        addToggleButton(gbc, firstPartialPanel, ComputerSettings::setEnableUnmuteVolumeAutomatically, FileManager::saveComputerSettings, toUnmuteVolume, 120, 30);
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
        addToggleButton(gbc, secondPartialPanel, ComputerSettings::setEnableExchangeToSpeakerAudioOutput, FileManager::saveComputerSettings, toSpeaker, 120, 30);

        setDimension(++partialIndex, 0);
        addLabel(gbc, secondPartialPanel, "Restore audio output used after alert:");
        setColumn(1);
        addToggleButton(gbc, secondPartialPanel, ComputerSettings::setEnableExchangeToAudioOutputUsed, FileManager::saveComputerSettings, toUsed, 120, 30);
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
        addToggleButton(gbc, thirdPartialPanel, ComputerSettings::setEnablingSoundLevelChange, FileManager::saveComputerSettings, toChangeSoundLevel, 120, 30);

        setDimension(++partialIndex, 0);
        addLabel(gbc, thirdPartialPanel, "Restore sound level after alert:");
        setColumn(1);
        addToggleButton(gbc, thirdPartialPanel, ComputerSettings::setRestoringSoundLevelAfterAlert, FileManager::saveComputerSettings, toRestoreLevel, 120, 30);
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

    private static void displayPopUpMenu(JLabel about, JPopupMenu popupMenu){
        popupMenu.show(about, 0, about.getHeight());
    }

    public static JPanel createSeparatorPanel(Color separatorColor, int thickness) {
        JPanel separator = new JPanel();
        separator.setBackground(separatorColor);
        separator.setPreferredSize(new Dimension(100, thickness));
        return separator;
    }
}