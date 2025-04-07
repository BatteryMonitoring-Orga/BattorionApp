package com.battery_level_alarm.monitoring.visual_effects.appearance;

public class ThemesStatics {
    public static class ThemeNames {
        public static final String LIGHT = "Light";
        public static final String DARK ="Dark";
        public static final String TIME_BASED_FIRST_OPTION ="Time based 'first option'";
        public static final String TIME_BASED_SECOND_OPTION ="Time based 'second option'";

        public static String[] getThemeNames(){
            return new String[]{
                    LIGHT, DARK, TIME_BASED_FIRST_OPTION, TIME_BASED_SECOND_OPTION
            };
        }
    }

    public static class ThemeIcons {
        public static final String THEME_ICON_FOLDER_PATH = "/com/battery_level_alarm/monitoring/ThemeIco/";
        public static final String LIGHT_THEME_ICON = "18762164";
        public static final String DARK_THEME_ICON = "2";
        public static final String TIME_BASED_FIRST_OPTION_ICON = "17682150";
        public static final String TIME_BASED_SECOND_OPTION_ICON = "17682132";
    }
}