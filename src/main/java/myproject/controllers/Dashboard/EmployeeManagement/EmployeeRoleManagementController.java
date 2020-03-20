package myproject.controllers.Dashboard.EmployeeManagement;

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
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myproject.ErrorMessages;
import myproject.controllers.Dashboard.DashboardController;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.RoleRepository;
import myproject.repositories.ScheduleRepository;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Component
public class EmployeeRoleManagementController implements Initializable {


    /*=============================
           Employee Controls
    =============================*/
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
            employeeRoleColumn,
            descriptionColumn,
            hoursColumn,
            daysColumn;

    @FXML
    private TabPane managementTabPane;

    /*=============================
             Role Controls
    =============================*/
    @FXML
    private Label roleTitleLabel;

    @FXML
    private TextField roleText;

    @FXML
    private TextArea roleDescTextA;

    @FXML
    private TableView<TblRoles> roleTableView;

    @FXML
    private Button saveRoleButton,
            editRoleButton,
            resetRoleButton,
            deleteRoleButton;

    @FXML
    private TableColumn<TblRoles, String>
            roleColumn,
            roleDescColumn;

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

    @Autowired
    private RoleRepository roleRepository;

    private ObservableList<Tblemployee> listOfEmployees;
    private ObservableList<TblRoles> listOfRoles;

    private FilteredList<Tblemployee> filteredListOfEmployees;
    private FilteredList<TblRoles> filteredListOfRoles;

    private LocalDate today = LocalDate.now();
    public LocalDate sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
    public LocalDate saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize the observable list and add all the employees to the list
        listOfEmployees = FXCollections.observableArrayList();
        listOfRoles = FXCollections.observableArrayList();

        listOfEmployees.addAll(employeeRepository.findAllEmployee());
        listOfRoles.addAll(roleRepository.findAll());

        reloadEmployeeTableView();
        setDataForEmployeeTableView();
        addActionListenersForCrudButtons();

        reloadRoleTableView();
        setDataForRoleTableView();
        addActionListenersForRoleCrudButtons();

        //Filters the employee management view list
        searchText.setOnKeyReleased(event -> {
            filteredListOfEmployees.setPredicate(emp -> emp.getName().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getEmail().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getAddress().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getPhone().contains(searchText.getText())
                    || emp.employeeSchedule(Date.valueOf(sunday), Date.valueOf(saturday)).toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getRole().getRoleName().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getRole().getRoleDesc().toLowerCase().contains(searchText.getText().toLowerCase()));
        });

        managementTabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            titleLabel.setText(t1.getText() + " Management");
        });

    }


    //Reloads (refreshes) the employee tableview
    private void reloadEmployeeTableView() {
        listOfEmployees.clear();

        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        filteredListOfEmployees = new FilteredList<>(listOfEmployees);

        employeeTableView.setItems(filteredListOfEmployees);
    }

    //Sets the data for each of the columns in the table view
    private void setDataForEmployeeTableView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        employeeRoleColumn.setCellValueFactory(role -> new ReadOnlyStringWrapper(role.getValue().getRole().getRoleName()));
        descriptionColumn.setCellValueFactory(desc -> new ReadOnlyStringWrapper(desc.getValue().getRole().getRoleDesc()));
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void handleCrudButton(ActionEvent event) {
        //Grab the button that was clicked
        Button clickedButton = (Button) event.getSource();

        //Hold the selected data of the employee
        Tblemployee emp = employeeTableView.getSelectionModel().getSelectedItem();

        //Grabs the text of the button that was clicked
        switch (clickedButton.getText()) {
            case "Add New Employee":
                System.out.println("Add a Employee");
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudEmployee.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Employee Manager");

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
                    stage.setTitle("Employee Manager");

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
                if (choice.get() == ButtonType.OK) {
                    if (emp != null) {

                        /*
                          ON DELETE CASCADE works in a way we can't apply, so we have to delete
                          each row in order
                        */
                        scheduleService.deleteSchedule(emp.getId());

                        employeeService.deleteEmployee(emp.getId());
                    }

                    reloadEmployeeTableView();
                    resetButtons();
                    System.out.println("Table View reloaded!");
                }
                break;
        }
    }

    //This will enable/disable the edit and delete buttons if a employee was chosen from the table view
    private void addActionListenersForCrudButtons() {
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                editEmployeeButton.setDisable(false);
                deleteEmployeeButton.setDisable(false);
            }
        });
    }

    //Resets the buttons back to its default value (disable = true)
    private void resetButtons() {
        editEmployeeButton.setDisable(true);
        deleteEmployeeButton.setDisable(true);
    }


    /*====================================
                Role Management
     ====================================*/

    private void reloadRoleTableView() {
        listOfRoles.clear();

        listOfRoles.addAll(roleRepository.findAll());

        filteredListOfRoles = new FilteredList<>(listOfRoles);

        roleTableView.setItems(filteredListOfRoles);
    }

    private void setDataForRoleTableView() {
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        roleDescColumn.setCellValueFactory(new PropertyValueFactory<>("roleDesc"));
    }

    @FXML
    private void handleSaveRole(ActionEvent event) {
        Button button = (Button) event.getSource();

        TblRoles role = roleTableView.getSelectionModel().getSelectedItem();

        switch (button.getText()) {
            case "+ Create Role":
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudRole.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Role Manager");

                    CrudRoleController crudRoleController = fxmlLoader.getController();
                    crudRoleController.setLabel("Add Role", "Add");
                    crudRoleController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
                    reloadRoleTableView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Edit Role":
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudRole.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Role Manager");

                    CrudRoleController crudRoleController = fxmlLoader.getController();
                    crudRoleController.setLabel("Edit Role", "Update");
                    crudRoleController.setRole(role);
                    crudRoleController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
                    reloadRoleTableView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Delete":
                System.out.println("Delete a Employee");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Role");
                alert.setHeaderText("Are you sure?");
                alert.setContentText("You are about to delete: " + role.getRoleName());

                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    if (role != null && roleRepository.findAllEmployeeWithRoleId(role.getRoleId()).isEmpty()) {

                        /*
                          ON DELETE CASCADE works in a way we can't apply, so we have to delete
                          each row in order
                        */
                        roleRepository.delete(role);

                        reloadRoleTableView();
                    } else {
                        StringBuilder roleError = new StringBuilder();
                        for (String name : roleRepository.findAllEmployeeWithRoleId(role.getRoleId())) {
                            roleError.append("\t- " + name + "\n");
                        }

                        ErrorMessages.showWarningMessage("Warning!", "Please make sure no employees have this role.",
                                "The following employees have this role: \n" + roleError);
                    }
                }
                break;
        }
    }

    @FXML
    private void handleResetRole(){
        roleTableView.getSelectionModel().clearSelection();
        editRoleButton.setDisable(true);
        deleteRoleButton.setDisable(true);
    }

    private void addActionListenersForRoleCrudButtons() {
        roleTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                resetRoleButton.setDisable(false);
                editRoleButton.setDisable(false);
                deleteRoleButton.setDisable(false);
            }
        });
    }
}
