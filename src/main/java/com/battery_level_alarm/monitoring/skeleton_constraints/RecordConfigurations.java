package com.battery_level_alarm.monitoring.skeleton_constraints;
import com.battery_level_alarm.monitoring.configuration_records.GridBagConstraintsConfiguration;
import java.awt.*;

public class RecordConfigurations {
    public static final GridBagConstraintsConfiguration GRID_BAG_CONSTRAINTS_CONFIGURATION = new GridBagConstraintsConfiguration(
            0,
            0,
            1,
            1,
            1.0,
            1.0,
            GridBagConstraints.WEST,
            GridBagConstraints.BOTH,
            new Insets(10, 10, 10, 10),
            0,
            0
    );
}