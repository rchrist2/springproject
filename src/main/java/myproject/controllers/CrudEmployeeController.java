package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.repositories.DayRepository;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.RoleRepository;
import myproject.repositories.ScheduleRepository;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

@Component
public class CrudEmployeeController implements Initializable {

    @FXML
    private Button saveButton,
            cancelButton,
            deleteDayButton,
            addDaysButton;

    @FXML
    private TextField nameText,
            emailText,
            addressText,
            phoneText,
            startTimeText,
            endTimeText;

    @FXML
    private VBox timePerDayVBox;

    @FXML
    private ListView<String> daysToWorkListView;

    @FXML
    private ComboBox<String>
            roleComboBox,
            daysToWorkComboBox,
            startCombo,
            endCombo;

    @FXML
    private Label crudEmployeeLabel;

    private ConfigurableApplicationContext springContext;
    private EmployeeRepository employeeRepository;
    private EmployeeManagementController employeeManagementController;
    private EmployeeService employeeService;
    private RoleRepository roleRepository;
    private ScheduleRepository scheduleRepository;
    private DayRepository dayRepository;
    private ScheduleService scheduleService;

    private ObservableList<String> listOfDaysObs, listOfRoleObs, listOfAMandPM, possibleTimes;

    private List<String> listOfDayHours = new ArrayList<>();

    //The employee returned from the EmployeeManagementController
    private Tblemployee selectedEmployee;

    @Autowired
    public CrudEmployeeController(ConfigurableApplicationContext springContext, EmployeeRepository employeeRepository, EmployeeService employeeService, RoleRepository roleRepository,
                                  ScheduleRepository scheduleRepository, DayRepository dayRepository, ScheduleService scheduleService) {
        this.springContext = springContext;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.scheduleRepository = scheduleRepository;
        this.dayRepository = dayRepository;
        this.scheduleService = scheduleService;
    }

