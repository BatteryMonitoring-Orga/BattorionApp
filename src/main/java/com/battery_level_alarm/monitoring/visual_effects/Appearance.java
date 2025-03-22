package com.battery_level_alarm.monitoring.visual_effects;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.visual_effects.StyleFiles.*;
import static com.battery_level_alarm.monitoring.graphics.GraphsDefinitions.CSS_FILE_NAME;

import com.battery_level_alarm.monitoring.system_core.Battorion;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;
import javax.swing.*;

public class Appearance {
    public static final String THEME_ICON_FOLDER_PATH = "/com/battery_level_alarm/monitoring/ThemeIco/";
    private static final String toLightThemeIcon = "18762164";
    private static final String toDarkThemeIcon = "2";
    private static final String timeBasedFirstOptionIcon = "17682150";
    private static final String timeBasedSecondOptionIcon = "17682132";
    public static String iconName;

    private static String themeName;
    private static Color borderColor;
    private static Timer timer;
    private static int delay = 100;
    public static boolean started = true;
    private static boolean isBeforeNoon = false;

    public static String getThemeName() {
        return themeName;
    }
    public static void setThemeName(String themeName) {
        Appearance.themeName = themeName;
    }
    public static Color getBorderColor() {
        return borderColor;
    }

    public static void switchToOtherMode() {
        switch (themeName) {
            case "Light" -> setTheme(Color.WHITE, toLightThemeIcon, "Dark");
            case "Dark" -> setTheme(Color.BLACK, toDarkThemeIcon, "Light");
            case "Time based 'first option'", "Time based 'second option'" -> {
                stopTimer();
                if(isAfterNoon(true, null)){
                    setTheme(Color.BLACK, toDarkThemeIcon, "Light");
                } else {
                    setTheme(Color.WHITE, toLightThemeIcon, "Dark");
                }
            }
        }
    }

    private static void setTheme(Color color, String icon, String newThemeName) {
        borderColor = color;
        iconName = icon;
        themeName = newThemeName;
    }

    public static JPopupMenu getPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem lightMenuItem = new JMenuItem("Light");
        JMenuItem darkMenuItem = new JMenuItem("Dark");
        JMenuItem timeBasedFirstOptionMenuItem = new JMenuItem("Time based 'first option'");
        JMenuItem timeBasedSecondOptionMenuItem = new JMenuItem("Time based 'second option'");

        lightMenuItem.addActionListener(_ -> {
            borderColor = Color.BLACK;
            iconName = toDarkThemeIcon;
            themeName = "Light";
            ConfigurationFilesManager.saveGeneralConfigurations();
            Battorion.rebuild();
        });
        darkMenuItem.addActionListener(_ -> {
            borderColor = Color.WHITE;
            iconName = toLightThemeIcon;
            themeName = "Dark";
            ConfigurationFilesManager.saveGeneralConfigurations();
            Battorion.rebuild();
        });
        timeBasedFirstOptionMenuItem.addActionListener(_ -> {
            iconName = timeBasedFirstOptionIcon;
            setupTimingTheme(Appearance::updateThemeFirstOption, "Time based 'first option'");
        });
        timeBasedSecondOptionMenuItem.addActionListener(_ -> {
            iconName = timeBasedSecondOptionIcon;
            setupTimingTheme(Appearance::updateThemeSecondOption, "Time based 'second option'");
        });

        popupMenu.add(lightMenuItem);
        popupMenu.add(darkMenuItem);
        popupMenu.add(timeBasedFirstOptionMenuItem);
        popupMenu.add(timeBasedSecondOptionMenuItem);
        return popupMenu;
    }

    public static void theme_setup() {
        switch (themeName) {
            case "Light" -> {
                FlatMacLightLaf.setup();
                isDarkMode = false;
                CSS_FILE_NAME = FLAT_MAC_LIGHT;
                borderColor = Color.BLACK;
                iconName = toDarkThemeIcon;
            }
            case "Dark" -> {
                FlatMacDarkLaf.setup();
                isDarkMode = true;
                CSS_FILE_NAME = FLAT_MAC_DARK;
                borderColor = Color.WHITE;
                iconName = toLightThemeIcon;
            }
            case "Time based 'first option'" -> {
                iconName = timeBasedFirstOptionIcon;
                setupTimingTheme(Appearance::updateThemeFirstOption, "Time based 'first option'");
            }
            case "Time based 'second option'" -> {
                iconName = timeBasedSecondOptionIcon;
                setupTimingTheme(Appearance::updateThemeSecondOption, "Time based 'second option'");
            }
            default -> {
                Appearance.themeName = "Light";
                FlatMacLightLaf.setup();
                isDarkMode = false;
                CSS_FILE_NAME = FLAT_MAC_LIGHT;
                borderColor = Color.BLACK;
                iconName = toDarkThemeIcon;
            }
        }
    }

    private static void setupTimingTheme(Runnable updater, String themeName) {
        Appearance.themeName = themeName;
        if(!started){
            restart();
        }

        updater.run();
        stopTimer();
        delay = 100;
        timer = new Timer(delay, _ -> updater.run());
        timer.start();
    }

    private static void stopTimer(){
        if (timer != null) {
            timer.stop();
        }
    }

    private static void updateThemeFirstOption() {
        if (!isAfterNoon(true, null)) {
            FlatIntelliJLaf.setup();
            isDarkMode = false;
            CSS_FILE_NAME = FLAT_INTELLIJ;
            setToLightTheme();
        } else {
            FlatHiberbeeDarkIJTheme.setup();
            isDarkMode = true;
            CSS_FILE_NAME = FLAT_HIBERBEE_DARK;
            setToDarkTheme();
        }
    }

    private static void updateThemeSecondOption() {
        if (!isAfterNoon(true, null)) {
            FlatAtomOneLightIJTheme.setup();
            isDarkMode = false;
            CSS_FILE_NAME = FLAT_ATOM_ONE_LIGHT;
            setToLightTheme();
        } else {
            FlatAtomOneDarkIJTheme.setup();
            isDarkMode = true;
            CSS_FILE_NAME = FLAT_ATOM_ONE_DARK;
            setToDarkTheme();
        }
    }

    private static void setToLightTheme(){
        borderColor = Color.BLACK;
        isAfterNoon(false, LocalTime.NOON);
        if(!isBeforeNoon && !started){
            restart();
        }
        isBeforeNoon = true;
    }

    private static void setToDarkTheme(){
        borderColor = Color.WHITE;
        isAfterNoon(false, LocalTime.MAX);
        if(isBeforeNoon && !started){
            restart();
        }
        isBeforeNoon = false;
    }

    private static boolean isAfterNoon(boolean justAskAboutTime, LocalTime type){
        LocalTime currentTime = LocalTime.now();
        if(!justAskAboutTime){
            delay = (int) (Duration.between(currentTime, type).toSeconds() * 1000);
            if(timer != null){
                timer.setDelay(delay);
            }
        }
        return !currentTime.isBefore(LocalTime.NOON);
    }

    private static void restart(){
        ConfigurationFilesManager.saveGeneralConfigurations();
        Battorion.rebuild();
    }
}