package myproject.controllers.Dashboard.Calendar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static java.time.temporal.TemporalAdjusters.*;

import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class WeeklyScheduleController implements Initializable {

    @FXML
    private GridPane weeklyCalendarGridPane,
                dayGridPane;

    @FXML
    private ScrollPane gridpaneScrollPane;

    @FXML
    private Label titleLabel,
                monthYearLabel,
                weekLabel,
                sundayLabel,
                mondayLabel,
                tuesdayLabel,
                wednesdayLabel,
                thursdayLabel,
                fridayLabel,
                saturdayLabel;

    @FXML
    private VBox titleVBox;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    private List<Tblemployee> listOfEmployees;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("MM/dd");

    private ArrayList<String> days = new ArrayList<>(
            Arrays.asList("Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat")
    );

    private LocalDate today = LocalDate.now();
    public LocalDate sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
    public LocalDate saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
        saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

        listOfEmployees = employeeRepository.findAllEmployee();
        gridpaneScrollPane.setFitToHeight(true);

        monthYearLabel.setText(LocalDate.now().getMonth().toString() + ", " + LocalDate.now().getYear());

        /*LocalDate sunday = today;
        while(sunday.getDayOfWeek() != DayOfWeek.SUNDAY){
            sunday = sunday.minusDays(1);
        }

        LocalDate saturday = today;
        while (saturday.getDayOfWeek() != DayOfWeek.SATURDAY){
            saturday = saturday.plusDays(1);
        }*/

        weekLabel.setText(sunday.format(dateFormat) + " - " + saturday.format(dateFormat));

        refreshDayLabels();
        populateWeeklyCalendar();
    }

    private void populateWeeklyCalendar(){
        Instant start = Instant.now();

        String strDateFormat = "HH:mm a";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(strDateFormat);

        for (int i = 1; i <= listOfEmployees.size(); i++) {
            int x = 0;

            //The beginning of the current week, makes sure that with each new employee
            //the week "restarts" back to the first day
            LocalDate currentDay = sunday;

            Tblemployee name = listOfEmployees.get(i - 1);

            //Get the name of the employee in the list and add it to the gridpane
            Label employeeName = new Label(name.getName());
            employeeName.setStyle("-fx-padding: 5 0 5 0");
            weeklyCalendarGridPane.add(employeeName, 0, i);

            //Loop through the days of the week and check if the employee
            //is working on that day
            for (int j = 1; j <= 7; j++) {
                //Store the hours of the employees in a list
                List<Tblschedule> employeeSchedule = scheduleRepository.findScheduleForEmployee(name.getId());
                System.out.println("The size of employeeSchedule: " + employeeSchedule.size());

                /*if(x < employeeSchedule.size()) {
                    Tblschedule currSchedule = employeeSchedule.get(x);

                    if (currSchedule.getDay().getDayDesc().toLowerCase().equals(currentDay.getDayOfWeek().toString().toLowerCase())) {
                        Label hours = new Label();
                        hours.setText(currSchedule.getScheduleTimeBegin().toLocalTime().format(dateTimeFormatter) + " - "
                                + currSchedule.getScheduleTimeEnd().toLocalTime().format(dateTimeFormatter));

                        hours.setFont(Font.font("System", 11));
                        hours.setStyle("-fx-padding: 0 0 0 5");

                        weeklyCalendarGridPane.add(hours, j, i);
                        x++;
                    }
                }*/


                for (Tblschedule tblSchedule : employeeSchedule){
                    //Check to see if the current day matches the day in the employee schedule
                    if(tblSchedule.getDay().getDayDesc().toLowerCase().equals(currentDay.getDayOfWeek().toString().toLowerCase())){
                        Label hours = new Label();
                        hours.setText(tblSchedule.getScheduleTimeBegin().toLocalTime().format(dateTimeFormatter) + " - "
                                + tblSchedule.getScheduleTimeEnd().toLocalTime().format(dateTimeFormatter));

                        hours.setFont(Font.font("System", 11));
                        hours.setStyle("-fx-padding: 0 0 0 5");

                        weeklyCalendarGridPane.add(hours, j, i);
                    }
                }

                //Add one day to the current day
                //Use the current day to compare the time off
                currentDay = currentDay.plusDays(1);
            }
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);

        System.out.println("The time elapsed is: " + timeElapsed);
    }

    private void refreshDayLabels(){
        LocalDate dayOfWeek = sunday;

/*        for (int i = 1; i <= 7 ; i++) {
            Label day = new Label();
            day.setText(days.get(i - 1) + " " + dayOfWeek.format(dayFormat));
            day.setFont(Font.font("System", FontPosture.ITALIC, 16));

            dayGridPane.add(day, i, 0);
            dayOfWeek = dayOfWeek.plusDays(1);
        }*/

        sundayLabel.setText("Sun " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        mondayLabel.setText("Mon " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        tuesdayLabel.setText("Tues " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        wednesdayLabel.setText("Wed " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        thursdayLabel.setText("Thur " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        fridayLabel.setText("Fri " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        saturdayLabel.setText("Sat " + dayOfWeek.format(dayFormat));
    }

    @SuppressWarnings("Duplicates")
    public void handleNextMonth(){
        sunday = sunday.with(next(DayOfWeek.SUNDAY));
        saturday = saturday.with(next(DayOfWeek.SATURDAY));

        if(!sunday.getMonth().toString().equals(saturday.getMonth().toString())){
            monthYearLabel.setText(sunday.getMonth().toString() + " to " + saturday.getMonth().toString() + ", " + sunday.getYear());
            if(sunday.getYear() != saturday.getYear()){
                monthYearLabel.setText(sunday.getMonth().toString() + ", " + sunday.getYear() + " to " + saturday.getMonth().toString() + ", " + saturday.getYear());
            }
        } else {
            monthYearLabel.setText(sunday.getMonth().toString() + ", " + saturday.getYear());
        }

        weekLabel.setText(sunday.format(dateFormat) + ", " + saturday.format(dateFormat));
        refreshDayLabels();
    }

    @SuppressWarnings("Duplicates")
    public void handlePreviousMonth(){
        sunday = sunday.with(previous(DayOfWeek.SUNDAY));
        saturday = saturday.with(previous(DayOfWeek.SATURDAY));

        if(!sunday.getMonth().toString().equals(saturday.getMonth().toString())){
            monthYearLabel.setText(sunday.getMonth().toString() + " to " + saturday.getMonth().toString() + ", " + sunday.getYear());
            if(sunday.getYear() != saturday.getYear()){
                monthYearLabel.setText(sunday.getMonth().toString() + ", " + sunday.getYear() + " to " + saturday.getMonth().toString() + ", " + saturday.getYear());
            }
        } else {
            monthYearLabel.setText(sunday.getMonth().toString() + ", " + saturday.getYear());
        }

        weekLabel.setText(sunday.format(dateFormat) + ", " + saturday.format(dateFormat));
        refreshDayLabels();
    }
}
