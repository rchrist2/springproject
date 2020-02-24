package myproject.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myproject.ErrorMessages;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.TimeOffRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Component;

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

    @Autowired
    private EmployeeRepository employeeRepository;

    @FXML
    private Pane formPane;

    @FXML
    private Pane optionsPane;

    @FXML
    private Pane optionsPane2;

    @FXML
    private Pane optionsPane3;

    @FXML
    private Pane optionsPane4;

    @FXML
    public Label tableUserLabel;

    @FXML
    private Button submitRequestButton;

    @FXML
    private Button timeOffDeleteButton;

    @FXML
    private Button timeOffEditButton;

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
    public ComboBox<Integer> beginHrList;
    /*@FXML
    public ComboBox<Integer> beginMinList;*/
    @FXML
    public ComboBox<Integer> endHrList;
    /*@FXML
    public ComboBox<Integer> endMinList;*/
    @FXML
    public ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;

    @FXML
    public TextArea reasonInput;

    public ObservableList<Tblschedule> scheduleData;

    private ObservableList<Tbltimeoff> listOfTimeOffs;
    private FilteredList<Tbltimeoff> filteredListOfTimeOff;

    private ObservableList<Integer> hrList;

    //private ObservableList<Integer> minList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    public Tbltimeoff selectedTimeOff;

    public static String selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        tableUserLabel.setText("Time Off Requests for " + currentUser);

        //Initialize the observable list and add all the time offs to the list
        listOfTimeOffs = FXCollections.observableArrayList();
        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));

        //initialize the schedule dates for the current user
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));
        scheduleList.setItems(scheduleData);

        hrList = FXCollections.observableArrayList();
        //minList = FXCollections.observableArrayList();

        hrList.addAll(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));
        //minList.addAll(IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList()));

        //fill the hour, minute, and AM/PM comboboxes with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        beginHrList.setItems(hrList);
        //beginMinList.setItems(minList);
        endHrList.setItems(hrList);
        //endMinList.setItems(minList);

        reloadTimeOffTableView();
        setDataForTimeOffTableView();
        addActionListenersForCrudButtons(timeOffDeleteButton);
        addActionListenersForCrudButtons(timeOffEditButton);

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
            selectedUser = selectedTimeOff.getSchedule().getEmployee().getUser().getUsername();
        });

        //if the user is the owner or manager, they can see buttons to approve requests
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
        || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")){
            //declare variables
            Button showAllUser = new Button();
            Button showThisUser = new Button();
            Button approveRequest = new Button();
            Button disapproveRequest = new Button();

            //add buttons to panes
            optionsPane.getChildren().add(showAllUser);
            optionsPane2.getChildren().add(showThisUser);
            optionsPane3.getChildren().add(approveRequest);
            optionsPane4.getChildren().add(disapproveRequest);

            //set style and action for approving
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

            });

            //set up other buttons for showing all users or current user
            showThisUser.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            showThisUser.setText("Current User");
            showThisUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showThisUser.setOnAction(event ->{
                reloadTimeOffTableView();
                setDataForTimeOffTableView();

            });

            showAllUser.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
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

    @FXML
    private void submitTimeOffRequest(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        Tbltimeoff newTimeOff = new Tbltimeoff();

        //WIP verifying valid time
        /*if(!(beginHrList.getSelectionModel().getSelectedItem() > endHrList.getSelectionModel().getSelectedItem() &&
                beginPMList.getSelectionModel().getSelectedItem().equals("PM"))
        && !(endHrList.getSelectionModel().getSelectedItem() < beginHrList.getSelectionModel().getSelectedItem() &&
                beginPMList.getSelectionModel().getSelectedItem().equals("AM"))) {*/

            //convert combobox values to 24 hour clock depending if AM or PM was selected
            if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {
                newTimeOff.setBeginTimeOffDate(Time.valueOf(beginHrList.getSelectionModel().getSelectedItem().toString()
                        + ":00:00"));
            } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {
                newTimeOff.setBeginTimeOffDate(Time.valueOf((beginHrList.getSelectionModel().getSelectedItem() + 12)
                        + ":00:00"));
            }

            if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {
                newTimeOff.setEndTimeOffDate(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                        + ":00:00"));
            } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {
                newTimeOff.setEndTimeOffDate(Time.valueOf((endHrList.getSelectionModel().getSelectedItem() + 12)
                        + ":00:00"));
            }

            newTimeOff.setApproved(false);
            newTimeOff.setReasonDesc(reasonInput.getText());
            newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

            timeOffRepository.save(newTimeOff);

            reloadTimeOffTableView();

            /*else{
            ErrorMessages.showErrorMessage("Invalid hour range","Please check hour range",
                    "Start time begins after end time or end time begins before start time");
        }*/
        }

    @FXML
    private void editTimeOff(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudTimeOff.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Edit Time Off Request");

            CrudTimeOffController crudTimeOffController = fxmlLoader.getController();
            crudTimeOffController.setLabel("Edit Time Off for "
                    + selectedTimeOff.getSchedule().getEmployee().getUser().getUsername());
            crudTimeOffController.setTimeOff(selectedTimeOff);
            crudTimeOffController.setController(this);

            stage.setScene(new Scene(parent));

            stage.showAndWait();
            //reload table w/ all users if the user column is visible (only visible if all users are shown)
            if(userCol.isVisible()){
                reloadTimeOffTableViewAllUsers();
                setDataForTimeOffTableView();
            }
            else{
                reloadTimeOffTableView();
                setDataForTimeOffTableView();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTimeOff(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        Tbltimeoff tf = selectedTimeOff;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Time Off Request");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You are about to delete time off request for: " + currentUser + " on " +
                tf.getSchedule().getScheduleDate() + " " +
                timeFormat.format(tf.getBeginTimeOffDate()) + " to " + timeFormat.format(tf.getEndTimeOffDate()));

        Optional<ButtonType> choice = alert.showAndWait();
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
        }
        else{
            reloadTimeOffTableView();
            setDataForTimeOffTableView();
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

        approveTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("approved"));
        reasonTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("reasonDesc"));

        //show the users for each time off request using SimpleObjectProperty
        userCol.setCellValueFactory(tf ->
                new SimpleObjectProperty<>(tf.getValue().getSchedule().getEmployee().getUser().getUsername()));
    }

    private void reloadTimeOffTableViewAllUsers(){
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
        listOfTimeOffs.clear();
        timeOffTable.setItems(listOfTimeOffs);

        //will remove the user column if it was visible
        userCol.setVisible(false);

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(filteredListOfTimeOff);

        //set this back to current user in case All Users were shown
        tableUserLabel.setText("Time Off Requests for " + currentUser);

    }

    private void addActionListenersForCrudButtons(Button button){
        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                button.setDisable(false);
            }
        });
    }

}
