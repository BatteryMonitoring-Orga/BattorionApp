module monitoring {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires javafx.swing;
	requires javafx.web;
	
	requires java.logging;
	requires java.prefs;
	requires java.management;
	requires jdk.jsobject;

	requires com.formdev.flatlaf;
	requires com.formdev.flatlaf.intellijthemes;
	requires org.jetbrains.annotations;
	requires system.tray.notifications;
	requires jlayer;
	requires org.json;
	requires swingx.core;
	
	exports com.battery_level_alarm.monitoring.battery_emulator;
	exports com.battery_level_alarm.monitoring.battery_report;
	exports com.battery_level_alarm.monitoring.command_executors;
	exports com.battery_level_alarm.monitoring.core_utilities;
	exports com.battery_level_alarm.monitoring.download_tracker;
	exports com.battery_level_alarm.monitoring.file_manager;
	exports com.battery_level_alarm.monitoring.graphics;
	exports com.battery_level_alarm.monitoring.skeleton_constraints;
	exports com.battery_level_alarm.monitoring.system_automation;
	exports com.battery_level_alarm.monitoring.system_core;
	exports com.battery_level_alarm.monitoring.visual_effects;
	exports com.battery_level_alarm.monitoring.visual_effects.appearance;
	exports com.battery_level_alarm.monitoring.visual_effects.gradient;
	exports com.battery_level_alarm.monitoring.tray_manager.tray_executors.actions;
	exports com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor;
	exports com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
	exports com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
	exports com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
	exports com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab;
	exports com.battery_level_alarm.monitoring.tray_manager.modern_component;
	exports com.battery_level_alarm.monitoring.user_interface.ui_config;
	exports com.battery_level_alarm.monitoring.user_interface.ui_constraints;
	exports com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper;
	exports com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper;
	exports com.battery_level_alarm.monitoring.user_interface.ui_setup;
	exports com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
	exports com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui to javafx.graphics;
	
	opens com.battery_level_alarm.monitoring.battery_report to javafx.graphics;
	opens com.battery_level_alarm.monitoring.graphics to javafx.graphics;
	opens com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications to javafx.swing;
	opens com.battery_level_alarm.monitoring.tray_manager.tray_executors.actions to javafx.swing;
}
