package BusinessLogic;

import Model.*;
import java.util.*;

public class ShortestTimeStrategy implements Strategy{
    public void addTask(List<Server> servers, Task task)
    {
        int waitingMin = Integer.MAX_VALUE;
        Server bestServer = null;
        for(Server server : servers)
        {
            if(server.getWaitingPeriod().get() < waitingMin)
            {
                bestServer = server;
                waitingMin = server.getWaitingPeriod().get();
            }
        }
        if(bestServer != null && waitingMin != Integer.MAX_VALUE)
        {
            bestServer.addTask(task);
        }
    }
}
