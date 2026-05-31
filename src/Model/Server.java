package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(BlockingQueue<Task> tasks, AtomicInteger waitingPeriod) {
        this.tasks = tasks;
        this.waitingPeriod = waitingPeriod;
    }

    public BlockingQueue<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(BlockingQueue<Task> tasks)
    {
        this.tasks = tasks;
    }

    public AtomicInteger getWaitingPeriod()
    {
        return waitingPeriod;
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod)
    {
        this.waitingPeriod = waitingPeriod;
    }

    public void addTask(Task newTask)
    {
        this.tasks.add(newTask);
        this.waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            try {
                Task crtTask = tasks.peek();
                if (crtTask != null)
                {
                    int originalService = crtTask.getServiceTime();
                    for (int i = 0; i < originalService; i++)
                    {
                        Thread.sleep(1000L);
                        crtTask.setServiceTime(crtTask.getServiceTime() - 1);
                        waitingPeriod.decrementAndGet();
                    }
                    tasks.take();
                } else
                {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
