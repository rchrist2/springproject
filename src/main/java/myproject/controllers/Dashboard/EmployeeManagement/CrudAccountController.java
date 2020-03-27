/*
package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.SecurePassword;
import myproject.Validation;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.UserRepository;
import myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CrudAccountController implements Initializable {

    @FXML
    private Label crudAccountLabel;

    @FXML
    private TextField usernameText,
                    passwordText;

    @FXML
    private Button saveButton,
                cancelButton;

    @FXML
    private ComboBox<String> employeeComboBox;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserService userService;

    private Tblusers user;
    private EmployeeRoleUserManagementController employeeRoleUserManagementController;

    private ObservableList<String> listOfEmployeeNames;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfEmployeeNames = FXCollections.observableArrayList();
        listOfEmployeeNames.setAll(userRepository.listOfEmployeeWithoutAccounts());

        //show different prompt text in employee combo box if no employees are found
        if(!(listOfEmployeeNames.isEmpty())){
            employeeComboBox.setItems(listOfEmployeeNames);
            employeeComboBox.setPromptText("Select Employee");

            employeeComboBox.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty || item == null) {
                        setText("Select Employee");
                    } else {
                        setText(item);
                    }
                }
            });
        }
        else{
            employeeComboBox.setPromptText("No Employees w/o User");
        }

        //when the user selects an employee, set that email address in the username textfield
        employeeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                usernameText.setText(employeeComboBox.getSelectionModel().getSelectedItem());
            }
        });

    }

    public void setController(EmployeeRoleUserManagementController employeeRoleUserManagementController){
        this.employeeRoleUserManagementController = employeeRoleUserManagementController;
    }

    public void setAccount(Tblusers user){
        this.user = user;
        setTextFieldsForEdit(this.user);
    }

    public void setLabel(String string, String buttonLabel){
        crudAccountLabel.setText(string);
        saveButton.setText(buttonLabel);
    }

    private void setTextFieldsForEdit(Tblusers user){
        usernameText.setText(user.getUsername());

        if(user.getPassword() == null){
            passwordText.setText(null);
        } else {
            passwordText.setText(user.getPassword());
        }

        employeeComboBox.setValue(userRepository.findEmailFromUser(user.getUserId()));
    }

    @FXML
    private void handleSaveAccount(ActionEvent event){
        Button button = (Button) event.getSource();

        Tblemployee selectedEmployee = employeeRepository.findEmployeeFromEmployeeEmail(employeeComboBox.getSelectionModel().getSelectedItem());

        //Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), selectedEmployee);

        if(!(usernameText.getText().isEmpty()
                || passwordText.getText().isEmpty()
                || employeeComboBox.getSelectionModel().getSelectedItem().isEmpty())){
            if(usernameText.getText().equals(employeeComboBox.getSelectionModel().getSelectedItem())) {
                if (Validation.validateEmail(usernameText.getText())) {
                    switch (button.getText()){
                        case "Add":
                            try{
                                if(usernameText.getText().equals(selectedEmployee.getEmail())){

                                    byte[] salt = SecurePassword.getSalt();
                                    String hashedPassword = SecurePassword.getSecurePassword(passwordText.getText(), salt);
                                    Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), salt, hashedPassword, selectedEmployee);

                                    userRepository.save(newUser);

                                    Stage stage = (Stage)saveButton.getScene().getWindow();
                                    ErrorMessages.showInformationMessage("Successful",
                                            "User Account Success",
                                            "Added user account successfully");

                                    stage.close();
                                }
                                else{
                                    ErrorMessages.showErrorMessage("Error!","Email does not match",
                                            "Username and employee email must be the same.");
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            break;
                        case "Update":
                            try{
                                byte[] salt;
                                String hashed_password;

                                Stage stage = (Stage)saveButton.getScene().getWindow();
                                System.out.println("Employee Id: " + selectedEmployee.getId() + "\nUser Id: " + user.getUserId());

                                //Checks to see if the user has a null salt or a null password
                                if(user.getSaltPassword() == null || user.getHashedPassword() == null){
                                    user.setSaltPassword(null);
                                    user.setHashedPassword(null);

                                    salt = SecurePassword.getSalt();
                                    hashed_password = SecurePassword.getSecurePassword(passwordText.getText(), salt);
                                }
                                else {
                                    salt = user.getSaltPassword();
                                    hashed_password = user.getHashedPassword();
                                }


                                userService.insertUser(usernameText.getText(), passwordText.getText(), selectedEmployee.getId(),
                                        salt, hashed_password, user.getUserId());

                                ErrorMessages.showInformationMessage("Successful", "User Account Updated",
                                        "Updated user account successfully");

                                stage.close();
                            } catch (Exception e){
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
    private void handleCancelAccount(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
*/
