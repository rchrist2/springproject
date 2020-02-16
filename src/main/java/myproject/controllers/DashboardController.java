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
import myproject.repositories.EmployeeRepository;
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
        //loggedUserLabel.setText(LoggedUser.getInstance().getUserName());
    }

    //Will change the pane to the button that was clicked
    @FXML
    private void handleNavigationButton(ActionEvent event){

        //Captures the button that was clicked
        Button clickedButton = (Button)event.getSource();

        //Will check the clicked button and do the action
        switch(clickedButton.getText()){
            case "Calendar":
                //titleLabel.setText("Calendar");
                System.out.println("You clicked the calendar button");
                switchWindow("/view/CalendarView.fxml");
                //calendarNavBar.toFront();
                break;
            case "Employee Management":
                //titleLabel.setText("Employee Management");
                System.out.println("You clicked the Employee Management button");
                switchWindow("/view/EmployeeManagementView.fxml");
                //employeeManagementNavBar.toFront();
                break;
            case "Clock In/Out":
                //titleLabel.setText("Button Placeholder");
                System.out.println("You clicked the Clock In/Out button");
                switchWindow("/view/ClockInOutView.fxml");
                //buttonNavBar.toFront();
                break;
            case "Request Time Off":
                //titleLabel.setText("Button Placeholder");
                System.out.println("You clicked the Request Time Off button");
                switchWindow("/view/TimeOffRequestView.fxml");
                //buttonNavBar.toFront();
                break;
            case "Approve/Deny Time Off":
                //titleLabel.setText("Button Placeholder");
                System.out.println("You clicked the Approve/Deny Time Off button");
                switchWindow("/view/TimeOffApproveView.fxml");
                //buttonNavBar.toFront();
                break;
            case "Button":
                //titleLabel.setText("Button Placeholder");
                System.out.println("You clicked the Button button");
                //buttonNavBar.toFront();
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
