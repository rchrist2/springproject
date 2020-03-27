package myproject.controllers.WelcomeLoginSignup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import myproject.SecurePassword;
import myproject.Validation;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WelcomeController implements Initializable {

    @FXML
    private TextField usernameText, passwordText;

    @FXML
    private Button closeButton;

    @FXML
    private Pane welcomeLoginPane;

    private ConfigurableApplicationContext springContext;
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    public AnchorPane signinAnchor;
    public Rectangle2D screenBounds;

    @Autowired
    public WelcomeController(ConfigurableApplicationContext springContext, UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.springContext = springContext;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        screenBounds = Screen.getPrimary().getVisualBounds();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signin.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Pane pane = fxmlLoader.load();

            welcomeLoginPane.getChildren().add(pane);
        } catch (IOException io){
            io.printStackTrace();
        }
    }
}
