package com.project.mySite.UtilsComponent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    private Utils(){}

    public static Timestamp formatLocalDateTime(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }



}
