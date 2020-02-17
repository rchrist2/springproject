package myproject.controllers;

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
    public TableColumn<Tbltimeoff, java.sql.Time> beginTimeCol;

    @FXML
    public TableColumn<Tbltimeoff, java.sql.Time> endTimeCol;

    @FXML
    public TableColumn<Tbltimeoff, String> approveTimeOffCol;

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
    public TextArea reasonInput;

    public ObservableList<Tblschedule> scheduleData = FXCollections.observableArrayList();

    private ObservableList<Tbltimeoff> listOfTimeOffs;
    private FilteredList<Tbltimeoff> filteredListOfTimeOff;

    private ObservableList<Integer> hrList = FXCollections.observableArrayList();
    private FilteredList<Integer> filteredHrList;

    private ObservableList<Integer> minList = FXCollections.observableArrayList();
    private FilteredList<Integer> filteredMinList;

    public Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        tableUserLabel.setText("Time Off Requests for " + currentUser);

        //Initialize the observable list and add all the time offs to the list
        listOfTimeOffs = FXCollections.observableArrayList();
        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));

        scheduleData.addAll(scheduleRepository.findScheduleForUser(currentUser));
        scheduleList.setItems(scheduleData);

        //TODO allow user to select AM/PM or just use 24 hour clock
        List<Integer> hrRange = IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList());
        List<Integer> minRange = IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList());

        hrList.addAll(hrRange);
        minList.addAll(minRange);

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
        newTimeOff.setBeginTimeOffDate(Time.valueOf(beginHrList.getSelectionModel().getSelectedItem().toString()
        + ":" + beginMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
        newTimeOff.setEndTimeOffDate(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                + ":" + endMinList.getSelectionModel().getSelectedItem().toString() + ":00"));
        newTimeOff.setApproved(false);
        newTimeOff.setReasonDesc(reasonInput.getText());
        newTimeOff.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

        timeOffRepository.save(newTimeOff);

        reloadTimeOffTableView();
    }

    private void setDataForTimeOffTableView(){
        //TODO Show column time values as AM/PM
        scheduleDateCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        beginTimeCol.setCellValueFactory(new PropertyValueFactory<>("beginTimeOffDate"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTimeOffDate"));
        approveTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("approved"));
    }

    private void reloadTimeOffTableView(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        listOfTimeOffs.clear();

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));
        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        List<Integer> hrRange = IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList());
        List<Integer> minRange = IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList());

        hrList.addAll(hrRange);
        filteredHrList = new FilteredList<>(hrList);

        minList.addAll(minRange);
        filteredMinList = new FilteredList<>(minList);

        beginHrList.setItems(filteredHrList);
        beginMinList.setItems(filteredMinList);
        endHrList.setItems(filteredHrList);
        endMinList.setItems(filteredMinList);

        timeOffTable.setItems(filteredListOfTimeOff);

    }

}
