package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.Tblemployee;
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

        employeeComboBox.setItems(listOfEmployeeNames);
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
        passwordText.setText(user.getPassword());
        employeeComboBox.setValue(userRepository.findEmailFromUser(user.getUserId()));
    }

    @FXML
    private void handleSaveAccount(ActionEvent event){
        Button button = (Button) event.getSource();

        Tblemployee selectedEmployee = employeeRepository.findEmployeeFromEmployeeEmail(employeeComboBox.getSelectionModel().getSelectedItem());

        Tblusers newUser = new Tblusers(usernameText.getText(), passwordText.getText(), selectedEmployee);

        switch (button.getText()){
            case "Add":
                try{
                    userRepository.save(newUser);

                    Stage stage = (Stage)saveButton.getScene().getWindow();
                    ErrorMessages.showInformationMessage("Successful",
                            "User Account Success",
                            "Added user account successfully");

                    stage.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case "Update":
                try{
                    Stage stage = (Stage)saveButton.getScene().getWindow();
                    ErrorMessages.showInformationMessage("Successful", "User Account Updated", "Updated user account successfully");

                    System.out.println("Employee Id: " + selectedEmployee.getId() + "\nUser Id: " + user.getUserId());

                    userService.insertUser(usernameText.getText(), passwordText.getText(), selectedEmployee.getId(), user.getUserId());

                    stage.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @FXML
    private void handleCancelAccount(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
