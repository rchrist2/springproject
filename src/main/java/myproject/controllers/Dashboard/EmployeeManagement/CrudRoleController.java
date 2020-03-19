package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.repositories.RoleRepository;
import myproject.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class CrudRoleController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField roleText;

    @FXML
    private TextArea roleDescTextA;

    @FXML
    private Button saveRoleButton,
                resetButton;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    private EmployeeRoleManagementController employeeRoleManagementController;
    private TblRoles selectedRole;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setController(EmployeeRoleManagementController employeeRoleManagementController) {
        this.employeeRoleManagementController = employeeRoleManagementController;
    }

    public void setLabel(String string, String buttonLabel){
        titleLabel.setText(string);
        saveRoleButton.setText(buttonLabel);
    }

    public void setRole(TblRoles selectedRole){
        this.selectedRole = selectedRole;
        setTextFieldsForEdit(this.selectedRole);
    }

    private void setTextFieldsForEdit(TblRoles roles){
        roleText.setText(roles.getRoleName());
        roleDescTextA.setText(roles.getRoleDesc());
    }

    //This will act as adding a new role and updating a role
    @FXML
    private void handleSaveRole(ActionEvent event){
        Button button = (Button) event.getSource();
        Stage stage = (Stage)saveRoleButton.getScene().getWindow();

        switch (button.getText()) {
            case "Add":
                try {
                    System.out.println("Add a role");

                    TblRoles newRole = new TblRoles(roleText.getText(), roleDescTextA.getText());

                    roleRepository.save(newRole);
                    stage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Update":
                try{
                    System.out.println("Update a role");

                    roleService.updateRole(roleText.getText(), roleDescTextA.getText(),
                            selectedRole.getRoleId());

                } catch (Exception e){
                    e.printStackTrace();
                }

                stage.close();
                break;
        }
    }

    @FXML
    private void handleCancelButton(){
        Stage stage = (Stage)saveRoleButton.getScene().getWindow();
        stage.close();
    }
}
