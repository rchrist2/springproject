package myproject.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import myproject.models.Tblschedule;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
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
    private TextField searchText;

    @FXML
    private TableView<Tblemployee> employeeTableView;

    public TableColumn<Tblemployee, String>
            nameColumn,
            emailColumn,
            addressColumn,
            phoneColumn,
            roleColumn,
            hoursColumn,
            daysColumn;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private EmployeeService employeeService;

    private ObservableList<Tblemployee> listOfEmployees;
    private FilteredList<Tblemployee> filteredListOfEmployees;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize the observable list and add all the employees to the list
        listOfEmployees = FXCollections.observableArrayList();
        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        reloadEmployeeTableView();
        setDataForEmployeeTableView();
        addActionListenersForCrudButtons();

        //Filters the employee management view list
        searchText.setOnKeyReleased(event -> {
            filteredListOfEmployees.setPredicate(emp -> emp.getName().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getEmail().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getAddress().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getPhone().contains(searchText.getText())
                    || emp.employeeSchedule().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getRole().getRoleDesc().toLowerCase().contains(searchText.getText().toLowerCase()));
        });
    }

    //Reloads (refreshes) the employee tableview
    private void reloadEmployeeTableView(){
        listOfEmployees.clear();

        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        filteredListOfEmployees = new FilteredList<>(listOfEmployees);

        employeeTableView.setItems(filteredListOfEmployees);
    }

    //Sets the data for each of the columns in the table view
    private void setDataForEmployeeTableView(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(role -> new ReadOnlyStringWrapper(role.getValue().getRole().getRoleDesc()));

        hoursColumn.setCellValueFactory(hours -> new ReadOnlyStringWrapper(hours.getValue().employeeHours()));
        daysColumn.setCellValueFactory(days -> new ReadOnlyStringWrapper(days.getValue().employeeSchedule()));
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void handleCrudButton(ActionEvent event){
        //Grab the button that was clicked
        Button clickedButton = (Button)event.getSource();

        //Hold the selected data of the employee
        Tblemployee emp = employeeTableView.getSelectionModel().getSelectedItem();

        //Grabs the text of the button that was clicked
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
                alert.setContentText("You are about to delete: " + emp.getName());

                Optional<ButtonType> choice = alert.showAndWait();
                if(choice.get() == ButtonType.OK) {
                    if (emp != null) {

                        /*
                          ON DELETE CASCADE works in a way we can't apply, so we have to delete
                          each row one by one
                        */
                        scheduleService.deleteSchedule(emp.getId());

                        employeeService.deleteEmployee(emp.getId());
                    }

                    reloadEmployeeTableView();
                    resetButtons();
                    System.out.println("Table View reloaded!");
                } else {

                }
                break;
        }
    }

    //This will enable/disable the edit and delete buttons if a employee was chosen from the table view
    private void addActionListenersForCrudButtons(){
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                editEmployeeButton.setDisable(false);
                deleteEmployeeButton.setDisable(false);
            }
        });
    }

    //Resets the buttons back to its default value (disable = true)
    private void resetButtons(){
        editEmployeeButton.setDisable(true);
        deleteEmployeeButton.setDisable(true);
    }
}
