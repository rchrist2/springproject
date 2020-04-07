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
import javafx.scene.layout.AnchorPane;
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
import myproject.services.TimeOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    @Autowired
    private TimeOffService timeOffService;

    @FXML
    private Pane optionsPane, optionsPane2,
            datePane, movePane, tablePane, fullFormPane;

    @FXML
    private AnchorPane timeAnchor;

    @FXML
    private Label tableUserLabel;

    @FXML
    private Button timeOffDeleteButton;

    @FXML
    private Button timeOffEditButton;

    @FXML
    private Button submitRequestButton;

    @FXML
    private RadioButton dayOffCheck;

    @FXML
    private RadioButton noSchedCheck;

    @FXML
    private TableView<Tbltimeoff> timeOffTable;

    @FXML
    private TableColumn<Tbltimeoff, String> userCol;

    @FXML
    private TableColumn<Tbltimeoff, String> scheduleDateCol;

    @FXML
    private TableColumn<Tbltimeoff, String> beginTimeCol;

    @FXML
    public TableColumn<Tbltimeoff, String> endTimeCol;

    @FXML
    private TableColumn<Tbltimeoff, String> approveTimeOffCol;

    @FXML
    private TableColumn<Tbltimeoff, String> dayOffCol;

    @FXML
    private TableColumn<Tbltimeoff, String> reasonTimeOffCol;

    @FXML
    private ComboBox<Tblschedule> scheduleList;

    @FXML
    private DatePicker beginDate;
    @FXML
    private DatePicker endDate;

    @FXML
    private TextField reasonInput;

    private ObservableList<Tblschedule> scheduleData;
    private FilteredList<Tblschedule> filteredScheduleData;

    private ObservableList<Tbltimeoff> listOfTimeOffs;
    private FilteredList<Tbltimeoff> filteredListOfTimeOff;

    private ObservableList<String> hrList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //Initialize the observable lists
        listOfTimeOffs = FXCollections.observableArrayList();
        scheduleData = FXCollections.observableArrayList();

        //if user is owner, don't show request time off forms
        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());

            tableUserLabel.setText("Time Off Requests for All Users");

            timeAnchor.getChildren().remove(fullFormPane);
            timeAnchor.getChildren().remove(tablePane);
            movePane.getChildren().add(tablePane);
            tablePane.setLayoutX(68);
            tablePane.setLayoutY(30);
            timeOffTable.setPrefHeight(537);
            movePane.setVisible(true);

            //reload table, set data, and add listeners to buttons
            reloadTimeOffTableViewAllUsers();
            setDataForTimeOffTableView();
            addActionListenersForCrudButtons(timeOffDeleteButton);
            addActionListenersForCrudButtons(timeOffEditButton);
        }
        else{
            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));

            tableUserLabel.setText("Time Off Requests for " + currUser.getEmployee().getName());

            //reload table, set data, and add listeners to buttons
            reloadTimeOffTableView();
            setDataForTimeOffTableView();
            reloadScheduleList();
            addActionListenersForCrudButtons(timeOffDeleteButton);
            addActionListenersForCrudButtons(timeOffEditButton);
            addToggleGroupForRadioButtons();
        }

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });

        //set "Request Day Off" to selected by default
        dayOffCheck.setSelected(true);

        //disable past dates for datepickers
        beginDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        endDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        setButtonsForManagerOwner();

    }

    @FXML
    private void submitTimeOffRequestWithoutSchedule(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if (!(beginDate.getValue() == null
                || endDate.getValue() == null
                || reasonInput.getText().trim().isEmpty())) {
            Tbltimeoff newTimeOff = new Tbltimeoff();

            newTimeOff.setSchedule(null);
            newTimeOff.setBeginTimeOffDate(Date.valueOf(beginDate.getValue()));
            newTimeOff.setEndTimeOffDate(Date.valueOf(endDate.getValue()));
            newTimeOff.setApproved(false);
            newTimeOff.setReasonDesc(reasonInput.getText());
            //newTimeOff.setDayOff(true);
            newTimeOff.setDay(null);
            newTimeOff.setEmployee(currUser.getEmployee());

            if (newTimeOff.getBeginTimeOffDate().before(newTimeOff.getEndTimeOffDate())
                    && newTimeOff.getEndTimeOffDate().after(newTimeOff.getBeginTimeOffDate())
                    || newTimeOff.getBeginTimeOffDate().equals(newTimeOff.getEndTimeOffDate())){
                timeOffRepository.save(newTimeOff);

                reloadTimeOffTableView();
            }
            else{
                ErrorMessages.showErrorMessage("Invalid date values", "Date range is invalid",
                        "Please edit the date range for this time off request.");
            }

        }
        else {
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }
    }

    @FXML
    private void submitTimeOffRequestWithSchedule(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //check if any of the fields are empty
        if (!(scheduleList.getSelectionModel().isEmpty()
                || reasonInput.getText().trim().isEmpty())) {
            Tbltimeoff newTimeOff = new Tbltimeoff();

            newTimeOff.setBeginTimeOffDate(scheduleList.getSelectionModel().getSelectedItem().getScheduleDate());
            newTimeOff.setEndTimeOffDate(scheduleList.getSelectionModel().getSelectedItem().getScheduleDate());
            newTimeOff.setApproved(false);
            newTimeOff.setReasonDesc(reasonInput.getText());
            newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
            //newTimeOff.setDayOff(true);
            newTimeOff.setDay(scheduleList.getSelectionModel().getSelectedItem().getDay());
            newTimeOff.setEmployee(currUser.getEmployee());

            //check if the time range is valid
            if(scheduleList.getSelectionModel().getSelectedItem().getTimeOffs() == null){
                timeOffRepository.save(newTimeOff);
            }
            else{
                ErrorMessages.showErrorMessage("Cannot add request to selected schedule",
                        "This schedule already has a time off request",
                        "Please select a schedule you have not made a time off request for.");
            }

            reloadTimeOffTableView();
        }
        else {
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }

    }

    @FXML
    private void editTimeOff(){
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //can only edit an approved request if you are manager or owner
        if((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")
                && selectedTimeOff.isApproved()  || !(selectedTimeOff.isApproved()))
                || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                && (selectedTimeOff.isApproved()  || !(selectedTimeOff.isApproved()))
                && !(selectedTimeOff.getEmployee().getRole().getRoleName().equals("Manager"))))) {

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
                            + selectedTimeOff.getEmployee().getName());
                    crudTimeOffController.setTimeOff(selectedTimeOff);
                    crudTimeOffController.setController(this);

                    stage.setScene(new Scene(parent));

                    stage.showAndWait();
                    parent.requestFocus();

                    //reload table w/ all users if the user column is visible (only visible if all users are shown)
                    if (userCol.isVisible()) {
                        reloadTimeOffTableViewAllUsers();
                        setDataForTimeOffTableView();
                        reloadScheduleList();
                        resetButtons();
                    } else {
                        reloadTimeOffTableView();
                        setDataForTimeOffTableView();
                        reloadScheduleList();
                        resetButtons();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        else{
            ErrorMessages.showErrorMessage("Insufficient privileges to edit","Cannot edit an approved request",
                        "You do not have the privileges to edit an already approved request");
        }
    }

    @FXML
    private void deleteTimeOff(){
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //get the selected entry from the table
        Tbltimeoff tf = selectedTimeOff;

        //can only delete an approved request if you are owner
        if((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")
                && selectedTimeOff.isApproved()  || !(selectedTimeOff.isApproved()))
                || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                && (selectedTimeOff.isApproved()  || !(selectedTimeOff.isApproved()))
                && !(selectedTimeOff.getEmployee().getRole().getRoleName().equals("Manager"))))) {

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

                //get the selected entry's user
                String selectedUser = tf.getEmployee().getUser().getUsername();

                //ask the user if they are sure about the deletion
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Time Off Request");
                alert.setHeaderText("Are you sure?");
                alert.setContentText("You are about to delete time off request for: " + selectedUser + " on " +
                        tf.getBeginTimeOffDate() + " to " + tf.getEndTimeOffDate() + " " +
                        timeFormat.format(tf.getBeginTimeOffDate()) + " to " + timeFormat.format(tf.getEndTimeOffDate()));

                Optional<ButtonType> choice = alert.showAndWait();

                //if the user clicks "OK", delete the entry
                if (choice.get() == ButtonType.OK) {
                    timeOffService.deleteTimeOff(tf.getTimeOffId());
                } else {
                    System.out.println("Delete cancelled");
                }

                //reload table with all users if the user column is visible
                if (userCol.isVisible()) {
                    reloadTimeOffTableViewAllUsers();
                    setDataForTimeOffTableView();
                    resetButtons();
                    reloadScheduleList();
                } else {
                    reloadTimeOffTableView();
                    setDataForTimeOffTableView();
                    resetButtons();
                    reloadScheduleList();
                }
            }
        else {
            ErrorMessages.showErrorMessage("Insufficient privileges to delete","Cannot delete an approved request",
                        "You do not have the privileges to delete an already approved request");
        }
    }

    private void setDataForTimeOffTableView(){
        //scheduleDateCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        scheduleDateCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            if(Tbltimeoff.getValue().getSchedule() == null){
                property.setValue("Not set");
            }
            else{
                property.setValue(String.valueOf(Tbltimeoff.getValue().getSchedule()));
            }

            return property;
        });

        //using lambda to display with AM and PM
        beginTimeCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
            property.setValue(timeFormat.format(Tbltimeoff.getValue().getBeginTimeOffDate()));
            return property;
        });

        endTimeCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
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

        //change "true" and "false" to "Yes" and "No" in the Approved column
        /*dayOffCol.setCellValueFactory(Tbltimeoff -> {
            SimpleStringProperty property = new SimpleStringProperty();
            if(Tbltimeoff.getValue().isDayOff()){
                property.setValue("Yes");
            }
            else{
                property.setValue("No");
            }
            return property;
        });*/

        reasonTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("reasonDesc"));

        //show the users for each time off request using SimpleObjectProperty
        userCol.setCellValueFactory(tf ->
                new SimpleObjectProperty<>(tf.getValue().getEmployee().getName()));
    }

    private void reloadTimeOffTableViewAllUsers(){
        //reload the table to show all users (only for managers/owner)
        listOfTimeOffs.clear();
        timeOffTable.setItems(listOfTimeOffs);
        userCol.setVisible(true);

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if (currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());
        }
        else if(currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByRole());
            listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
        }

        //filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(listOfTimeOffs);
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
        //filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(listOfTimeOffs);

        //set this back to current user in case All Users were shown
        tableUserLabel.setText("Time Off Requests for " + currUser.getEmployee().getName());
    }

    //used in case the schedule time range is updated after approval
    private void reloadScheduleList(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        scheduleData.clear();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));

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

    private void setButtonsForManagerOwner(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        //if the user is the owner or manager, they can see buttons to approve requests or show all users
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")){
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
            System.out.println("User is not manager");
        }
    }

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
        noSchedCheck.setToggleGroup(toggleGroup);

    }

    @FXML
    private void dayOffChecked(){
        datePane.setVisible(false);
        submitRequestButton.setVisible(true);

        scheduleList.setDisable(false);
    }

    @FXML
    private void noSchedChecked(){
        datePane.setVisible(true);
        submitRequestButton.setVisible(false);

        scheduleList.setDisable(true);
    }

}
