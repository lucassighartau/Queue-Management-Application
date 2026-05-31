package BusinessLogic;

import Model.*;
import java.util.*;

public class ShortestQueueStrategy implements Strategy{
    public void addTask(List<Server> servers, Task task)
    {
        Server bestServer = null;
        int bestLen = Integer.MAX_VALUE;

        for(Server server: servers)
        {
            if(server.getTasks().size() < bestLen)
            {
                bestLen = server.getTasks().size();
                bestServer = server;
            }
        }

        if((bestServer != null) && (bestLen != Integer.MAX_VALUE))
            bestServer.addTask(task);
    }
}
