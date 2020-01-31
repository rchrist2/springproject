package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import myproject.ConnectionClass;
import myproject.ErrorMessages;
import myproject.models.TblUsers;
import myproject.repositories.RoleRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {
    public TextField userInput;
    public PasswordField passInput;
    public Label connLabel;

    public StackPane changeSigninStack;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        try{
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signin.fxml"));
//            fxmlLoader.setControllerFactory(springContext::getBean);
//            Pane pane = fxmlLoader.load();
//
//            changeSigninStack.getChildren().setAll(pane);
//        } catch (IOException io){
//            io.printStackTrace();
//        }
    }

    /*public void login(){

        try {
            TblUsers loggedInUser = userRepository.findUserLogin(userInput.getText(), passInput.getText());

            if(loggedInUser != null){
                System.out.println("Login is successful\n");
                System.out.printf("Username: %s\n" +
                        "Password: %s\n" +
                        "Role: %s", loggedInUser.getUsername(), loggedInUser.getPassword(), roleRepository.findRoleDesc(loggedInUser.getUserId()));
            } else
                System.out.println("Login failed");
        }
        catch (Exception e) //catch any exceptions
        {
            e.printStackTrace(); //print the error

            //output an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid username or password");
            alert.setContentText("Please check that username and password are correct or contact administrator.");
            alert.showAndWait();
        }
    }*/
}
