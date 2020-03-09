package myproject.models;

import java.time.YearMonth;
import java.util.Calendar;

public class YearMonthInstance {
    private final static YearMonthInstance currentYearMonth = new YearMonthInstance();

    public YearMonth yearMonth;

    //Returns the current instance of the MonthInstance
    public static YearMonthInstance getInstance(){
        return currentYearMonth;
    }

    //Returns the current YearMonth
    public YearMonth getYearMonth(){
        return yearMonth;
    }

    //Sets the yearMonth to the current YearMonth
    public void setCurrentMonthYear(){
        yearMonth = YearMonth.now();
    }

    //Increases the yearMonth by 1
    public void nextMonth(){
        yearMonth = yearMonth.plusMonths(1);
    }

    //Decreases the yearMonth by 1
    public void prevMonth(){
        yearMonth = yearMonth.minusMonths(1);
    }

    public void getDayStartOfWeek(){
        Calendar calendar = Calendar.getInstance();




    }
}
