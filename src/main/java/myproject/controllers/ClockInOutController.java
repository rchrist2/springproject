package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import myproject.models.TblAvailability;
import myproject.repositories.AvailabilityRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

@Component
public class ClockInOutController implements Initializable {

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
    }

    @FXML
    public void clockIn(){
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;
        TblAvailability newAvail = new TblAvailability();
        newAvail.setTimeBegin(java.sql.Time.valueOf(LocalTime.now()));

        newAvail.setTimeEnd(java.sql.Time.valueOf("00:00:00"));
        newAvail.setAssigned(true);
        newAvail.setUser(userRepository.findUsername(currentUser));
        //newAvail.setDate_created(java.sql.Date.valueOf(LocalDate.now()));

        //gets the day of week as a string (matches capitalized format ex. "Monday" in database)
        String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        newAvail.setDay(dayRepository.findDay(dayOfWeek));

        //save the new availability
        availabilityRepository.save(newAvail);

    }

    @FXML
    public void clockOut(){
        //get the current user
        String currentUser = LoginController.userStore;

        //get the current day of week
        String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();

        //find the availability set earlier, which would be on the same day
        TblAvailability newAvail2 = availabilityRepository.findTblAvailabilitiesByUserAndDay(currentUser, dayOfWeek);
        newAvail2.setTimeEnd(java.sql.Time.valueOf(LocalTime.now()));

        availabilityRepository.save(newAvail2);
    }
}
