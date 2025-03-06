package com.battery_level_alarm.monitoring.configuration_records;
import java.awt.Dimension;
import javax.swing.border.Border;

public record ScrollConfiguration(
        boolean isFocusable,
        boolean isVisible,
        boolean isEnabled,
        boolean isOpaque,
        Border scrollBorder,
        Dimension scrollSize
) {
    public ScrollConfiguration {
        if (scrollSize == null) {
            throw new IllegalArgumentException("Scroll size can't be zero or empty 'null'!");
        }
    }

    public boolean isInteractive() {
        return isFocusable && isVisible && isEnabled;
    }

    public String getSummary() {
        return "Scroll Config: " + (isVisible ? "Visible" : "Hidden") +
                ", " + (isEnabled ? "Enabled" : "Disabled") +
                ", Size: " + scrollSize;
    }

    public ScrollConfiguration withEnabled(boolean enabled) {
        return new ScrollConfiguration(isFocusable, isVisible, enabled, isOpaque, scrollBorder, scrollSize);
    }

    public static ScrollConfiguration defaultConfig() {
        return new ScrollConfiguration(true, true, true, false, null, new Dimension(100, 200));
    }

    @Override
    public String toString() {
        return String.format("ScrollConfiguration[Focusable=%b, Visible=%b, Enabled=%b, Opaque=%b, Size=%s]",
                isFocusable, isVisible, isEnabled, isOpaque, scrollSize);
    }
}