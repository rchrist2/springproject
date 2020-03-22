package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
            daysToWorkComboBox,
            startCombo,
            endCombo;

    @FXML
    private ComboBox<TblRoles> roleComboBox;

    @FXML
    private Label crudEmployeeLabel,
                descriptionLabel;

    private ConfigurableApplicationContext springContext;
    private EmployeeRepository employeeRepository;
    private EmployeeRoleUserManagementController employeeRoleManagementController;
    private EmployeeService employeeService;
    private RoleRepository roleRepository;
    private ScheduleRepository scheduleRepository;
    private DayRepository dayRepository;
    private ScheduleService scheduleService;

    private ObservableList<String> listOfDaysObs, listOfAMandPM, possibleTimes;
    private ObservableList<TblRoles> listOfRoleObs;

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

    public void setController(EmployeeRoleUserManagementController employeeRoleManagementController) {
        this.employeeRoleManagementController = employeeRoleManagementController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfDaysObs = FXCollections.observableArrayList();
        listOfRoleObs = FXCollections.observableArrayList();

        listOfDaysObs.setAll(dayRepository.findAllDays());
        listOfRoleObs.setAll(roleRepository.findAll());

        roleComboBox.setItems(listOfRoleObs);

        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null)
                descriptionLabel.setText(roleRepository.findRoleDescFromRoleId(roleComboBox.getSelectionModel().getSelectedItem().getRoleId()));
        });
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

        Button selectedButton = (Button)event.getSource();
        TblRoles selectedRole = roleRepository.findRole(roleComboBox.getSelectionModel().getSelectedIndex() + 1);
        Tblemployee newEmp = new Tblemployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText(), selectedRole);

        switch (selectedButton.getText()){
            case "Add":
                try {
                    employeeRepository.save(newEmp);

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
                                selectedEmployee.getId(),
                                roleComboBox.getSelectionModel().getSelectedItem().getRoleId()
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
