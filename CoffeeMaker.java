import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*
 * Inherited class CoffeeMaker, to show inheritance.
 * 
 * Sub-Classes such as :
 *      -Control Panel
 *      -Heater
 *      -Water Tank 
 *  show Composition, as they cannot exist without The Actual appliance itself.
 * 
 * Whereas 
 *      -Filter
 *      Shows Aggregation as they can be used without the applinace.
 */
public class CoffeeMaker extends Appliance {
    
    //Attibutes of Coffe Maker
    private double temperature;
    private double volume;
    private double waterLeft;
    private boolean isFiltered;
    private static String mode;

    //Lock to Syncronize Threads
    private static Lock lock = new ReentrantLock();
    private static Condition temperatureReached = lock.newCondition();

    //Instances/Attributes of other sub classes
    private Heater heater;
    private WaterTank waterTank;
    private Filter filter;
    private ControlPanel controlPanel;

    public static Scanner input = new Scanner(System.in);


    public CoffeeMaker(double inputPower, double outputPower, String parentCompany, String mode, double tankCapacity, double maxTemperature) {
        super(inputPower, outputPower, parentCompany);
        this.mode = mode;
        this.temperature = 0;
        this.volume = 0;
        this.waterLeft = 0;
        this.isFiltered = false;
        this.heater = new Heater(maxTemperature);
        this.waterTank = new WaterTank(tankCapacity);
        this.filter = new Filter();
        this.controlPanel = new ControlPanel();
    }

    //
    public static String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    //To implement brewing thread,
    public void startBrewing() {
        Thread brewingThread = new Thread(() -> {
            setMode("Brewing");
            //This lock the thread, ensuring other threads (like Temperature thread is finished first 
            lock.lock();
            try {
                while (temperature < 85) {
                    System.out.println("Waiting for temperature to reach 85°C...");
                    //This calls for the Temperature monitoring thread
                    temperatureReached.await();
                }
                System.out.println("Brewing started...");
                logOperation("Brewing started");
                Thread.sleep(5000);
                System.out.println("Brewing completed. \n Type 'stop' to plug out the Coffee Maker.");
                logOperation("Brewing completed");
            } catch (InterruptedException e) {
                System.out.println("Brewing interrupted.");
                logOperation("Brewing interrupted");
            } finally {
                //Finally block unlocks the thread after it's done running
                lock.unlock();
            }
        });
        brewingThread.start();
    }

    public void monitorTemperature() {
        Thread monitoringThread = new Thread(() -> {
            // temperature = 50;
            setMode("Heating");

            boolean monitoring = true;
            while (monitoring) {
                //The lock is transfered from brewing to temperature.
                lock.lock();
                try {
                    System.out.println("Current temperature: " + temperature);
                    logOperation("Temperature monitored: " + temperature);
                    Thread.sleep(1500); 
                    temperature = Heater.heatUp(1);

                    // temperature++;
                    if (temperature >= 85) {
                        //This signals the brewing thread condition has been met.
                        temperatureReached.signalAll();
                        monitoring = false;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Monitoring interrupted.");
                    logOperation("Monitoring interrupted");
                    break;
                } finally {
                    //This unlocks the thread to avoid deadlocks between competing threads.
                    lock.unlock();
                }
            }
        });
        monitoringThread.start();
    }

    private void logOperation(String message) {
        try (FileWriter writer = new FileWriter("operations.log", true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            System.out.println("Error logging operation: " + e.getMessage());
        }
    }

    public void start() {
        controlPanel.turnOn();
        setMode("Turned On");

        if (!filter.isFiltered()) {
            System.out.println("Filter not applied. Applying filter...");
            filter.applyFilter();
        }
        if (waterTank.getWaterLeft() == 0) {
            System.out.println("Water tank is empty. Refilling...");
            waterTank.refill();
        }
        startBrewing();
        monitorTemperature();
    }

    public void stop() {
        controlPanel.turnOff();
        setMode("Turned Off");

        System.out.println("CoffeeMaker stopped.");
        logOperation("CoffeeMaker stopped");
    }

    public static void main(String[] args) {
        CoffeeMaker coffeeMaker = new CoffeeMaker(1000, 800, "CoffeeCo", null, 2.0, 100.0);
        // Scanner input = new Scanner(System.in);
        String command;

        System.out.println("Welcome to CoffeeMaker! Type 'start' to begin or 'stop' to end.");
        while (true) {
            command = input.nextLine();
            // System.out.println("Enter one of the following modes: \n1.Black Coffee.\n2.Cappucino.\n3.Latte.");
            // String enteredMode = input.nextLine();
            // coffeeMaker.setMode(enteredMode);
            if (command.equalsIgnoreCase("start")) {
                System.out.println("Enter one of the following modes: \n1.Black Coffee.\n2.Cappucino.\n3.Latte.");
                String enteredMode = input.nextLine();
                coffeeMaker.setMode(enteredMode);
                coffeeMaker.operate(getInputPower(), getOutputPower(), getParentCompany(), getMode() , command);
                // coffeeMaker.start();
            } else if (command.equalsIgnoreCase("stop")) {
                coffeeMaker.stop();
                break;
            } else {
                System.out.println("Invalid command. Please type 'start' or 'stop'.");
            }
        }
        input.close();
    }

    // @Override
    public void operate(double inputPower, double outputPower, String parentCompany, String mode, String command) {
        super.operate(inputPower, outputPower, parentCompany);
        // setMode(enteredMode);
        System.out.println("Operating CoffeeMaker in mode: " + getMode());
        start();
    }
}
