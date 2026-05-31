package BusinessLogic;

import Model.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler { //alegem la ce coadă băgăm pe ăla
    private List<Server> servers;
    private int maxNoServers;
    public int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer)
    {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        for(int i = 0; i < maxNoServers; i++)
        {
            Server server = new Server(new LinkedBlockingDeque<>(maxTasksPerServer),new AtomicInteger(0));
            servers.add(server);

            Thread thread = new Thread(server);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy)
    {
        if(policy == SelectionPolicy.SHORTEST_QUEUE)
        {
            strategy = new ShortestQueueStrategy();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME)
        {
            strategy = new ShortestTimeStrategy();
        }
    }

    public void dispatchTask(Task t)
    {
        strategy.addTask(servers,t);
    }

    public List<Server> getServers()
    {
        return servers;
    }
}
