package myproject.controllers.Dashboard.TimeOff;

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
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.TimeOffRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TimeOffController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @FXML
    private Pane optionsPane;

    @FXML
    private Pane optionsPane2;

    @FXML
    private Pane timePane;

    /*@FXML
    private Pane optionsPane3;

    @FXML
    private Pane optionsPane4;*/

    @FXML
    public Label tableUserLabel;

    @FXML
    private Button timeOffDeleteButton;

    @FXML
    private Button timeOffEditButton;

    @FXML
    private Button submitRequestButton;

    @FXML
    private Button submitRequestButton1;

    @FXML
    public RadioButton dayOffCheck;

    @FXML
    public RadioButton changeAvailCheck;

    /*@FXML
    public RadioButton allTimeCheck;

    @FXML
    public RadioButton currentWeekCheck;*/

    @FXML
    public TableView<Tbltimeoff> timeOffTable;

    @FXML
    private TableColumn<Tbltimeoff, String> userCol;

    @FXML
    public TableColumn<Tbltimeoff, java.sql.Date> scheduleDateCol;

    @FXML
    public TableColumn<Tbltimeoff, String> beginTimeCol;

    @FXML
    public TableColumn<Tbltimeoff, String> endTimeCol;

    @FXML
    public TableColumn<Tbltimeoff, String> approveTimeOffCol;

    @FXML
    public TableColumn<Tbltimeoff, String> reasonTimeOffCol;

    @FXML
    public ComboBox<Tblschedule> scheduleList;
    @FXML
    public ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;

    @FXML
    public Spinner<Integer> beginHrList;
    @FXML
    public Spinner<Integer> endHrList;

    @FXML
    public TextField reasonInput;

    public ObservableList<Tblschedule> scheduleData;
    private FilteredList<Tblschedule> filteredScheduleData;

    private ObservableList<Tbltimeoff> listOfTimeOffs;
    private FilteredList<Tbltimeoff> filteredListOfTimeOff;

    private ObservableList<Integer> hrList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    public Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);
        tableUserLabel.setText("Time Off Requests for " + currUser.getEmployee().getName());

        //Initialize the observable lists
        listOfTimeOffs = FXCollections.observableArrayList();
        scheduleData = FXCollections.observableArrayList();

        //reload table, set data, and add listeners to buttons
        setDataForHourPMLists();
        reloadTimeOffTableView();
        setDataForTimeOffTableView();
        reloadScheduleList();
        addActionListenersForCrudButtons(timeOffDeleteButton);
        addActionListenersForCrudButtons(timeOffEditButton);
        addToggleGroupForRadioButtons();

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });

        //set "Request Day Off" to selected by default
        dayOffCheck.setSelected(true);

        setButtonsForManagerOwner();

    }

    @FXML
    private void submitTimeOffRequestWithoutTime(){
        if (!(scheduleList.getSelectionModel().isEmpty()
                || reasonInput.getText().trim().isEmpty())) {
            Tbltimeoff newTimeOff = new Tbltimeoff();

            newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
            newTimeOff.setBeginTimeOffDate(scheduleList.getSelectionModel().getSelectedItem().getScheduleTimeBegin());
            newTimeOff.setEndTimeOffDate(scheduleList.getSelectionModel().getSelectedItem().getScheduleTimeEnd());
            newTimeOff.setApproved(false);
            newTimeOff.setReasonDesc(reasonInput.getText());

            timeOffRepository.save(newTimeOff);

            reloadTimeOffTableView();
        } else {
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }
    }

    @FXML
    private void submitTimeOffRequestWithTime(){
        //check if any of the fields are empty
        if (!(scheduleList.getSelectionModel().isEmpty() || beginHrList.getValue() == 0
                || beginPMList.getSelectionModel().isEmpty() || endHrList.getValue() == 0
                || endPMList.getSelectionModel().isEmpty() || reasonInput.getText().trim().isEmpty())) {
            Tbltimeoff newTimeOff = new Tbltimeoff();

            //convert combobox values to 24 hour clock depending if AM or PM was selected
            if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                //if the beginning hour is 12 am
                if (beginHrList.getValue().toString().equals("12")) {
                    newTimeOff.setBeginTimeOffDate(Time.valueOf("00"
                            + ":00:00"));
                } else {
                    newTimeOff.setBeginTimeOffDate(Time.valueOf(beginHrList.getValue().toString()
                            + ":00:00"));
                }
            } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                //if the beginning hour is 12 pm
                if (beginHrList.getValue().toString().equals("12")) {
                    newTimeOff.setBeginTimeOffDate(Time.valueOf("12"
                            + ":00:00"));
                } else {
                    newTimeOff.setBeginTimeOffDate(Time.valueOf((beginHrList.getValue() + 12)
                            + ":00:00"));
                }
            }

            if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                //if the ending hour is 12 am
                if (endHrList.getValue().toString().equals("12")) {
                    newTimeOff.setEndTimeOffDate(Time.valueOf("00"
                            + ":00:00"));
                } else {
                    newTimeOff.setEndTimeOffDate(Time.valueOf(endHrList.getValue().toString()
                            + ":00:00"));
                }
            } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                //if the ending hour is 12 pm
                if (endHrList.getValue().toString().equals("12")) {
                    newTimeOff.setEndTimeOffDate(Time.valueOf("12"
                            + ":00:00"));
                } else {
                    newTimeOff.setEndTimeOffDate(Time.valueOf((endHrList.getValue() + 12)
                            + ":00:00"));
                }
            }

            newTimeOff.setApproved(false);
            newTimeOff.setReasonDesc(reasonInput.getText());
            newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

            //check if the time range is valid
            if (newTimeOff.getBeginTimeOffDate().before(newTimeOff.getEndTimeOffDate())
                    && newTimeOff.getEndTimeOffDate().after(newTimeOff.getBeginTimeOffDate())) {
                timeOffRepository.save(newTimeOff);
            } else {
                ErrorMessages.showErrorMessage("Invalid time values", "Time range for time" +
                        " off request is invalid", "Please edit time range for this time off request");
            }

            reloadTimeOffTableView();
        } else {
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }

    }

    @FXML
    private void editTimeOff(){
        try {
            //open the CRUD form
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudTimeOff.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Edit Time Off Request");

            CrudTimeOffController crudTimeOffController = fxmlLoader.getController();
            crudTimeOffController.setLabel("Edit Time Off for "
                    + selectedTimeOff.getSchedule().getEmployee().getName());
            crudTimeOffController.setTimeOff(selectedTimeOff);
            crudTimeOffController.setController(this);

            stage.setScene(new Scene(parent));

            stage.showAndWait();
            parent.requestFocus();

            //reload table w/ all users if the user column is visible (only visible if all users are shown)
            if(userCol.isVisible()){
                reloadTimeOffTableViewAllUsers();
                setDataForTimeOffTableView();
                reloadScheduleList();
                resetButtons();
            }
            else{
                reloadTimeOffTableView();
                setDataForTimeOffTableView();
                reloadScheduleList();
                resetButtons();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTimeOff(){
        //get the selected entry from the table
        Tbltimeoff tf = selectedTimeOff;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        //get the selected entry's user
        String selectedUser = tf.getSchedule().getEmployee().getUser().getUsername();

        //ask the user if they are sure about the deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Time Off Request");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You are about to delete time off request for: " + selectedUser + " on " +
                tf.getSchedule().getScheduleDate() + " " +
                timeFormat.format(tf.getBeginTimeOffDate()) + " to " + timeFormat.format(tf.getEndTimeOffDate()));

        Optional<ButtonType> choice = alert.showAndWait();

        //if the user clicks "OK", delete the entry
        if(choice.get() == ButtonType.OK) {
            timeOffRepository.delete(tf);
        }
        else{
            System.out.println("Delete cancelled");
        }

        //reload table with all users if the user column is visible
        if(userCol.isVisible()){
            reloadTimeOffTableViewAllUsers();
            setDataForTimeOffTableView();
            resetButtons();
        }
        else{
            reloadTimeOffTableView();
            setDataForTimeOffTableView();
            resetButtons();
        }
    }


    private void setDataForTimeOffTableView(){
        scheduleDateCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));

        //using lambda to display with AM and PM
        beginTimeCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            property.setValue(timeFormat.format(Tbltimeoff.getValue().getBeginTimeOffDate()));
            return property;
        });

        endTimeCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            property.setValue(timeFormat.format(Tbltimeoff.getValue().getEndTimeOffDate()));
            return property;
        });

        //change "true" and "false" to "Yes" and "No" in the Approved column
        approveTimeOffCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            if(Tbltimeoff.getValue().isApproved()){
                property.setValue("Yes");
            }
            else{
                property.setValue("No");
            }
            return property;
        });

        reasonTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("reasonDesc"));

        //show the users for each time off request using SimpleObjectProperty
        userCol.setCellValueFactory(tf ->
                new SimpleObjectProperty<>(tf.getValue().getSchedule().getEmployee().getUser().getUsername()));
    }

    private void reloadTimeOffTableViewAllUsers(){
        //reload the table to show all users (only for managers/owner)
        listOfTimeOffs.clear();
        timeOffTable.setItems(listOfTimeOffs);
        userCol.setVisible(true);

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());
        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
        timeOffTable.setItems(filteredListOfTimeOff);
        tableUserLabel.setText("Time Off Requests for All Users");
        setDataForTimeOffTableView();
    }

    private void reloadTimeOffTableView(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        listOfTimeOffs.clear();
        timeOffTable.setItems(listOfTimeOffs);

        //will remove the user column if it was visible
        userCol.setVisible(false);

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(filteredListOfTimeOff);

        //set this back to current user in case All Users were shown
        tableUserLabel.setText("Time Off Requests for " + currUser.getEmployee().getName());
    }

    //used in case the schedule time range is updated after approval
    private void reloadScheduleList(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        scheduleData.clear();
        scheduleData.addAll(scheduleRepository.findScheduleThisWeekForUser(currentUser));

        if(!(scheduleData.isEmpty())){
            scheduleList.setItems(scheduleData);
            scheduleList.setPromptText("Select Schedule");

            //used to fix bug where prompt text disappears
            scheduleList.setButtonCell(new ListCell<Tblschedule>() {
                @Override
                protected void updateItem(Tblschedule item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty || item == null) {
                        setText("Select Schedule");
                    } else {
                        setText(String.valueOf(item));
                    }
                }
            });
        }
        else{
            scheduleList.setPromptText("No Schedules Exist");
        }
    }

    private void setDataForHourPMLists(){
        //make a list of hours from 0 to 12 (0 if they did not select an hour)
        hrList = FXCollections.observableArrayList();
        hrList.addAll(IntStream.rangeClosed(0,12).boxed().collect(Collectors.toList()));

        //fill the hour, minute, and AM/PM comboboxes with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        SpinnerValueFactory<Integer> bHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);
        SpinnerValueFactory<Integer> eHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);

        beginHrList.setValueFactory(bHours);
        endHrList.setValueFactory(eHours);
    }

    private void setButtonsForManagerOwner(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        //if the user is the owner or manager, they can see buttons to approve requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")){
            //declare variables
            Button showAllUser = new Button();
            Button showThisUser = new Button();
            /*Button approveRequest = new Button();
            Button disapproveRequest = new Button();*/

            //add buttons to panes
            optionsPane.getChildren().add(showAllUser);
            optionsPane2.getChildren().add(showThisUser);
            /*optionsPane3.getChildren().add(approveRequest);
            optionsPane4.getChildren().add(disapproveRequest);*/

           /* //set style and action for approving
            approveRequest.setDisable(true);
            addActionListenersForCrudButtons(approveRequest);
            approveRequest.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            approveRequest.setText("Approve");
            approveRequest.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            approveRequest.setOnAction(event ->{
                Tbltimeoff tfApprove = selectedTimeOff;
                selectedTimeOff.setApproved(true);

                timeOffRepository.save(tfApprove);

                //reload table with all users if the user column is visible
                if(userCol.isVisible()){
                    reloadTimeOffTableViewAllUsers();
                    setDataForTimeOffTableView();
                }
                else{
                    reloadTimeOffTableView();
                    setDataForTimeOffTableView();
                }

            });

            //set up button for disapproving
            disapproveRequest.setDisable(true);
            addActionListenersForCrudButtons(disapproveRequest);
            disapproveRequest.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            disapproveRequest.setText("Deny");
            disapproveRequest.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            disapproveRequest.setOnAction(event ->{
                Tbltimeoff tfApprove = selectedTimeOff;
                selectedTimeOff.setApproved(false);

                timeOffRepository.save(tfApprove);

                //reload table w/ all users if the user column is visible (only visible if all users are shown)
                if(userCol.isVisible()){
                    reloadTimeOffTableViewAllUsers();
                    setDataForTimeOffTableView();
                }
                else{
                    reloadTimeOffTableView();
                    setDataForTimeOffTableView();
                }

            });*/

            //set up other buttons for showing all users or current user
            showThisUser.setText("Current User");
            showThisUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showThisUser.setOnAction(event ->{
                reloadTimeOffTableView();
                setDataForTimeOffTableView();
            });

            showAllUser.setText("All Users");
            showAllUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showAllUser.setOnAction(event -> {
                //use function to reload table showing all users
                reloadTimeOffTableViewAllUsers();
                setDataForTimeOffTableView();
            });
        }
        else{
            //user does not have privileges to approve requests
            System.out.println("User is not owner or manager");
        }
    }

    /*@FXML
    private void showCurrentWeek(){
        //if the user column is visible (which is only for managers/owner)
        if(userCol.isVisible()){
            //reload the table to show all users (only for managers/owner)
            listOfTimeOffs.clear();
            timeOffTable.setItems(listOfTimeOffs);

            listOfTimeOffs.addAll(timeOffRepository.findTimeOffThisWeekAllUser());
            filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
            timeOffTable.setItems(filteredListOfTimeOff);
            tableUserLabel.setText("Time Off Requests This Week for All Users");
            setDataForTimeOffTableView();
        }
        else{
            String currentUser = LoginController.userStore;
            Tblusers currUser = userRepository.findUsername(currentUser);

            listOfTimeOffs.clear();
            timeOffTable.setItems(listOfTimeOffs);

            listOfTimeOffs.addAll(timeOffRepository.findTimeOffThisWeekForUser(currentUser));
            filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
            timeOffTable.setItems(filteredListOfTimeOff);
            tableUserLabel.setText("Time Off Requests This Week for " + currUser.getEmployee().getName());
            setDataForTimeOffTableView();
        }
    }

    @FXML
    private void showAllWeeks(){
        //if the user column is visible (which is only for managers/owner)
        if(userCol.isVisible()){
            //reload the table to show all users (only for managers/owner)
            listOfTimeOffs.clear();
            timeOffTable.setItems(listOfTimeOffs);

            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());
            filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
            timeOffTable.setItems(filteredListOfTimeOff);
            tableUserLabel.setText("Time Off Requests All Time for All Users");
            setDataForTimeOffTableView();
        }
        else{
            String currentUser = LoginController.userStore;
            Tblusers currUser = userRepository.findUsername(currentUser);

            listOfTimeOffs.clear();
            timeOffTable.setItems(listOfTimeOffs);

            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
            filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
            timeOffTable.setItems(filteredListOfTimeOff);
            tableUserLabel.setText("Time Off Requests All Time for " + currUser.getEmployee().getName());
            setDataForTimeOffTableView();
        }
    }*/

    private void addActionListenersForCrudButtons(Button button){
        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                button.setDisable(false);
            }
        });
    }

    private void resetButtons(){
        timeOffEditButton.setDisable(true);
        timeOffDeleteButton.setDisable(true);
    }

    private void addToggleGroupForRadioButtons(){
        ToggleGroup toggleGroup = new ToggleGroup();

        dayOffCheck.setToggleGroup(toggleGroup);
        changeAvailCheck.setToggleGroup(toggleGroup);

    }

    @FXML
    private void dayOffChecked(){
        timePane.setVisible(false);
        submitRequestButton.setVisible(true);
    }

    @FXML
    private void changeAvailabilityChecked(){
        timePane.setVisible(true);
        submitRequestButton.setVisible(false);
    }

}
