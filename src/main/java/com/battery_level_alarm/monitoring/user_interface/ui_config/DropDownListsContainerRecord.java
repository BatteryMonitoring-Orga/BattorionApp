package com.battery_level_alarm.monitoring.user_interface.ui_config;
import org.jdesktop.swingx.border.DropShadowBorder;
import javax.swing.*;
import java.awt.*;

public record DropDownListsContainerRecord(
        String title,
        Font titleListFont,
        DropShadowBorder containerListShadow,
        JPanel[] PartialPanelsArray,
        SingleDropDownListRecord[] SingleDropDownListRecordsArray
){}