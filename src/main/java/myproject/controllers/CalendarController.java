package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
import java.util.Calendar;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

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

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
}
