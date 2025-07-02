package com.battery_level_alarm.monitoring.questionnaires;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StaticQuestionnaire {
    public static final Map<String, String> aboutActionTitle = new HashMap<>();
    static {
        aboutActionTitle.put("openTab-openComprehensiveBatteryGuideInArabic", COMPREHENSIVE_BATTERY_GUIDE_AR);
        aboutActionTitle.put("openTab-openComprehensiveBatteryGuideInEnglish", COMPREHENSIVE_BATTERY_GUIDE_EN);
    }
    
    public static @NotNull Map<String, String> getStringRunnableMap() {
        Map<String, String> actionsMap = new HashMap<>();
        actionsMap.put("openTab-openComprehensiveBatteryGuideInArabic",
                System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - Arabic.html"
        );
        actionsMap.put("openTab-openComprehensiveBatteryGuideInEnglish",
                System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - English.html"
        );
        return actionsMap;
    }
    
    public static String getAboutDispatchText() {
        return """
            <html>
                <head>
                    <style>
                        body {
                            font-family: Serif, sans-serif;
                            line-height: 1.6;
                            color: #222;
                            padding: 20px 30px;
                            margin: 0;
                            background-color: #fff;
                        }
                        h1, h2, b {
                            color: #111;
                        }
                        h2 {
                            margin-top: 0;
                            margin-bottom: 15px;
                            font-weight: bold;
                            font-size: 18px;
                        }
                        p {
                            margin: 10px 0;
                        }
                        ul {
                            margin: 10px 0 20px 20px;
                        }
                        a {
                            text-decoration: none;
                            color: #007acc;
                            font-weight: bold;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                    </style>
                </head>
                <body>
                    <h2>About Battorion</h2>
                    <p><b>Version: </b>""" + APP_VERSION +
            """
                    </p>
                    <p><b>Author:</b> Muath Hassoun</p>
                    <p>This application monitors your battery level and provides alerts in the following cases:</p>
                    <ul>
                                    <li><b>High Battery</b>: If the level reaches <b>85% or more</b>, you will be asked to unplug the charger.</li>
                                    <li><b>Low Battery</b>: If the level drops to <b>25% or less</b>, you will be reminded to charge the battery.</li>
                    </ul>
                    <p>The program operates in the background, periodically checking the battery level.</p>
                    <p>It is compatible with Windows, Linux, and macOS.</p>
                    <p>To use the application:</p>
                    <ul>
                                    <li>Click '<b>Start</b>' to begin monitoring.</li>
                                    <li>Click '<b>Stop</b>' to halt monitoring.</li>
                    </ul>
                    <p>Thank you for using Battorion!</p>
                    <p><a href='openTab-openComprehensiveBatteryGuideInArabic'>Comprehensive guide in Arabic</a></p>
                    <p><a href='openTab-openComprehensiveBatteryGuideInEnglish'>Comprehensive guide in English</a></p>
                </body>
            </html>
            """;
    }
    
    public static String getDiskInfoExplanation() {
        return """
        <html>
            <body style="font-family: Serif, sans-serif; padding: 10px;">
                <h2>Disk Cleanup Panel</h2>
                <p>
                    This panel provides real-time information about temporary files stored on your system.
                    Temporary files are created by the operating system and applications to store short-term data.
                </p>
                <p>
                    The table displays key statistics, including:
                </p>
                <ul>
                    <li><b>Number of temporary files:</b> The count of files considered unnecessary.</li>
                    <li><b>Number of directories:</b> The count of folders under the temporary file location.</li>
                    <li><b>Total size of temporary files:</b> How much disk space is currently used by these files.</li>
                    <li><b>Available disk space:</b> The remaining free space on the disk.</li>
                </ul>
                <h3>Features</h3>
                <ul>
                    <li><b>Clean Temp Files:</b> Allows you to remove unnecessary files safely to free up disk space.</li>
                    <li><b>Explanation Button:</b> Gives you a detailed description of what temp files are and why they can be deleted.</li>
                </ul>
                <p style="color: red; font-weight: bold;">
                    Note: While most temp files are safe to delete, make sure no applications are currently using them.
                </p>
            </body>
        </html>
        """;
    }
    
    public static String getStatisticsContainerExplanation() {
        return """
            <html>
                <body style="font-family: Serif, sans-serif; padding: 10px; color: #222;">
                    <h2 style="color:#004080;">Statistics Container</h2>
        
                    <h3>Battery Statistics Panel</h3>
                    <p>
                        This panel provides a comprehensive interface for monitoring and analyzing battery performance in real-time.
                    </p>
                    <ul>
                        <li><b>Battery Statistics:</b> Displays detailed information including:
                            <ul>
                                <li>Current battery states (Charging or Discharging).</li>
                                <li>The sharpest difference in battery levels during charging or discharging.</li>
                                <li>The time required to fully charge or discharge the battery.</li>
                                <li>Battery levels at the start and end of the charging/discharging process.</li>
                            </ul>
                        </li>
                        <li><b>History:</b> Provides insights into past battery activity, such as:
                            <ul>
                                <li>Charging History: Analyzes the frequency of changes in charging levels.</li>
                                <li>Discharging History: Offers a similar analysis for discharging activities.</li>
                            </ul>
                        </li>
                        <li><b>Interactive Controls:</b> Includes buttons for viewing historical data and accessing PC-specific details.</li>
                    </ul>
                    <p>This panel is designed to enhance user understanding of battery behavior through detailed statistics and interactive features.</p>
        
                    <hr style="margin: 20px 0;">
        
                    <h3>Disk Cleanup Panel</h3>
                    <p>
                        This panel provides real-time information about temporary files stored on your system.
                        Temporary files are created by the operating system and applications to store short-term data.
                    </p>
                    <p>The table displays key statistics, including:</p>
                    <ul>
                        <li><b>Number of temporary files:</b> The count of files considered unnecessary.</li>
                        <li><b>Number of directories:</b> The count of folders under the temporary file location.</li>
                        <li><b>Total size of temporary files:</b> How much disk space is currently used by these files.</li>
                        <li><b>Available disk space:</b> The remaining free space on the disk.</li>
                    </ul>
                    <h4>Features</h4>
                    <ul>
                        <li><b>Clean Temp Files:</b> Allows you to remove unnecessary files safely to free up disk space.</li>
                        <li><b>Explanation Button:</b> Gives you a detailed description of what temp files are and why they can be deleted.</li>
                    </ul>
                    <p style="color: #b00020; font-weight: bold;">
                        Note: While most temp files are safe to delete, make sure no applications are currently using them.
                    </p>
                </body>
            </html>
            """;
    }
}