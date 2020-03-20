package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private RoleService roleService;

    private ObservableList<String> roleObservableList;
    private EmployeeRoleManagementController employeeRoleManagementController;
    private TblRoles selectedRole;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleObservableList = FXCollections.observableArrayList(Arrays.asList("Manager", "Employee"));

        roleComboBox.setItems(roleObservableList);

        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null && newV.equals("Employee")){
                otherRoleLabel.setDisable(false);
                roleText.setDisable(false);
            } else {
                otherRoleLabel.setDisable(true);
                roleText.setDisable(true);
            }
        });
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
        roleComboBox.setDisable(true);
        roleText.setDisable(false);
        setTextFieldsForEdit(this.selectedRole);
    }

    private void setTextFieldsForEdit(TblRoles roles){
        if(roles.getRoleName().equals("Manager") || roles.getRoleName().equals("Owner")){
            roleComboBox.setValue(roles.getRoleName());
            otherRoleLabel.setDisable(true);
            roleText.setDisable(true);
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

        switch (button.getText()) {
            case "Add":
                TblRoles newRole = new TblRoles();

                try {
                    System.out.println("Add a role");

                    if(roleComboBox.getSelectionModel().getSelectedItem().equals("Employee"))
                        newRole.setRoleName(roleText.getText());
                    else
                        newRole.setRoleName(roleComboBox.getSelectionModel().getSelectedItem());

                    newRole.setRoleDesc(roleDescTextA.getText());
                    roleRepository.save(newRole);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                stage.close();
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
