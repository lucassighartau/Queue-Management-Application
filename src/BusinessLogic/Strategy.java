package BusinessLogic;

import Model.*;
import java.util.*;

public interface Strategy {
    public void addTask(List<Server> servers, Task t);
}
