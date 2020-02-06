package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CalendarController implements Initializable {

    @FXML
    private GridPane calendarGridPane;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
