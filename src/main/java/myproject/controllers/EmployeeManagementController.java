package myproject.controllers;

import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class EmployeeManagementController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
