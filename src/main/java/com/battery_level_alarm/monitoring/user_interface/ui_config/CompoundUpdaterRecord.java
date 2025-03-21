package com.battery_level_alarm.monitoring.user_interface.ui_config;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record CompoundUpdaterRecord(
        Consumer<Boolean> setFlagConsumer,
        Supplier<Boolean> Supplier,
        Runnable Action,
        ProgressBarValueUpdater progressBarValueUpdater,
        EffectDirection effectDirection,
        ComponentHierarchy hierarchy,
        Runnable panelSizeUpdater,
        boolean isFromDropDownList,
        boolean isAbleToUseConsumers
){}