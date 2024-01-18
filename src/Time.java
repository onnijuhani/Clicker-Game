
public class Time {
    public static int year;
    public static int month;
    public static int week;
    public static int day;

    public Time() {
    }

    public void incrementDay() {
        day++;

        if (day > 7) {
            day = 1;
            week++;
        }

        if (week > 4) {
            week = 1;
            month++;
        }

        if (month > 12) {
            month = 1;
            year++;
        }

        TimeEventManager.notifyTimeUpdate(day, week, month, year);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public String getClock() {
        return String.format("%04d years, %02d months, %02d weeks, %02d days",
                year, month, week, day);
    }
}
