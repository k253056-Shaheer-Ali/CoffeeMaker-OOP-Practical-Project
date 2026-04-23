public class Heater {
    private static double currentTemperature;
    private static double maxTemperature;

    public Heater(double maxTemperature) {
        Heater.currentTemperature = 37.00;
        Heater.maxTemperature = 85.00;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public static double heatUp(double increment) {
        if (currentTemperature + increment <= maxTemperature) {
            currentTemperature += increment;
        } else {
            currentTemperature = maxTemperature;
        }

        return currentTemperature;
    }

    public void coolDown(double decrement) {
        if (currentTemperature - decrement >= 0) {
            currentTemperature -= decrement;
        } else {
            currentTemperature = 0;
        }
    }
}
