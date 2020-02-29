package myproject.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import myproject.ErrorMessages;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.repositories.DayRepository;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
                listOfEmployeeLabel;

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
                selectButton;

    @FXML
    private GridPane scheduleGridPane;

    @FXML
    private TableColumn<Tblemployee, String> nameColumn,
                                        startTimeColumn,
                                        endTimeColumn,
                                        dayColumn;

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

    private Tblemployee selectedEmployee;
    private ObservableList<String> times = FXCollections.observableArrayList(
            "01:00:00", "02:00:00", "03:00:00", "04:00:00",
            "05:00:00", "06:00:00", "07:00:00", "08:00:00",
            "09:00:00", "10:00:00", "11:00:00", "12:00:00",
            "13:00:00", "14:00:00", "15:00:00", "16:00:00",
            "17:00:00", "18:00:00", "19:00:00", "20:00:00",
            "21:00:00", "22:00:00", "23:00:00", "24:00:00"
    );


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfEmployees = FXCollections.observableArrayList();
        listOfSchedules = FXCollections.observableArrayList();

        listOfEmployees.setAll(employeeRepository.findAllEmployeesWithoutSchedule());

        employeeListView.setItems(listOfEmployees);

        addListenersToCheckBoxes();
        addTimesToSpinner();
        setCellData();
        loadDataToTable();
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

    @FXML
    private void handleResetEmployee(){
        scheduleGridPane.setDisable(true);
        scheduleButton.setDisable(true);
        employeeLabel.setText("[Schedule]");
        employeeLabel.setDisable(true);

        resetButton.setDisable(true);
        employeeListView.setDisable(false);
        listOfEmployeeLabel.setDisable(false);

        selectButton.setDisable(false);
    }

    @FXML
    private void handleAddSchedule(){
        List<String> listOfTimes = new ArrayList<>();
        List<String> listOfDays = new ArrayList<>();
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

            listOfDays.add("Sunday");
        }

        if(mondayCheck.isSelected()){

            if(spinnerValidation(mondayStartSpinner.getValue(), mondayEndSpinner.getValue())){
                listOfTimes.add(mondayStartSpinner.getValue());
                listOfTimes.add(mondayEndSpinner.getValue());
            } else {

                errors = true;
                errorString += "- Monday\n";
            }

            listOfDays.add("Monday");
        }

        if(tuesdayCheck.isSelected()){

            if(spinnerValidation(tuesdayStartSpinner.getValue(), tuesdayEndSpinner.getValue())) {
                listOfTimes.add(tuesdayStartSpinner.getValue());
                listOfTimes.add(tuesdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Tuesday\n";
            }

            listOfDays.add("Tuesday");
        }

        if(wednesdayCheck.isSelected()){

            if(spinnerValidation(wednesdayStartSpinner.getValue(), wednesdayEndSpinner.getValue())) {
                listOfTimes.add(wednesdayStartSpinner.getValue());
                listOfTimes.add(wednesdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Wednesday\n";
            }

            listOfDays.add("Wednesday");
        }

        if(thursdayCheck.isSelected()){

            if(spinnerValidation(thursdayStartSpinner.getValue(), thursdayEndSpinner.getValue())) {
                listOfTimes.add(thursdayStartSpinner.getValue());
                listOfTimes.add(thursdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Thursday\n";
            }

            listOfDays.add("Thursday");
        }

        if(fridayCheck.isSelected()){

            if(spinnerValidation(fridayStartSpinner.getValue(), fridayEndSpinner.getValue())) {
                listOfTimes.add(fridayStartSpinner.getValue());
                listOfTimes.add(fridayEndSpinner.getValue());
            } else {

                errors = true;
                errorString += "- Friday\n";
            }

            listOfDays.add("Friday");
        }

        if(saturdayCheck.isSelected()){

            if(spinnerValidation(saturdayStartSpinner.getValue(), saturdayEndSpinner.getValue())) {
                listOfTimes.add(saturdayStartSpinner.getValue());
                listOfTimes.add(saturdayEndSpinner.getValue());
            } else{

                errors = true;
                errorString += "- Saturday\n";
            }

            listOfDays.add("Saturday");
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

        int timeIndex = 0;

        //Debugging to check if schedule is done correctly
        /*for (String day : listOfDays){
            System.out.println(selectedEmployee.getName() + " - " + day + ": "
                    + listOfTimes.get(timeIndex++) + " - " + listOfTimes.get(timeIndex++));
        }*/

        if(!errors) {
            for (String day : listOfDays) {
                scheduleService.insertSchedule(Time.valueOf(listOfTimes.get(timeIndex++)), Time.valueOf(listOfTimes.get(timeIndex++)),
                        Date.valueOf(LocalDate.now()), selectedEmployee.getId(), dayRepository.findDay(day).getDayId());
            }

            ErrorMessages.showInformationMessage("Successful", "Saved Schedule", selectedEmployee + "'s schedule was saved successfully");
            loadDataToTable();
        }


    }

    @FXML
    private void setCellData(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        startTimeColumn.setCellValueFactory(startTime -> new ReadOnlyStringWrapper(startTime.getValue().employeeStartHours()));
        endTimeColumn.setCellValueFactory(startTime -> new ReadOnlyStringWrapper(startTime.getValue().employeeEndHours()));

        dayColumn.setCellValueFactory(day -> new ReadOnlyStringWrapper(day.getValue().employeeSchedule()));
    }

    @FXML
    private void loadDataToTable(){
        listOfSchedules.clear();
        listOfEmployees.clear();

        listOfSchedules.addAll(employeeRepository.findAllEmployee());
        listOfEmployees.addAll(employeeRepository.findAllEmployeesWithoutSchedule());

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
}