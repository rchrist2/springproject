package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import myproject.models.TblEmployee;
import myproject.models.TblTimeOff;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.TimeOffRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

@Component
public class TimeOffController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @Autowired
    private UserRepository userRepository;

    @FXML
    public Label tableUserLabel;

    @FXML
    public DatePicker timeOffDatePicker;

    @FXML
    public TableView<TblTimeOff> timeOffTable;

    @FXML
    public TableColumn<TblTimeOff, java.sql.Date> timeOffDateCol;

    @FXML
    public TableColumn<TblTimeOff, String> approveTimeOffCol;

    private ObservableList<TblTimeOff> listOfTimeOffs;
    private FilteredList<TblTimeOff> filteredListOfTimeOff;

    public TblTimeOff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        tableUserLabel.setText("Time Off Requests for " + currentUser);
        //Initialize the observable list and add all the time offs to the list
        listOfTimeOffs = FXCollections.observableArrayList();
        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));

        reloadTimeOffTableView();
        setDataForTimeOffTableView();

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });
    }

    @FXML
    public void submitTimeOffRequest(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        TblTimeOff newTimeOff = new TblTimeOff();
        java.sql.Date newTimeOffDate = java.sql.Date.valueOf(timeOffDatePicker.getValue());
        newTimeOff.setApproved(false);
        newTimeOff.setUser(userRepository.findUsername(currentUser));
        newTimeOff.setTimeOffDate(newTimeOffDate);

        timeOffRepository.save(newTimeOff);

        reloadTimeOffTableView();
    }

    private void setDataForTimeOffTableView(){
        timeOffDateCol.setCellValueFactory(new PropertyValueFactory<>("timeOffDate"));
        approveTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("approved"));
    }

    private void reloadTimeOffTableView(){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;
        listOfTimeOffs.clear();

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOffByUser(currentUser));

        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(filteredListOfTimeOff);
    }

}
