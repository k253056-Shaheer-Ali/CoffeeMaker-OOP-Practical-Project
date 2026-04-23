public class Heater {
    private double currentTemperature;
    private final double maxTemperature;

    public Heater(double maxTemperature) {
        this.currentTemperature = 25.0;
        this.maxTemperature = maxTemperature;
    }

    public synchronized double getCurrentTemperature() {
        return currentTemperature;
    }

    public synchronized double heatUp(double increment) {
        if (currentTemperature + increment <= maxTemperature) {
            currentTemperature += increment;
        } else {
            currentTemperature = maxTemperature;
        }

        return currentTemperature;
    }

    public synchronized void coolDown(double decrement) {
        if (currentTemperature - decrement >= 0) {
            currentTemperature -= decrement;
        } else {
            currentTemperature = 0;
        }
    }

    public synchronized void resetTemperature(double value) {
        currentTemperature = Math.max(0, Math.min(value, maxTemperature));
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }
}
