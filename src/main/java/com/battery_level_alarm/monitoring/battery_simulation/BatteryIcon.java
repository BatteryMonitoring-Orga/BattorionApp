package com.battery_level_alarm.monitoring.battery_simulation;
import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.getSpinnerValue;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import com.battery_level_alarm.monitoring.effects.CallResources;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BatteryIcon extends JPanel {
    private static BatteryIcon battery;
    private static JFrame frame;
    private static Timer simulator;

    private static int chargeLevel;
    private static int spinnerValue = 65;

    public static BatteryIcon createBatteryIconObject(int batteryLevel) {
        if (battery == null) {
            battery = new BatteryIcon(batteryLevel);
        } else {
            updateChargeLevel(batteryLevel);
        }
        return battery;
    }

    private BatteryIcon(int batteryLevel) {
        chargeLevel = batteryLevel;
        setLayout(new BorderLayout());
        add(createBatteryInfoLabel(), BorderLayout.SOUTH);
    }

    public static void BatterySimulationStart(){
        if(frame != null){
            frame.dispose();
        }

        BatteryIcon batteryIcon = createBatteryIconObject(batteryLevel);
        frame = createSimulatorFrame();
        addMouseListenerForFrame(frame);
        frame.add(createNorthSimulatorPanel(), BorderLayout.NORTH);
        frame.add(batteryIcon, BorderLayout.CENTER);
        StartSimulator();
    }

    private static JFrame createSimulatorFrame(){
        JFrame frame = new JFrame("Battery Simulator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = CallResources.getImage(IMAGES_FOLDER_PATH, "5550932");
        frame.setIconImage(icon.getImage());
        frame.setLayout(new BorderLayout());
        frame.setSize(520, 470);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    private static void addMouseListenerForFrame(JFrame frame){
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                StopSimulator();
            }
        });
    }

    private static void StartSimulator(){
        simulator = new Timer(
                1000,
                _ -> updateChargeLevel(batteryLevel)
        );
        simulator.start();
    }

    public static void StopSimulator(){
        simulator.stop();
    }

    private static void updateChargeLevel(int batteryLevel) {
        if(simulatorMode){
            chargeLevel = spinnerValue;
        } else {
            chargeLevel = batteryLevel;
        }
        battery.repaint();
    }

    private static JPanel createNorthSimulatorPanel(){
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(createCheckBox());
        panel.add(createSpinnerPanel());
        return panel;
    }

    private static JPanel createCheckBox(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox("Use simulated value instead of real battery level");
        checkBox.setFont(DEFAULT_FONT);
        checkBox.setSelected(simulatorMode);
        checkBox.addActionListener(e -> {
            simulatorMode = ((JCheckBox) e.getSource()).isSelected();
            ConfigurationFilesManager.saveGeneralConfigurations();
        });
        panel.add(checkBox);
        return panel;
    }

    private static JPanel createSpinnerPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jLabel = new JLabel("Enter Battery Level (0 - 100):");
        jLabel.setFont(DEFAULT_FONT);
        panel.add(jLabel);

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(spinnerValue, 0, 100, 1));
        spinner.setFont(DEFAULT_FONT);
        spinner.setPreferredSize(new Dimension(80, 30));
        spinner.addChangeListener(
                e -> spinnerValue = getSpinnerValue((JSpinner) e.getSource(), 0, chargeLevel)
        );
        panel.add(spinner);
        return panel;
    }

    private JLabel createBatteryInfoLabel() {
        String infoText = "<html><div style='font-family:Serif; text-align: left; font-size: 12px;'>"
                + "Monitoring battery charge levels is essential for ensuring long-term battery health. "
                + "Overcharging above 85% can cause overheating and reduce battery lifespan, "
                + "while deep discharge below 25% may lead to instability and performance degradation. "
                + "Keeping the charge between 25% and 85% helps maintain optimal efficiency and durability."
                + "</div></html>";

        JLabel infoLabel = new JLabel(infoText, SwingConstants.LEFT);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return infoLabel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = 85, height = 200;
        int x = 20, y = 20;
        int capWidth = 30, capHeight = 9;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(x, y, width, height);

        g2d.fillRect(x + (width / 2) - (capWidth / 2), y - capHeight, capWidth, capHeight);
        int fillHeight = (int) ((chargeLevel / 100.0) * (height - 2));
        int fillY = y + (height - fillHeight - 2);
        g2d.setColor(getBatteryColor(chargeLevel));
        g2d.fillRect(x + 2, fillY, width - 3, fillHeight);

        drawArrowWithText(g2d, x + width + 20, 25, "Overcharge (85% or Above)", Color.DARK_GRAY);
        drawArrowWithText(g2d, x + width + 20, 85, "High Charge (60% - 84%)", Color.GREEN);
        drawArrowWithText(g2d, x + width + 20, 135, "Medium Charge (26% - 59%)", Color.ORANGE);
        drawArrowWithText(g2d, x + width + 20, 195, "Low Charge (25% or Below)", Color.RED);
        drawBatteryPercentage(g2d, x + width / 2, y + height / 2);
    }

    private Color getBatteryColor(int charge) {
        if (charge >= 85) return Color.DARK_GRAY;
        else if (charge >= 60) return Color.GREEN;
        else if (charge > 25) return Color.ORANGE;
        else return Color.RED;
    }

    private void drawArrowWithText(Graphics2D g2d, int x, int y, String text, Color color) {
        g2d.setColor(color);
        int[] xPoints = {x, x + 15, x};
        int[] yPoints = {y, y + 10, y + 20};
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(text, x + 20, y + 15);
    }

    private void drawBatteryPercentage(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String percentageText = chargeLevel + "%";
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = centerX - (metrics.stringWidth(percentageText) / 2);
        int textY = centerY + (metrics.getHeight() / 3);
        g2d.drawString(percentageText, textX, textY);
    }
}