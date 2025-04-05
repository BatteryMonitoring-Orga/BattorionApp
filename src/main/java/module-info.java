module monitoring {
    requires com.formdev.flatlaf.intellijthemes;
    requires com.formdev.flatlaf;
    requires java.logging;
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.base;
    requires javafx.graphics;
    requires jlayer;
    requires org.jetbrains.annotations;
    requires org.json;
    requires swingx.core;
    requires system.tray.notifications;

    exports com.battery_level_alarm.monitoring.system_core;
    exports com.battery_level_alarm.monitoring.visual_effects;
    exports com.battery_level_alarm.monitoring.graphics;
    exports com.battery_level_alarm.monitoring.battery_report;

    opens com.battery_level_alarm.monitoring.battery_report to javafx.graphics;
    opens com.battery_level_alarm.monitoring.graphics to javafx.graphics;
}