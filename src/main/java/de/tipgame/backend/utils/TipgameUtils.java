package de.tipgame.backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TipgameUtils {

    public static Boolean isTimeToDisable(String timeToDisable)
    {
        Boolean result = false;
        try {
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 0); // adds one hour
            Date date = cal.getTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            Date parsedDate = dateFormat.parse(timeToDisable);
            if (date.compareTo(parsedDate) == 0 || date.compareTo(parsedDate) > 0)
            {
                result = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
