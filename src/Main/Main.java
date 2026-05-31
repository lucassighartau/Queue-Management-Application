package Main;

import BusinessLogic.SimulationManager;

public class Main {
    public static void main(String[] args)
    {
        SimulationManager gen = SimulationManager.getInstance();
        Thread thread = new Thread(gen);
        thread.start();
    }
}