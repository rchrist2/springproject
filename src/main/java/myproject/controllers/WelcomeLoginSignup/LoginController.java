package myproject.controllers.WelcomeLoginSignup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.SecurePassword;
import myproject.models.Tblemployee;
import myproject.models.Tblusers;
import myproject.models.YearMonthInstance;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

import static myproject.Validation.isAlpha;

@Component
public class LoginController implements Initializable {

    @FXML
    public TextField usernameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button closeButton;

    @FXML
    private Pane welcomeLoginPane;

    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private ConfigurableApplicationContext springContext;
    public Rectangle2D screenBounds;
    public static String userStore;
    public static YearMonth currentYearAndMonth;

    public double xOffset = 0, yOffset = 0;


    //public static LoggedUser loggedUserStore;
    //public static TblEmployee employeeStore;

    @Autowired
    public LoginController(UserRepository userRepository, ConfigurableApplicationContext springContext, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.springContext = springContext;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        screenBounds = Screen.getPrimary().getVisualBounds();
    }

    @FXML
    private void signinUser(ActionEvent event){
        //find user and pass in db to login
        Tblusers currentUser = userRepository.findUsername(usernameText.getText());

        System.out.println("Boolean: " + isAlpha("das2"));

        if(currentUser != null && SecurePassword.checkPassword(userRepository.findHashFromUserId(currentUser.getEmployee().getId()), passwordText.getText(),
                userRepository.findSaltFromUserId(currentUser.getEmployee().getId()))){

            System.out.println("User successfully logged in");
            userStore = usernameText.getText();
            YearMonthInstance.getInstance().setCurrentMonthYear();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
                fxmlLoader.setControllerFactory(springContext::getBean);
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();

                currentStage.setScene(scene);
                currentStage.setX((screenBounds.getWidth() - currentStage.getWidth()) / 2);
                currentStage.setY((screenBounds.getHeight() - currentStage.getHeight()) / 2);

                parent.setOnMousePressed((moveEvent -> {
                    xOffset = moveEvent.getSceneX();
                    yOffset = moveEvent.getSceneY();
                }));

                parent.setOnMouseDragged((moveEvent) -> {
                    currentStage.setX(moveEvent.getScreenX() - xOffset);
                    currentStage.setY(moveEvent.getScreenY() - yOffset);
                });

                scene.getStylesheets().add(getClass().getResource("/css/Dashboard.css").toExternalForm());

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Username or Password is incorrect");

                //output an error message for user
                ErrorMessages.showErrorMessage("Login Failed","Username or Password is incorrect",
                        "Please check that username and password are correct or contact administrator");
            }

        }
        else{
            System.out.println("Username or Password is incorrect");

            //output an error message for user
            ErrorMessages.showErrorMessage("Login Failed","Username or Password is incorrect",
                    "Please check that username and password are correct or contact administrator");
        }
    }

    @FXML
    private void changeToSignup(){
        //open the sign up page
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ForgotPassView.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Pane pane = fxmlLoader.load();

            welcomeLoginPane.getChildren().setAll(pane);
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    @FXML
    private void needHelp(){
        ErrorMessages.showInformationMessage("Need Help?","Contact info listed below",
                "Email: scsolutions475@gmail.com");
    }

    @FXML
    private void closeLogin(){
        //get current stage and close it
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
