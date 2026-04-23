public class WaterTank {

    private double capacity;
    private double waterLeft;

    public WaterTank(double capacity) {
        this.capacity = capacity;
        this.waterLeft = capacity;
    }

    public double dispenseWater(double amount) {
        if (amount > waterLeft) {
            double dispensed = waterLeft;
            waterLeft = 0;
            return dispensed;
        } else {
            waterLeft -= amount;
            return amount;
        }
    }

    public double getWaterLeft() {
        return waterLeft;
    }

    public void refill() {
        this.waterLeft = capacity;
    }
}
