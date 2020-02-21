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
import javafx.scene.layout.Pane;
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

import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
    public Label tableUserLabel;

    @FXML
    private Button submitRequestButton;

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

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });

        //if the user is the owner or manager, they can see buttons to approve requests
        if(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Manager")
        || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleDesc().equals("Owner")){
            //declare variables
            Button showAllUser = new Button();
            Button showThisUser = new Button();
            Button approveRequest = new Button();

            //add buttons to panes
            optionsPane.getChildren().add(showAllUser);
            optionsPane2.getChildren().add(showThisUser);
            optionsPane3.getChildren().add(approveRequest);

            //set style and action for approving
            approveRequest.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            approveRequest.setText("Approve Request");
            approveRequest.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            approveRequest.setOnAction(event ->{
                System.out.println("You clicked the Approve Request button.");

            });

            //set up other buttons for showing all users or current user
            showThisUser.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            showThisUser.setText("Show Current User");
            showThisUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showThisUser.setOnAction(event ->{
                listOfTimeOffs.clear();
                timeOffTable.setItems(listOfTimeOffs);
                userCol.setVisible(false);

                listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
                filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
                timeOffTable.setItems(filteredListOfTimeOff);
                tableUserLabel.setText("Time Off Requests for " + currentUser);
                setDataForTimeOffTableView();

            });

            showAllUser.setPrefSize(submitRequestButton.getPrefWidth(), submitRequestButton.getPrefHeight());
            showAllUser.setText("Show All Users");
            showAllUser.setStyle("-fx-text-fill:white; -fx-background-color: #39a7c3;");
            showAllUser.setOnAction(event -> {
                listOfTimeOffs.clear();
                timeOffTable.setItems(listOfTimeOffs);
                userCol.setVisible(true);

                listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());
                filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);
                timeOffTable.setItems(filteredListOfTimeOff);
                tableUserLabel.setText("Time Off Requests for All Users");
                setDataForTimeOffTableView();
            });
        }
        else{
            //user does not have privileges to approve requests
            System.out.println("User is not owner or manager");
        }
    }

    @FXML
    public void submitTimeOffRequest(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        Tbltimeoff newTimeOff = new Tbltimeoff();

        //convert combobox values to 24 hour clock depending if AM or PM was selected
        if(beginPMList.getSelectionModel().getSelectedItem().equals("AM"))
        {
            newTimeOff.setBeginTimeOffDate(Time.valueOf(beginHrList.getSelectionModel().getSelectedItem().toString()
                    + ":00:00"));
        }
        else if(beginPMList.getSelectionModel().getSelectedItem().equals("PM")){
            newTimeOff.setBeginTimeOffDate(Time.valueOf((beginHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":00:00"));
        }

        if(endPMList.getSelectionModel().getSelectedItem().equals("AM")){
            newTimeOff.setEndTimeOffDate(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                    + ":00:00"));
        }
        else if(endPMList.getSelectionModel().getSelectedItem().equals("PM")){
            newTimeOff.setEndTimeOffDate(Time.valueOf((endHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":00:00"));
        }

        newTimeOff.setApproved(false);
        newTimeOff.setReasonDesc(reasonInput.getText());
        newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

        timeOffRepository.save(newTimeOff);

        reloadTimeOffTableView();
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

}
