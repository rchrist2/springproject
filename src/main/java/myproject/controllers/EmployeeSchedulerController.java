package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import myproject.ErrorMessages;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Time;
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

    @Autowired
    private EmployeeRepository employeeRepository;

    private ObservableList<Tblemployee> listOfEmployees;
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

        listOfEmployees.setAll(employeeRepository.findAllEmployee());

        employeeListView.setItems(listOfEmployees);

        addListenersToCheckBoxes();
        addTimesToSpinner();
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
        boolean noErrors = false;

        scheduleGridPane.setDisable(true);

        if(sundayCheck.isSelected()){

            if(spinnerValidation(sundayStartSpinner.getValue(), sundayEndSpinner.getValue())) {
                listOfTimes.add(sundayStartSpinner.getValue());
                listOfTimes.add(sundayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Sunday\n";
            }

            listOfDays.add("Sunday");
        }

        if(mondayCheck.isSelected()){

            if(spinnerValidation(mondayStartSpinner.getValue(), mondayEndSpinner.getValue())){
                listOfTimes.add(mondayStartSpinner.getValue());
                listOfTimes.add(mondayEndSpinner.getValue());
            } else {

                noErrors = true;
                errorString += "- Monday\n";
            }

            listOfDays.add("Monday");
        }

        if(tuesdayCheck.isSelected()){

            if(spinnerValidation(tuesdayStartSpinner.getValue(), tuesdayEndSpinner.getValue())) {
                listOfTimes.add(tuesdayStartSpinner.getValue());
                listOfTimes.add(tuesdayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Tuesday\n";
            }

            listOfDays.add("Tuesday");
        }

        if(wednesdayCheck.isSelected()){

            if(spinnerValidation(wednesdayStartSpinner.getValue(), wednesdayEndSpinner.getValue())) {
                listOfTimes.add(wednesdayStartSpinner.getValue());
                listOfTimes.add(wednesdayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Wednesday\n";
            }

            listOfDays.add("Wednesday");
        }

        if(thursdayCheck.isSelected()){

            if(spinnerValidation(thursdayStartSpinner.getValue(), thursdayEndSpinner.getValue())) {
                listOfTimes.add(thursdayStartSpinner.getValue());
                listOfTimes.add(thursdayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Thursday\n";
            }

            listOfDays.add("Thursday");
        }

        if(fridayCheck.isSelected()){

            if(spinnerValidation(fridayStartSpinner.getValue(), fridayEndSpinner.getValue())) {
                listOfTimes.add(fridayStartSpinner.getValue());
                listOfTimes.add(fridayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Friday\n";
            }

            listOfDays.add("Friday");
        }

        if(saturdayCheck.isSelected()){

            if(spinnerValidation(saturdayStartSpinner.getValue(), saturdayEndSpinner.getValue())) {
                listOfTimes.add(saturdayStartSpinner.getValue());
                listOfTimes.add(saturdayEndSpinner.getValue());
            } else{

                noErrors = true;
                errorString += "- Saturday\n";
            }

            listOfDays.add("Saturday");
        }

        if(noErrors){
            ErrorMessages.showErrorMessage("Missing Time Error", "No Time Provided",
                    "Please check the times on the following days:\n" + errorString);
        }

        employeeLabel.setVisible(false);

        resetButton.setDisable(true);
        selectButton.setDisable(false);
        employeeListView.setDisable(false);
        listOfEmployeeLabel.setDisable(false);

        int timeIndex = 0;

        for (String day : listOfDays){
            System.out.println(selectedEmployee.getName() + " - " + day + ": "
                    + listOfTimes.get(timeIndex++) + " - " + listOfTimes.get(timeIndex++));
        }
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
        SpinnerValueFactory<String> spinnerValueTimes =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(times);

        sundayStartSpinner.setValueFactory(spinnerValueTimes);
        sundayEndSpinner.setValueFactory(spinnerValueTimes);
        mondayStartSpinner.setValueFactory(spinnerValueTimes);
        mondayEndSpinner.setValueFactory(spinnerValueTimes);
        tuesdayStartSpinner.setValueFactory(spinnerValueTimes);
        tuesdayEndSpinner.setValueFactory(spinnerValueTimes);
        wednesdayStartSpinner.setValueFactory(spinnerValueTimes);
        wednesdayEndSpinner.setValueFactory(spinnerValueTimes);
        thursdayStartSpinner.setValueFactory(spinnerValueTimes);
        thursdayEndSpinner.setValueFactory(spinnerValueTimes);
        fridayStartSpinner.setValueFactory(spinnerValueTimes);
        fridayEndSpinner.setValueFactory(spinnerValueTimes);
        saturdayStartSpinner.setValueFactory(spinnerValueTimes);
        saturdayEndSpinner.setValueFactory(spinnerValueTimes);
    }
}