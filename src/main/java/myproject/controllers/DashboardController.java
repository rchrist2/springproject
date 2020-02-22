package myproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import myproject.ErrorMessages;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML
    private Label titleLabel,
            loggedUserLabel;

    @FXML
    private Button calendarButton, employeeButton, buttonButton;

    @FXML
    private Pane dashboardPane,
            movePane;

    @FXML
    private HBox calendarNavBar, employeeManagementNavBar, buttonNavBar;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchWindow("/view/CalendarView.fxml");

        //Checks for the current logged in user for their role
        Tblusers currentLoggedUser = userRepository.findUsername(LoginController.userStore);

        //Disables the employee management button for employees
        if(currentLoggedUser.getEmployee().getRole().getRoleId() == 2){
            employeeButton.setDisable(true);
        }

        loggedUserLabel.setText(LoginController.userStore);

    }

    //Will change the pane to the button that was clicked
    @FXML
    private void handleNavigationButton(ActionEvent event){

        //Captures the button that was clicked
        Button clickedButton = (Button)event.getSource();

        /*
        Checks which button is pressed and changes the screen
        to the correct window (ex. Calender button -> CalendarView.Fxml)
         */
        switch(clickedButton.getText()){
            case "Calendar":
                System.out.println("You clicked the calendar button");
                switchWindow("/view/CalendarView.fxml");
                break;
            case "Employee Management":
                System.out.println("You clicked the Employee Management button");
                switchWindow("/view/EmployeeManagementView.fxml");
                break;
            case "Clock In/Out":
                System.out.println("You clicked the Clock In/Out button");
                switchWindow("/view/ClockInOutView.fxml");
                break;
            case "Time Off":
                System.out.println("You clicked the Request Time Off button");
                switchWindow("/view/TimeOffView.fxml");
                break;
            case "Return to Sign In":
                System.out.println("You clicked the Return to Sign In button");
                //switchWindow("/view/welcome.fxml");
                break;
            case "Button":
                System.out.println("You clicked the Button button");
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
