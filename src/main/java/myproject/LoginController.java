package myproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class LoginController {
    public TextField userInput;
    public PasswordField passInput;
    public Label connLabel;

    public void login(){
        Connection c;

        try {
            c = ConnectionClass.connect();
            String SQL = "SELECT * FROM Users WHERE Username = '" + userInput.getText()
                    + "' AND Password = " + passInput.getText();
            ResultSet rs = c.createStatement().executeQuery(SQL);

            if(rs.next()) //if it connected successfully
            {
                connLabel.setText("Connected");
            }
            else {
                connLabel.setText("Connection Failed");
            }
            c.close();
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

    }
}
