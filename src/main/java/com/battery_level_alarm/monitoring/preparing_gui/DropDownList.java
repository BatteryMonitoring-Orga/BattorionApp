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
import java.util.function.Supplier;

public class DropDownList {
    private static final Font titleListsFont = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
    private static final Color borderForegroundColor = UIManager.getColor("Label.foreground");
    private static DropShadowBorder shadow;

    private static void createShadowObject(){
        shadow = new DropShadowBorder(
                DropDownList.borderForegroundColor, 5, 0.5f, 5,
            true, true, true, true
        );
    }

    public static JPanel prepareCheckLists(GridBagConstraints gbc){
        createShadowObject();
        JPanel Panel = new JPanel();
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("\u2003Do these procedures automatically:");
        mainTitle.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 16));
        JPanel mainTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainTitlePanel.add(mainTitle);

        JLabel generalList = new JLabel(ONE_SPACE + "▼ General Settings:");
        generalList.setFont(titleListsFont);
        JPanel generalListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalListPanel.add(generalList);

        JPanel firstPartialPanel = firstPartialPanel(gbc);
        JPanel firstPartialFooter = createFirstPartialPanelFooter();
        firstPartialPanel.setMaximumSize(new Dimension(480, 170));
        firstPartialPanel.setPreferredSize(new Dimension(480, 170));
        generalList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                firstPartialPanel.setVisible(!firstPartialPanel.isVisible());
                firstPartialFooter.setVisible(firstPartialPanel.isVisible());
                if(firstPartialPanel.isVisible()){
                    generalList.setText(ONE_SPACE + "▼ General Settings:");
                } else {
                    generalList.setText(ONE_SPACE + "▶ General Settings ...");
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                generalList.setForeground(Color.LIGHT_GRAY);
                generalList.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                generalList.setForeground(UIManager.getColor("Label.Foreground"));
                generalList.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        firstPartialPanel.setVisible(true);
        firstPartialFooter.setVisible(true);

        JLabel audioOutputList = new JLabel(ONE_SPACE + "▶ Audio Output ...");
        audioOutputList.setFont(titleListsFont);
        JPanel audioOutputListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        audioOutputListPanel.add(audioOutputList);

        JPanel secondPartialPanel = secondPartialPanel(gbc);
        JPanel secondPartialFooter = createSecondPartialPanelFooter();
        secondPartialPanel.setMaximumSize(new Dimension(480, 120));
        secondPartialPanel.setPreferredSize(new Dimension(480, 120));
        audioOutputList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                secondPartialPanel.setVisible(!secondPartialPanel.isVisible());
                secondPartialFooter.setVisible(secondPartialPanel.isVisible());
                if(secondPartialPanel.isVisible()){
                    audioOutputList.setText(ONE_SPACE + "▼ Audio Output:");
                } else {
                    audioOutputList.setText(ONE_SPACE + "▶ Audio Output ...");
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                audioOutputList.setForeground(Color.LIGHT_GRAY);
                audioOutputList.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                audioOutputList.setForeground(UIManager.getColor("Label.Foreground"));
                audioOutputList.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        secondPartialPanel.setVisible(false);
        secondPartialFooter.setVisible(false);

        JLabel soundLevelList = new JLabel(ONE_SPACE + "▶ Sound Level ...");
        soundLevelList.setFont(titleListsFont);
        JPanel soundLevelListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        soundLevelListPanel.add(soundLevelList);

        JPanel thirdPartialPanel = thirdPartialPanel(gbc);
        JPanel thirdPartialFooter = createThirdPartialPanelFooter();
        thirdPartialPanel.setMaximumSize(new Dimension(480, 120));
        thirdPartialPanel.setPreferredSize(new Dimension(480, 120));
        soundLevelList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                thirdPartialPanel.setVisible(!thirdPartialPanel.isVisible());
                thirdPartialFooter.setVisible(thirdPartialPanel.isVisible());
                if(thirdPartialPanel.isVisible()){
                    soundLevelList.setText(ONE_SPACE + "▼ Sound Level:");
                } else {
                    soundLevelList.setText(ONE_SPACE + "▶ Sound Level ...");
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                soundLevelList.setForeground(Color.LIGHT_GRAY);
                soundLevelList.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                soundLevelList.setForeground(UIManager.getColor("Label.Foreground"));
                soundLevelList.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        thirdPartialPanel.setVisible(false);
        thirdPartialFooter.setVisible(false);

        Panel.add(mainTitlePanel);
        Panel.add(generalListPanel);
        Panel.add(firstPartialPanel);
        Panel.add(firstPartialFooter);
        Panel.add(audioOutputListPanel);
        Panel.add(secondPartialPanel);
        Panel.add(secondPartialFooter);
        Panel.add(soundLevelListPanel);
        Panel.add(thirdPartialPanel);
        Panel.add(thirdPartialFooter);
        return Panel;
    }

    private static JPanel firstPartialPanel(GridBagConstraints gbc){
        JPanel firstPartialPanel = new JPanel(new GridBagLayout());
        firstPartialPanel.setOpaque(false);
        firstPartialPanel.setBorder(shadow);

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
        JLabel about = new JLabel("What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getFirstPartialQuestionnaires)
                )
        );

        JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPanel.add(about);
        return aboutPanel;
    }

    private static JPanel secondPartialPanel(GridBagConstraints gbc){
        JPanel secondPartialPanel = new JPanel(new GridBagLayout());
        secondPartialPanel.setOpaque(false);
        secondPartialPanel.setBorder(shadow);

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
        JLabel about = new JLabel("What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getSecondPartialQuestionnaires)
                )
        );

        JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPanel.add(about);
        return aboutPanel;
    }

    private static JPanel thirdPartialPanel(GridBagConstraints gbc){
        JPanel thirdPartialPanel = new JPanel(new GridBagLayout());
        thirdPartialPanel.setOpaque(false);
        thirdPartialPanel.setBorder(shadow);

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
        JLabel about = new JLabel("What do these options mean?" + TWO_SPACE);
        about.setFont(titleListsFont);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getThirdPartialQuestionnaires)
                )
        );

        JPanel aboutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutPanel.add(about);
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
}