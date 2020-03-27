package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.SecurePassword;
import myproject.Validation;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.models.Tblusers;
import myproject.repositories.*;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
import myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@Component
public class CrudEmployeeController implements Initializable {

    @FXML
    private Button saveButton,
            cancelButton,
            deleteDayButton,
            addDaysButton,
            changePasswordButton;

    @FXML
    private TextField nameText,
            emailText,
            addressText,
            phoneText,
            startTimeText,
            endTimeText,
            usernameText,
            passwordText,
            newPasswordText;

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
                descriptionLabel,
                currentPasswordLabel,
                changePasswordLabel;

    private ConfigurableApplicationContext springContext;
    private EmployeeRepository employeeRepository;
    private EmployeeRoleUserManagementController employeeRoleManagementController;
    private EmployeeService employeeService;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private UserService userService;
    private ScheduleRepository scheduleRepository;
    private DayRepository dayRepository;
    private ScheduleService scheduleService;

    private ObservableList<String> listOfDaysObs, listOfAMandPM, possibleTimes;
    private ObservableList<TblRoles> listOfRoleObs;

    //The employee returned from the EmployeeManagementController
    private Tblemployee selectedEmployee;
    public boolean changePasswordChecked;

    @Autowired
    public CrudEmployeeController(ConfigurableApplicationContext springContext, EmployeeRepository employeeRepository, EmployeeService employeeService, RoleRepository roleRepository,
                                  ScheduleRepository scheduleRepository, DayRepository dayRepository, ScheduleService scheduleService, UserRepository userRepository, UserService userService) {
        this.springContext = springContext;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.scheduleRepository = scheduleRepository;
        this.dayRepository = dayRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void setController(EmployeeRoleUserManagementController employeeRoleManagementController) {
        this.employeeRoleManagementController = employeeRoleManagementController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changePasswordChecked = false;

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
    }

    public void setEmployeeForEdit(){
        changePasswordButton.setVisible(true);

        currentPasswordLabel.setVisible(false);
        passwordText.setVisible(false);
    }

    @FXML
    private void handleChangePassword(){
        changePasswordButton.setDisable(true);

        currentPasswordLabel.setText("Current Password: ");
        currentPasswordLabel.setVisible(true);
        passwordText.setVisible(true);

        changePasswordLabel.setVisible(true);
        newPasswordText.setVisible(true);

        changePasswordChecked = true;
    }

    public void handleSaveEmployee(ActionEvent event){

        Button selectedButton = (Button)event.getSource();
        TblRoles selectedRole = roleRepository.findRole(roleComboBox.getSelectionModel().getSelectedItem().getRoleName());
        Tblemployee newEmp = new Tblemployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText(), selectedRole);
        Tblemployee updateEmp = selectedEmployee;

        if(!(nameText.getText().isEmpty()
                || emailText.getText().isEmpty()
                || addressText.getText().isEmpty()
                || phoneText.getText().isEmpty()
                || usernameText.getText().isEmpty()
                || passwordText.getText().isEmpty()
                || roleComboBox.getSelectionModel().getSelectedItem() == null)){
            if(usernameText.getText().equals(emailText.getText())){
                if(Validation.validateEmail(usernameText.getText())){
                    switch (selectedButton.getText()){
                        case "Add":
                            try {
                                employeeRepository.save(newEmp);

                                //Creates the hash for the password and the salt
                                byte[] salt = SecurePassword.getSalt();
                                String hashedPassword = SecurePassword.getSecurePassword(passwordText.getText(), salt);
                                Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), salt, hashedPassword, newEmp);

                                userRepository.save(newUser);

                                Stage stage = (Stage)saveButton.getScene().getWindow();
                                ErrorMessages.showInformationMessage("Successful", "Employee Success", "Added " + nameText.getText() + " successfully");

                                stage.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                ErrorMessages.showWarningMessage("Error", "Error Adding Employee",
                                        "Something went wrong trying to add an employee");

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

                                employeeRepository.save(updateEmp);

                                if(changePasswordChecked){
                                    if (SecurePassword.checkPassword(userRepository.findHashFromUserId(updateEmp.getId()),
                                            passwordText.getText(), userRepository.findSaltFromUserId(updateEmp.getId()))) {

                                        Tblusers changePasswordUser = userRepository.findUsername(usernameText.getText());

                                        byte[] salt = SecurePassword.getSalt();
                                        String newPassword = SecurePassword.getSecurePassword(newPasswordText.getText(), salt);

                                        changePasswordUser.setHashedPassword(newPassword);
                                        changePasswordUser.setSaltPassword(salt);

                                        userRepository.save(changePasswordUser);

                                        ErrorMessages.showInformationMessage("Success", "Password Changed Successfully",
                                                "The password was changed successfully");
                                        stage.close();
                                        System.out.println("Saved");
                                    } else {
                                        ErrorMessages.showWarningMessage("Password Mismatch", "Passwords do not equal",
                                                "Passwords do not match, please re-check your password");
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
