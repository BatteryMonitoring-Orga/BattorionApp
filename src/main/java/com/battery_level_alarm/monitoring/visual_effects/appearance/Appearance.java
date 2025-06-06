package com.battery_level_alarm.monitoring.visual_effects.appearance;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.StyleFiles.*;
import static com.battery_level_alarm.monitoring.graphics.GraphsDefinitions.CSS_FILE_NAME;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeNames.*;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeIcons.*;

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
    
    public static void theme_setup() {
        switch (themeName) {
            case LIGHT -> {
                FlatMacLightLaf.setup();
                isDarkMode = false;
                CSS_FILE_NAME = FLAT_MAC_LIGHT;
                borderColor = Color.BLACK;
                iconName = DARK_THEME_ICON;
            }
            case DARK -> {
                FlatMacDarkLaf.setup();
                isDarkMode = true;
                CSS_FILE_NAME = FLAT_MAC_DARK;
                borderColor = Color.WHITE;
                iconName = LIGHT_THEME_ICON;
            }
            case TIME_BASED_FIRST_OPTION -> {
                iconName = TIME_BASED_FIRST_OPTION_ICON;
                setupTimingTheme(Appearance::updateThemeFirstOption, TIME_BASED_FIRST_OPTION);
            }
            case TIME_BASED_SECOND_OPTION -> {
                iconName = TIME_BASED_SECOND_OPTION_ICON;
                setupTimingTheme(Appearance::updateThemeSecondOption, TIME_BASED_SECOND_OPTION);
            }
            default -> {
                Appearance.themeName = LIGHT;
                FlatMacLightLaf.setup();
                isDarkMode = false;
                CSS_FILE_NAME = FLAT_MAC_LIGHT;
                borderColor = Color.BLACK;
                iconName = DARK_THEME_ICON;
            }
        }
    }

    public static void switchToOtherMode() {
        switch (themeName) {
            case LIGHT -> setTheme(Color.WHITE, LIGHT_THEME_ICON, DARK);
            case DARK -> setTheme(Color.BLACK, DARK_THEME_ICON, LIGHT);
            case TIME_BASED_FIRST_OPTION, TIME_BASED_SECOND_OPTION -> {
                stopTimer();
                if(isAfterNoon(true, null)){
                    setTheme(Color.BLACK, DARK_THEME_ICON, LIGHT);
                } else {
                    setTheme(Color.WHITE, LIGHT_THEME_ICON, DARK);
                }
            }
        }
    }

    public static void switchToTheSelectedMode() {
        stopTimer();
        switch (themeName) {
            case LIGHT -> setTheme(Color.BLACK, DARK_THEME_ICON, LIGHT);
            case DARK -> setTheme(Color.WHITE, LIGHT_THEME_ICON, DARK);
            case TIME_BASED_FIRST_OPTION -> {
                iconName = TIME_BASED_FIRST_OPTION_ICON;
                setupTimingTheme(Appearance::updateThemeFirstOption, TIME_BASED_FIRST_OPTION);
            }
            case TIME_BASED_SECOND_OPTION -> {
                iconName = TIME_BASED_SECOND_OPTION_ICON;
                setupTimingTheme(Appearance::updateThemeSecondOption, TIME_BASED_SECOND_OPTION);
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
        JMenuItem lightMenuItem = new JMenuItem(LIGHT);
        JMenuItem darkMenuItem = new JMenuItem(DARK);
        JMenuItem timeBasedFirstOptionMenuItem = new JMenuItem(TIME_BASED_FIRST_OPTION);
        JMenuItem timeBasedSecondOptionMenuItem = new JMenuItem(TIME_BASED_SECOND_OPTION);

        lightMenuItem.addActionListener(_ -> {
            borderColor = Color.BLACK;
            iconName = DARK_THEME_ICON;
            themeName = LIGHT;
            ConfigurationFilesManager.saveGeneralConfigurations();
            Battorion.rebuild();
        });
        darkMenuItem.addActionListener(_ -> {
            borderColor = Color.WHITE;
            iconName = LIGHT_THEME_ICON;
            themeName = DARK;
            ConfigurationFilesManager.saveGeneralConfigurations();
            Battorion.rebuild();
        });
        timeBasedFirstOptionMenuItem.addActionListener(_ -> {
            iconName = TIME_BASED_FIRST_OPTION_ICON;
            setupTimingTheme(Appearance::updateThemeFirstOption, TIME_BASED_FIRST_OPTION);
        });
        timeBasedSecondOptionMenuItem.addActionListener(_ -> {
            iconName = TIME_BASED_SECOND_OPTION_ICON;
            setupTimingTheme(Appearance::updateThemeSecondOption, TIME_BASED_SECOND_OPTION);
        });

        popupMenu.add(lightMenuItem);
        popupMenu.add(darkMenuItem);
        popupMenu.add(timeBasedFirstOptionMenuItem);
        popupMenu.add(timeBasedSecondOptionMenuItem);
        return popupMenu;
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