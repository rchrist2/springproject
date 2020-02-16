package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myproject.models.Tblemployee;
import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class EmployeeManagementController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Button editEmployeeButton,
            deleteEmployeeButton;

    @FXML
    private TableView<Tblemployee> employeeTableView;

    public TableColumn<Tblemployee, String>
            idColumn,
            nameColumn,
            emailColumn,
            addressColumn,
            phoneColumn,
            roleColumn;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    private ObservableList<Tblemployee> listOfEmployees;
    private FilteredList<Tblemployee> filteredListOfEmployees;

    public Tblemployee selectedEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize the observable list and add all the employees to the list
        listOfEmployees = FXCollections.observableArrayList();
        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        reloadEmployeeTableView();
        setDataForEmployeeTableView();
        addActionListenersForCrudButtons();
    }

    private void reloadEmployeeTableView(){
        listOfEmployees.clear();

        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        filteredListOfEmployees = new FilteredList<>(listOfEmployees);

        employeeTableView.setItems(filteredListOfEmployees);
    }

    private void setDataForEmployeeTableView(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        //roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void handleCrudButton(ActionEvent event){
        Button clickedButton = (Button)event.getSource();
        Tblemployee emp = employeeTableView.getSelectionModel().getSelectedItem();

        switch(clickedButton.getText()){
            case "Add New Employee":
                System.out.println("Add a Employee");
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudEmployee.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Add Employee");

                    CrudEmployeeController crudEmployeeController = fxmlLoader.getController();
                    crudEmployeeController.setLabel("Add Employee", "Add");
                    crudEmployeeController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
                    reloadEmployeeTableView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Edit Employee":
                System.out.println("Edit a Employee");

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudEmployee.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Edit Employee");

                    CrudEmployeeController crudEmployeeController = fxmlLoader.getController();
                    crudEmployeeController.setLabel("Edit Employee", "Save");
                    crudEmployeeController.setEmployee(emp);
                    crudEmployeeController.setController(this);

                    stage.setScene(new Scene(parent));

                    stage.showAndWait();
                    reloadEmployeeTableView();
                    resetButtons();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Delete":
                System.out.println("Delete a Employee");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Employee");
                alert.setHeaderText("Are you sure?");

                Optional<ButtonType> choice = alert.showAndWait();
                if(choice.get() == ButtonType.OK) {
                    if (emp != null) {
                        employeeRepository.delete(emp);
                    }

                    reloadEmployeeTableView();
                    resetButtons();
                    System.out.println("Table View reloaded!");
                } else {

                }
                break;
        }
    }

    private void addActionListenersForCrudButtons(){
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                editEmployeeButton.setDisable(false);
                deleteEmployeeButton.setDisable(false);
            }
        });
    }

    private void resetButtons(){
        editEmployeeButton.setDisable(true);
        deleteEmployeeButton.setDisable(true);
    }
}
