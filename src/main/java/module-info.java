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

    opens com.battery_level_alarm.monitoring.battery_report to javafx.graphics;
}