package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblEmployee;
import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Button calendarButton, employeeButton, buttonButton;

    @FXML
    private Pane dashboardPane;

    @FXML
    private HBox calendarNavBar, employeeManagementNavBar, buttonNavBar;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchWindow("/view/CalendarView.fxml");
        calendarNavBar.toFront();
    }


    @FXML
    private void handleCrudButton(ActionEvent event){
        Button clickedButton = (Button)event.getSource();
        TblEmployee emp = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeManagementView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            loader.load();
            EmployeeManagementController employeeManagementController = loader.getController();
            emp = employeeManagementController.getSelectedEmployeeFromTable();
            System.out.println("The employee brought back " + emp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(clickedButton.getText()){
            case "Add New Employee":
                System.out.println("Add a Employee");
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CrudEmployee.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Add Employee");

                    CrudEmployeeController crudEmployeeController = fxmlLoader.getController();
                    crudEmployeeController.setLabel("Add Employee", "Add");
                    crudEmployeeController.setController(this);

                    stage.setScene(new Scene(parent));
                    stage.showAndWait();
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
                    stage.setTitle("Edit Employee");

                    CrudEmployeeController crudEmployeeController = fxmlLoader.getController();
                    crudEmployeeController.setLabel("Edit Employee", "Save");
                    crudEmployeeController.setEmployee(emp);
                    crudEmployeeController.setController(this);

                    stage.setScene(new Scene(parent));

                    stage.showAndWait();
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
                if(choice.get() == ButtonType.OK){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeManagementView.fxml"));
                        loader.setControllerFactory(springContext::getBean);
                        loader.load();

                        if (emp != null) {
                            employeeRepository.delete(emp);
                        }

                        System.out.println("Table View reloaded!");
                        switchWindow("/view/EmployeeManagementView.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                //This will automatically close the dialog because its a cancel button?
                }
                break;
        }
    }

    //Will change the pane to the button that was clicked
    @FXML
    private void handleNavigationButton(ActionEvent event){

        //Captures the button that was clicked
        Button clickedButton = (Button)event.getSource();

        //Will check the clicked button and do the action
        switch(clickedButton.getText()){
            case "Calendar":
                titleLabel.setText("Calendar");
                System.out.println("You clicked the calendar button");
                switchWindow("/view/CalendarView.fxml");
                calendarNavBar.toFront();
                break;
            case "Employee Management":
                titleLabel.setText("Employee Management");
                System.out.println("You clicked the Employee Management button");
                switchWindow("/view/EmployeeManagementView.fxml");
                employeeManagementNavBar.toFront();
                break;
            case "Button":
                titleLabel.setText("Button Placeholder");
                System.out.println("You clicked the Button button");
                buttonNavBar.toFront();
                break;
        }
    }

    //Function to switch windows within the pane
    void switchWindow(String string){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(string));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Pane pane = fxmlLoader.load();

            dashboardPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();

            ErrorMessages.showErrorMessage("Window Error", "Switch window unsuccessful",
                    "Something went wrong trying to set the window");
        }
    }
}
