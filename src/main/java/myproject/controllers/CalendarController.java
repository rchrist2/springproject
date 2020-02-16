package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import myproject.ErrorMessages;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Component
public class CalendarController implements Initializable {

    @FXML
    private GridPane calendarGridPane;

    @FXML
    private Label availabilityLabelMon1;

    @FXML
    private Label availabilityLabelSun1;

    @FXML
    private Label availabilityLabelSat1;

    @FXML
    private Label titleLabel;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private UserRepository userRepository;

    private ArrayList<VBox> calendarDays = new ArrayList<>(35);

    private static YearMonth currentYearAndMonth;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //currentYearAndMonth = YearMonth.now();
        //System.out.println("Current Year: " + currentYearAndMonth.getYear() + " Month: " + currentYearAndMonth.getMonth());

        createCalendar();

        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        System.out.println("Current user is " + currentUser);

        //get the current user as an object
        TblUsers user = userRepository.findUsername(currentUser);

        //make a set of their availabilities
        Set<TblAvailability> availabilitySet = user.getAvailabilities();

        try {
            //find the day of the week for each availability using function
            assignBasedOnDay(availabilitySet);
        } catch (Exception e) {
                e.printStackTrace();

                System.out.println("Cannot locate availabilities for " + currentUser);

                //output an error message for user
                ErrorMessages.showErrorMessage("Failed to load calendar","Cannot locate availability for \" + currentUser",
                        "Availability for " + currentUser + " does not exist or cannot be found.");
            }


    }

    public void assignBasedOnDay(Set<TblAvailability> availability){
        //format java.sql.Time values to 12-hr clock
        SimpleDateFormat sdfAM = new SimpleDateFormat("hh:mm a");

        //check the day of week for each availability and assign to appropriate label
        for(TblAvailability a : availability){
        switch (a.getDay().getDayDesc()) {
            case "Monday":
                availabilityLabelMon1.setText(sdfAM.format(a.getTimeBegin()) + " to "
                        + sdfAM.format(a.getTimeEnd()));
                continue;
            case "Sunday":
                availabilityLabelSun1.setText(sdfAM.format(a.getTimeBegin()) + " to "
                        + sdfAM.format(a.getTimeEnd()));
                continue;
            case "Saturday":
                availabilityLabelSat1.setText(sdfAM.format(a.getTimeBegin()) + " to "
                        + sdfAM.format(a.getTimeEnd()));
                continue;
            default:
                //Other days do not have labels yet, so output this temporarily
                System.out.println("Other day labels are not set yet");
            }
        }
    }

    //Create the calender
    public void createCalendar(){
        calendarDays.clear();
        calendarGridPane.getChildren().removeAll(calendarDays);

        //currentYearAndMonth = YearMonth.now();

        int rows = 5, columns = 7;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {

                VBox vBox = new VBox();

                GridPane.setVgrow(vBox, Priority.ALWAYS);

                calendarGridPane.add(vBox, c, r);
                calendarDays.add(vBox);
            }
        }

        refreshCalendar(YearMonthInstance.getInstance().getYearMonth());
    }

    //Populates the calender with dates
    private void refreshCalendar(YearMonth currentYearMonth){
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();
        int offsetDays = 0;

        LocalDate localDate = LocalDate.of(year, month, 1);
        System.out.println(year + ", " + month );

        Label dayLabel;
        VBox employeeHoursBox;

        //Set the title of the calendar
        titleLabel.setText(monthFormat(YearMonthInstance.getInstance().getYearMonth().getMonth().toString()) + " "
                + YearMonthInstance.getInstance().getYearMonth().getYear());

        /*
        Subtracts days of the month until we reach sunday so we get
        the correct first day of the current month
        */
        while (!localDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            localDate = localDate.minusDays(1);
            offsetDays++;
        }

        //Goes through each day of the month
        for(VBox dayBox : calendarDays){

            //Delete everything inside of each day box
            if(dayBox.getChildren().size() != 0){
                do {
                    dayBox.getChildren().remove(0);
                } while (dayBox.getChildren().size() != 0);
            }

            System.out.println(localDate.getDayOfMonth() + " :::::: " + localDate.getMonth().maxLength());

            //Day label and sets their css
            dayLabel = new Label(String.valueOf(localDate.getDayOfMonth()));
            dayLabel.setTextAlignment(TextAlignment.LEFT);
            dayLabel.setStyle("-fx-padding: 0 0 0 5");

            //vBox holding each employee hour
            employeeHoursBox = new VBox();
            employeeHoursBox.setSpacing(5);

            //Sets the style of the boxes that are not a part of the current month
            if(offsetDays != 0) {
                dayLabel.setStyle("-fx-padding: 0 0 0 5; -fx-text-fill: #A9A9A9");
                offsetDays--;
            }

            //Add the day number and employee vBox into the Day Box
            dayBox.getChildren().addAll(dayLabel, employeeHoursBox);

            //Add 1 to the day
            localDate = localDate.plusDays(1);
        }
    }

    @FXML
    private void handlePreviousMonth(){

        //Goes back a month
        YearMonthInstance.getInstance().prevMonth();

        //Refreshes the calendar
        refreshCalendar(YearMonthInstance.getInstance().getYearMonth());
    }

    @FXML
    private void handleNextMonth(){

        //Goes forward a month
        YearMonthInstance.getInstance().nextMonth();

        //Refreshes the calendar
        refreshCalendar(YearMonthInstance.getInstance().getYearMonth());
    }

    /*
    Returns the month into the correct format "January, February, March..."
     */
    private String monthFormat(String word){
        char[] month = word.toLowerCase().toCharArray();
        month[0] = Character.toUpperCase(month[0]);

        return String.valueOf(month);
    }
}
