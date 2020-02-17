package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.repositories.ClockRepository;
import myproject.repositories.ScheduleRepository;
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
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private UserRepository userRepository;

    public ObservableList<Tblschedule> scheduleData = FXCollections.observableArrayList();


    @FXML
    public ComboBox<Tblschedule> scheduleList;
    @FXML
    public Label feedbackLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;

        //TODO sort with most recent date listed first
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));

        scheduleList.setItems(scheduleData);
    }

    @FXML
    public void clockIn(){
        Tblclock newClock = new Tblclock();

        newClock.setPunchIn(java.sql.Time.valueOf(LocalTime.now()));

        newClock.setPunchOut(java.sql.Time.valueOf("00:00:00"));
        newClock.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
        //newAvail.setDate_created(java.sql.Date.valueOf(LocalDate.now()));

        //gets the day of week as a string (matches capitalized format ex. "Monday" in database)
        /*String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        newAvail.setDay(dayRepository.findDay(dayOfWeek));*/

        //save the new clock-in
        clockRepository.save(newClock);

        feedbackLabel.setText("Successfully clocked in");

    }

    @FXML
    public void clockOut(){
        Tblclock newClock2 = clockRepository.findClockBySchedule(scheduleList.getSelectionModel().getSelectedItem());

        //get the current day of week
        /*String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        */
        //find the availability set earlier, which would be on the same day
        newClock2.setPunchOut(java.sql.Time.valueOf(LocalTime.now()));

        clockRepository.save(newClock2);

        feedbackLabel.setText("Successfully clocked out");
    }
}
