package com.battery_level_alarm.monitoring.effects;
import com.battery_level_alarm.monitoring.core.BattorionMain;
import com.battery_level_alarm.monitoring.core.FileManager;
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
    private static final String toLightThemeIcon = "ThemeIco/18762164";
    private static final String toDarkThemeIcon = "ThemeIco/2";
    private static final String timeBasedFirstOptionIcon = "ThemeIco/17682150";
    private static final String timeBasedSecondOptionIcon = "ThemeIco/17682132";
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

        lightMenuItem.addActionListener(e -> {
            borderColor = Color.BLACK;
            iconName = toDarkThemeIcon;
            themeName = "Light";
            FileManager.saveGeneralConfigurations();
            BattorionMain.rebuild();
        });
        darkMenuItem.addActionListener(e -> {
            borderColor = Color.WHITE;
            iconName = toLightThemeIcon;
            themeName = "Dark";
            FileManager.saveGeneralConfigurations();
            BattorionMain.rebuild();
        });
        timeBasedFirstOptionMenuItem.addActionListener(e -> {
            iconName = timeBasedFirstOptionIcon;
            setupTimingTheme(Appearance::updateThemeFirstOption, "Time based 'first option'");
        });
        timeBasedSecondOptionMenuItem.addActionListener(e -> {
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
                borderColor = Color.BLACK;
                iconName = toDarkThemeIcon;
            }
            case "Dark" -> {
                FlatMacDarkLaf.setup();
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
        timer = new Timer(delay, e -> updater.run());
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
            setToLightTheme();
        } else {
            FlatHiberbeeDarkIJTheme.setup();
            setToDarkTheme();
        }
    }

    private static void updateThemeSecondOption() {
        if (!isAfterNoon(true, null)) {
            FlatAtomOneLightIJTheme.setup();
            setToLightTheme();
        } else {
            FlatAtomOneDarkIJTheme.setup();
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
        FileManager.saveGeneralConfigurations();
        BattorionMain.rebuild();
    }
}