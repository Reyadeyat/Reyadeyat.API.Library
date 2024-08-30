package net.reyadeyat.api.library.logger;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EmbeddedLogger {

    final static private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSSSSS");
    private EmbeddedLogger() {}
    static private void handleLogger(String log_level, String print_break, Boolean print_logger, Boolean print_time, String log_path, String file_name) throws Exception {
        new File(log_path).mkdirs();
        String log_file = log_path+"/"+file_name+"."+sdf.format(Date.from(Instant.now().atZone(ZoneId.of("UTC")).toInstant()))+"."+UUID.randomUUID().toString()+".log";
        EmbeddedLevel.init();
        FileHandler file_handler = new FileHandler(log_file);
        Files.deleteIfExists(new File(log_file+".lck").toPath());
        if (print_logger == true && print_time == true) {
            file_handler.setFormatter(new SimpleFormatter());
        } else {
            file_handler.setFormatter(new EmbeddedFormatter(print_break, print_logger, print_time));
        }
        LogManager log_manager = LogManager.getLogManager();
        Iterator<String> logget_name_iterator = log_manager.getLoggerNames().asIterator();
        while(logget_name_iterator.hasNext()) {
            String logger_name = logget_name_iterator.next();
            Logger logger = Logger.getLogger(logger_name);
            logger.setUseParentHandlers(true);
            logger.setLevel(Level.parse(log_level));
            fixHandlers(logger, log_level, print_break, print_logger, print_time);
        }
        Logger root_logger = Logger.getLogger("");
        root_logger.setLevel(Level.parse(log_level));
        root_logger.setUseParentHandlers(false);
        fixHandlers(root_logger, log_level, print_break, print_logger, print_time);
        root_logger.addHandler(file_handler);
    }
    
    static public void fixHandlers(Logger logger, String log_level, String print_break, Boolean print_logger, Boolean print_time) throws Exception {
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(Level.parse(log_level));
            handler.setEncoding("UTF-8");
            if (handler instanceof FileHandler) {
                logger.removeHandler(handler);
                continue;
            }
            if (!print_logger || !print_time) {
                handler.setFormatter(new EmbeddedFormatter(print_break, print_logger, print_time));
            }
        }
    }

    static public void build(String app_name, String log_level, String print_break, Boolean print_logger, Boolean print_time, String log_file_path) throws Exception {
        EmbeddedLogger.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.setProperty(".level", log_level);
        System.setProperty("javax.net.debug","info");
        System.setProperty("jdk.tls.client.enableSessionTicketExtension", "false");
        EmbeddedLogger.handleLogger(log_level, print_break, print_logger, print_time, log_file_path, app_name);
        Runnable logger_task = new Runnable() {
            @Override
            public void run() {
                try {
                    EmbeddedLogger.handleLogger(log_level, print_break, print_logger, print_time, log_file_path, app_name+".log");
                } catch (Exception ex) {
                    Logger.getLogger(EmbeddedLogger.class.getName()).log(Level.SEVERE, "Logger Sechduled Task error", ex);
                }
            }
        };
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long initial_delay = Duration.between(now, midnight).toMinutes();
        long period = 24 * 60;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(logger_task, initial_delay, period, TimeUnit.MINUTES);
    }
}
