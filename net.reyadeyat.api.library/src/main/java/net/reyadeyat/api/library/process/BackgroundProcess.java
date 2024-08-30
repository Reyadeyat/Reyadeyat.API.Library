package net.reyadeyat.api.library.process;

import java.util.concurrent.ScheduledFuture;

public class BackgroundProcess {
    final protected BackgroundProcessScheduler background_process_scheduler;
    private ScheduledFuture<?> scheduled_future;
    boolean started;

    public BackgroundProcess(BackgroundProcessScheduler background_process_scheduler) {
        this.background_process_scheduler = background_process_scheduler;
        started = false;
    }
    
    public void startTask(Runnable runnable) throws Exception {
        scheduled_future = background_process_scheduler.schedule(runnable);
        started = true;
    }

    public void restartTask(Runnable runnable) throws Exception {
        if (scheduled_future != null) {
            scheduled_future.cancel(false);
            started = false;
            startTask(runnable);
        }
    }
}
