package com.adi;

import java.time.Instant;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

    public static String convertToRegular(String time) {
        long millis = Long.parseLong(time.replaceAll("[^0-9]", ""));

        LocalTime newTime = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a");
        
        if (newTime.getMinute() >= 30) {
            newTime = newTime.plusHours(1);
        }

        newTime = newTime.withMinute(0).withSecond(0).withNano(0);

        return newTime.format(formatter);
    }
    
    public static String convertFromISO(String iso) {
        LocalDateTime time = LocalDateTime.parse(iso);

       DateTimeFormatter output =
        DateTimeFormatter.ofPattern("hh:mm a");

        String regular = time.format(output);
        return regular;
        
    }
}
