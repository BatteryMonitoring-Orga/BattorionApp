package com.battery_level_alarm.monitoring.system_automation;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ComponentHierarchy;
import javax.swing.*;

public class Updater {
    public static void update(JFrame mainFrame, JPanel motherPanel, JPanel... panels){
        if(panels != null){
            for(JPanel panel : panels){
                panel.repaint();
                panel.revalidate();
            }
        }

        if(motherPanel != null){
            motherPanel.repaint();
            motherPanel.revalidate();
        }

        if(mainFrame != null){
            mainFrame.repaint();
            mainFrame.revalidate();
        }
    }

    public static void update(ComponentHierarchy hierarchy){
        if(hierarchy.children() != null){
            hierarchy.renovated().run();
            for(JComponent entry : hierarchy.children()){
                if (entry != null) {
                    entry.repaint();
                    entry.revalidate();
                }
            }
        } if(hierarchy.container() != null){
            hierarchy.container().repaint();
            hierarchy.container().revalidate();
        } if(hierarchy.frame() != null){
            hierarchy.frame().repaint();
            hierarchy.frame().revalidate();
        }
    }
}