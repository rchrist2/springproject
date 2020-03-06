package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import myproject.models.Tblemployee;
import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class WeeklyScheduleController implements Initializable {

    @FXML
    private GridPane weeklyCalendarGridPane;

    @Autowired
    private EmployeeRepository employeeRepository;

    private List<Tblemployee> listOfEmployees;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfEmployees = employeeRepository.findAllEmployee();

        populateWeeklyCalendar();
    };

    private void populateWeeklyCalendar(){
        for (int i = 0; i < listOfEmployees.size(); i++) {
            Label employeeName = new Label(listOfEmployees.get(i).getName());
            employeeName.setStyle("-fx-padding: 5 0 5 0");
            weeklyCalendarGridPane.add(employeeName, 0, i + 1);
        }
    }

    public void handlePreviousMonth(){

    }

    public void handleNextMonth(){

    }
}
