package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import myproject.ErrorMessages;
import myproject.SecurePassword;
import myproject.Validation;
import myproject.controllers.WelcomeLoginSignup.LoginController;
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
import java.util.List;
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
    private Tooltip phoneTip;

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
                changePasswordLabel,
                passwordRequirementText,
                passwordRequirementTitleText;

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

    private List<String> listOfEmails;

    private Tblusers currUser;

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
        //get the current user
        String currentUser = LoginController.userStore;
        currUser = userRepository.findUsername(currentUser);

        changePasswordChecked = false;

        listOfDaysObs = FXCollections.observableArrayList();
        listOfRoleObs = FXCollections.observableArrayList();

        listOfDaysObs.setAll(dayRepository.findAllDays());

        //if they are not an owner, they are a manager and cannot create owner accounts
        listOfRoleObs.setAll(roleRepository.findNotOwnerAndManagerRoles());

        roleComboBox.setItems(listOfRoleObs);

        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null)
                descriptionLabel.setText(roleRepository.findRoleDescFromRoleId(roleComboBox.getSelectionModel().getSelectedItem().getRoleId()));
        });

        phoneTip.setText("ex.\t 281-545-2213\n" +
                "\t(832) 939-9182");

        //Whatever you type in email it copies to username
        emailText.textProperty().bindBidirectional(usernameText.textProperty());

        passwordRequirementText.setFont(Font.font("System", 10));
        passwordRequirementText.setText("- Password must be 8 to 30 characters long\n" +
                                        "- Password must contain the following characters\n" +
                                        "\t- Uppercase characters [A-Z]\n" +
                                        "\t- Lowercase characters [a-z]\n" +
                                        "\t- Contains digit [0-9]\n" +
                                        "\t- Contains special characters (!, $, #, etc)");

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

        listOfRoleObs.setAll(roleRepository.findNotOwnerAndManagerRoles());
        roleComboBox.setItems(listOfRoleObs);

        roleComboBox.getSelectionModel().select(emp1.getRole());
        usernameText.setText(emp1.getUser().getUsername());
    }

    public void setEmployeeForEdit(){
        changePasswordButton.setVisible(true);
        currentPasswordLabel.setVisible(false);
        passwordText.setVisible(false);

        passwordRequirementText.setVisible(false);
        passwordRequirementTitleText.setVisible(false);
    }

    @FXML
    private void handleChangePassword(){
        changePasswordButton.setDisable(true);

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            currentPasswordLabel.setText("New Password: ");

            changePasswordLabel.setVisible(false);
            newPasswordText.setVisible(false);
        } else {
            currentPasswordLabel.setText("Current Password: ");

            changePasswordLabel.setVisible(true);
            newPasswordText.setVisible(true);
        }

        currentPasswordLabel.setVisible(true);
        passwordText.setVisible(true);

        changePasswordChecked = true;

        passwordRequirementText.setVisible(true);
        passwordRequirementTitleText.setVisible(true);
    }

    public void handleSaveEmployee(ActionEvent event){
        Button selectedButton = (Button)event.getSource();

        if(!(nameText.getText().isEmpty() || emailText.getText().isEmpty() || addressText.getText().isEmpty()
                || phoneText.getText().isEmpty() || usernameText.getText().isEmpty() || roleComboBox.getSelectionModel().getSelectedItem() == null)) {

            TblRoles selectedRole = roleRepository.findRole(roleComboBox.getSelectionModel().getSelectedItem().getRoleId());
            Tblemployee newEmp = new Tblemployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText(), selectedRole);
            Tblemployee updateEmp = selectedEmployee;

                switch (selectedButton.getText()) {
                    case "Add":
                        listOfEmails = employeeRepository.findAllEmails();
                        Pair[] error = Validation.validateCrudAccount(nameText.getText(), emailText.getText(), addressText.getText(),
                                phoneText.getText(), usernameText.getText(), roleComboBox.getSelectionModel().getSelectedItem(), listOfEmails);

                        try {
                            Pair[] passwordError = Validation.validatePasswordRequirement(passwordText.getText());

                            if (!passwordText.getText().isEmpty()) {
                                if(newEmp.getRole().getRoleName().equals("Owner")
                                && employeeRepository.numberOfOwner() > 0){

                                    ErrorMessages.showErrorMessage("Error",
                                            "Owner already exists",
                                            "There is already an employee with the Owner role. Only one Owner can exist at a time.");
                                }
                                else{
                                    if(!(Boolean)error[0].getKey()) {
                                        System.out.println("Adding Password.....");
                                        if (!(Boolean) passwordError[0].getKey()) {
                                            employeeRepository.save(newEmp);

                                            //Creates the hash for the password and the salt
                                            byte[] salt = SecurePassword.getSalt();
                                            String hashedPassword = SecurePassword.getSecurePassword(passwordText.getText(), salt);
                                            Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), salt, hashedPassword, newEmp);

                                            userRepository.save(newUser);

                                            Stage stage = (Stage) saveButton.getScene().getWindow();
                                            ErrorMessages.showInformationMessage("Successful", "Employee Success", "Added " + nameText.getText() + " successfully");

                                            stage.close();
                                        } else {
                                            ErrorMessages.showErrorMessage("Password Error", "Password validation error", passwordError[0].getValue().toString());
                                        }
                                    }
                                    else{
                                        ErrorMessages.showErrorMessage("Error", "Invalid values provided", error[0].getValue().toString());
                                    }
                                }
                            } else {
                                ErrorMessages.showErrorMessage("Error", "Invalid values", "Please provides values for each field");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ErrorMessages.showWarningMessage("Error", "Error Adding Employee",
                                    "Something went wrong trying to add an employee");

                            System.out.println("Adding Employee Error");
                        }

                        break;
                    case "Save":
                        //get all emails except the current user's email to check if someone else has that email
                        listOfEmails = employeeRepository.findAllEmailsExcept(selectedEmployee.getEmail());
                        Pair[] error1 = Validation.validateCrudAccount(nameText.getText(), emailText.getText(), addressText.getText(),
                                phoneText.getText(), usernameText.getText(), roleComboBox.getSelectionModel().getSelectedItem(), listOfEmails);

                        Stage stage = (Stage) saveButton.getScene().getWindow();

                        try {
                            if (!(Boolean) error1[0].getKey()) {
                                //if the selected employee is an owner and they are trying to change role to something other than owner
                                //and there is one owner in the system (themselves) <- may not need this check
                                if (updateEmp.getRole().getRoleName().equals("Owner")
                                        && !(roleComboBox.getSelectionModel().getSelectedItem().getRoleName().equals("Owner"))
                                        && employeeRepository.numberOfOwner() == 1) {
                                    ErrorMessages.showErrorMessage("Error",
                                            "Cannot demote Owner",
                                            "There must be exactly one Owner in the system.");
                                } else {
                                    //changed this to use setters since previous method doesn't update user
                                    updateEmp.setName(nameText.getText());
                                    updateEmp.setEmail(emailText.getText());
                                    updateEmp.setAddress(addressText.getText());
                                    updateEmp.setPhone(phoneText.getText());
                                    updateEmp.setRole(roleComboBox.getSelectionModel().getSelectedItem());
                                    updateEmp.getUser().setUsername(usernameText.getText());

                                    //if you are changing the role to owner and there is already an owner existing
                                    //and the existing owner is the selected employee
                                    if (updateEmp.getRole().getRoleName().equals("Owner")
                                            && employeeRepository.numberOfOwner() > 0
                                            && !(employeeRepository.numberOfOwnerGetEmp().getId() == updateEmp.getId())) {
                                        ErrorMessages.showErrorMessage("Error",
                                                "Owner already exists",
                                                "There is already an employee with the Owner role. Only one Owner can exist at a time.");

                                        //reset the employee's role back to their original role
                                        TblRoles originalRole = employeeRepository.findEmployeeById(updateEmp.getId()).getRole();
                                        updateEmp.setRole(originalRole);
                                    } else {
                                        employeeRepository.save(updateEmp);

                                        if (changePasswordChecked) {
                                            //Changing passwords as a owner
                                            if (currUser.getEmployee().getRole().getRoleName().equals("Owner")) {
                                                System.out.println("Save as Owner");
                                                Pair[] ownerChangePassword = Validation.validatePasswordRequirement(passwordText.getText());

                                                if (!(Boolean) ownerChangePassword[0].getKey()) {
                                                    System.out.println("Onwer Saved password");
                                                    Tblusers changePasswordUser = userRepository.findUsername(usernameText.getText());

                                                    byte[] salt = SecurePassword.getSalt();
                                                    String newPassword = SecurePassword.getSecurePassword(passwordText.getText(), salt);

                                                    changePasswordUser.setHashedPassword(newPassword);
                                                    changePasswordUser.setSaltPassword(salt);

                                                    userRepository.save(changePasswordUser);
                                                    System.out.println("New Password: " + passwordText.getText());
                                                    ErrorMessages.showInformationMessage("Success", "Password Changed Successfully",
                                                            "The password was changed successfully");
                                                    stage.close();
                                                } else {
                                                    ErrorMessages.showErrorMessage("Change Password Error", "Password validation error", ownerChangePassword[0].getValue().toString());
                                                }
                                            } else {
                                                if (SecurePassword.checkPassword(userRepository.findHashFromUserId(updateEmp.getId()),
                                                        passwordText.getText(), userRepository.findSaltFromUserId(updateEmp.getId()))) {

                                                    Tblusers changePasswordUser = userRepository.findUsername(usernameText.getText());

                                                    Pair[] changePasswordError = Validation.validatePasswordRequirement(newPasswordText.getText());

                                                    if (!(Boolean) changePasswordError[0].getKey()) {
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
                                                        ErrorMessages.showErrorMessage("Change Password Error", "Password validation error", changePasswordError[0].getValue().toString());
                                                    }
                                                } else {
                                                    ErrorMessages.showWarningMessage("Password Mismatch", "Passwords do not equal",
                                                            "Password doesn't match the current password");
                                                }
                                            }
                                        } else {
                                            ErrorMessages.showInformationMessage("Success", "Employee Changed Successfully",
                                                    "The employee was saved");

                                            stage.close();
                                        }
                                    }
                                }

                            } else {
                                ErrorMessages.showErrorMessage("Error", "Invalid values provided", error1[0].getValue().toString());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
        } else {
            ErrorMessages.showErrorMessage("Error", "Invalid values", "Please provides values for each field");
        }
    }

    @FXML
    private void handleCancelEmployee(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
