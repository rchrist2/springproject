package myproject.controllers.Dashboard.ClockInOut;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.controllers.Dashboard.EmployeeManagement.EmployeeRoleUserManagementController;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.Tblclock;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import myproject.repositories.ClockRepository;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CrudClockController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @FXML
    private Label crudLabel;

    @FXML
    private Pane schedulePane;

    @FXML
    private Pane employeePane;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<Tblschedule> scheduleList;

    @FXML
    private ComboBox<Tblemployee> employeeList;

    @FXML
    private Spinner<Integer> beginHrList;
    @FXML
    private Spinner<String> beginMinList;
    @FXML
    public Spinner<Integer> endHrList;
    @FXML
    private Spinner<String> endMinList;
    @FXML
    private ComboBox<String> beginPMList;
    @FXML
    private ComboBox<String> endPMList;

    private ObservableList<Tblschedule> scheduleData;

    private ObservableList<Tblemployee> employeeData;

    private ObservableList<Integer> hrList;

    private ObservableList<String> minList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private ClockInOutController clockController;

    //The clock record returned from the ClockInOutController
    private Tblclock selectedClock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //initialize drop down menus lists
        hrList = FXCollections.observableArrayList();
        minList = FXCollections.observableArrayList();

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            if(ClockInOutController.clickedButton.getText().equals("Add")){
                employeePane.setVisible(true);

                employeeData = FXCollections.observableArrayList();
                employeeData.addAll(employeeRepository.findAllEmployeeByRole());
                employeeList.setItems(employeeData);

                scheduleData = FXCollections.observableArrayList();

                //only show schedule list with the selected employee's schedules (all time)
                employeeList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        scheduleData.clear();
                        scheduleList.getItems().clear();
                        schedulePane.setVisible(true);

                        scheduleData.addAll(scheduleRepository.findAllScheduleForUserLessThanEqualToToday(newValue.getUser().getUsername()));
                        scheduleList.setItems(scheduleData);
                    }

                });
            }


        }

        setDataForHourPMLists();

    }

    @Autowired
    public CrudClockController(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setController(ClockInOutController clockController) {
        this.clockController = clockController;
    }

    public void setLabel(String string, String buttonLabel){
        crudLabel.setText(string);
        saveButton.setText(buttonLabel);
    }

    public void setClock(Tblclock selectedClock){
        this.selectedClock = selectedClock;
        setFieldsForEdit(this.selectedClock);
    }

    private void setFieldsForEdit(Tblclock cl1){
        //use Calendar class to find if time has AM or PM
        Calendar begPM=Calendar.getInstance();
        begPM.setTime(cl1.getPunchIn());

        Calendar endPM=Calendar.getInstance();
        endPM.setTime(cl1.getPunchOut());

        //if time has AM or PM, assign appropriate value in AM/PM drop-downs
        if(begPM.get(Calendar.AM_PM) == Calendar.AM
                && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginPMList.getSelectionModel().select(0);
            endPMList.getSelectionModel().select(0);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.AM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginPMList.getSelectionModel().select(0);
            endPMList.getSelectionModel().select(1);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginPMList.getSelectionModel().select(1);
            endPMList.getSelectionModel().select(0);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginPMList.getSelectionModel().select(1);
            endPMList.getSelectionModel().select(1);
        }

        /*//initialize the schedule dates for the current user
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findAllScheduleForUser(selectedClock.getSchedule().getEmployee().getUser().getUsername()));
        scheduleList.setItems(scheduleData);

        //get the schedule for this time off request and select it in drop-down
        scheduleList.getSelectionModel().select(selectedClock.getSchedule());*/

        //use Calendar class to extract only hour or minute from time
        Calendar calendarBeg=Calendar.getInstance();
        Calendar calendarEnd=Calendar.getInstance();

        //extract hours
        calendarBeg.setTime(cl1.getPunchIn());
        int beginHour = calendarBeg.get(Calendar.HOUR);

        calendarEnd.setTime(cl1.getPunchOut());
        int endHour = calendarEnd.get(Calendar.HOUR);

        //extract minutes
        calendarBeg.setTime(cl1.getPunchIn());
        int beginMin = calendarBeg.get(Calendar.MINUTE);

        calendarEnd.setTime(cl1.getPunchOut());
        int endMin = calendarEnd.get(Calendar.MINUTE);

        //assigns hour comboboxes
        if(beginHour == 0 && endHour == 0){ //if any of the hours are equal to 12
            beginHrList.getValueFactory().setValue(12);
            endHrList.getValueFactory().setValue(12);
        }
        else if(endHour == 0){
            beginHrList.getValueFactory().setValue(beginHour);
            endHrList.getValueFactory().setValue(12);
        }
        else if(beginHour == 0){
            beginHrList.getValueFactory().setValue(12);
            endHrList.getValueFactory().setValue(endHour);
        }
        else{
            beginHrList.getValueFactory().setValue(beginHour);
            endHrList.getValueFactory().setValue(endHour);
        }

        //assigns selected minute in drop down menu using leading zeroes
        beginMinList.getValueFactory().setValue(String.format("%02d", beginMin));
        endMinList.getValueFactory().setValue(String.format("%02d", endMin));

    }

    public void handleSave(ActionEvent event){
        Button selectedButton = (Button)event.getSource();

        switch (selectedButton.getText()){
            case "Add":
                Tblclock cl1 = new Tblclock();

                if(!(beginPMList.getSelectionModel().isEmpty()
                        || endPMList.getSelectionModel().isEmpty()
                        || employeeList.getSelectionModel().isEmpty()
                        || scheduleList.getSelectionModel().isEmpty())) {
                    //convert combobox values to 24 hour clock depending if AM or PM was selected
                    if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                        //if the beginning hour is 12 am
                        if(beginHrList.getValue().toString().equals("12")){
                            cl1.setPunchIn(Time.valueOf("00"
                                    + ":" + beginMinList.getValue()
                                    + ":00"));
                        }
                        else {
                            cl1.setPunchIn(Time.valueOf(beginHrList.getValue().toString()
                                    + ":" + beginMinList.getValue()
                                    + ":00"));
                        }
                    } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                        //if the beginning hour is 12 pm
                        if(beginHrList.getValue().toString().equals("12")) {
                            cl1.setPunchIn(Time.valueOf("12"
                                    + ":" + beginMinList.getValue()
                                    + ":00"));
                        }
                        else{
                            cl1.setPunchIn(Time.valueOf((beginHrList.getValue() + 12)
                                    + ":" + beginMinList.getValue()
                                    + ":00"));
                        }
                    }

                    if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                        //if the ending hour is 12 am
                        if(endHrList.getValue().toString().equals("12")){
                            cl1.setPunchOut(Time.valueOf("00"
                                    + ":" + endMinList.getValue()
                                    + ":00"));
                        }
                        else {
                            cl1.setPunchOut(Time.valueOf(endHrList.getValue().toString()
                                    + ":" + endMinList.getValue()
                                    + ":00"));
                        }
                    } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                        //if the ending hour is 12 pm
                        if(endHrList.getValue().toString().equals("12")) {
                            cl1.setPunchOut(Time.valueOf("12"
                                    + ":" + endMinList.getValue()
                                    + ":00"));
                        }
                        else {
                            cl1.setPunchOut(Time.valueOf((endHrList.getValue() + 12)
                                    + ":" + endMinList.getValue()
                                    + ":00"));
                        }
                    }

                    cl1.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
                    cl1.setDay(scheduleList.getSelectionModel().getSelectedItem().getDay());
                    cl1.setDateCreated(new java.sql.Timestamp(new java.util.Date().getTime()));
                    //cl1.setEmployee(employeeList.getSelectionModel().getSelectedItem());

                    //check if any fields were empty or using default selection of "Hour"
                    //check that the selected time range is valid
                    if (cl1.getPunchIn().before(cl1.getPunchOut())
                            && cl1.getPunchOut().after(cl1.getPunchIn())) {
                        clockRepository.save(cl1);

                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        System.out.println("Saved");
                        stage.close();
                    }
                    else {
                        ErrorMessages.showErrorMessage("Invalid time values",
                                "Time range is invalid",
                                "Please edit time range for this clock in/out record");
                    }
                }
                else{
                    ErrorMessages.showErrorMessage("Fields are empty",
                            "Selections missing from drop-down menus\nor time spinners",
                            "Please select from the drop-down menus and time spinners.");
                }

                break;
            case "Save":
                Tblclock cl = selectedClock;

                //convert combobox values to 24 hour clock depending if AM or PM was selected
                if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                    //if the beginning hour is 12 am
                    if(beginHrList.getValue().toString().equals("12")){
                        cl.setPunchIn(Time.valueOf("00"
                                + ":" + beginMinList.getValue()
                                + ":00"));
                    }
                    else {
                        cl.setPunchIn(Time.valueOf(beginHrList.getValue().toString()
                                + ":" + beginMinList.getValue()
                                + ":00"));
                    }
                } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                    //if the beginning hour is 12 pm
                    if(beginHrList.getValue().toString().equals("12")) {
                        cl.setPunchIn(Time.valueOf("12"
                                + ":" + beginMinList.getValue()
                                + ":00"));
                    }
                    else{
                        cl.setPunchIn(Time.valueOf((beginHrList.getValue() + 12)
                                + ":" + beginMinList.getValue()
                                + ":00"));
                    }
                }

                if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {

                    //if the ending hour is 12 am
                    if(endHrList.getValue().toString().equals("12")){
                        cl.setPunchOut(Time.valueOf("00"
                                + ":" + endMinList.getValue()
                                + ":00"));
                    }
                    else {
                        cl.setPunchOut(Time.valueOf(endHrList.getValue().toString()
                                + ":" + endMinList.getValue()
                                + ":00"));
                    }
                } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {

                    //if the ending hour is 12 pm
                    if(endHrList.getValue().toString().equals("12")) {
                        cl.setPunchOut(Time.valueOf("12"
                                + ":" + endMinList.getValue()
                                + ":00"));
                    }
                    else {
                        cl.setPunchOut(Time.valueOf((endHrList.getValue() + 12)
                                + ":" + endMinList.getValue()
                                + ":00"));
                    }
                }

                //check if any fields were empty or using default selection of "Hour"
                if(!(beginPMList.getSelectionModel().isEmpty() ||
                        endPMList.getSelectionModel().isEmpty())) {
                    //check that the selected time range is valid
                    if (cl.getPunchIn().before(cl.getPunchOut())
                            && cl.getPunchOut().after(cl.getPunchIn())) {
                        clockRepository.save(cl);

                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        System.out.println("Saved");
                        stage.close();
                    } else {
                        ErrorMessages.showErrorMessage("Invalid time values",
                                "Time range is invalid",
                                "Please edit time range for this clock in/out record");
                    }
                }
                else{
                    ErrorMessages.showErrorMessage("Fields are empty",
                            "Selections missing from drop-down menus",
                            "Please select from the drop-down menus");
                }
                break;
        }


    }

    @FXML
    private void handleCancel(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }

    private void setDataForHourPMLists(){
        hrList.addAll(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));
        List<Integer> minListInt = IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList());

        //make a list of minutes with leading zeroes for single-digit numbers
        List<String> minListString = new ArrayList<String>(minListInt.size());
        for (Integer myInt : minListInt) {
            minListString.add(String.format("%02d", myInt));
        }

        minList.addAll(minListString);

        //fill the hour, minute, and AM/PM drop-downs or spinners with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        SpinnerValueFactory<Integer> bHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);
        SpinnerValueFactory<String> bMins =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(minList);
        SpinnerValueFactory<Integer> eHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);
        SpinnerValueFactory<String> eMins =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(minList);

        beginHrList.setValueFactory(bHours);
        beginMinList.setValueFactory(bMins);
        endHrList.setValueFactory(eHours);
        endMinList.setValueFactory(eMins);
    }
}
