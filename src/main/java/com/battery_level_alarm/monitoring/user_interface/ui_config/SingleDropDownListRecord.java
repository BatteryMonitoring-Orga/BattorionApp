package com.battery_level_alarm.monitoring.user_interface.ui_config;
import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public record SingleDropDownListRecord(
        String title,
        Dimension listSize,
        DropShadowBorder openPanelShadow,
        DropShadowBorder closedPanelShadow,
        boolean initiallyVisible,
        int index,
        JProgressBar progressBar,
        JPanel dropDownPanel,
        Consumer<Boolean> setStateConsumer
){
    /*!
      Supplier<JPanel> panelSupplier
     */
}