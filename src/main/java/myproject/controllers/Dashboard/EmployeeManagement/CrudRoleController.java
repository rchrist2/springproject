package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import myproject.ErrorMessages;
import myproject.Validation;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.models.Tblusers;
import myproject.repositories.RoleRepository;
import myproject.repositories.UserRepository;
import myproject.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static myproject.Validation.checkRoleDuplicates;
import static myproject.Validation.isAlpha;

@Component
public class CrudRoleController implements Initializable {

    @FXML
    private Label titleLabel,
                otherRoleLabel;

    @FXML
    private TextField roleText;

    @FXML
    private TextArea roleDescTextA;

    @FXML
    private Button saveRoleButton,
                resetButton;

    @FXML
    private ComboBox<String> roleComboBox;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    private ObservableList<String> roleObservableList;
    private List<String> listOfRoles;
    private EmployeeRoleUserManagementController employeeRoleManagementController;
    private TblRoles selectedRole;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            roleObservableList = FXCollections.observableArrayList(Arrays.asList("Manager","Employee"));
        }
        else{
            roleObservableList = FXCollections.observableArrayList(Arrays.asList("Employee"));
        }

        roleComboBox.setItems(roleObservableList);

        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null && newV.equals("Employee")){
                otherRoleLabel.setDisable(false);
                roleText.setDisable(false);
                roleText.setText(newV);
            } else {
                otherRoleLabel.setDisable(true);
                roleText.setDisable(true);
                roleText.setText(newV);
            }
        });
    }

    public void setController(EmployeeRoleUserManagementController employeeRoleManagementController) {
        this.employeeRoleManagementController = employeeRoleManagementController;
    }

    public void setLabel(String string, String buttonLabel){
        titleLabel.setText(string);
        saveRoleButton.setText(buttonLabel);
    }

    public void setRole(TblRoles selectedRole){
        this.selectedRole = selectedRole;
        roleComboBox.setDisable(true);
        roleText.setDisable(false);
        setTextFieldsForEdit(this.selectedRole);
    }

    private void setTextFieldsForEdit(TblRoles roles){
        if(roles.getRoleName().equals("Manager") || roles.getRoleName().equals("Owner")){
            roleComboBox.setValue(roles.getRoleName());
            otherRoleLabel.setDisable(true);
            roleText.setDisable(true);
            roleText.setText(roles.getRoleName());
            roleComboBox.setDisable(true);
        } else {
            roleComboBox.setValue("Employee");
            otherRoleLabel.setDisable(false);
            roleText.setDisable(false);
            roleText.setText(roles.getRoleName());
        }

        roleDescTextA.setText(roles.getRoleDesc());
    }

    //This will act as adding a new role and updating a role
    @FXML
    private void handleSaveRole(ActionEvent event){
        Button button = (Button) event.getSource();
        Stage stage = (Stage)saveRoleButton.getScene().getWindow();

        //compare currently selected role name against all other role names in database
        listOfRoles = roleRepository.findAllRoleNameExcept(selectedRole.getRoleName());

        if(!(roleText.getText().isEmpty()
                || roleDescTextA.getText().isEmpty()
                || roleComboBox.getSelectionModel().getSelectedItem().isEmpty())){

            Pair[] error = checkRoleDuplicates(roleText.getText(), listOfRoles);

            switch (button.getText()) {
                case "Add":
                    TblRoles newRole = new TblRoles();

                    try {
                        System.out.println("Add a role");

                        if(roleComboBox.getSelectionModel().getSelectedItem().equals("Employee"))
                            newRole.setRoleName(roleText.getText());
                        else
                            newRole.setRoleName(roleComboBox.getSelectionModel().getSelectedItem());

                        if(!(Boolean)error[0].getKey()) {
                            newRole.setRoleDesc(roleDescTextA.getText());
                            roleRepository.save(newRole);

                            stage.close();
                        } else {
                            ErrorMessages.showErrorMessage("Error", "Invalid values provided", error[0].getValue().toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Update":
                    try{
                        System.out.println("Update a role");

                        if(!(Boolean)error[0].getKey()) {
                            roleService.updateRole(roleText.getText(), roleDescTextA.getText(),
                                    selectedRole.getRoleId());

                            stage.close();
                        } else {
                            ErrorMessages.showErrorMessage("Error", "Invalid values provided", error[0].getValue().toString());
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "There are empty fields",
                    "Please select items from drop-down menus or enter text for fields");
        }

    }

    @FXML
    private void handleCancelButton(){
        Stage stage = (Stage)saveRoleButton.getScene().getWindow();
        stage.close();
    }
}
