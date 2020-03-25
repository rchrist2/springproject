package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import myproject.ErrorMessages;
import myproject.models.*;
import myproject.repositories.*;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.previous;

@Component
public class EmployeeSchedulerController implements Initializable {

    @FXML
    private ListView<Tblemployee> employeeListView;

    @FXML
    private CheckBox sundayCheck,
                mondayCheck,
                tuesdayCheck,
                wednesdayCheck,
                thursdayCheck,
                fridayCheck,
                saturdayCheck;

    @FXML
    private Label employeeLabel,
                employeeSelectError,
                listOfEmployeeLabel,
                dateLabel;

    @FXML
    private Spinner<String> sundayStartSpinner, sundayEndSpinner,
            mondayStartSpinner, mondayEndSpinner,
            tuesdayStartSpinner, tuesdayEndSpinner,
            wednesdayStartSpinner, wednesdayEndSpinner,
            thursdayStartSpinner, thursdayEndSpinner,
            fridayStartSpinner, fridayEndSpinner,
            saturdayStartSpinner, saturdayEndSpinner;

    @FXML
    private Button scheduleButton,
                resetButton,
                selectButton,
                nextWeekScheduleButton;

    @FXML
    private GridPane scheduleGridPane;

    @FXML
    private TableColumn<Tblemployee, String> nameColumn,
                                        startTimeColumn,
                                        endTimeColumn,
                                        dayColumn,
                                        dateColumn,
                                        descriptionColumn;

    @FXML
    private TableView<Tblemployee> scheduleTableView;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DayRepository dayRepository;

    private ObservableList<Tblemployee> listOfEmployees;
    private ObservableList<Tblemployee> listOfSchedules;

    private FilteredList<Tblemployee> filteredEmployeeList;

    private LocalDate today = LocalDate.now();
    private LocalDate sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
    private LocalDate saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

    public LocalDate nextSunday = today.with(next(DayOfWeek.SUNDAY));
    public LocalDate nextSaturday = today.with(next(DayOfWeek.SATURDAY));

    private DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private DateTimeFormatter sqlDateTimeConvert = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateFormat timeConvert = new SimpleDateFormat("hh:mm a");


    private Tblemployee selectedEmployee;

    private ObservableList<String> times = FXCollections.observableArrayList(
            "12:00:00 AM", "01:00:00 AM", "02:00:00 AM", "03:00:00 AM",
            "04:00:00 AM", "05:00:00 AM", "06:00:00 AM", "07:00:00 AM",
            "08:00:00 AM", "09:00:00 AM", "10:00:00 AM", "11:00:00 AM",
            "12:00:00 PM","01:00:00 PM", "02:00:00 PM", "03:00:00 PM",
            "04:00:00 PM", "05:00:00 PM", "06:00:00 PM", "07:00:00 PM",
            "08:00:00 PM", "09:00:00 PM", "10:00:00 PM", "11:00:00 PM"
    );

    List<Tblschedule> schedList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /*
        Add a way to take out the Time Off Schedules from the current list
         */
        sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
        saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

        listOfEmployees = FXCollections.observableArrayList();
        listOfSchedules = FXCollections.observableArrayList();

