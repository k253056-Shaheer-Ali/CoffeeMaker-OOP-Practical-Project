# CoffeeMaker-OOP-Practical-Project

## Objective
This project models a real-world appliance, a **Coffee Maker**, using Object-Oriented Programming (OOP) principles and multithreading in Java. The application is console-based and demonstrates various OOP concepts, thread synchronization, and logging.

---

## Chosen Appliance
**Appliance:** Coffee Maker  
**Category:** Electric Appliance  

The Coffee Maker is designed to brew coffee while monitoring its internal temperature. It includes components such as a heater, water tank, filter, and control panel, each modeled as separate classes.

---

## Features
1. **Basic Operations:**
   - Turn the coffee maker on and off.
   - Start and stop the brewing process.
   - Monitor the internal temperature.

2. **Unique Feature:**
   - Automatic filter application if the filter is not already applied.

3. **Logging:**
   - All operations (e.g., start, stop, temperature monitoring) are logged to a file (`operations.log`) for traceability.

---

## Implementation

### OOP Concepts
1. **Composition:**
   - The `CoffeeMaker` class has a "has-a" relationship with its components:
     - `Heater`
     - `WaterTank`
     - `ControlPanel`

2. **Aggregation:**
   - The `Filter` class can exist independently of the `CoffeeMaker` class, demonstrating aggregation.

3. **Inheritance:**
   - The `CoffeeMaker` class extends the `Appliance` base class, inheriting its attributes and methods.

4. **Polymorphism:**
   - The `operate` method is defined in the `Appliance` class and overridden in the `CoffeeMaker` class.

5. **Encapsulation:**
   - Private attributes are used in all classes (e.g., `inputPower` in `Appliance`, `currentTemperature` in `Heater`) with public getters and setters.

### Threading
1. **Concurrent Tasks:**
   - Two threads are implemented:
     - **Brewing Thread:** Handles the brewing process.
     - **Monitoring Thread:** Continuously monitors the temperature.

2. **Thread Synchronization:**
   - A `ReentrantLock` and `Condition` are used to synchronize the threads, ensuring that brewing starts only after the temperature reaches 85°C.

### Logging
- Operations are logged to a file (`operations.log`) using the `logOperation` method in the `CoffeeMaker` class. This includes timestamps for better traceability.

---

## Challenges and Solutions
1. **Challenge:** Ensuring that brewing starts only after the temperature reaches the required threshold.
   - **Solution:** Used thread synchronization with `ReentrantLock` and `Condition` to coordinate between the brewing and monitoring threads.

2. **Challenge:** Managing shared resources like temperature across multiple threads.
   - **Solution:** Properly synchronized access to shared resources to avoid race conditions.

3. **Challenge:** Designing a modular and extensible system.
   - **Solution:** Followed OOP principles like composition, inheritance, and encapsulation to create a clean and maintainable design.

---

## How to Run
1. Compile the Java files:
   ```
   javac *.java
   ```
2. Run the `CoffeeMaker` class:
   ```
   java CoffeeMaker
   ```
3. Follow the console prompts to start or stop the coffee maker.

---

## Deliverables
1. **Source Code:**
   - Fully commented Java code implementing the Coffee Maker.

2. **Documentation:**
   - This README file serves as the project documentation, explaining:
     - Chosen appliance and features.
     - Implementation of OOP, threading, and logging.
     - Challenges and solutions.

---

## Submission
- Submit the source code and this documentation by **9th of May**.