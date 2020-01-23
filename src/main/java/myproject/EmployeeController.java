package myproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class EmployeeController {
    /*@Autowired
    private EmployeeRepository employeeRepository;

    //declare variables
    public TextField nameInput = new TextField();
    public TextField lastInput = new TextField();
    public TextField emailInput  = new TextField();
    public TextField phoneInput = new TextField();

    public TableColumn<Employee, String> firstName;
    public TableColumn<Employee, String> lastName;
    public TableColumn<Employee, String> email;
    public TableColumn<Employee, String> phone;
    public TableView<Employee> tableEmp;

    public ObservableList<Employee> data;

    //initialize the tables
    public void initialize()
    {
        //set up list with data
        data = FXCollections.observableArrayList();
        for(Employee employee : employeeRepository.findAll()){
            data.add(employee);
        }

        //set up columns
        firstName.setCellValueFactory(data -> data.getValue().firstNameProperty());
        lastName.setCellValueFactory(data -> data.getValue().lastNameProperty());
        email.setCellValueFactory(data -> data.getValue().emailProperty());
        phone.setCellValueFactory(data -> data.getValue().phoneProperty());
        tableEmp.setEditable(true);
        tableEmp.setItems(data);
    }

    //button to submit a new employee
    public void submitButton(){
        //make a new employee and set values equal to the text in the form
        Employee employee = new Employee();
        employee.setLastName(lastInput.getText());
        employee.setFirstName(nameInput.getText());
        employee.setEmail(emailInput.getText());
        employee.setPhone(phoneInput.getText());

        //check if there is a duplicate value in the employee to verify email is unique
        //SOURCE: https://stackoverflow.com/questions/27582757/catch-duplicate-entry-exception
        try {
            employeeRepository.save(employee);
            data.add(employee);
            tableEmp.setItems(data); //set table again to show new employee
        }
        catch (DataIntegrityViolationException e) { //catch the integrity violation
            System.out.println("Email already exists");
        }


    }*/
}
