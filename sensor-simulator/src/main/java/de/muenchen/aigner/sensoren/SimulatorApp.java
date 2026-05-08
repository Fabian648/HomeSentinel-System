package de.muenchen.aigner.sensoren;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorApp {
    static void main(String[] args) {
        Simulator.counter = new AtomicInteger();
        ExecutorService ex = Executors.newFixedThreadPool(10);
        for(int i = 0; i < 10; i++) {
            ex.execute(new Simulator());
        }

    }
}
