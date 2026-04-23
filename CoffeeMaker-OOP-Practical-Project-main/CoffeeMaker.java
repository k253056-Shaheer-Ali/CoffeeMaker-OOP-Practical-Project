import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoffeeMaker extends Appliance {

    public enum Mode {
        BLACK_COFFEE,
        CAPPUCCINO,
        LATTE;

        @Override
        public String toString() {
            switch (this) {
                case BLACK_COFFEE:
                    return "Black Coffee";
                case CAPPUCCINO:
                    return "Cappuccino";
                default:
                    return "Latte";
            }
        }
    }

    public enum State {
        OFF,
        IDLE,
        HEATING,
        BREWING,
        COMPLETED,
        STOPPED,
        ERROR
    }

    private static class Recipe {
        private final double targetTemperature;
        private final double waterRequired;
        private final int brewTimeMs;

        Recipe(double targetTemperature, double waterRequired, int brewTimeMs) {
            this.targetTemperature = targetTemperature;
            this.waterRequired = waterRequired;
            this.brewTimeMs = brewTimeMs;
        }
    }

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Heater heater;
    private final WaterTank waterTank;
    private final Filter filter;
    private final ControlPanel controlPanel;
    private final Object stateLock;
    private final List<String> eventLog;

    private Thread brewThread;
    private volatile boolean stopRequested;
    private volatile int progressPercent;
    private Mode mode;
    private State state;
    private String statusMessage;
    private double cupVolumeMl;

    public CoffeeMaker(double inputPower, double outputPower, String parentCompany, double tankCapacity, double maxTemperature) {
        super(inputPower, outputPower, parentCompany);
        this.heater = new Heater(maxTemperature);
        this.waterTank = new WaterTank(tankCapacity);
        this.filter = new Filter();
        this.controlPanel = new ControlPanel();
        this.stateLock = new Object();
        this.eventLog = new ArrayList<>();
        this.mode = Mode.BLACK_COFFEE;
        this.state = State.OFF;
        this.statusMessage = "Machine is off";
        this.cupVolumeMl = 0;
        this.progressPercent = 0;
        this.stopRequested = false;
    }

    public void powerOn() {
        synchronized (stateLock) {
            if (!controlPanel.isPowerOn()) {
                controlPanel.turnOn();
                state = State.IDLE;
                statusMessage = "Ready to brew";
                logOperation("Power ON");
            }
        }
    }

    public void powerOff() {
        stopBrewing();
        synchronized (stateLock) {
            controlPanel.turnOff();
            state = State.OFF;
            statusMessage = "Machine is off";
            progressPercent = 0;
            logOperation("Power OFF");
        }
    }

    public boolean setMode(String modeName) {
        Mode parsedMode = parseMode(modeName);
        if (parsedMode == null) {
            return false;
        }
        synchronized (stateLock) {
            if (state == State.HEATING || state == State.BREWING) {
                return false;
            }
            mode = parsedMode;
            statusMessage = "Mode set to " + mode;
            logOperation("Mode changed to " + mode);
            return true;
        }
    }

    public boolean startBrewingCycle() {
        synchronized (stateLock) {
            if (!controlPanel.isPowerOn()) {
                statusMessage = "Turn on power first";
                logOperation(statusMessage);
                return false;
            }
            if (state == State.HEATING || state == State.BREWING) {
                statusMessage = "Brewing already in progress";
                return false;
            }
            stopRequested = false;
            progressPercent = 0;
            cupVolumeMl = 0;
            brewThread = new Thread(this::runBrewingCycle, "coffee-brewer");
            brewThread.start();
            return true;
        }
    }

    public void stopBrewing() {
        Thread activeThread;
        synchronized (stateLock) {
            stopRequested = true;
            activeThread = brewThread;
        }
        if (activeThread != null && activeThread.isAlive()) {
            activeThread.interrupt();
            try {
                activeThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        synchronized (stateLock) {
            if (state == State.HEATING || state == State.BREWING) {
                state = State.STOPPED;
                statusMessage = "Brewing stopped by user";
                logOperation(statusMessage);
            }
            progressPercent = 0;
        }
    }

    public void refillWaterTank() {
        synchronized (stateLock) {
            waterTank.refill();
            statusMessage = "Water tank refilled";
            logOperation(statusMessage);
        }
    }

    public void applyFilter() {
        synchronized (stateLock) {
            filter.applyFilter();
            statusMessage = "Filter applied";
            logOperation(statusMessage);
        }
    }

    public void removeFilter() {
        synchronized (stateLock) {
            filter.removeFilter();
            statusMessage = "Filter removed";
            logOperation(statusMessage);
        }
    }

    public String getMode() {
        synchronized (stateLock) {
            return mode.toString();
        }
    }

    public String getState() {
        synchronized (stateLock) {
            return state.name();
        }
    }

    public String getStatusMessage() {
        synchronized (stateLock) {
            return statusMessage;
        }
    }

    public double getTemperature() {
        return heater.getCurrentTemperature();
    }

    public double getWaterLeft() {
        synchronized (stateLock) {
            return waterTank.getWaterLeft();
        }
    }

    public boolean isPowerOn() {
        return controlPanel.isPowerOn();
    }

    public boolean isFilterApplied() {
        synchronized (stateLock) {
            return filter.isFiltered();
        }
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public double getCupVolumeMl() {
        synchronized (stateLock) {
            return cupVolumeMl;
        }
    }

    public List<String> getEventLogSnapshot() {
        synchronized (stateLock) {
            return new ArrayList<>(eventLog);
        }
    }

    private void runBrewingCycle() {
        Recipe recipe = getRecipe(mode);
        updateState(State.HEATING, "Heating water to " + recipe.targetTemperature + " C");
        while (!stopRequested && heater.getCurrentTemperature() < recipe.targetTemperature) {
            heater.heatUp(1.0);
            updateProgress((int) ((heater.getCurrentTemperature() / recipe.targetTemperature) * 50.0));
            sleepStep(150);
        }

        if (stopRequested) {
            updateState(State.STOPPED, "Brewing stopped while heating");
            return;
        }

        double dispensed = waterTank.dispenseWater(recipe.waterRequired);
        if (dispensed < recipe.waterRequired) {
            updateState(State.ERROR, "Not enough water. Please refill tank.");
            updateProgress(0);
            return;
        }

        if (!filter.isFiltered()) {
            filter.applyFilter();
            logOperation("Filter auto-applied");
        }

        updateState(State.BREWING, "Brewing " + mode + "...");
        int elapsed = 0;
        while (!stopRequested && elapsed < recipe.brewTimeMs) {
            elapsed += 200;
            int brewProgress = 50 + (int) (((double) elapsed / recipe.brewTimeMs) * 50.0);
            updateProgress(Math.min(100, brewProgress));
            sleepStep(200);
        }

        if (stopRequested) {
            updateState(State.STOPPED, "Brewing stopped by user");
            updateProgress(0);
            return;
        }

        synchronized (stateLock) {
            cupVolumeMl = dispensed;
        }
        updateProgress(100);
        updateState(State.COMPLETED, mode + " is ready (" + dispensed + " ml)");
        heater.coolDown(10.0);
    }

    private void sleepStep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            stopRequested = true;
            Thread.currentThread().interrupt();
        }
    }

    private void updateState(State newState, String message) {
        synchronized (stateLock) {
            state = newState;
            statusMessage = message;
            logOperation(message);
        }
    }

    private void updateProgress(int progress) {
        progressPercent = Math.max(0, Math.min(100, progress));
    }

    private Recipe getRecipe(Mode selectedMode) {
        switch (selectedMode) {
            case CAPPUCCINO:
                return new Recipe(82.0, 220.0, 3800);
            case LATTE:
                return new Recipe(85.0, 260.0, 4400);
            default:
                return new Recipe(80.0, 180.0, 3000);
        }
    }

    private Mode parseMode(String modeName) {
        if (modeName == null) {
            return null;
        }
        String clean = modeName.trim().toUpperCase(Locale.ROOT).replace(" ", "_");
        if ("BLACK".equals(clean) || "BLACK_COFFEE".equals(clean)) {
            return Mode.BLACK_COFFEE;
        }
        if ("CAPPUCCINO".equals(clean)) {
            return Mode.CAPPUCCINO;
        }
        if ("LATTE".equals(clean)) {
            return Mode.LATTE;
        }
        return null;
    }

    private void logOperation(String message) {
        String logLine = "[" + LocalTime.now().format(CLOCK) + "] " + message;
        eventLog.add(logLine);
        if (eventLog.size() > 200) {
            eventLog.remove(0);
        }
        try (FileWriter writer = new FileWriter("operations.log", true)) {
            writer.write(logLine + System.lineSeparator());
        } catch (IOException e) {
            // Logging failure should not stop the machine.
        }
    }
}
