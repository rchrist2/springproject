package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML
    private Button calendarButton, employeeButton, buttonButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
