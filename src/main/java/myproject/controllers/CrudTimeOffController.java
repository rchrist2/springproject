package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.TblRoles;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import myproject.repositories.*;
import myproject.services.EmployeeService;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CrudTimeOffController implements Initializable {
    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @FXML
    private Label crudLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    public ComboBox<Tblschedule> scheduleList;

    @FXML
    public ComboBox<Integer> beginHrList;
    @FXML
    public ComboBox<Integer> endHrList;
    @FXML
    public ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;

    @FXML
    public ComboBox<String> approveList;

    @FXML
    public TextArea reasonInput;

    public ObservableList<Tblschedule> scheduleData;

    private ObservableList<Integer> hrList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private ObservableList<String> approveData;

    private TimeOffController timeOffController;

    //The time off returned from the TimeOffController
    private Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //initialize drop down menus
        approveData = FXCollections.observableArrayList(Arrays.asList("Approve","Deny"));
        approveList.setItems(approveData);

        hrList = FXCollections.observableArrayList();
        hrList.addAll(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));

        //fill the hour, minute, and AM/PM drop-downs with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        beginHrList.setItems(hrList);
        endHrList.setItems(hrList);
    }

    @Autowired
    public CrudTimeOffController(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setController(TimeOffController timeOffController) {
        this.timeOffController = timeOffController;
    }

    public void setLabel(String string){
        crudLabel.setText(string);
    }

    public void setTimeOff(Tbltimeoff selectedTimeOff){
        this.selectedTimeOff = selectedTimeOff;
        setFieldsForEdit(this.selectedTimeOff);
    }

    private void setFieldsForEdit(Tbltimeoff tf1){
        //use Calendar class to find if time has AM or PM
        Calendar begPM=Calendar.getInstance();
        begPM.setTime(tf1.getBeginTimeOffDate());

        Calendar endPM=Calendar.getInstance();
        endPM.setTime(tf1.getEndTimeOffDate());

        //if time has AM or PM, assign appropriate value in AM/PM drop-downs
        if(begPM.get(Calendar.AM_PM) == Calendar.AM
        && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginPMList.getSelectionModel().select(0);
            endPMList.getSelectionModel().select(0);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.AM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginPMList.getSelectionModel().select(0);
            endPMList.getSelectionModel().select(1);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginPMList.getSelectionModel().select(1);
            endPMList.getSelectionModel().select(0);
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginPMList.getSelectionModel().select(1);
            endPMList.getSelectionModel().select(1);
        }

        //initialize the schedule dates for the current user
        //not done in initialize() method due to nullpointerexception
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(selectedTimeOff.getSchedule().getEmployee().getUser().getUsername()));
        scheduleList.setItems(scheduleData);

        //get the schedule for this time off request and select it in drop-down
        scheduleList.getSelectionModel().select(selectedTimeOff.getSchedule());

        //use Calendar class to extract only hour from time
        Calendar calendarBeg=Calendar.getInstance();
        calendarBeg.setTime(tf1.getBeginTimeOffDate());
        int beginHour = calendarBeg.get(Calendar.HOUR);

        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.setTime(tf1.getEndTimeOffDate());
        int endHour = calendarEnd.get(Calendar.HOUR);

        //assigns via array index, so subtract 1 to display correct hour value
        beginHrList.getSelectionModel().select(beginHour-1);
        endHrList.getSelectionModel().select(endHour-1);

        //find whether time is approved or not, and set appropriate drop-down value
        String approveSelect = "null";
        if(tf1.isApproved()){
            approveSelect = "Approve";
        }
        else if(!tf1.isApproved()){
            approveSelect = "Deny";
        }
        approveList.getSelectionModel().select(approveSelect);

        reasonInput.setText(tf1.getReasonDesc());

    }

    public void handleSave(ActionEvent event){
        Tbltimeoff tf = selectedTimeOff;

        //save the time off with approved/denied based on selection in drop-down
        Boolean isApproved = tf.isApproved();
        if(approveList.getSelectionModel().getSelectedItem().equals("Approve")) {
            isApproved = true;
        }
        else if(approveList.getSelectionModel().getSelectedItem().equals("Deny")) {
            isApproved = false;
        }

        //convert combobox values to 24 hour clock depending if AM or PM was selected
        if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            tf.setBeginTimeOffDate(Time.valueOf(beginHrList.getSelectionModel().getSelectedItem().toString()
                    + ":00:00"));
        } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            tf.setBeginTimeOffDate(Time.valueOf((beginHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":00:00"));
        }

        if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            tf.setEndTimeOffDate(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                    + ":00:00"));
        } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            tf.setEndTimeOffDate(Time.valueOf((endHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":00:00"));
        }

        tf.setApproved(isApproved);
        tf.setReasonDesc(reasonInput.getText());
        tf.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

        if(tf.getBeginTimeOffDate().before(tf.getEndTimeOffDate())
                && tf.getEndTimeOffDate().after(tf.getBeginTimeOffDate())){
            timeOffRepository.save(tf);

            Stage stage = (Stage)saveButton.getScene().getWindow();
            System.out.println("Saved");
            stage.close();
        }
        else{
            ErrorMessages.showErrorMessage("Invalid time values","Time range for time" +
                    " off request is invalid","Please edit time range for this time off request");
        }

    }

    @FXML
    private void handleCancel(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }

}
