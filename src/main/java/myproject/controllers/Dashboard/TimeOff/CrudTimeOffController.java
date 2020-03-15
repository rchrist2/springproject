package myproject.controllers.Dashboard.TimeOff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import myproject.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Time;
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

    @Autowired
    private UserRepository userRepository;

    @FXML
    private Pane errorMsgPane;

    @FXML
    private Label crudLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    public Spinner<Integer> beginHrList;
    @FXML
    public Spinner<Integer> endHrList;

    @FXML
    public ComboBox<Tblschedule> scheduleList;
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;

        //initialize drop down menus and their observable lists
        approveData = FXCollections.observableArrayList(Arrays.asList("Approve", "Deny"));
        approveList.setItems(approveData);

        //make a list of hours from 1 to 12
        hrList = FXCollections.observableArrayList();
        hrList.addAll(IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList()));

        //fill the hour, minute, and AM/PM drop-downs or spinners with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        SpinnerValueFactory<Integer> bHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);
        SpinnerValueFactory<Integer> eHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);

        beginHrList.setValueFactory(bHours);
        endHrList.setValueFactory(eHours);

        //give only managers and owner ability to approve/disapprove
        if (userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")) {

            approveList.setDisable(false);
            errorMsgPane.setVisible(false);

        }
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

        //initialize the schedule dates for the current user (not done in initialize() method due to nullpointerexception)
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

        //assigns hour spinners
        if(beginHour == 0 && endHour == 0){ //if any of the hours are equal to 12
            beginHrList.getValueFactory().setValue(12);
            endHrList.getValueFactory().setValue(12);
        }
        else if(endHour == 0){
            beginHrList.getValueFactory().setValue(beginHour);
            endHrList.getValueFactory().setValue(12);
        }
        else if(beginHour == 0){
            beginHrList.getValueFactory().setValue(12);
            endHrList.getValueFactory().setValue(endHour);
        }
        else{
            beginHrList.getValueFactory().setValue(beginHour);
            endHrList.getValueFactory().setValue(endHour);
        }

        //find whether request is approved or not, and set appropriate drop-down value
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
        //get the current user
        String currentUser = LoginController.userStore;

        //get the selected time off request
        Tbltimeoff tf = selectedTimeOff;
        Boolean isApproved = tf.isApproved();

        //if approved, use this variable to change the time range for the selected schedule
        Tblschedule sch = scheduleRepository.findByScheduleId(scheduleList.getSelectionModel().getSelectedItem().getScheduleId());

        //check if the user has privileges to approve or deny the request
        if (userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")) {
            //approve or deny time off based on selection in drop-down
            if(approveList.getSelectionModel().getSelectedItem().equals("Approve")) {
                isApproved = true;

                //beginning hour
                if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {
                    //if the beginning hour is 12 am
                    if(beginHrList.getValue().toString().equals("12")){
                        sch.setScheduleTimeBegin(Time.valueOf("00"
                                + ":00:00"));
                    }
                    else {
                        sch.setScheduleTimeBegin(Time.valueOf(beginHrList.getValue().toString()
                                + ":00:00"));
                    }
                } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {
                    //if the beginning hour is 12 pm
                    if(beginHrList.getValue().toString().equals("12")) {
                        sch.setScheduleTimeBegin(Time.valueOf("12"
                                + ":00:00"));
                    }
                    else{
                        sch.setScheduleTimeBegin(Time.valueOf((beginHrList.getValue() + 12)
                                + ":00:00"));
                    }
                }

                //ending hour
                if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {
                    //if the ending hour is 12 am
                    if(endHrList.getValue().toString().equals("12")){
                        sch.setScheduleTimeEnd(Time.valueOf("00"
                                + ":00:00"));
                    }
                    else {
                        sch.setScheduleTimeEnd(Time.valueOf(endHrList.getValue().toString()
                                + ":00:00"));
                    }
                } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {
                    //if the ending hour is 12 pm
                    if(endHrList.getValue().toString().equals("12")) {
                        sch.setScheduleTimeEnd(Time.valueOf("12"
                                + ":00:00"));
                    }
                    else {
                        sch.setScheduleTimeEnd(Time.valueOf((endHrList.getValue() + 12)
                                + ":00:00"));
                    }
                }
            }
            else if(approveList.getSelectionModel().getSelectedItem().equals("Deny")) {
                isApproved = false;
            }
        }
        else{
            //if they don't have privileges, deny the request by default
            isApproved = false;
        }

        //convert spinner values to 24 hour clock depending if AM or PM was selected
        if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            //if the beginning hour is 12 am
            if(beginHrList.getValue().toString().equals("12")){
                tf.setBeginTimeOffDate(Time.valueOf("00"
                        + ":00:00"));
            }
            else {
                tf.setBeginTimeOffDate(Time.valueOf(beginHrList.getValue().toString()
                        + ":00:00"));
            }
        } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            //if the beginning hour is 12 pm
            if(beginHrList.getValue().toString().equals("12")) {
                tf.setBeginTimeOffDate(Time.valueOf("12"
                        + ":00:00"));
            }
            else{
                tf.setBeginTimeOffDate(Time.valueOf((beginHrList.getValue() + 12)
                        + ":00:00"));
            }
        }

        if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            //if the ending hour is 12 am
            if(endHrList.getValue().toString().equals("12")){
                tf.setEndTimeOffDate(Time.valueOf("00"
                        + ":00:00"));
            }
            else {
                tf.setEndTimeOffDate(Time.valueOf(endHrList.getValue().toString()
                        + ":00:00"));
            }
        } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            //if the ending hour is 12 pm
            if(endHrList.getValue().toString().equals("12")) {
                tf.setEndTimeOffDate(Time.valueOf("12"
                        + ":00:00"));
            }
            else {
                tf.setEndTimeOffDate(Time.valueOf((endHrList.getValue() + 12)
                        + ":00:00"));
            }
        }

        tf.setApproved(isApproved);
        tf.setReasonDesc(reasonInput.getText());
        tf.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

        //check if any fields are empty, probably not necessary since these are set already
        if(!(approveList.getSelectionModel().isEmpty() ||
        beginPMList.getSelectionModel().isEmpty() ||
        endPMList.getSelectionModel().isEmpty() ||
        reasonInput.getText().trim().isEmpty() ||
        scheduleList.getSelectionModel().isEmpty())) {

            //also check if the selected time range is valid before saving
            if (tf.getBeginTimeOffDate().before(tf.getEndTimeOffDate())
                    && tf.getEndTimeOffDate().after(tf.getBeginTimeOffDate())) {
                timeOffRepository.save(tf);
                scheduleRepository.save(sch);

                Stage stage = (Stage) saveButton.getScene().getWindow();
                System.out.println("Saved");
                stage.close();
            } else {
                ErrorMessages.showErrorMessage("Invalid time values", "Time range for time" +
                        " off request is invalid", "Please edit time range for this time off request");
            }
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty",
                    "Selections missing or text fields are blank",
                    "Please select from the drop-down menus and fill in text fields");
        }
    }

    @FXML
    private void handleCancel(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }

    //show an error message that tells an unprivileged user why they cannot approve/deny request
    @FXML
    private void approveClicked() {
        //get the current user
        String currentUser = LoginController.userStore;

        if (!(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))) {
            ErrorMessages.showErrorMessage("Insufficient privileges","Cannot approve/deny request",
                    "You do not have sufficient privileges to approve or deny this request.");
        }
    }

}
