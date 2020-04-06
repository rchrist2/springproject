package myproject.controllers.Dashboard.ClockInOut;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myproject.ErrorMessages;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.ErrorMessages;
import myproject.models.Tbltimeoff;
import myproject.models.Tblusers;
import myproject.repositories.ClockRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.DayRepository;
import myproject.repositories.UserRepository;
import myproject.services.ClockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
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

    @Autowired
    private ClockService clockService;

    @FXML
    private Label scheduleList;

    @FXML
    private Pane optionsPane;

    @FXML
    private Pane optionsPane2;

    @FXML
    private Pane clockPane;

    @FXML
    private Pane tablePane;

    @FXML
    private Pane tablePaneMoved;

    @FXML
    private AnchorPane clockAnchor;

    @FXML
    private Button clockDeleteButton;

    @FXML
    private Button clockEditButton;

    @FXML
    private Button clockAddButton;

    @FXML
    private Label tableUserLabel;

    @FXML
    private Label lastActionLabel;

    @FXML
    private RadioButton allTimeCheck;

    @FXML
    private RadioButton currentWeekCheck;

    @FXML
    private TableView<Tblclock> clockTable;

    @FXML
    private TableColumn<Tblclock, String> punchInCol;

    @FXML
    private TableColumn<Tblclock, String> punchOutCol;

    @FXML
    private TableColumn<Tblclock, java.sql.Date> scheduleCol;

    @FXML
    private TableColumn<Tblclock, String> userCol;

    private Tblclock selectedClock;

    private Tblschedule today;

    private ObservableList<Tblclock> listOfClock;
    private FilteredList<Tblclock> filteredListOfClock;

    private ObservableList<Tblschedule> scheduleData;

    public static Button clickedButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //initialize list of user's clock history
        listOfClock = FXCollections.observableArrayList();
        //listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));

        //initialize the schedule drop down menu for current week
        //scheduleData = FXCollections.observableArrayList();
        today = scheduleRepository.findScheduleThisWeekForUserSameDay(currentUser);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        currentWeekCheck.setSelected(true);

        //only show schedule equal to the current day
        if(!(today == null)){
            scheduleList.setText(today.getDay().getDayDesc() + ", " + dateFormat.format(today.getScheduleDate()));
        }
        else{ //if no schedules were found for today
            if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
                scheduleList.setText(null);
            }
            else{
                scheduleList.setText("No Schedules Today");
            }
        }

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfClock.addAll(clockRepository.findClockThisWeekAllUser());

            tableUserLabel.setText("Clock History This Week for All Users");

            clockAnchor.getChildren().remove(clockPane);
            clockAnchor.getChildren().remove(tablePane);
            tablePaneMoved.getChildren().add(tablePane);
            tablePane.setLayoutX(68);
            tablePane.setLayoutY(30);
            clockTable.setPrefHeight(508);
            tablePaneMoved.setVisible(true);

            reloadClockTableAllUsers();
            setDataForClockTableView();
            addActionListenersForCrudButtons(clockDeleteButton);
            addActionListenersForCrudButtons(clockEditButton);
            addToggleGroupForRadioButtons();
        }
        else{
            listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));

            tableUserLabel.setText("Clock History This Week for " + currUser.getEmployee().getName());

            setLastActionLabel();
            reloadClockTable();
            setDataForClockTableView();
            addActionListenersForCrudButtons(clockDeleteButton);
            addActionListenersForCrudButtons(clockEditButton);
            addToggleGroupForRadioButtons();
        }

        clockTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedClock = newv;
        });

        setButtonsForOwnerManager();
    }

    @FXML
    public void clockIn(){
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //get the current date
        Date dateNow = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //if a schedule was selected
        if(!(today == null)) {
            if(!(clockRepository.findRecentClockForUser(currentUser) == null)){
                if(!(clockRepository.findRecentClockForUser(currentUser).getPunchOut().equals(Time.valueOf("00:00:00")))){
                    //check if the schedule date is equal to today's date
                    if(sdf.format(today.getScheduleDate()).equals(sdf.format(dateNow))) {

                        Tblclock newClock = new Tblclock();

                        newClock.setPunchIn(java.sql.Time.valueOf(LocalTime.now()));

                        newClock.setPunchOut(java.sql.Time.valueOf("00:00:00"));
                        newClock.setSchedule(today);
                        //newClock.setEmployee(currUser.getEmployee());
                        newClock.setDateCreated(new java.sql.Timestamp(new java.util.Date().getTime()));
                        newClock.setDay(today.getDay());

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
                    ErrorMessages.showErrorMessage("Error!",
                            "Cannot clock-in without clocking out",
                            "You have already clocked in recently. You must clock out before clocking in again.");
                }

            }
            else if (clockRepository.findRecentClockForUser(currentUser) == null){
                //check if the schedule date is equal to today's date
                if(sdf.format(today.getScheduleDate()).equals(sdf.format(dateNow))) {

                    Tblclock newClock = new Tblclock();

                    newClock.setPunchIn(java.sql.Time.valueOf(LocalTime.now()));

                    newClock.setPunchOut(java.sql.Time.valueOf("00:00:00"));
                    newClock.setSchedule(today);
                    //newClock.setEmployee(currUser.getEmployee());
                    newClock.setDateCreated(new java.sql.Timestamp(new java.util.Date().getTime()));
                    newClock.setDay(today.getDay());

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


        }
        else{
            ErrorMessages.showErrorMessage("No Schedule Exists",
                    "No schedules exist for today",
                    "You cannot clock in since you do not have any schedules today.");
        }

    }

    @FXML
    public void clockOut(){
        //get the current date
        Date dateNow = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if(!(today == null)) {

            //check if the schedule date is equal to today's date
            if(sdf.format(today.getScheduleDate()).equals(sdf.format(dateNow))) {

                //finds recent clock record based on selected schedule
                Tblclock newClock2 = clockRepository.findRecentClockForSchedule(today.getScheduleId());

                if(newClock2 != null){
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
                    ErrorMessages.showErrorMessage("No Schedule Found",
                            "There is no schedule to clock out for",
                            "You do not have a recent clock in to clock out for.");
                }

            }
            else{
                ErrorMessages.showErrorMessage("Invalid schedule selected",
                        "Schedule must be equal to today's date",
                        "You cannot clock out for a date earlier than or later than today's date");
            }
        }
        else{
            ErrorMessages.showErrorMessage("No Schedule Exists",
                    "No schedules exist for today",
                    "You cannot clock out since you do not have any schedules today.");
        }

    }

    @FXML
    private void editAddClock(ActionEvent event){
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //Grab the button that was clicked
        clickedButton = (Button) event.getSource();

        //Grabs the text of the button that was clicked
        switch (clickedButton.getText()){
            case "Edit":
                if((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))
                        || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                        && !(selectedClock.getSchedule().getEmployee().getRole().getRoleName().equals("Manager"))))){
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
                                + selectedClock.getSchedule().getEmployee().getName(), "Save");
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
                else{
                    ErrorMessages.showErrorMessage("Insufficient privileges","Cannot edit clock record",
                            "You do not have sufficient privileges to edit this clock record.");
                }
                break;
            case "Add":
                if((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))
                        || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                        && !(selectedClock.getSchedule().getEmployee().getRole().getRoleName().equals("Manager"))))){
                    try {
                        //open the CRUD form
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudClock.fxml"));
                        fxmlLoader.setControllerFactory(springContext::getBean);
                        Parent parent = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setTitle("Add Clock In/Out Record");

                        CrudClockController crudClockController = fxmlLoader.getController();
                        crudClockController.setLabel("Add Clock Record", "Add");
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
                else{
                    ErrorMessages.showErrorMessage("Insufficient privileges","Cannot edit clock record",
                            "You do not have sufficient privileges to edit this clock record.");
                }
                break;
        }




    }

    @FXML
    private void deleteClock(){
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))
                || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                && !(selectedClock.getSchedule().getEmployee().getRole().getRoleName().equals("Manager"))))){
            Tblclock cl = selectedClock;
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            //get the selected entry's user and other info
            String selectedUser = cl.getSchedule().getEmployee().getUser().getUsername();
            String selectedDay = cl.getDay().getDayDesc();
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
                clockService.deleteClock(cl.getClockId());
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
        else{
            ErrorMessages.showErrorMessage("Insufficient privileges","Cannot delete clock record",
                    "You do not have sufficient privileges to delete this clock record.");
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
            String time = timeFormat.format(Tblclock.getValue().getPunchOut());

            if(time.equals("12:00 AM")){
                property.setValue("Not set");
            }
            else{
                property.setValue(time);
            }

            return property;
        });

        //show the users for each clock record using SimpleObjectProperty
        userCol.setCellValueFactory(cl ->
                new SimpleObjectProperty<>(cl.getValue().getSchedule().getEmployee().getName()));
    }

    private void reloadClockTableAllUsers(){
        //reload the table to show all users (only for managers/owner)
        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(true);

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if (currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            if(currentWeekCheck.isSelected()){
                listOfClock.addAll(clockRepository.findClockThisWeekAllUser());
                tableUserLabel.setText("Clock History This Week for All Users");
            }
            else{
                listOfClock.addAll(clockRepository.findAll());
                tableUserLabel.setText("Clock History All Time for All Users");
            }

        }
        else if(currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            if(currentWeekCheck.isSelected()){
                listOfClock.addAll(clockRepository.findClockThisWeekAllUserByRole());
                listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));
                tableUserLabel.setText("Clock History This Week for All Users");
            }
            else{
                listOfClock.addAll(clockRepository.findAllByRole());
                tableUserLabel.setText("Clock History All Time for All Users");
            }
        }

        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
        setDataForClockTableView();
    }

    private void reloadClockTable(){
        //get the current user and create a new availability for them
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        listOfClock.clear();
        clockTable.setItems(listOfClock);
        userCol.setVisible(false);

        if(allTimeCheck.isSelected()){
            listOfClock.addAll(clockRepository.findClockForUser(currentUser));
            tableUserLabel.setText("Clock History All Time for " + currUser.getEmployee().getName());
        }
        else{
            listOfClock.addAll(clockRepository.findClockThisWeekForUser(currentUser));
            tableUserLabel.setText("Clock History This Week for " + currUser.getEmployee().getName());
        }

        filteredListOfClock = new FilteredList<>(listOfClock);
        clockTable.setItems(filteredListOfClock);
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
            reloadClockTableAllUsers();
            setDataForClockTableView();
        }
        else{
            reloadClockTable();
            setDataForClockTableView();
        }
    }

    @FXML
    private void showAllWeeks(){
        //if the user column is visible (which is only for managers/owner)
        if(userCol.isVisible()){
            //reload the table to show all users (only for managers/owner)
            reloadClockTableAllUsers();
            setDataForClockTableView();
        }
        else{
            reloadClockTable();
            setDataForClockTableView();
        }
    }

    private void addToggleGroupForRadioButtons(){
        ToggleGroup toggleGroup = new ToggleGroup();

        currentWeekCheck.setToggleGroup(toggleGroup);
        allTimeCheck.setToggleGroup(toggleGroup);

    }

    private void setButtonsForOwnerManager(){
        String currentUser = LoginController.userStore;

        //if the user is the owner or manager, they can see buttons to edit requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")) {

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
        else if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")){
            //only managers/owner can add, edit, or delete clock history
            clockEditButton.setVisible(true);
            clockDeleteButton.setVisible(true);
            clockAddButton.setVisible(true);
        }
    }

    private void setLastActionLabel(){
        String currentUser = LoginController.userStore;

        //show the last clock in/out action for this user
        if(!(listOfClock.isEmpty())) {
            Tblclock recentClockForUser = clockRepository.findRecentClockForUser(currentUser);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            //get the recent clock in/out details
            String recentDay = recentClockForUser.getDay().getDayDesc();
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
    }
}
