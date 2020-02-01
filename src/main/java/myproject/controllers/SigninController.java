package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblUsers;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class SigninController implements Initializable {

    @FXML
    private TextField usernameText, passwordText;

    @FXML
    private Button closeButton;

    private ConfigurableApplicationContext springContext;
    private UserRepository userRepository;
    public AnchorPane signinAnchor;

    @Autowired
    public SigninController(ConfigurableApplicationContext springContext, UserRepository userRepository) {
        this.springContext = springContext;
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void signinUser(){
        //find user and pass in db to login
        TblUsers currentUser = userRepository.findUserLogin(usernameText.getText(), passwordText.getText());

        if(currentUser != null){
            System.out.println("User successfully logged in");
        } else {
            System.out.println("Username or Password is incorrect");

            //output an error message for user
            ErrorMessages errormsg = new ErrorMessages();
            errormsg.showErrorMessage("Login Failed","Username or Password is incorrect",
                    "Please check that username and password are correct or contact administrator");
        }
    }

    @FXML
    private void changeToSignup(){
        //open the sign up page
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Pane pane = fxmlLoader.load();

            signinAnchor.getChildren().setAll(pane);
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    @FXML
    private void closeLogin(){
        //get current stage and close it
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    //still need method for "Forgot Password?" and "Need Help?" buttons
}