    public void setController(EmployeeManagementController employeeManagementController) {
        this.employeeManagementController = employeeManagementController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfDaysObs = FXCollections.observableArrayList();
        listOfRoleObs = FXCollections.observableArrayList();
        listOfAMandPM = FXCollections.observableArrayList("AM", "PM");

        listOfDaysObs.setAll(dayRepository.findAllDays());
        listOfRoleObs.setAll(roleRepository.findAllRoleDesc());

        daysToWorkComboBox.setItems(listOfDaysObs);
        roleComboBox.setItems(listOfRoleObs);
        //startCombo.setItems(listOfAMandPM);
        //endCombo.setItems(listOfAMandPM);

        daysToWorkListView.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if(daysToWorkListView.getSelectionModel().getSelectedItem() != null)
                deleteDayButton.setDisable(false);
        });

        daysToWorkComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if(daysToWorkComboBox.getSelectionModel().getSelectedItem() != null)
                addDaysButton.setDisable(false);
            else
                addDaysButton.setDisable(true);
        });

        possibleTimes = FXCollections.observableArrayList(
                "01:00:00", "02:00:00", "03:00:00", "04:00:00",
                "05:00:00", "06:00:00", "07:00:00", "08:00:00",
                "09:00:00", "10:00:00", "11:00:00", "12:00:00",
                "13:00:00", "14:00:00", "15:00:00", "16:00:00",
                "17:00:00", "18:00:00", "19:00:00", "20:00:00",
                "21:00:00", "22:00:00", "23:00:00", "24:00:00");

    }

    public void setLabel(String string, String buttonLabel){
        crudEmployeeLabel.setText(string);
        saveButton.setText(buttonLabel);
    }

    public void setEmployee(Tblemployee selectedEmployee){
        this.selectedEmployee = selectedEmployee;
        setTextFieldsForEdit(this.selectedEmployee);
    }

    private void setTextFieldsForEdit(Tblemployee emp1){
        nameText.setText(emp1.getName());
        emailText.setText(emp1.getEmail());
        addressText.setText(emp1.getAddress());
        phoneText.setText(emp1.getPhone());
    }

    public void handleSaveEmployee(ActionEvent event){
        int listHoursIndex = 0;

        Button selectedButton = (Button)event.getSource();
        TblRoles selectedRole = roleRepository.findRole(roleComboBox.getSelectionModel().getSelectedIndex() + 1);
        Tblemployee newEmp = new Tblemployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText(), selectedRole);

        switch (selectedButton.getText()){
            case "Add":
                try {
                    employeeRepository.save(newEmp);


                    /*
                        Grab all of the times in the Vbox that contains the times
                     */
                    // (This is for testing purposes) List<String> timesOfSelectedDays = new ArrayList<>();
                    for (Node node : timePerDayVBox.getChildren()) {
                        //Converts the node to a Hbox because theres Hbox's in the VBox
                        HBox timesComboBoxHbox = (HBox) node;

                        for (Node node1 : timesComboBoxHbox.getChildren()) {
                            //Convert the node into a combobox
                            ComboBox nodeToCombo = (ComboBox) node1;

                            //Add the times to the list
                            listOfDayHours.add(nodeToCombo.getSelectionModel().getSelectedItem().toString());
                        }
                    }


                    //Save the chosen days for the new employee
                    for (String days : daysToWorkListView.getItems()) {

                               //TODO Make sure to change the time to the textfield.get() || Create a time formatter for the textfields
                        //Tblschedule newSchedTest = new Tblschedule(Time.valueOf("08:00:00"), Time.valueOf("12:00:00"), Date.valueOf(LocalDate.now()), newEmp, dayRepository.findDay(days));

                        String beginTime = listOfDayHours.get(listHoursIndex++);
                        String endTime = listOfDayHours.get(listHoursIndex);

                        Tblschedule testing = new Tblschedule(Time.valueOf(beginTime), Time.valueOf(endTime),
                                Date.valueOf(LocalDate.now()), newEmp, dayRepository.findDay(days));

                        //Save the schedule to the employee
                        scheduleRepository.save(testing);
                    }

                    Stage stage = (Stage)saveButton.getScene().getWindow();
                    ErrorMessages.showInformationMessage("Successful", "Employee Success", "Added " + nameText.getText() + " successfully");

                    stage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Adding Employee Error");
                }

                break;
            case "Save":

                Stage stage = (Stage)saveButton.getScene().getWindow();
                try {
                        employeeService.updateEmployee(
                                nameText.getText(),
                                emailText.getText(),
                                addressText.getText(),
                                phoneText.getText(),
                                selectedEmployee.getId()
                        );





                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Saved");
                stage.close();
                break;
        }
    }

    @FXML
    private void addDaysToList(){
        String selectedDay = daysToWorkComboBox.getSelectionModel().getSelectedItem();
        daysToWorkComboBox.getItems().remove(selectedDay);

        HBox hoursForEachDayHBox = new HBox();
        hoursForEachDayHBox.setSpacing(20);

        ComboBox<String> beginTimes = new ComboBox<>(possibleTimes);
        ComboBox<String> endTimes = new ComboBox<>(possibleTimes);

        //sfslkdflsakjf

        hoursForEachDayHBox.getChildren().addAll(beginTimes, endTimes);

        timePerDayVBox.getChildren().add(hoursForEachDayHBox);
        daysToWorkListView.getItems().add(selectedDay);

        daysToWorkComboBox.setValue(null);
        addDaysButton.setDisable(true);
    }

    @FXML
    private void deleteDayFromList(){
        String selectedDay = daysToWorkListView.getSelectionModel().getSelectedItem();
        daysToWorkComboBox.getItems().add(selectedDay);

        daysToWorkListView.getItems().remove(selectedDay);
        deleteDayButton.setDisable(true);
    }

    @FXML
    private void handleCancelEmployee(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
