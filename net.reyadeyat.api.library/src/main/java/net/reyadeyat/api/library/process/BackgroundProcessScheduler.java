package net.reyadeyat.api.library.process;

import java.util.concurrent.ScheduledFuture;

public interface BackgroundProcessScheduler {
    public String getDescription();
    public ScheduledFuture<?> schedule(Runnable task) throws Exception;
}
