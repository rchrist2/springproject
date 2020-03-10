package myproject.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class ClockInOutController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private UserRepository userRepository;

    @FXML
    public ComboBox<Tblschedule> scheduleList;

    @FXML
    private Pane optionsPane;

    @FXML
    private Pane optionsPane2;

    @FXML
    private Button clockDeleteButton;

    @FXML
    private Button clockEditButton;

    @FXML
    public Label tableUserLabel;

    @FXML
    public Label lastActionLabel;

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

    public Tblclock selectedClock;

    private ObservableList<Tblclock> listOfClock;
    private FilteredList<Tblclock> filteredListOfClock;

    public ObservableList<Tblschedule> scheduleData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        tableUserLabel.setText("Clock History for " + currentUser);

        //initialize list of user's clock history
        listOfClock = FXCollections.observableArrayList();
        listOfClock.addAll(clockRepository.findClockForUser(currentUser));

        if(!(listOfClock.isEmpty())) {
            Tblclock recentClockForUser = clockRepository.findRecentClockForUser(currentUser);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            //get the recent clock in/out details
            String recentDay = recentClockForUser.getSchedule().getDay().getDayDesc();
            String recentDate = dateFormat.format(recentClockForUser.getSchedule().getScheduleDate());

            if(recentClockForUser.getPunchOut().equals(Time.valueOf("00:00:00"))){
                lastActionLabel.setText("Last Action \"In\" on " + recentDay + ", " +
                        recentDate + " at " +
                        timeFormat.format(recentClockForUser.getPunchIn()));
            }
            else if(!(recentClockForUser.getPunchOut().equals(Time.valueOf("00:00:00")))){
                lastActionLabel.setText("Last Action \"Out\" on " + recentDay + ", " +
                        recentDate + " at " +
                        timeFormat.format(recentClockForUser.getPunchOut()));
            }
        }

        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));

        scheduleList.setItems(scheduleData);

        reloadClockTable();
        setDataForClockTableView();
        addActionListenersForCrudButtons(clockDeleteButton);
        addActionListenersForCrudButtons(clockEditButton);


        clockTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedClock = newv;
        });

        //if the user is the owner or manager, they can see buttons to approve requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")) {
            //declare variables
            Button showAllUser = new Button();
            Button showThisUser = new Button();

            //add buttons to panes
            optionsPane.getChildren().add(showAllUser);
            optionsPane2.getChildren().add(showThisUser);

            //set up other buttons for showing all users or current user
            showThisUser.setText("Current User");
            showThisUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showThisUser.setOnAction(event ->{
                reloadClockTable();
                setDataForClockTableView();
            });

            showAllUser.setText("All Users");
            showAllUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showAllUser.setOnAction(event -> {
                //use function to reload table showing all users
                reloadClockTableAllUsers();
                setDataForClockTableView();
            });

        }
    }

    @FXML
    public void clockIn(){
        if(!(scheduleList.getSelectionModel().isEmpty())) {
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

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String day = newClock.getSchedule().getDay().getDayDesc();
            String date = dateFormat.format(newClock.getSchedule().getScheduleDate());
            lastActionLabel.setText("Last Action \"In\" on " + day +
                    ", " + date + " at "
                    + timeFormat.format(newClock.getPunchIn()));

            reloadClockTable();
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "No Schedule selected",
                    "Please select a Schedule from the drop-down menu");
        }

    }

    @FXML
    public void clockOut(){
        if(!(scheduleList.getSelectionModel().isEmpty())) {
            //finds recent clock record based on selected schedule
            Tblclock newClock2 = clockRepository.findRecentClockForSchedule(scheduleList.getSelectionModel().getSelectedItem().getScheduleId());

            //get the current day of week
        /*String dayOfWeek = String.valueOf(DayOfWeek.from(LocalDate.now())).toLowerCase();
        dayOfWeek = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();*/

            newClock2.setPunchOut(java.sql.Time.valueOf(LocalTime.now()));

            clockRepository.save(newClock2);

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String day = newClock2.getSchedule().getDay().getDayDesc();
            String date = dateFormat.format(newClock2.getSchedule().getScheduleDate());
            lastActionLabel.setText("Last Action \"Out\" on " + day +
                    ", " + date + " at "
                    + timeFormat.format(newClock2.getPunchOut()));

            reloadClockTable();
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "No Schedule selected",
                    "Please select a Schedule from the drop-down menu");
        }

    }

    @FXML
    private void editClock(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudClock.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Edit Clock In/Out Record");

            CrudClockController crudClockController = fxmlLoader.getController();
            crudClockController.setLabel("Edit Clock Record for "
                    + selectedClock.getSchedule().getEmployee().getUser().getUsername());
            crudClockController.setClock(selectedClock);
            crudClockController.setController(this);

            stage.setScene(new Scene(parent));

            stage.showAndWait();

            //reload table w/ all users if the user column is visible (only visible if all users are shown)
            if(userCol.isVisible()){
                reloadClockTableAllUsers();
                setDataForClockTableView();
                resetButtons();
            }
            else{
                reloadClockTable();
                setDataForClockTableView();
                resetButtons();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteClock(){
        //get the selected entry from the table
        Tblclock cl = selectedClock;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        //get the selected entry's user
        String selectedUser = cl.getSchedule().getEmployee().getUser().getUsername();
        String selectedDay = cl.getSchedule().getDay().getDayDesc();
        String selectedDate = dateFormat.format(cl.getSchedule().getScheduleDate());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Clock In/Out Record");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You are about to delete a clock in/out record for: " + selectedUser + " on " +
                selectedDay + ", " + selectedDate + " from " +
                timeFormat.format(cl.getPunchIn()) + " to " + timeFormat.format(cl.getPunchOut()));

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == ButtonType.OK) {
            clockRepository.delete(cl);
        }
        else{
            System.out.println("Delete cancelled");
        }

        //reload table with all users if the user column is visible
        if(userCol.isVisible()){
            reloadClockTableAllUsers();
            setDataForClockTableView();
            resetButtons();
        }
        else{
            reloadClockTable();
            setDataForClockTableView();
            resetButtons();
        }
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

    private void reloadClockTableAllUsers(){
        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(true);

        listOfClock.addAll(clockRepository.findAll());
        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        tableUserLabel.setText("Clock History for All Users");
        setDataForClockTableView();
    }

    private void reloadClockTable(){
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;

        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(false);

        listOfClock.addAll(clockRepository.findClockForUser(currentUser));
        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        tableUserLabel.setText("Clock History for " + currentUser);
        setDataForClockTableView();
    }

    private void addActionListenersForCrudButtons(Button button){
        clockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                button.setDisable(false);
            }
        });
    }

    private void resetButtons(){
        clockEditButton.setDisable(true);
        clockDeleteButton.setDisable(true);
    }
}
