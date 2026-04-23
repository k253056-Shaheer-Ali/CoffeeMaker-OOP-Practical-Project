public class ControlPanel {
    private boolean powerOn;

    public ControlPanel() {
        this.powerOn = false;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public void turnOn() {
        this.powerOn = true;
        System.out.println("Power is ON.");
    }

    public void turnOff() {
        this.powerOn = false;
        System.out.println("Power is OFF.");
    }
}
