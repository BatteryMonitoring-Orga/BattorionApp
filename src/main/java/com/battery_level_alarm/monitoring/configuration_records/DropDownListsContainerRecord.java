package com.battery_level_alarm.monitoring.configuration_records;
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