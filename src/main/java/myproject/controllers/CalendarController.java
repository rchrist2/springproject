package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import myproject.models.TblAvailability;
import myproject.models.TblUsers;
import myproject.repositories.AvailabilityRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CalendarController implements Initializable {

    @FXML
    private GridPane calendarGridPane;

    @FXML
    private Label availabilityLabel;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //can also store this in a session rather than taking from textfield
        String currentUser = LoginController.userStore;

        //sets label to show start and end time for user, but does not check for day of week
        TblAvailability availability = availabilityRepository.findTblAvailabilitiesByUser(currentUser);
        availabilityLabel.setText("Available from " + availability.getTimeBegin().toString() + " to " + availability.getTimeEnd().toString());

        //TODO currently using a one-to-many with tblAvailability and tblUsers, but should be many-to-many with tblDay and tblUsers
        //TODO needs to check day of week and assign the proper column

    }
}
