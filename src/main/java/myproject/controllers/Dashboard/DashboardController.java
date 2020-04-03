package myproject.controllers.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.controllers.WelcomeLoginSignup.WelcomeController;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML
    private Label titleLabel,
            loggedUserLabel;

    @FXML
    private Button calendarButton, employeeButton, employeeButton1, buttonButton;

    @FXML
    private Pane dashboardPane,
            movePane;

    @FXML
    private VBox buttonVBox;

    @FXML
    private AnchorPane dashboardAnchorPane;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public Rectangle2D screenBounds;
    private double xOffset = 0, yOffset = 0;
    private Button newButton = new Button();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        screenBounds = Screen.getPrimary().getVisualBounds();

        switchWindow("/view/WeeklySchedule.fxml");

        //Checks for the current logged in user for their role
        Tblusers currentLoggedUser = userRepository.findUsername(LoginController.userStore);

        //Disables the employee management button for employees
        if(!currentLoggedUser.getEmployee().getRole().getRoleName().equals("Manager")
                && !currentLoggedUser.getEmployee().getRole().getRoleName().equals("Owner")){
            employeeButton.setDisable(true);
        }

        System.out.println("The role of this employee: " + currentLoggedUser.getEmployee().getRole().getRoleName());

        //show the current user's full name
        Tblusers currUser = userRepository.findUsername(LoginController.userStore);
        loggedUserLabel.setText("Welcome, " + currUser.getEmployee().getName());

        //don't allow regular employees to see the management page
        if (!(currUser.getEmployee().getRole().getRoleName().equals("Manager")
                || currUser.getEmployee().getRole().getRoleName().equals("Owner"))){
            System.out.println("User does not have privileges to enter Management or Scheduler");
            buttonVBox.getChildren().remove(employeeButton);
            buttonVBox.getChildren().remove(employeeButton1);
        }

    }

    //Will change the pane to the button that was clicked
    @FXML
    private void handleNavigationButton(ActionEvent event){
        //Captures the button that was clicked
        Button clickedButton = (Button)event.getSource();

        //Checks which button is pressed and changes the screen
        //to the correct window (ex. Calender button -> CalendarView.Fxml)
        switch(clickedButton.getText()){
/*            case "Calendar":
                newButton.setDisable(false);
                System.out.println("You clicked the calendar button");
                switchWindow("/view/CalendarView.fxml");
                newButton = clickedButton;
                newButton.setDisable(true);

                break;*/
            case "Management":
                 newButton.setDisable(false);
                 System.out.println("You clicked the Employee Management button");
                 switchWindow("/view/EmployeeManagementView.fxml");
                 dashboardPane.setStyle("-fx-background-color: white");
                 newButton = clickedButton;
                 newButton.setDisable(true);

                break;
            case "Scheduler":
                newButton.setDisable(false);
                System.out.println("You clicked the Employee Scheduler button");
                switchWindow("/view/EmployeeScheduler.fxml");
                dashboardPane.setStyle("-fx-background-color: white");
                newButton = clickedButton;
                newButton.setDisable(true);
                break;
            case "Clock In/Out":
                newButton.setDisable(false);
                System.out.println("You clicked the Clock In/Out button");
                switchWindow("/view/ClockInOutView.fxml");
                dashboardPane.setStyle("-fx-background-color: white");
                newButton = clickedButton;
                newButton.setDisable(true);
                break;
            case "Time Off":
                newButton.setDisable(false);
                System.out.println("You clicked the Request Time Off button");
                switchWindow("/view/TimeOffView.fxml");
                dashboardPane.setStyle("-fx-background-color: white");
                newButton = clickedButton;
                newButton.setDisable(true);
                break;
            case "Reports":
                newButton.setDisable(false);
                System.out.println("You clicked the Reports button");
                switchWindow("/view/ReportView.fxml");
                dashboardPane.setStyle("-fx-background-color: white");
                newButton = clickedButton;
                newButton.setDisable(true);
                break;
            case "Logout":
                System.out.println("You clicked the Return to Sign In button");

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/welcome.fxml"));
                    fxmlLoader.setControllerFactory(springContext::getBean);
                    Parent parent = fxmlLoader.load();
                    Scene scene = new Scene(parent);

                    scene.setFill(Color.TRANSPARENT);

                    Stage newStage = (Stage)dashboardAnchorPane.getScene().getWindow();
                    newStage.setScene(scene);

                    newStage.setX((screenBounds.getWidth() - newStage.getWidth()) / 2);
                    newStage.setY((screenBounds.getHeight() - newStage.getHeight()) / 2);

                    parent.setOnMousePressed((moveEvent -> {
                        xOffset = moveEvent.getSceneX();
                        yOffset = moveEvent.getSceneY();
                    }));

                    parent.setOnMouseDragged((moveEvent) -> {
                        newStage.setX(moveEvent.getScreenX() - xOffset);
                        newStage.setY(moveEvent.getScreenY() - yOffset);
                    });

                    newStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "Weekly Calendar":
                dashboardPane.setStyle(null);
                newButton.setDisable(false);
                switchWindow("/view/WeeklySchedule.fxml");

                System.out.println("You clicked the Weekly Calendar button");
                newButton = clickedButton;
                newButton.setDisable(true);
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
