package myproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import myproject.ErrorMessages;
import myproject.models.TblEmployee;
import myproject.repositories.EmployeeRepository;
import myproject.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CrudEmployeeController implements Initializable {

    @FXML
    private Button saveButton,
            cancelButton;

    @FXML
    private TextField nameText,
            emailText,
            addressText,
            phoneText;

    @FXML
    private ComboBox<Integer> roleComboBox;

    @FXML
    private Label crudEmployeeLabel;

    private ConfigurableApplicationContext springContext;
    private EmployeeRepository employeeRepository;
    private EmployeeManagementController employeeManagementController;
    private EmployeeService employeeService;

    private TblEmployee tblEmployee;

    @Autowired
    public CrudEmployeeController(ConfigurableApplicationContext springContext, EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.springContext = springContext;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    public void setController(EmployeeManagementController employeeManagementController) {
        this.employeeManagementController = employeeManagementController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setLabel(String string, String buttonLabel){
        crudEmployeeLabel.setText(string);
        saveButton.setText(buttonLabel);
    }

    public void setEmployee(TblEmployee selectedEmployee){
        tblEmployee = selectedEmployee;
        setTextFieldsForEdit(tblEmployee);
    }

    private void setTextFieldsForEdit(TblEmployee emp1){
        nameText.setText(emp1.getName());
        emailText.setText(emp1.getEmail());
        addressText.setText(emp1.getAddress());
        phoneText.setText(emp1.getPhone());
    }

    public void handleSaveEmployee(ActionEvent event){
        Button selectedButton = (Button)event.getSource();
        TblEmployee newEmp = new TblEmployee(nameText.getText(), emailText.getText(), addressText.getText(), phoneText.getText());

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
                    employeeService.updateEmployee(nameText.getText(),
                            emailText.getText(),
                            addressText.getText(),
                            phoneText.getText(),
                            tblEmployee.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Saved");
                stage.close();
                break;
        }
    }

    @FXML
    private void handleCancelEmployee(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
