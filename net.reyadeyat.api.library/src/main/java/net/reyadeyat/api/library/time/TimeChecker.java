package net.reyadeyat.api.library.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class TimeChecker {

    private LocalDateTime currentDateTime;
    public String from_time;
    public String to_time;

    public TimeChecker(LocalDateTime currentDateTime, String from_time, String to_time) {
        this.currentDateTime = currentDateTime;
        this.from_time = from_time;
        this.to_time = to_time;
    }

    public int intime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime fromTime = LocalDateTime.parse(currentDateTime.toLocalDate() + "T" + from_time, formatter);
        LocalDateTime toTime = LocalDateTime.parse(currentDateTime.toLocalDate() + "T" + to_time, formatter);
        //long time = fromTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        //long time = System.currentTimeMillis()+3600000;
        //long time = toTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Duration durationFrom = Duration.between(fromTime, currentDateTime);
        Duration durationTo = Duration.between(currentDateTime, toTime);

        if (durationFrom.isNegative()) {
            /*before time*/
            return -1;
        } else if (durationTo.isPositive()) {
            /*in time*/
            return 0;
        }
        /*after time*/
        return 1;
    }

    public static void main(String[] args) {
        try {
            LocalDateTime now = LocalDateTime.now();
            TimeChecker timeChecker = new TimeChecker(now, "20:00", "20:30");
            int result = timeChecker.intime();
            System.out.println("Result: " + result);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}