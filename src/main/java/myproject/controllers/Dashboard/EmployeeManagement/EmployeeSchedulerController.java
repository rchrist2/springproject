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
import myproject.models.Tblemployee;
import myproject.repositories.DayRepository;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
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
                                        dateColumn;

    @FXML
    private TableView<Tblemployee> scheduleTableView;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

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
            "00:00:00", "01:00:00", "02:00:00", "03:00:00",
            "04:00:00", "05:00:00", "06:00:00", "07:00:00",
            "08:00:00", "09:00:00", "10:00:00", "11:00:00",
            "12:00:00", "13:00:00", "14:00:00", "15:00:00",
            "16:00:00", "17:00:00", "18:00:00", "19:00:00",
            "20:00:00", "21:00:00", "22:00:00", "23:00:00"
    );


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

        sundayStartSpinner.getValueFactory().setValue("10:00:00");

    }


    @FXML
    private void handleSelectedEmployee(){
        selectedEmployee = employeeRepository.findEmployeeById(employeeListView.getSelectionModel().getSelectedItem().getId());

        if(selectedEmployee != null){
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
            employeeSelectError.setVisible(true);
            employeeSelectError.setText("Please select a employee");
            employeeSelectError.setTextFill(Color.RED);
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

        List<String> listOfTimes = new ArrayList<>();
        List<String> listOfDays = new ArrayList<>();
        List<LocalDate> listOfDates = new ArrayList<>();

        String errorString = "";
        boolean errors = false;

        scheduleGridPane.setDisable(true);

        if(sundayCheck.isSelected()){

            if(spinnerValidation(sundayStartSpinner.getValue(), sundayEndSpinner.getValue())) {
                listOfTimes.add(sundayStartSpinner.getValue());
                listOfTimes.add(sundayEndSpinner.getValue());
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
                listOfTimes.add(mondayStartSpinner.getValue());
                listOfTimes.add(mondayEndSpinner.getValue());
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
                listOfTimes.add(tuesdayStartSpinner.getValue());
                listOfTimes.add(tuesdayEndSpinner.getValue());
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
                listOfTimes.add(wednesdayStartSpinner.getValue());
                listOfTimes.add(wednesdayEndSpinner.getValue());
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
                listOfTimes.add(thursdayStartSpinner.getValue());
                listOfTimes.add(thursdayEndSpinner.getValue());
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
                listOfTimes.add(fridayStartSpinner.getValue());
                listOfTimes.add(fridayEndSpinner.getValue());
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
                listOfTimes.add(saturdayStartSpinner.getValue());
                listOfTimes.add(saturdayEndSpinner.getValue());
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

            if(scheduleButton.getText().equals("Update Schedule")){
                //TODO this causes an error if time off requests/clock history exists for the schedule

                scheduleService.deleteSchedule(selectedEmployee.getId());
            }

            for (String day : listOfDays) {
                    scheduleService.insertSchedule(Time.valueOf(listOfTimes.get(timeIndex++)), Time.valueOf(listOfTimes.get(timeIndex++)),
                            Date.valueOf(listOfDates.get(i++)), false, selectedEmployee.getId(), dayRepository.findDay(day).getDayId());
            }

            ErrorMessages.showInformationMessage("Successful", "Saved Schedule", selectedEmployee + "'s schedule was saved successfully");
            loadDataToTable();
        }

        resetCheckBoxes();
        resetSpinners();
    }

    private void handleEdittingEmployee(){
        scheduleTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue != null) {
                employeeListView.setDisable(true);

                selectedEmployee = scheduleTableView.getSelectionModel().getSelectedItem();
                List<String> days = scheduleRepository.findEmployeeDays(selectedEmployee.getId(), sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert));

                scheduleGridPane.setDisable(false);
                resetCheckBoxes();
                for (String day : days) {
                    System.out.println("Day: " + day);
                    switch (day.toLowerCase()) {
                        case "sunday":
                            sundayCheck.setSelected(true);
                            sundayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 1, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            sundayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 1, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            sundayStartSpinner.setDisable(false);
                            sundayEndSpinner.setDisable(false);
                            break;
                        case "monday":
                            mondayCheck.setSelected(true);
                            mondayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 2, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            mondayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 2, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            mondayStartSpinner.setDisable(false);
                            mondayEndSpinner.setDisable(false);
                            break;
                        case "tuesday":
                            tuesdayCheck.setSelected(true);
                            tuesdayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 3, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            tuesdayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 3, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            tuesdayStartSpinner.setDisable(false);
                            tuesdayEndSpinner.setDisable(false);
                            break;
                        case "wednesday":
                            wednesdayCheck.setSelected(true);
                            wednesdayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 4, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            wednesdayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 4, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            wednesdayStartSpinner.setDisable(false);
                            wednesdayEndSpinner.setDisable(false);
                            break;
                        case "thursday":
                            thursdayCheck.setSelected(true);
                            thursdayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 5, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            thursdayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 5, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            thursdayStartSpinner.setDisable(false);
                            thursdayEndSpinner.setDisable(false);
                            break;
                        case "friday":
                            fridayCheck.setSelected(true);
                            fridayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 6, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            fridayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 6, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

                            fridayStartSpinner.setDisable(false);
                            fridayEndSpinner.setDisable(false);
                            break;
                        case "saturday":
                            saturdayCheck.setSelected(true);
                            saturdayStartSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeStartHours(selectedEmployee.getId(), 7, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));
                            saturdayEndSpinner.getValueFactory().setValue(scheduleRepository.findEmployeeEndHours(selectedEmployee.getId(), 7, sunday.format(sqlDateTimeConvert), saturday.format(sqlDateTimeConvert)));

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
        sundayStartSpinner.getValueFactory().setValue("00:00:00");
        sundayEndSpinner.getValueFactory().setValue("00:00:00");
        mondayStartSpinner.getValueFactory().setValue("00:00:00");
        mondayEndSpinner.getValueFactory().setValue("00:00:00");
        tuesdayStartSpinner.getValueFactory().setValue("00:00:00");
        tuesdayEndSpinner.getValueFactory().setValue("00:00:00");
        wednesdayStartSpinner.getValueFactory().setValue("00:00:00");
        wednesdayEndSpinner.getValueFactory().setValue("00:00:00");
        thursdayStartSpinner.getValueFactory().setValue("00:00:00");
        thursdayEndSpinner.getValueFactory().setValue("00:00:00");
        fridayStartSpinner.getValueFactory().setValue("00:00:00");
        fridayEndSpinner.getValueFactory().setValue("00:00:00");
        saturdayStartSpinner.getValueFactory().setValue("00:00:00");
        saturdayEndSpinner.getValueFactory().setValue("00:00:00");
    }
}