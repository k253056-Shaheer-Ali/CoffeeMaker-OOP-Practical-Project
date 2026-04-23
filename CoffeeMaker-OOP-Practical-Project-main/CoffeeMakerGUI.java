import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class CoffeeMakerGUI extends JFrame {

    private final CoffeeMaker coffeeMaker;

    private final JLabel powerLabel;
    private final JLabel modeLabel;
    private final JLabel stateLabel;
    private final JLabel temperatureLabel;
    private final JLabel waterLabel;
    private final JLabel cupLabel;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private final JTextArea logArea;
    private final JComboBox<String> modeComboBox;

    private int shownLogCount;

    public CoffeeMakerGUI() {
        this.coffeeMaker = new CoffeeMaker(1000, 800, "CoffeeCo", 2000, 90);
        this.powerLabel = createValueLabel();
        this.modeLabel = createValueLabel();
        this.stateLabel = createValueLabel();
        this.temperatureLabel = createValueLabel();
        this.waterLabel = createValueLabel();
        this.cupLabel = createValueLabel();
        this.statusLabel = createValueLabel();
        this.progressBar = new JProgressBar(0, 100);
        this.logArea = new JTextArea();
        this.modeComboBox = new JComboBox<>(new String[] { "Black Coffee", "Cappuccino", "Latte" });
        this.shownLogCount = 0;

        buildUI();
        attachListeners();
        startRefreshTimer();
        refreshUI();
    }

    private void buildUI() {
        setTitle("Smart CoffeeMaker - OOP Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(28, 28, 36));
        setLayout(new BorderLayout(14, 14));

        JLabel title = new JLabel("Smart CoffeeMaker Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(245, 245, 245));
        title.setBorder(BorderFactory.createEmptyBorder(12, 8, 0, 8));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 6, 12));

        JPanel statusPanel = new JPanel(new GridLayout(9, 1, 8, 8));
        statusPanel.setBackground(new Color(44, 44, 56));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(83, 101, 255), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        statusPanel.add(createStatusRow("Power", powerLabel));
        statusPanel.add(createStatusRow("Mode", modeLabel));
        statusPanel.add(createStatusRow("State", stateLabel));
        statusPanel.add(createStatusRow("Temperature", temperatureLabel));
        statusPanel.add(createStatusRow("Water Left", waterLabel));
        statusPanel.add(createStatusRow("Cup Volume", cupLabel));
        statusPanel.add(createStatusRow("Status", statusLabel));

        JLabel progressTitle = new JLabel("Progress");
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        progressTitle.setForeground(new Color(245, 245, 245));
        statusPanel.add(progressTitle);

        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(99, 219, 148));
        progressBar.setBackground(new Color(33, 33, 42));
        statusPanel.add(progressBar);

        JPanel logPanel = new JPanel(new BorderLayout(8, 8));
        logPanel.setBackground(new Color(44, 44, 56));
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(83, 101, 255), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel logTitle = new JLabel("Live Operation Log");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logTitle.setForeground(new Color(245, 245, 245));
        logPanel.add(logTitle, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setBackground(new Color(31, 31, 40));
        logArea.setForeground(new Color(211, 223, 255));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(420, 360));
        logPanel.add(logScroll, BorderLayout.CENTER);

        centerPanel.add(statusPanel);
        centerPanel.add(logPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));

        JButton powerOnButton = createButton("Power ON", new Color(44, 181, 109));
        powerOnButton.addActionListener(e -> {
            coffeeMaker.powerOn();
            refreshUI();
        });

        JButton powerOffButton = createButton("Power OFF", new Color(220, 92, 92));
        powerOffButton.addActionListener(e -> {
            coffeeMaker.powerOff();
            refreshUI();
        });

        JButton startButton = createButton("Start Brew", new Color(83, 101, 255));
        startButton.addActionListener(e -> {
            coffeeMaker.setMode((String) modeComboBox.getSelectedItem());
            coffeeMaker.startBrewingCycle();
            refreshUI();
        });

        JButton stopButton = createButton("Stop", new Color(255, 146, 43));
        stopButton.addActionListener(e -> {
            coffeeMaker.stopBrewing();
            refreshUI();
        });

        JButton refillButton = createButton("Refill Tank", new Color(38, 174, 255));
        refillButton.addActionListener(e -> {
            coffeeMaker.refillWaterTank();
            refreshUI();
        });

        JButton filterButton = createButton("Toggle Filter", new Color(168, 118, 255));
        filterButton.addActionListener(e -> {
            if (coffeeMaker.isFilterApplied()) {
                coffeeMaker.removeFilter();
            } else {
                coffeeMaker.applyFilter();
            }
            refreshUI();
        });

        controls.add(modeComboBox);
        controls.add(startButton);
        controls.add(stopButton);
        controls.add(refillButton);
        controls.add(filterButton);
        controls.add(powerOnButton);
        controls.add(powerOffButton);

        add(controls, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        modeComboBox.addActionListener(e -> {
            coffeeMaker.setMode((String) modeComboBox.getSelectedItem());
            refreshUI();
        });
    }

    private void startRefreshTimer() {
        Timer timer = new Timer(350, e -> refreshUI());
        timer.start();
    }

    private void refreshUI() {
        powerLabel.setText(coffeeMaker.isPowerOn() ? "ON" : "OFF");
        modeLabel.setText(coffeeMaker.getMode());
        stateLabel.setText(formatState(coffeeMaker.getState()));
        temperatureLabel.setText(String.format("%.1f C", coffeeMaker.getTemperature()));
        waterLabel.setText(String.format("%.0f ml", coffeeMaker.getWaterLeft()));
        cupLabel.setText(String.format("%.0f ml", coffeeMaker.getCupVolumeMl()));
        statusLabel.setText(coffeeMaker.getStatusMessage());
        progressBar.setValue(coffeeMaker.getProgressPercent());
        progressBar.setString(coffeeMaker.getProgressPercent() + "%");
        updateLogPanel();
    }

    private void updateLogPanel() {
        List<String> logs = coffeeMaker.getEventLogSnapshot();
        if (logs.size() < shownLogCount) {
            logArea.setText("");
            shownLogCount = 0;
        }
        for (int i = shownLogCount; i < logs.size(); i++) {
            logArea.append(logs.get(i) + System.lineSeparator());
        }
        shownLogCount = logs.size();
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private JPanel createStatusRow(String key, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(6, 6));
        row.setOpaque(false);
        JLabel keyLabel = new JLabel(key + ":");
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        keyLabel.setForeground(new Color(210, 214, 255));
        row.add(keyLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        return row;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(235, 235, 245));
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        return button;
    }

    private String formatState(String state) {
        return state.charAt(0) + state.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Safe fallback to default look and feel.
        }
        SwingUtilities.invokeLater(() -> new CoffeeMakerGUI().setVisible(true));
    }
}
