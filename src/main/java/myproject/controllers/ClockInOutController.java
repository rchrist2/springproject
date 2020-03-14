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
import myproject.models.Tblusers;
import myproject.repositories.ClockRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
    public RadioButton allTimeCheck;

    @FXML
    public RadioButton currentWeekCheck;

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
        Tblusers currUser = userRepository.findUsername(currentUser);
        tableUserLabel.setText("Clock History This Week for " + currUser.getEmployee().getName());

        //initialize list of user's clock history
        listOfClock = FXCollections.observableArrayList();
        listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));

        //show the last clock in/out action for this user
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
        else{
            lastActionLabel.setText("");
        }

        //initialize the schedule drop down menu for current week
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleThisWeekForUser(currentUser));

        if(!(scheduleData.isEmpty())){
            scheduleList.setItems(scheduleData);
        }
        else{
            scheduleList.setItems(null);
        }

        reloadClockTable();
        setDataForClockTableView();
        addActionListenersForCrudButtons(clockDeleteButton);
        addActionListenersForCrudButtons(clockEditButton);
        addToggleGroupForRadioButtons();

        clockTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedClock = newv;
        });

        //if the user is the owner or manager, they can see buttons to edit requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")) {

            //only managers/owner can edit or delete clock history
            clockEditButton.setVisible(true);
            clockDeleteButton.setVisible(true);

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
        //get the current date
        Date dateNow = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //if a schedule was selected
        if(!(scheduleList.getSelectionModel().isEmpty())) {

            //check if the schedule date is equal to today's date
            if(sdf.format(scheduleList.getSelectionModel().getSelectedItem()
                    .getScheduleDate()).equals(sdf.format(dateNow))) {

                Tblclock newClock = new Tblclock();

                newClock.setPunchIn(java.sql.Time.valueOf(LocalTime.now()));

                newClock.setPunchOut(java.sql.Time.valueOf("00:00:00"));
                newClock.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
                newClock.setDateCreated(new java.sql.Timestamp(new java.util.Date().getTime()));

                //save the new clock-in
                clockRepository.save(newClock);

                //update the "Last Action" label
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
                ErrorMessages.showErrorMessage("Invalid schedule selected",
                        "Schedule must be equal to today's date",
                        "You cannot clock in for a date earlier than or later than today's date");
            }
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "No Schedule selected",
                    "Please select a Schedule from the drop-down menu");
        }

    }

    @FXML
    public void clockOut(){
        //get the current date
        Date dateNow = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if(!(scheduleList.getSelectionModel().isEmpty())) {

            //check if the schedule date is equal to today's date
            if(sdf.format(scheduleList.getSelectionModel().getSelectedItem()
                    .getScheduleDate()).equals(sdf.format(dateNow))) {

                //finds recent clock record based on selected schedule
                Tblclock newClock2 = clockRepository.findRecentClockForSchedule(scheduleList.getSelectionModel().getSelectedItem().getScheduleId());

                newClock2.setPunchOut(java.sql.Time.valueOf(LocalTime.now()));

                clockRepository.save(newClock2);

                //update the "Last Action" label
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
                ErrorMessages.showErrorMessage("Invalid schedule selected",
                        "Schedule must be equal to today's date",
                        "You cannot clock out for a date earlier than or later than today's date");
            }
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
            //open the CRUD form
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudClock.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Edit Clock In/Out Record");

            CrudClockController crudClockController = fxmlLoader.getController();
            crudClockController.setLabel("Edit Clock Record for "
                    + selectedClock.getSchedule().getEmployee().getName());
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

        //get the selected entry's user and other info
        String selectedUser = cl.getSchedule().getEmployee().getUser().getUsername();
        String selectedDay = cl.getSchedule().getDay().getDayDesc();
        String selectedDate = dateFormat.format(cl.getSchedule().getScheduleDate());

        //ask the user if they are sure about deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Clock In/Out Record");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You are about to delete a clock in/out record for: " + selectedUser + " on " +
                selectedDay + ", " + selectedDate + " from " +
                timeFormat.format(cl.getPunchIn()) + " to " + timeFormat.format(cl.getPunchOut()));

        Optional<ButtonType> choice = alert.showAndWait();

        //if the user clicks "OK", delete the entry
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
        //reload the table to show all users (only for managers/owner)
        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(true);

        listOfClock.addAll(clockRepository.findClockThisWeekAllUser());
        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        tableUserLabel.setText("Clock History This Week for All Users");
        setDataForClockTableView();
    }

    private void reloadClockTable(){
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(false);

        listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));
        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        tableUserLabel.setText("Clock History This Week for " + currUser.getEmployee().getName());
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

    @FXML
    private void showCurrentWeek(){
        //if the user column is visible (which is only for managers/owner)
        if(userCol.isVisible()){
            //reload the table to show all users (only for managers/owner)
            listOfClock.clear();
            clockTable.setItems(listOfClock);

            listOfClock.addAll(clockRepository.findClockThisWeekAllUser());
            filteredListOfClock = new FilteredList<>(listOfClock);
            clockTable.setItems(filteredListOfClock);
            tableUserLabel.setText("Clock History This Week for All Users");
            setDataForClockTableView();
        }
        else{
            String currentUser = LoginController.userStore;
            Tblusers currUser = userRepository.findUsername(currentUser);

            listOfClock.clear();
            clockTable.setItems(listOfClock);

            listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));
            filteredListOfClock = new FilteredList<>(listOfClock);
            clockTable.setItems(filteredListOfClock);
            tableUserLabel.setText("Clock History This Week for " + currUser.getEmployee().getName());
            setDataForClockTableView();
        }
    }

    @FXML
    private void showAllWeeks(){
        //if the user column is visible (which is only for managers/owner)
        if(userCol.isVisible()){
            //reload the table to show all users (only for managers/owner)
            listOfClock.clear();
            clockTable.setItems(listOfClock);

            listOfClock.addAll(clockRepository.findAll());
            filteredListOfClock = new FilteredList<>(listOfClock);
            clockTable.setItems(filteredListOfClock);
            tableUserLabel.setText("Clock History All Time for All Users");
            setDataForClockTableView();
        }
        else{
            String currentUser = LoginController.userStore;
            Tblusers currUser = userRepository.findUsername(currentUser);

            listOfClock.clear();
            clockTable.setItems(listOfClock);

            listOfClock.addAll(clockRepository.findClockForUser(currentUser));
            filteredListOfClock = new FilteredList<>(listOfClock);
            clockTable.setItems(filteredListOfClock);
            tableUserLabel.setText("Clock History All Time for " + currUser.getEmployee().getName());
            setDataForClockTableView();
        }
    }

    private void addToggleGroupForRadioButtons(){
        ToggleGroup toggleGroup = new ToggleGroup();

        currentWeekCheck.setToggleGroup(toggleGroup);
        allTimeCheck.setToggleGroup(toggleGroup);

    }
}
