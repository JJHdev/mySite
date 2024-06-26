package com.project.mySite.component.Utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Utils {
    private Utils(){}

    public static Timestamp formatLocalDateTime(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }

}
