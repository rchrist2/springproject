package myproject.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import myproject.ErrorMessages;
import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import myproject.repositories.ClockRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
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

    public ObservableList<Tblschedule> scheduleData;

    @FXML
    public ComboBox<Tblschedule> scheduleList;
    @FXML
    public Label feedbackLabel;

    @FXML
    public TableView<Tblclock> clockTable;

    @FXML
    public TableColumn<Tblclock, String> punchInCol;

    @FXML
    public TableColumn<Tblclock, String> punchOutCol;

    @FXML
    public TableColumn<Tblclock, java.sql.Date> scheduleCol;

    @FXML
    public TableColumn<Tblclock, String> userCol;

    @FXML
    public Label tableUserLabel;

    public Tblclock selectedClock;

    private ObservableList<Tblclock> listOfClock;
    private FilteredList<Tblclock> filteredListOfClock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        tableUserLabel.setText("Clock History for " + currentUser);

        //initialize list of user's clock history
        listOfClock = FXCollections.observableArrayList();
        listOfClock.addAll(clockRepository.findClockForUser(currentUser));

        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));

        scheduleList.setItems(scheduleData);

        reloadClockTable();
        setDataForClockTableView();

        clockTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedClock = newv;
        });

        //if the user is the owner or manager, they can see buttons to approve requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")) {
            userCol.setVisible(true);

        }
    }

    @FXML
    public void clockIn(){
        Tblclock newClock = new Tblclock();

        newClock.setPunchIn(java.sql.Time.valueOf(LocalTime.now()));

        newClock.setPunchOut(java.sql.Time.valueOf("00:00:00"));
        newClock.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
        newClock.setDate_created(new java.sql.Timestamp(new java.util.Date().getTime()));

        //gets the day of week as a string (matches capitalized format ex. "Monday" in database)
        /*String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        newAvail.setDay(dayRepository.findDay(dayOfWeek));*/

        //save the new clock-in
        clockRepository.save(newClock);

        feedbackLabel.setText("Successfully clocked in");

        reloadClockTable();

    }

    @FXML
    public void clockOut(){
        //get the current user
        String currentUser = LoginController.userStore;

        //finds recent clock record based on user
        //TODO fix issue with this query
        Tblclock newClock2 = clockRepository.findRecentClockForUser(currentUser);
        System.out.print(newClock2.getPunchIn());

        //get the current day of week
        /*String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();*/

        /*newClock2.setPunchOut(java.sql.Time.valueOf(LocalTime.now()));

        clockRepository.save(newClock2);

        feedbackLabel.setText("Successfully clocked out");

        reloadClockTable();*/

    }

    private void setDataForClockTableView(){
        scheduleCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));

        //using lambda to display with AM and PM
        punchInCol.setCellValueFactory(Tblclock -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            property.setValue(timeFormat.format(Tblclock.getValue().getPunchIn()));
            return property;
        });

        punchOutCol.setCellValueFactory(Tblclock -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            property.setValue(timeFormat.format(Tblclock.getValue().getPunchOut()));
            return property;
        });

        //show the users for each time off request using SimpleObjectProperty
        userCol.setCellValueFactory(tf ->
                new SimpleObjectProperty<>(tf.getValue().getSchedule().getEmployee().getUser().getUsername()));
    }

    private void reloadClockTable(){
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;

        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(true);

        listOfClock.addAll(clockRepository.findClockForUser(currentUser));
        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        tableUserLabel.setText("Clock History for " + currentUser);
        setDataForClockTableView();
    }
}