        listOfEmployees.setAll(employeeRepository.findAllEmployeesWithoutScheduleByWeek(sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

        employeeListView.setItems(listOfEmployees);

        //Displays the date of the first day of week to end day
        dateLabel.setText(sunday.format(dayFormat) + " - " + saturday.format(dayFormat));

        addListenersToCheckBoxes();
        addTimesToSpinner();
        setCellData();
        loadDataToTable();

        handleEdittingEmployee();

        scheduleTableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedEmployee = newv;
        });
    }


    @FXML
    private void handleSelectedEmployee(){
        if(employeeListView.getSelectionModel().getSelectedItem() != null){
            selectedEmployee = employeeRepository.findEmployeeById(employeeListView.getSelectionModel().getSelectedItem().getId());
            employeeSelectError.setVisible(false);
            scheduleGridPane.setDisable(false);
            scheduleButton.setDisable(false);

            employeeLabel.setText(selectedEmployee.getName() + "'s Schedule");
            employeeLabel.setVisible(true);
            employeeLabel.setDisable(false);

            resetButton.setDisable(false);
            employeeListView.setDisable(true);
            listOfEmployeeLabel.setDisable(true);

            selectButton.setDisable(true);

        } else {
            //employeeSelectError.setVisible(true);
            //employeeSelectError.setText("Please select a employee");
            //employeeSelectError.setTextFill(Color.RED);
            ErrorMessages.showErrorMessage("Error!","No employee selected",
                    "Please select an employee from the list.");
        }
    }

    public void handleResetEmployee(){
        scheduleGridPane.setDisable(true);
        scheduleButton.setDisable(true);
        employeeLabel.setText("[Schedule]");
        employeeLabel.setDisable(true);

        resetButton.setDisable(true);
        employeeListView.setDisable(false);
        listOfEmployeeLabel.setDisable(false);

        selectButton.setDisable(false);

        scheduleTableView.getSelectionModel().clearSelection();

        scheduleButton.setText("Add Schedule");

        resetCheckBoxes();
        resetSpinners();
    }

    @FXML
    private void handleNextWeekSchedule(){
        sunday = sunday.with(next(DayOfWeek.SUNDAY));
        saturday = saturday.with(next(DayOfWeek.SATURDAY));

        dateLabel.setText(sunday.format(dayFormat) + " - " + saturday.format(dayFormat));
        loadDataToTable();

        handleResetEmployee();
        resetCheckBoxes();
        resetSpinners();
    }

    @FXML
    private void handlePrevWeekSchedule(){
        sunday = sunday.with(previous(DayOfWeek.SUNDAY));
        saturday = saturday.with(previous(DayOfWeek.SATURDAY));

        dateLabel.setText(sunday.format(dayFormat) + " - " + saturday.format(dayFormat));
        loadDataToTable();

        handleResetEmployee();
        resetCheckBoxes();
        resetSpinners();
    }

    @FXML
    private void handleAddSchedule(){
        LocalDate begOfWeek = sunday;

        List<String> listOfBegTimes = new ArrayList<>();
        List<String> listOfEndTimes = new ArrayList<>();
        List<String> listOfDays = new ArrayList<>();
        List<LocalDate> listOfDates = new ArrayList<>();

        String errorString = "";
        boolean errors = false;

        scheduleGridPane.setDisable(true);

        if(sundayCheck.isSelected()){

            if(spinnerValidation(sundayStartSpinner.getValue(), sundayEndSpinner.getValue())) {
                listOfBegTimes.add(sundayStartSpinner.getValue());
                listOfEndTimes.add(sundayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Sunday\n";
            }

            listOfDates.add(begOfWeek);
            listOfDays.add("Sunday");

            begOfWeek = sunday;
        }

        if(mondayCheck.isSelected()){

            if(spinnerValidation(mondayStartSpinner.getValue(), mondayEndSpinner.getValue())){
                listOfBegTimes.add(mondayStartSpinner.getValue());
                listOfEndTimes.add(mondayEndSpinner.getValue());
            } else {

                errors = true;
                errorString += "- Monday\n";
            }

            begOfWeek = begOfWeek.plusDays(1);

            listOfDates.add(begOfWeek);
            listOfDays.add("Monday");

            begOfWeek = sunday;
        }

        if(tuesdayCheck.isSelected()){

            if(spinnerValidation(tuesdayStartSpinner.getValue(), tuesdayEndSpinner.getValue())) {
                listOfBegTimes.add(tuesdayStartSpinner.getValue());
                listOfEndTimes.add(tuesdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Tuesday\n";
            }

            begOfWeek = begOfWeek.plusDays(2);

            listOfDates.add(begOfWeek);
            listOfDays.add("Tuesday");

            begOfWeek = sunday;
        }

        if(wednesdayCheck.isSelected()){

            if(spinnerValidation(wednesdayStartSpinner.getValue(), wednesdayEndSpinner.getValue())) {
                listOfBegTimes.add(wednesdayStartSpinner.getValue());
                listOfEndTimes.add(wednesdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Wednesday\n";
            }

            begOfWeek = begOfWeek.plusDays(3);

            listOfDates.add(begOfWeek);
            listOfDays.add("Wednesday");

            begOfWeek = sunday;
        }

        if(thursdayCheck.isSelected()){

            if(spinnerValidation(thursdayStartSpinner.getValue(), thursdayEndSpinner.getValue())) {
                listOfBegTimes.add(thursdayStartSpinner.getValue());
                listOfEndTimes.add(thursdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Thursday\n";
            }

            begOfWeek = begOfWeek.plusDays(4);

            listOfDates.add(begOfWeek);
            listOfDays.add("Thursday");

            begOfWeek = sunday;
        }

        if(fridayCheck.isSelected()){

            if(spinnerValidation(fridayStartSpinner.getValue(), fridayEndSpinner.getValue())) {
                listOfBegTimes.add(fridayStartSpinner.getValue());
                listOfEndTimes.add(fridayEndSpinner.getValue());
            } else {

                errors = true;
                errorString += "- Friday\n";
            }

            begOfWeek = begOfWeek.plusDays(5);

            listOfDates.add(begOfWeek);
            listOfDays.add("Friday");

            begOfWeek = sunday;
        }

        if(saturdayCheck.isSelected()){

            if(spinnerValidation(saturdayStartSpinner.getValue(), saturdayEndSpinner.getValue())) {
                listOfBegTimes.add(saturdayStartSpinner.getValue());
                listOfEndTimes.add(saturdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Saturday\n";
            }

            begOfWeek = begOfWeek.plusDays(6);

            listOfDates.add(begOfWeek);
            listOfDays.add("Saturday");

            begOfWeek = sunday;
        }

        if(errors){
            ErrorMessages.showErrorMessage("Missing Time Error", "No Time Provided",
                    "Please check the times on the following days:\n" + errorString);
        }

        employeeLabel.setVisible(false);

        resetButton.setDisable(true);
        selectButton.setDisable(false);
        scheduleButton.setDisable(true);
        employeeListView.setDisable(false);
        listOfEmployeeLabel.setDisable(false);

        int timeIndex = 0, i = 0;

        if(!errors) {
            //variables used for deletion
            int id = 0;
            List<Tbltimeoff> tfs = new ArrayList<>();
            List<Tblclock> cls = new ArrayList<>();
            List<Tblschedule> newSchedList = new ArrayList<>();
            if(scheduleButton.getText().equals("Update Schedule")) {

                //declare variables for validating time ranges
                List<Tblschedule> invalidDays = new ArrayList<>();
                List<Tblschedule> schedToDel = new ArrayList<>();

                //get all spinner values to validate time range
                for (String day : listOfDays) {
                    Tblschedule s = new Tblschedule();
                    String currBegTime = listOfBegTimes.get(timeIndex);
                    String currEndTime = listOfEndTimes.get(timeIndex);

                    //convert combobox values to 24 hour clock depending if AM or PM was selected
                    if (currBegTime.contains("AM")) {

                        //if the beginning hour is 12 am
                        if (currBegTime.equals("12:00:00 AM")) {
                            s.setScheduleTimeBegin(Time.valueOf("00"
                                    + ":00:00"));
                        } else {
                            String begAMTime = currBegTime.replace(" AM","");
                            s.setScheduleTimeBegin(Time.valueOf(begAMTime));
                        }
                    } else if (currBegTime.contains("PM")) {

                        //if the beginning hour is 12 pm
                        if (currBegTime.equals("12:00:00 PM")) {
                            s.setScheduleTimeBegin(Time.valueOf("12"
                                    + ":00:00"));
                        } else {
                            String begPMTime = currBegTime.replace(" PM","");
                            s.setScheduleTimeBegin(Time.valueOf(begPMTime));
                            s.setScheduleTimeBegin(Time.valueOf(s.getScheduleTimeBegin().toLocalTime().plusHours(12)));
                        }
                    }

                    //convert combobox values to 24 hour clock depending if AM or PM was selected
                    if (currEndTime.contains("AM")) {

                        //if the beginning hour is 12 am
                        if (currEndTime.equals("12:00:00 AM")) {
                            s.setScheduleTimeEnd(Time.valueOf("00"
                                    + ":00:00"));
                        } else {
                            String endAMTime = currEndTime.replace(" AM","");
                            s.setScheduleTimeEnd(Time.valueOf(endAMTime));
                        }
                    } else if (currEndTime.contains("PM")) {

                        //if the beginning hour is 12 pm
                        if (currEndTime.equals("12:00:00 PM")) {
                            s.setScheduleTimeEnd(Time.valueOf("12"
                                    + ":00:00"));
                        } else {
                            String endPMTime = currEndTime.replace(" PM","");
                            s.setScheduleTimeEnd(Time.valueOf(endPMTime));
                            s.setScheduleTimeEnd(Time.valueOf(s.getScheduleTimeEnd().toLocalTime().plusHours(12)));
                        }
                    }

                    s.setScheduleDate(Date.valueOf(listOfDates.get(i++)));
                    s.setEmployee(employeeRepository.findEmployeeById(selectedEmployee.getId()));
                    s.setDay(dayRepository.findDayByID(dayRepository.findDay(day).getDayId()));
                    timeIndex++;

                    if(!(s.getScheduleTimeBegin().before(s.getScheduleTimeEnd())
                            && s.getScheduleTimeEnd().after(s.getScheduleTimeBegin()))){
                        invalidDays.add(s);
                    }

                    schedToDel.add(s);
                }

                //show an error message for each invalid schedule
                StringBuilder schedError = new StringBuilder();
                for (Tblschedule s : invalidDays) {
                    schedError.append("\t- " + s.getDay().getDayDesc() + "\n");
                }

                //show an error message with the days for each invalid schedule
                if(!(invalidDays.isEmpty())){
                    ErrorMessages.showErrorMessage("Invalid time values", "Time range for \n"
                            + schedError
                            + " is invalid", "Please edit the time range for this schedule.");
                    //prevents the table selection being null and permanently disabling spinners
                    //TODO stop the spinners from disabling after closing alert
                    scheduleTableView.getSelectionModel().clearSelection();
                }
                else{ //if there are no invalid time ranges..

                    //find related rows and set their schedule_ids to null
                    for(Tblschedule sch : schedList){
                        id = sch.getScheduleId();
                        if(timeOffRepository.findScheduleTimeOff(id, selectedEmployee.getId()) != null){
                            tfs.add(timeOffRepository.findScheduleTimeOff(id, selectedEmployee.getId()));
                            tfs.forEach((t) -> t.setSchedule(null));
                            for(Tbltimeoff t : tfs){
                                timeOffRepository.save(t);
                            }

                        }
                        if(clockRepository.findScheduleClock(id) != null){
                            cls.addAll(clockRepository.findScheduleClock(id));
                            cls.forEach((c) -> c.setSchedule(null));
                            for(Tblclock c : cls){
                                clockRepository.save(c);
                            }
                        }
                    }

                    //Delete the original schedule (Pre-updated schedule)
                    System.out.println("Schedule to delete: " + schedList);
                    for(Tblschedule sh : schedList){
                        scheduleService.deleteScheduleByID(sh.getScheduleId());
                        System.out.println("Deleted");

                    }

                    //set these to 0 to prevent out of bounds error
                    timeIndex = 0;
                    i = 0;

                    //re-insert the schedule that was deleted
                    for (String day : listOfDays) {
                        Tblschedule s = new Tblschedule();
                        String currBegTime = listOfBegTimes.get(timeIndex);
                        String currEndTime = listOfEndTimes.get(timeIndex);

                        //convert combobox values to 24 hour clock depending if AM or PM was selected
                        if (currBegTime.contains("AM")) {

                            //if the beginning hour is 12 am
                            if (currBegTime.equals("12:00:00 AM")) {
                                s.setScheduleTimeBegin(Time.valueOf("00"
                                        + ":00:00"));
                            } else {
                                String begAMTime = currBegTime.replace(" AM","");
                                s.setScheduleTimeBegin(Time.valueOf(begAMTime));
                            }
                        } else if (currBegTime.contains("PM")) {

                            //if the beginning hour is 12 pm
                            if (currBegTime.equals("12:00:00 PM")) {
                                s.setScheduleTimeBegin(Time.valueOf("12"
                                        + ":00:00"));
                            } else {
                                String begPMTime = currBegTime.replace(" PM","");
                                s.setScheduleTimeBegin(Time.valueOf(begPMTime));
                                s.setScheduleTimeBegin(Time.valueOf(s.getScheduleTimeBegin().toLocalTime().plusHours(12)));
                            }
                        }

                        //convert combobox values to 24 hour clock depending if AM or PM was selected
                        if (currEndTime.contains("AM")) {

                            //if the beginning hour is 12 am
                            if (currEndTime.equals("12:00:00 AM")) {
                                s.setScheduleTimeEnd(Time.valueOf("00"
                                        + ":00:00"));
                            } else {
                                String endAMTime = currEndTime.replace(" AM","");
                                s.setScheduleTimeEnd(Time.valueOf(endAMTime));
                            }
                        } else if (currEndTime.contains("PM")) {

                            //if the beginning hour is 12 pm
                            if (currEndTime.equals("12:00:00 PM")) {
                                s.setScheduleTimeEnd(Time.valueOf("12"
                                        + ":00:00"));
                            } else {
                                String endPMTime = currEndTime.replace(" PM","");
                                s.setScheduleTimeEnd(Time.valueOf(endPMTime));
                                s.setScheduleTimeEnd(Time.valueOf(s.getScheduleTimeEnd().toLocalTime().plusHours(12)));
                            }
                        }

                        s.setScheduleDate(Date.valueOf(listOfDates.get(i++)));
                        s.setEmployee(employeeRepository.findEmployeeById(selectedEmployee.getId()));
                        s.setDay(dayRepository.findDayByID(dayRepository.findDay(day).getDayId()));
                        timeIndex++;

                        scheduleRepository.save(s);
                        int newId = s.getScheduleId();
                        newSchedList.add(s);
                    }

                    //give related clock and time off records the new schedule ids
                    if(!(schedList.isEmpty())){
                        if(!(tfs.isEmpty())){
                            //loop through each new id
                            for(Tblschedule d : newSchedList){
                                //find the schedule with each new id
                                //Tblschedule newSched = scheduleRepository.findByScheduleId(d.getScheduleId());

                                //if the schedule day matches the day of the related time offs, set the id
                                for(Tbltimeoff t : tfs){
                                    if(d.getDay().getDayDesc().equals(t.getDay().getDayDesc())){
                                        t.setSchedule(d);
                                        timeOffRepository.save(t);
                                    }
                                }
                            }
                        }

                        if(!(cls.isEmpty())){
                            //do the same for clock ins/outs
                            for(Tblschedule k : newSchedList){
                                //Tblschedule newSched2 = scheduleRepository.findByScheduleId(k.getScheduleId());
                                for(Tblclock c : cls){
                                    if(k.getDay().getDayDesc().equals(c.getDay().getDayDesc())){
                                        c.setSchedule(k);
                                        clockRepository.save(c);
                                    }
                                }
                            }
                        }
                    }


                    ErrorMessages.showInformationMessage("Successful", "Saved Schedule", selectedEmployee + "'s schedule was saved successfully");
                    loadDataToTable();

                    resetCheckBoxes();
                    resetSpinners();
                }
            }
            else if(scheduleButton.getText().equals("Add Schedule")){

                //declare variables to use for validating time ranges
                List<Tblschedule> schedToAdd = new ArrayList<>();
                StringBuilder schedErrorAdd = new StringBuilder();
                List<Tblschedule> invalidDaysAdd = new ArrayList<>();

                //set these to 0 to avoid out of bounds error
                timeIndex = 0;
                i = 0;

                //gather schedules to be inserted, while also finding schedules w/ invalid time ranges
                for (String day : listOfDays) {
                    Tblschedule s = new Tblschedule();
                    String currBegTime = listOfBegTimes.get(timeIndex);
                    String currEndTime = listOfEndTimes.get(timeIndex);

                    //convert combobox values to 24 hour clock depending if AM or PM was selected
                    if (currBegTime.contains("AM")) {

                        //if the beginning hour is 12 am
                        if (currBegTime.equals("12:00:00 AM")) {
                            s.setScheduleTimeBegin(Time.valueOf("00"
                                    + ":00:00"));
                        } else {
                            String begAMTime = currBegTime.replace(" AM","");
                            s.setScheduleTimeBegin(Time.valueOf(begAMTime));
                        }
                    } else if (currBegTime.contains("PM")) {

                        //if the beginning hour is 12 pm
                        if (currBegTime.equals("12:00:00 PM")) {
                            s.setScheduleTimeBegin(Time.valueOf("12"
                                    + ":00:00"));
                        } else {
                            String begPMTime = currBegTime.replace(" PM","");
                            s.setScheduleTimeBegin(Time.valueOf(begPMTime));
                            s.setScheduleTimeBegin(Time.valueOf(s.getScheduleTimeBegin().toLocalTime().plusHours(12)));
                        }
                    }

                    //convert combobox values to 24 hour clock depending if AM or PM was selected
                    if (currEndTime.contains("AM")) {

                        //if the beginning hour is 12 am
                        if (currEndTime.equals("12:00:00 AM")) {
                            s.setScheduleTimeEnd(Time.valueOf("00"
                                    + ":00:00"));
                        } else {
                            String endAMTime = currEndTime.replace(" AM","");
                            s.setScheduleTimeEnd(Time.valueOf(endAMTime));
                        }
                    } else if (currEndTime.contains("PM")) {

                        //if the beginning hour is 12 pm
                        if (currEndTime.equals("12:00:00 PM")) {
                            s.setScheduleTimeEnd(Time.valueOf("12"
                                    + ":00:00"));
                        } else {
                            String endPMTime = currEndTime.replace(" PM","");
                            s.setScheduleTimeEnd(Time.valueOf(endPMTime));
                            s.setScheduleTimeEnd(Time.valueOf(s.getScheduleTimeEnd().toLocalTime().plusHours(12)));
                        }
                    }

                    s.setScheduleDate(Date.valueOf(listOfDates.get(i++)));
                    s.setEmployee(employeeRepository.findEmployeeById(selectedEmployee.getId()));
                    s.setDay(dayRepository.findDayByID(dayRepository.findDay(day).getDayId()));
                    timeIndex++;

                    //find all days with an invalid time range and add them to a list
                    if(!(s.getScheduleTimeBegin().before(s.getScheduleTimeEnd())
                            && s.getScheduleTimeEnd().after(s.getScheduleTimeBegin()))){
                        invalidDaysAdd.add(s);
                    }

                    //add the schedules to be added into another list
                    schedToAdd.add(s);
                }

                //show an error message for each invalid schedule
                for (Tblschedule sched : invalidDaysAdd) {
                    schedErrorAdd.append("\t- " + sched.getDay().getDayDesc() + "\n");
                }

                //if invalid schedules were found, show an error
                if(!(invalidDaysAdd.isEmpty())){
                    ErrorMessages.showErrorMessage("Invalid time values", "Time range for \n"
                            + schedErrorAdd
                            + " is invalid", "Please edit the time range for this schedule");

                    //prevents the list selection being null and permanently disabling spinners
                    //TODO stop the spinners from disabling after closing alert
                    employeeListView.getSelectionModel().clearSelection();
                }
                else{ //if no invalid time ranges were found, add the schedules
                    for(Tblschedule sc : schedToAdd){
                        scheduleRepository.save(sc);
                    }

                    ErrorMessages.showInformationMessage("Successful", "Saved Schedule", selectedEmployee + "'s schedule was saved successfully");
                    loadDataToTable();

                    resetCheckBoxes();
                    resetSpinners();
                }
            }

        }
    }

    private void handleEdittingEmployee(){
        scheduleTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue != null) {
                resetSpinners();
                employeeListView.setDisable(true);

                selectedEmployee = scheduleTableView.getSelectionModel().getSelectedItem();
                List<String> days = scheduleRepository.findEmployeeDays(selectedEmployee.getId(), sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert));

                schedList.clear();
                schedList = scheduleRepository.findScheduleForEmployeeSchedList(selectedEmployee.getId());
                System.out.println("Schedlist: " + schedList);

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                scheduleGridPane.setDisable(false);
                resetCheckBoxes();
                for (String day : days) {
                    System.out.println("Day: " + day);
                    switch (day.toLowerCase()) {
                        case "sunday":
                            sundayCheck.setSelected(true);
                            sundayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 1, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            sundayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 1, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            sundayStartSpinner.setDisable(false);
                            sundayEndSpinner.setDisable(false);
                            break;
                        case "monday":
                            mondayCheck.setSelected(true);
                            mondayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 2, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            mondayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 2, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            mondayStartSpinner.setDisable(false);
                            mondayEndSpinner.setDisable(false);
                            break;
                        case "tuesday":
                            tuesdayCheck.setSelected(true);
                            tuesdayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 3, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            tuesdayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 3, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            tuesdayStartSpinner.setDisable(false);
                            tuesdayEndSpinner.setDisable(false);
                            break;
                        case "wednesday":
                            wednesdayCheck.setSelected(true);
                            wednesdayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 4, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            wednesdayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 4, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            wednesdayStartSpinner.setDisable(false);
                            wednesdayEndSpinner.setDisable(false);
                            break;
                        case "thursday":
                            thursdayCheck.setSelected(true);
                            thursdayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 5, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            thursdayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 5, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            thursdayStartSpinner.setDisable(false);
                            thursdayEndSpinner.setDisable(false);
                            break;
                        case "friday":
                            fridayCheck.setSelected(true);
                            fridayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 6, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            fridayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 6, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            fridayStartSpinner.setDisable(false);
                            fridayEndSpinner.setDisable(false);
                            break;
                        case "saturday":
                            saturdayCheck.setSelected(true);
                            saturdayStartSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 7, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));
                            saturdayEndSpinner.getValueFactory().setValue(timeFormat.format(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 7, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))));

                            saturdayStartSpinner.setDisable(false);
                            saturdayEndSpinner.setDisable(false);
                            break;
                    }
                }

                scheduleButton.setDisable(false);
                scheduleButton.setText("Update Schedule");

                employeeLabel.setDisable(false);
                employeeLabel.setText(selectedEmployee.getName() + "'s Schedule");

                resetButton.setDisable(false);
                selectButton.setDisable(true);
            }
        });
    }

    @FXML
    private void setCellData(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        startTimeColumn.setCellValueFactory(startTime -> new ReadOnlyStringWrapper(startTime.getValue().employeeStartHours(Date.valueOf(sunday), Date.valueOf(saturday))));
        endTimeColumn.setCellValueFactory(startTime -> new ReadOnlyStringWrapper(startTime.getValue().employeeEndHours(Date.valueOf(sunday), Date.valueOf(saturday))));

        dayColumn.setCellValueFactory(day -> new ReadOnlyStringWrapper(day.getValue().employeeSchedule(Date.valueOf(sunday), Date.valueOf(saturday))));
        dateColumn.setCellValueFactory(date -> new ReadOnlyStringWrapper(date.getValue().employeeDates(Date.valueOf(sunday), Date.valueOf(saturday))));

    }

    @FXML
    private void loadDataToTable(){
        listOfSchedules.clear();
        listOfEmployees.clear();

        System.out.println("Sunday Date: " + sunday);
        System.out.println("Saturday Date: " + saturday);

        for (Tblemployee emp : employeeRepository.findAllEmployeeByWeek(sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert))) {
            System.out.println("Schedule: " + emp.getSchedules());
        }

        listOfSchedules.addAll(employeeRepository.findAllEmployeeByWeek(sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
        listOfEmployees.addAll(employeeRepository.findAllEmployeesWithoutScheduleByWeek(sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

        filteredEmployeeList = new FilteredList<>(listOfSchedules);

        scheduleTableView.setItems(filteredEmployeeList);
    }

    private boolean spinnerValidation(String start, String end){
        return start != null && end != null;
    }

    private void addListenersToCheckBoxes(){
        sundayCheck.selectedProperty().addListener((obs) -> {
            if(sundayCheck.isSelected()) {
                sundayStartSpinner.setDisable(false);
                sundayEndSpinner.setDisable(false);
            } else {
                sundayStartSpinner.setDisable(true);
                sundayEndSpinner.setDisable(true);
            }
        });

        mondayCheck.selectedProperty().addListener((obs) -> {
            if(mondayCheck.isSelected()) {
                mondayStartSpinner.setDisable(false);
                mondayEndSpinner.setDisable(false);
            } else {
                mondayStartSpinner.setDisable(true);
                mondayEndSpinner.setDisable(true);
            }
        });

        tuesdayCheck.selectedProperty().addListener((obs) -> {
            if(tuesdayCheck.isSelected()) {
                tuesdayStartSpinner.setDisable(false);
                tuesdayEndSpinner.setDisable(false);
            } else {
                tuesdayStartSpinner.setDisable(true);
                tuesdayEndSpinner.setDisable(true);
            }
        });

        wednesdayCheck.selectedProperty().addListener((obs) -> {
            if(wednesdayCheck.isSelected()) {
                wednesdayStartSpinner.setDisable(false);
                wednesdayEndSpinner.setDisable(false);
            } else {
                wednesdayStartSpinner.setDisable(true);
                wednesdayEndSpinner.setDisable(true);
            }
        });

        thursdayCheck.selectedProperty().addListener((obs) -> {
            if(thursdayCheck.isSelected()) {
                thursdayStartSpinner.setDisable(false);
                thursdayEndSpinner.setDisable(false);
            } else {
                thursdayStartSpinner.setDisable(true);
                thursdayEndSpinner.setDisable(true);
            }
        });

        fridayCheck.selectedProperty().addListener((obs) -> {
            if(fridayCheck.isSelected()) {
                fridayStartSpinner.setDisable(false);
                fridayEndSpinner.setDisable(false);
            } else {
                fridayStartSpinner.setDisable(true);
                fridayEndSpinner.setDisable(true);
            }
        });

        saturdayCheck.selectedProperty().addListener((obs) -> {
            if(saturdayCheck.isSelected()) {
                saturdayStartSpinner.setDisable(false);
                saturdayEndSpinner.setDisable(false);
            } else {
                saturdayStartSpinner.setDisable(true);
                saturdayEndSpinner.setDisable(true);
            }
        });
    }

    private void addTimesToSpinner(){
        SpinnerValueFactory<String> sundayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> sundayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> mondayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> mondayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> tuesdayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> tuesdayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> wednesdayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> wednesdayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> thursdayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> thursdayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> fridayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> fridayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        SpinnerValueFactory<String> saturdayStartTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);
        SpinnerValueFactory<String> saturdayEndTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        sundayStartSpinner.setValueFactory(sundayStartTimes);
        sundayEndSpinner.setValueFactory(sundayEndTimes);
        mondayStartSpinner.setValueFactory(mondayStartTimes);
        mondayEndSpinner.setValueFactory(mondayEndTimes);
        tuesdayStartSpinner.setValueFactory(tuesdayStartTimes);
        tuesdayEndSpinner.setValueFactory(tuesdayEndTimes);
        wednesdayStartSpinner.setValueFactory(wednesdayStartTimes);
        wednesdayEndSpinner.setValueFactory(wednesdayEndTimes);
        thursdayStartSpinner.setValueFactory(thursdayStartTimes);
        thursdayEndSpinner.setValueFactory(thursdayEndTimes);
        fridayStartSpinner.setValueFactory(fridayStartTimes);
        fridayEndSpinner.setValueFactory(fridayEndTimes);
        saturdayStartSpinner.setValueFactory(saturdayStartTimes);
        saturdayEndSpinner.setValueFactory(saturdayEndTimes);
    }

    private void resetCheckBoxes(){
        sundayCheck.setSelected(false);
        mondayCheck.setSelected(false);
        tuesdayCheck.setSelected(false);
        wednesdayCheck.setSelected(false);
        thursdayCheck.setSelected(false);
        fridayCheck.setSelected(false);
        saturdayCheck.setSelected(false);
    }

    private void resetSpinners(){
        sundayStartSpinner.getValueFactory().setValue(times.get(0));
        sundayEndSpinner.getValueFactory().setValue(times.get(0));
        mondayStartSpinner.getValueFactory().setValue(times.get(0));
        mondayEndSpinner.getValueFactory().setValue(times.get(0));
        tuesdayStartSpinner.getValueFactory().setValue(times.get(0));
        tuesdayEndSpinner.getValueFactory().setValue(times.get(0));
        wednesdayStartSpinner.getValueFactory().setValue(times.get(0));
        wednesdayEndSpinner.getValueFactory().setValue(times.get(0));
        thursdayStartSpinner.getValueFactory().setValue(times.get(0));
        thursdayEndSpinner.getValueFactory().setValue(times.get(0));
        fridayStartSpinner.getValueFactory().setValue(times.get(0));
        fridayEndSpinner.getValueFactory().setValue(times.get(0));
        saturdayStartSpinner.getValueFactory().setValue(times.get(0));
        saturdayEndSpinner.getValueFactory().setValue(times.get(0));
    }
}