package myproject.controllers.Dashboard.EmployeeManagement;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myproject.ErrorMessages;
import myproject.controllers.Dashboard.DashboardController;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.RoleRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.UserRepository;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
import myproject.services.TimeOffService;
import myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Component
public class EmployeeRoleUserManagementController implements Initializable {


    /*=============================
           Employee Controls
    =============================*/
    @FXML
    private Label titleLabel;

    @FXML
    private AnchorPane empAnchor;

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
    private AnchorPane roleAnchor;

    @FXML
    private AnchorPane fullPane;

    @FXML
    private Pane headerPane;

    @FXML
    private TabPane rolesTab;

    @FXML
    private TextField roleText, searchRoleText;

    @FXML
    private TextArea roleDescTextA;

    @FXML
    private TableView<TblRoles> roleTableView;

    @FXML
    private Button saveRoleButton,
            editRoleButton,
            deleteRoleButton;

    @FXML
    private TableColumn<TblRoles, String>
            roleColumn,
            roleDescColumn;

    /*=============================
          Account/User Controls
    =============================*/

    @FXML
    private TextField searchUserText;

    @FXML
    private AnchorPane userAnchor;

    @FXML
    private TableView<Tblusers> userTable;

    @FXML
    private Button editUserButton,
            deleteUserButton;

    @FXML
    private TableColumn<Tblusers, String>
            userCol,
            passCol,
            userEmpCol;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeOffService timeOffService;

    @Autowired
    private RoleRepository roleRepository;

    private ObservableList<Tblemployee> listOfEmployees;
    private ObservableList<TblRoles> listOfRoles;
    private ObservableList<Tblusers> listOfUsers;

    private FilteredList<Tblemployee> filteredListOfEmployees;
    private FilteredList<TblRoles> filteredListOfRoles;
    private FilteredList<Tblusers> filteredListOfUsers;

    private LocalDate today = LocalDate.now();
    public LocalDate sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
    public LocalDate saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Instant start = Instant.now();

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        //Initialize the observable list and add all the employees to the list
        listOfEmployees = FXCollections.observableArrayList();
        listOfRoles = FXCollections.observableArrayList();
        //listOfUsers = FXCollections.observableArrayList();

        if (currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfEmployees.addAll(employeeRepository.findAllEmployee());
            listOfRoles.addAll(roleRepository.findAll());
            //listOfUsers.addAll(userRepository.findAll());
        }
        else if(currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            //a manager can only see their own account and all employee accounts
            listOfEmployees.addAll(employeeRepository.findAllEmployeeByRoleEmployee());
            listOfEmployees.add(employeeRepository.findAllEmployeeByUser(currentUser));

            //hide roles tab and only show employee page
            managementTabPane.getTabs().remove(rolesTab);
            fullPane.getChildren().remove(managementTabPane);
            fullPane.getChildren().add(empAnchor);
            employeeTableView.setPrefHeight(530);

            //listOfRoles.addAll(roleRepository.findNotOwnerManagerRoles());
        }

        reloadEmployeeTableView();
        setDataForEmployeeTableView();
        addActionListenersForCrudButtons();

        reloadRoleTableView();
        setDataForRoleTableView();
        addActionListenersForRoleCrudButtons();

        //reloadUserTableView();
        //setDataForUserTableView();
        //addActionListenersForUserCrudButtons();

        //Filters the employee management view list
        setSearchBars();

        //reload tables when a new tab is clicked
        addActionListenersForTabPane();

        managementTabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            titleLabel.setText(t1.getText() + " Management");
        });

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);

        System.out.println("The time elapsed is: " + timeElapsed);
    }

    private void setSearchBars(){
        searchText.setOnKeyReleased(event -> {
            filteredListOfEmployees.setPredicate(emp -> emp.getName().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getEmail().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getAddress().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getPhone().contains(searchText.getText())
                    || emp.employeeSchedule(Date.valueOf(sunday), Date.valueOf(saturday)).toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getRole().getRoleName().toLowerCase().contains(searchText.getText().toLowerCase())
                    || emp.getRole().getRoleDesc().toLowerCase().contains(searchText.getText().toLowerCase()));
        });

        searchRoleText.setOnKeyReleased(event -> {
            filteredListOfRoles.setPredicate(r -> r.getRoleName().toLowerCase().contains(searchRoleText.getText().toLowerCase())
                    || r.getRoleDesc().toLowerCase().contains(searchRoleText.getText().toLowerCase()));
        });

/*        searchUserText.setOnKeyReleased(event -> {
            filteredListOfUsers.setPredicate(u -> u.getUsername().toLowerCase().contains(searchUserText.getText().toLowerCase())
                    || u.getPassword().contains(searchUserText.getText())
                    || u.getEmployee().getName().toLowerCase().contains(searchUserText.getText().toLowerCase()));
        });*/
    }

    private void addActionListenersForTabPane(){
        //reload tables when a new tab is clicked
        managementTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                reloadEmployeeTableView();
                reloadRoleTableView();
                //reloadUserTableView();
            }
        });
    }

    //Reloads (refreshes) the employee tableview
    private void reloadEmployeeTableView() {
        listOfEmployees.clear();

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if (currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfEmployees.addAll(employeeRepository.findAllEmployee());
        }
        else if(currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            //a manager can only see their own account and all employee accounts
            listOfEmployees.addAll(employeeRepository.findAllEmployeeByRoleEmployee());
            listOfEmployees.add(employeeRepository.findAllEmployeeByUser(currentUser));
        }

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
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

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

                if(emp.getUser().getUsername().equals(currentUser)
                        && currUser.getEmployee().getRole().getRoleName().equals("Manager")){
                    ErrorMessages.showErrorMessage("Error!", "Insufficient privileges",
                            "You do not have sufficient privileges to edit this account.");
                }
                else{
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
                        crudEmployeeController.setEmployeeForEdit();
                        crudEmployeeController.setEmployee(emp);
                        crudEmployeeController.setController(this);

                        stage.setScene(new Scene(parent));

                        stage.showAndWait();
                        reloadEmployeeTableView();
                        resetButtons();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;
            case "Delete":
                if(emp.getUser().getUsername().equals(currentUser)
                        && currUser.getEmployee().getRole().getRoleName().equals("Manager")){
                    ErrorMessages.showErrorMessage("Error!", "Insufficient privileges",
                            "You do not have sufficient privileges to delete this account.");
                }
                else{
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
                            if(!emp.getUser().getUsername().equals(LoginController.userStore)) {
                                //added due to issues with cascade delete in tbltimeoff
                                timeOffService.deleteTimeOffByEmp(emp.getId());

                                scheduleService.deleteSchedule(emp.getId());

                                employeeService.deleteEmployee(emp.getId());
                            } else {
                                ErrorMessages.showWarningMessage("Warning!", "Deleting user while logged in",
                                        "Cannot delete an account while logged in as the same employee");
                            }
                        }

                        reloadEmployeeTableView();
                        resetButtons();
                        System.out.println("Table View reloaded!");
                    }
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
            else{
                editEmployeeButton.setDisable(true);
                deleteEmployeeButton.setDisable(true);
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

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if (currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            listOfRoles.addAll(roleRepository.findAll());
        }
        else if(currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            listOfRoles.addAll(roleRepository.findNotOwnerManagerRoles());
        }

        filteredListOfRoles = new FilteredList<>(listOfRoles);

        roleTableView.setItems(filteredListOfRoles);
    }

    private void setDataForRoleTableView() {
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        roleDescColumn.setCellValueFactory(new PropertyValueFactory<>("roleDesc"));
    }

    @FXML
    private void handleSaveRole(ActionEvent event) {
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

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
                editRoleButton.setDisable(false);

                if(newValue.getRoleName().toLowerCase().equals("owner") || newValue.getRoleName().toLowerCase().equals("manager"))
                    deleteRoleButton.setDisable(true);
                else
                    deleteRoleButton.setDisable(false);
            }
            else{
                editRoleButton.setDisable(true);
                deleteRoleButton.setDisable(true);
            }
        });
    }

    /*====================================
            Account/User Management
     ====================================*/

    /*private void reloadUserTableView() {
        listOfUsers.clear();

        listOfUsers.addAll(userRepository.findAll());

        filteredListOfUsers = new FilteredList<>(listOfUsers);

        userTable.setItems(filteredListOfUsers);
    }

    private void setDataForUserTableView() {
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        passCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        userEmpCol.setCellValueFactory(Tblusers -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(Tblusers.getValue().getEmployee().getName());
            return property;
        });
    }

    @FXML
    private void handleSaveUser(ActionEvent event) {
        Button button = (Button) event.getSource();

        Tblusers user = userTable.getSelectionModel().getSelectedItem();

        switch (button.getText()) {
            case "+ Create User":
                System.out.println("Open create user form");

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudAccount.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Account Manager");

                    CrudAccountController crudAccountController = fxmlLoader.getController();
                    crudAccountController.setLabel("Add Role", "Add");
                    crudAccountController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
                    reloadUserTableView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Edit User":
                System.out.println("Open edit user form");

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudAccount.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Account Manager");

                    CrudAccountController crudAccountController = fxmlLoader.getController();
                    crudAccountController.setLabel("Add Account", "Update");
                    crudAccountController.setAccount(user);
                    crudAccountController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
                    reloadUserTableView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Delete":
                System.out.println("Delete the user");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Role");
                alert.setHeaderText("Are you sure?");
                alert.setContentText("You are about to delete: " + user.getEmployee().getName() +"'s account");

                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {

                    try {
                        userService.deleteUser(user.getUserId());
                        reloadUserTableView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                break;
        }
    }

    @FXML
    private void handleResetUser(){
        userTable.getSelectionModel().clearSelection();
        editUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
    }

    private void addActionListenersForUserCrudButtons() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                editUserButton.setDisable(false);
                deleteUserButton.setDisable(false);
            }
            else{
                editUserButton.setDisable(true);
                deleteUserButton.setDisable(true);
            }
        });
    }*/
}
