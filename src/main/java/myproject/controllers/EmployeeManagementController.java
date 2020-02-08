package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import myproject.models.TblEmployee;
import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class EmployeeManagementController implements Initializable {

    @FXML
    private TableView<TblEmployee> employeeTableView;

    public TableColumn<TblEmployee, String>
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

    private ObservableList<TblEmployee> listOfEmployees;
    private FilteredList<TblEmployee> filteredListOfEmployees;

    public TblEmployee selectedEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize the observable list and add all the employees to the list
        listOfEmployees = FXCollections.observableArrayList();
        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        reloadEmployeeTableView();
        setDataForEmployeeTableView();

        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedEmployee = newv;
        });
    }

    void reloadEmployeeTableView(){
        listOfEmployees.clear();

        listOfEmployees.addAll(employeeRepository.findAllEmployee());

        filteredListOfEmployees = new FilteredList<>(listOfEmployees);

        employeeTableView.setItems(filteredListOfEmployees);
    }

    private void setDataForEmployeeTableView(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        //roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    public TblEmployee getSelectedEmployeeFromTable(){

        return selectedEmployee;
    }
}
