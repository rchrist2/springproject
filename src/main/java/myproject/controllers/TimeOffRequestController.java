package myproject.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class TimeOffRequestController implements Initializable {

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
    public Label tableUserLabel;

    @FXML
    public TableView<Tbltimeoff> timeOffTable;

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
    @FXML
    public ComboBox<Integer> beginMinList;
    @FXML
    public ComboBox<Integer> endHrList;
    @FXML
    public ComboBox<Integer> endMinList;
    @FXML
    public ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;

    @FXML
    public TextArea reasonInput;

    public ObservableList<Tblschedule> scheduleData = FXCollections.observableArrayList();

    private ObservableList<Tbltimeoff> listOfTimeOffs;
    private FilteredList<Tbltimeoff> filteredListOfTimeOff;

    private ObservableList<Integer> hrList =
            FXCollections.observableArrayList(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));
    private FilteredList<Integer> filteredHrList;

    private ObservableList<Integer> minList =
            FXCollections.observableArrayList(IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList()));
    private FilteredList<Integer> filteredMinList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));
    private FilteredList<String> filteredPMList;

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
        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));
        scheduleList.setItems(scheduleData);

        //fill the hour, minute, and AM/PM comboboxes with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        beginHrList.setItems(hrList);
        beginMinList.setItems(minList);
        endHrList.setItems(hrList);
        endMinList.setItems(minList);

        reloadTimeOffTableView();
        setDataForTimeOffTableView();

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });
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
                    + ":" + beginMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
        }
        else if(beginPMList.getSelectionModel().getSelectedItem().equals("PM")){
            newTimeOff.setBeginTimeOffDate(Time.valueOf((beginHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":" + beginMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
        }

        if(endPMList.getSelectionModel().getSelectedItem().equals("AM")){
            newTimeOff.setEndTimeOffDate(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                    + ":" + endMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
        }
        else if(endPMList.getSelectionModel().getSelectedItem().equals("PM")){
            newTimeOff.setEndTimeOffDate(Time.valueOf((endHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":" + endMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
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
    }

    private void reloadTimeOffTableView(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        listOfTimeOffs.clear();

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        /*List<String> pmRange = Arrays.asList("AM","PM");
        pmList.addAll(pmRange);
        filteredPMList = new FilteredList<>(pmList);
        beginPMList.setItems(filteredPMList);
        endPMList.setItems(filteredPMList);

        List<Integer> hrRange = IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList());
        List<Integer> minRange = IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList());

        hrList.addAll(hrRange);
        filteredHrList = new FilteredList<>(hrList);

        minList.addAll(minRange);
        filteredMinList = new FilteredList<>(minList);

        beginHrList.setItems(filteredHrList);
        beginMinList.setItems(filteredMinList);
        endHrList.setItems(filteredHrList);
        endMinList.setItems(filteredMinList);*/

        timeOffTable.setItems(filteredListOfTimeOff);

    }

}
