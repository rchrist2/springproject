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
import myproject.models.Tblusers;
import myproject.repositories.*;
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
            endTimeText,
            usernameText,
            passwordText;

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
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;
    private DayRepository dayRepository;
    private ScheduleService scheduleService;

    private ObservableList<String> listOfDaysObs, listOfAMandPM, possibleTimes;
    private ObservableList<TblRoles> listOfRoleObs;

    //The employee returned from the EmployeeManagementController
    private Tblemployee selectedEmployee;

    @Autowired
    public CrudEmployeeController(ConfigurableApplicationContext springContext, EmployeeRepository employeeRepository, EmployeeService employeeService, RoleRepository roleRepository,
                                  ScheduleRepository scheduleRepository, DayRepository dayRepository, ScheduleService scheduleService, UserRepository userRepository) {
        this.springContext = springContext;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.scheduleRepository = scheduleRepository;
        this.dayRepository = dayRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
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
        roleComboBox.getSelectionModel().select(emp1.getRole());
        usernameText.setText(emp1.getUser().getUsername());
        passwordText.setText(emp1.getUser().getPassword());
    }

    public void handleSaveEmployee(ActionEvent event){

        Button selectedButton = (Button)event.getSource();
        TblRoles selectedRole = roleRepository.findRole(roleComboBox.getSelectionModel().getSelectedItem().getRoleName());
        Tblemployee newEmp = new Tblemployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText(), selectedRole);
        Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), newEmp);
        Tblemployee updateEmp = selectedEmployee;

        if(!(nameText.getText().isEmpty()
                || emailText.getText().isEmpty()
                || addressText.getText().isEmpty()
                || phoneText.getText().isEmpty()
                || usernameText.getText().isEmpty()
                || passwordText.getText().isEmpty()
                || roleComboBox.getSelectionModel().getSelectedItem() == null)){
            if(usernameText.getText().equals(emailText.getText())){
                if(usernameText.getText().contains("@") && usernameText.getText().contains(".com")){
                    switch (selectedButton.getText()){
                        case "Add":
                            try {
                                employeeRepository.save(newEmp);
                                userRepository.save(newUser);


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
                                //changed this to use setters since previous method doesn't update user
                                updateEmp.setName(nameText.getText());
                                updateEmp.setEmail(emailText.getText());
                                updateEmp.setAddress(addressText.getText());
                                updateEmp.setPhone(phoneText.getText());
                                updateEmp.setRole(roleComboBox.getSelectionModel().getSelectedItem());
                                updateEmp.getUser().setUsername(usernameText.getText());
                                updateEmp.getUser().setPassword(passwordText.getText());
                                employeeRepository.save(updateEmp);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println("Saved");
                            stage.close();
                            break;
                    }
                }
                else{
                    ErrorMessages.showErrorMessage("Error!","Username is not an email address",
                            "Username must be a valid email address.");
                }
            }
            else{
                ErrorMessages.showErrorMessage("Error!","Email does not match",
                        "Username and employee email must be the same.");
            }

        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }
    }

    @FXML
    private void handleCancelEmployee(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
