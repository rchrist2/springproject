package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import myproject.ErrorMessages;
import myproject.models.TblAvailability;
import myproject.models.TblDay;
import myproject.models.TblUsers;
import myproject.repositories.AvailabilityRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private UserRepository userRepository;

    private ArrayList<VBox> calendarDays = new ArrayList<>(35);

    private YearMonth currentYearAndMonth;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentYearAndMonth = YearMonth.now();
        System.out.println("Current Year: " + currentYearAndMonth.getYear() + " Month: " + currentYearAndMonth.getMonth());

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
        int rows = 5, columns = 7;

        for (int r = 0; r < columns; r++) {
            for (int c = 0; c < rows; c++) {

                VBox vBox = new VBox();

                GridPane.setVgrow(vBox, Priority.ALWAYS);

                calendarGridPane.add(vBox, r, c);

                calendarDays.add(vBox);
            }
        }

        refreshCalendar(currentYearAndMonth);
    }

    //Populates the calender with dates
    private void refreshCalendar(YearMonth currentYearMonth){
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();

        //Set the title of the calendar
        titleLabel.setText(currentYearAndMonth.getYear() + " Calendar");


        LocalDate localDate = LocalDate.of(year, month, 1);

        //Goes through each day of the month
        for(VBox dayBox : calendarDays){

            //Delete everything inside of each day box
            if(dayBox.getChildren().size() != 0){
                dayBox.getChildren().remove(0);
            }

            //Day label
            Label dayLabel = new Label(String.valueOf(localDate.getDayOfMonth()));
            dayLabel.setTextAlignment(TextAlignment.LEFT);

            //vBox holding each employee hour
            VBox employeeHoursBox = new VBox();

            //Add the day number and employee vBox into the Day Box
            dayBox.getChildren().addAll(dayLabel, employeeHoursBox);

            localDate = localDate.plusDays(1);
        }
    }

    @FXML
    private void handlePreviousMonth(){
        currentYearAndMonth = currentYearAndMonth.minusYears(1);
        titleLabel.setText(currentYearAndMonth.getYear() + " Calendar");
        //refreshCalendar(currentYearAndMonth);
    }

    @FXML
    private void handleNextMonth(){
        currentYearAndMonth = currentYearAndMonth.plusYears(1);
        titleLabel.setText(currentYearAndMonth.getYear() + " Calendar");
        //refreshCalendar(currentYearAndMonth);
    }

}
