package BusinessLogic;

import Model.*;
import GUI.*;
import java.util.*;
import java.io.*;

public class SimulationManager implements Runnable{
    public int timeLimit = 40;
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;

    public int maxTasksPerServer = 100;

    public int numberOfServers = 3;
    public int numberOfClients = 100;


    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    private PrintWriter logWriter;

    //pt statistici
    private double totalWaitingTime = 0;
    private double totalServiceTime = 0;
    private int peakHour;
    private int maxClientsAtOnce = -1;

    //variabila pt implementarea singleton
    private static SimulationManager instance = null;

    private SimulationManager()
    {
        this.frame = new SimulationFrame();
        setupLayout();
    }

    private void setupLayout()
    {
        try {
            logWriter = new PrintWriter(new FileWriter("log.txt",false));
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }

        this.frame.getBtnStart().addActionListener(e ->
        {
            try {
                //luam date gui
                this.numberOfClients = frame.getNumClients();
                this.numberOfServers = frame.getNumQueues();
                this.timeLimit = frame.getSimTime();
                this.minProcessingTime = frame.getMinService();
                this.maxProcessingTime = frame.getMaxService();
                this.selectionPolicy = frame.getSelectedPolicy();


                //cream scheduler
                this.scheduler = new Scheduler(numberOfServers, maxTasksPerServer);
                this.scheduler.changeStrategy(selectionPolicy);

                //generam clienti
                generateNRandomTasks();

                Thread t = new Thread(this);
                t.start();

                this.frame.displayMessage("Simularea a pornit");
            } catch (NumberFormatException ex) {
                this.frame.displayMessage("Eroare");
            }
        });
    }

    public static SimulationManager getInstance()
    {
        if(instance == null)
            instance = new SimulationManager();
        return instance;
    }

    public void generateNRandomTasks()
    {
        List<Task> tasks = new ArrayList<>();
        this.totalServiceTime = 0;

        Random random = new Random();
        for(int i = 0; i < numberOfClients; i++)
        {
            int arrTime = random.nextInt(frame.getMaxArrival() - frame.getMinArrival() + 1) + frame.getMinArrival();
            int procTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            Task crtTask = new Task(i + 1, arrTime, procTime);
            tasks.add(crtTask);
            this.totalServiceTime += procTime;
        }

        Collections.sort(tasks);
        this.generatedTasks = tasks;
    }

    public void runs()
    {
        int crtTime = 0;
        if(scheduler == null)
            return;

        while (crtTime < timeLimit)
        {
            synchronized(generatedTasks)
            {
                Iterator<Task> taskIterator = generatedTasks.iterator();
                while (taskIterator.hasNext())
                {
                    Task task = taskIterator.next();
                    if (task.getArrivalTime() == crtTime)
                    {
                        int minWait = Integer.MAX_VALUE;
                        for (Server s : scheduler.getServers())
                        {
                            if (s.getWaitingPeriod().get() < minWait)
                            {
                                minWait = s.getWaitingPeriod().get();
                            }
                        }
                        totalWaitingTime += minWait;

                        scheduler.dispatchTask(task);
                        taskIterator.remove();
                    }
                }
            }

            int currentClientsInQueues = 0;
            for (Server s : scheduler.getServers())
            {
                currentClientsInQueues += s.getTasks().size();
            }
            if (currentClientsInQueues > maxClientsAtOnce)
            {
                maxClientsAtOnce = currentClientsInQueues;
                peakHour = crtTime;
            }

            String currentStatus = getStatusString(crtTime);
            frame.updateDisplay(crtTime, generatedTasks, scheduler.getServers());

            if (logWriter != null)
            {
                logWriter.print(currentStatus);
                logWriter.flush();
            }

            crtTime++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (logWriter != null) {
            double avgWaiting = (numberOfClients > 0) ? (totalWaitingTime / numberOfClients) : 0;
            double avgService = (numberOfClients > 0) ? (totalServiceTime / numberOfClients) : 0;

            logWriter.println("Average Waiting Time: " + String.format("%.2f", avgWaiting));
            logWriter.println("Average Service Time: " + String.format("%.2f", avgService));
            logWriter.println("Peak Hour: " + peakHour);

            logWriter.flush(); //golim buffer temporar si scriem in log
            logWriter.close();
            System.out.println("Simulare finalizată. Statisticile au fost scrise.");
        }
    }

    private String getStatusString(int time)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Time ").append(time).append("\n");
        sb.append("Waiting clients: ");
        for (int i = 0; i < generatedTasks.size(); i++)
        {
            sb.append(generatedTasks.get(i).toString());
            if (i < generatedTasks.size() - 1) sb.append(", ");
        }
        sb.append("\n");

        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++)
        {
            sb.append("Queue ").append(i + 1).append(": ");
            Server s = servers.get(i);
            if (s.getTasks().isEmpty())
            {
                sb.append("closed");
            } else {
                for (Task t : s.getTasks())
                {
                    sb.append(t.toString()).append("; ");
                }
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}


â