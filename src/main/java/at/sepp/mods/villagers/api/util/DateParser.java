package at.sepp.mods.villagers.api.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {
    private final Calendar calendar;

    public DateParser(final Date date) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);
    }

    public DateParser(final String dateStr) {
        this(toUtilDate(dateStr));
    }

    public static Date toUtilDate(String dateStr) {
        Date date = null;
        
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                date = dateFormat.parse(dateStr);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return date;
    }

    public static Date toSqlDate(final Date date) {
        if (date == null)
            return null;

        return new Date(date.getTime());
    }

    public static Date toSqlDate(final String dateStr) {
        return toSqlDate(toUtilDate(dateStr));
    }

    public String getYear() {
        int year = this.calendar.get(1);
        return Integer.toString(year);
    }

    public String getXXYear() {
        return getYear().substring(2, 4);
    }

    public int getMonth() {
        return this.calendar.get(2) + 1;
    }

    public int getDayOfMonth() {
        return this.calendar.get(5);
    }

    public int getDayOfWeek() {
        return this.calendar.get(7);
    }

    public static void main(String[] args) {
        DateParser parser = new DateParser(args[0]);
        System.out.println("year=" + parser.getYear());
        System.out.println("year2=" + parser.getXXYear());
        System.out.println("month=" + parser.getMonth());
        System.out.println("day=" + parser.getDayOfMonth());
        System.out.println("week=" + parser.getDayOfWeek());
    }
}