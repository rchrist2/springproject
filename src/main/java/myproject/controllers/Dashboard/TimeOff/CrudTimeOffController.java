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
import java.text.SimpleDateFormat;
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
    private Spinner<String> beginHrList;
    @FXML
    private Spinner<String> endHrList;

    @FXML
    private ComboBox<Tblschedule> scheduleList;
    @FXML
    private ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;
    @FXML
    private ComboBox<String> approveList;

    @FXML
    private TextArea reasonInput;

    @FXML
    private RadioButton dayOffCheck;

    @FXML
    private RadioButton changeAvailCheck;

    private ObservableList<Tblschedule> scheduleData;

    private ObservableList<String> hrList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private ObservableList<String> approveData;

    private TimeOffController timeOffController;

    //The time off returned from the TimeOffController
    private Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialize drop down menus and their observable lists
        approveData = FXCollections.observableArrayList(Arrays.asList("Approve", "Deny"));
        approveList.setItems(approveData);

        hrList = FXCollections.observableArrayList();

        setDataForHourPMLists();
        enableApproveList();
        addToggleGroupForRadioButtons();
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
        //initialize the schedule dates for the current user (not done in initialize() method due to nullpointerexception)
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(selectedTimeOff.getSchedule().getEmployee().getUser().getUsername()));
        scheduleList.setItems(scheduleData);

        //get the schedule for this time off request and select it in drop-down
        scheduleList.getSelectionModel().select(selectedTimeOff.getSchedule());

        //use Calendar class to find if time has AM or PM
        Calendar begPM=Calendar.getInstance();
        begPM.setTime(tf1.getBeginTimeOffDate());

        Calendar endPM=Calendar.getInstance();
        endPM.setTime(tf1.getEndTimeOffDate());

        //use Calendar class to extract only hour from time
        Calendar calendarBeg=Calendar.getInstance();
        calendarBeg.setTime(tf1.getBeginTimeOffDate());
        int beginHour = calendarBeg.get(Calendar.HOUR);

        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.setTime(tf1.getEndTimeOffDate());
        int endHour = calendarEnd.get(Calendar.HOUR);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        //TODO can remove if statements since timeFormat gives correct AM/PM
        //assigns hour spinners
        if(begPM.get(Calendar.AM_PM) == Calendar.AM
                && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginHrList.getValueFactory().setValue(timeFormat.format(tf1.getBeginTimeOffDate()));
            endHrList.getValueFactory().setValue(timeFormat.format(tf1.getEndTimeOffDate()));
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.AM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginHrList.getValueFactory().setValue(timeFormat.format(tf1.getBeginTimeOffDate()));
            endHrList.getValueFactory().setValue(timeFormat.format(tf1.getEndTimeOffDate()));
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.AM){
            beginHrList.getValueFactory().setValue(timeFormat.format(tf1.getBeginTimeOffDate()));
            endHrList.getValueFactory().setValue(timeFormat.format(tf1.getEndTimeOffDate()));
        }
        else if(begPM.get(Calendar.AM_PM) == Calendar.PM
                && endPM.get(Calendar.AM_PM) == Calendar.PM){
            beginHrList.getValueFactory().setValue(timeFormat.format(tf1.getBeginTimeOffDate()));
            endHrList.getValueFactory().setValue(timeFormat.format(tf1.getEndTimeOffDate()));
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

        if(tf1.isDayOff()){
            dayOffCheck.setSelected(true);
            beginHrList.setDisable(true);
            endHrList.setDisable(true);
        }
        else{
            changeAvailCheck.setSelected(true);
        }

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

                //convert combobox values to 24 hour clock depending if AM or PM was selected
                if (beginHrList.getValue().contains("AM")) {

                    //if the beginning hour is 12 am
                    if (beginHrList.getValue().equals("12:00:00 AM")) {
                        sch.setScheduleTimeBegin(Time.valueOf("00"
                                + ":00:00"));
                    } else {
                        String begAMTime = beginHrList.getValue().replace(" AM","");
                        sch.setScheduleTimeBegin(Time.valueOf(begAMTime));
                    }
                } else if (beginHrList.getValue().contains("PM")) {

                    //if the beginning hour is 12 pm
                    if (beginHrList.getValue().equals("12:00:00 PM")) {
                        sch.setScheduleTimeBegin(Time.valueOf("12"
                                + ":00:00"));
                    } else {
                        String begPMTime = beginHrList.getValue().replace(" PM","");
                        sch.setScheduleTimeBegin(Time.valueOf(begPMTime));
                        sch.setScheduleTimeBegin(Time.valueOf(sch.getScheduleTimeBegin().toLocalTime().plusHours(12)));
                    }
                }

                if (endHrList.getValue().contains("AM")) {

                    //if the ending hour is 12 am
                    if (endHrList.getValue().equals("12:00:00 AM")) {
                        tf.setEndTimeOffDate(Time.valueOf("00"
                                + ":00:00"));
                    } else {
                        String endAMTime = endHrList.getValue().replace(" AM","");
                        tf.setEndTimeOffDate(Time.valueOf(endAMTime));
                    }
                } else if (endHrList.getValue().contains("PM")) {

                    //if the ending hour is 12 pm
                    if (endHrList.getValue().equals("12:00:00 PM")) {
                        tf.setEndTimeOffDate(Time.valueOf("12"
                                + ":00:00"));
                    } else {
                        String endPMTime = endHrList.getValue().replace(" PM","");
                        tf.setEndTimeOffDate(Time.valueOf(endPMTime));
                        tf.setEndTimeOffDate(Time.valueOf(tf.getEndTimeOffDate().toLocalTime().plusHours(12)));

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

        //convert combobox values to 24 hour clock depending if AM or PM was selected
        if (beginHrList.getValue().contains("AM")) {

            //if the beginning hour is 12 am
            if (beginHrList.getValue().equals("12:00:00 AM")) {
                tf.setBeginTimeOffDate(Time.valueOf("00"
                        + ":00:00"));
            } else {
                String begAMTime = beginHrList.getValue().replace(" AM","");
                tf.setBeginTimeOffDate(Time.valueOf(begAMTime));
            }
        } else if (beginHrList.getValue().contains("PM")) {

            //if the beginning hour is 12 pm
            if (beginHrList.getValue().equals("12:00:00 PM")) {
                tf.setBeginTimeOffDate(Time.valueOf("12"
                        + ":00:00"));
            } else {
                String begPMTime = beginHrList.getValue().replace(" PM","");
                tf.setBeginTimeOffDate(Time.valueOf(begPMTime));
                tf.setBeginTimeOffDate(Time.valueOf(tf.getBeginTimeOffDate().toLocalTime().plusHours(12)));
            }
        }

        if (endHrList.getValue().contains("AM")) {

            //if the ending hour is 12 am
            if (endHrList.getValue().equals("12:00:00 AM")) {
                tf.setEndTimeOffDate(Time.valueOf("00"
                        + ":00:00"));
            } else {
                String endAMTime = endHrList.getValue().replace(" AM","");
                tf.setEndTimeOffDate(Time.valueOf(endAMTime));
            }
        } else if (endHrList.getValue().contains("PM")) {

            //if the ending hour is 12 pm
            if (endHrList.getValue().equals("12:00:00 PM")) {
                tf.setEndTimeOffDate(Time.valueOf("12"
                        + ":00:00"));
            } else {
                String endPMTime = endHrList.getValue().replace(" PM", "");
                tf.setEndTimeOffDate(Time.valueOf(endPMTime));
                tf.setEndTimeOffDate(Time.valueOf(tf.getEndTimeOffDate().toLocalTime().plusHours(12)));

            }
        }

        tf.setApproved(isApproved);
        tf.setReasonDesc(reasonInput.getText());
        tf.setSchedule(scheduleList.getSelectionModel().getSelectedItem());
        tf.setDay(scheduleList.getSelectionModel().getSelectedItem().getDay());

        if(dayOffCheck.isSelected()){
            tf.setDayOff(true);
        }
        else if(changeAvailCheck.isSelected()){
            tf.setDayOff(false);
        }

        //check if any fields are empty, probably not necessary since these are set already
        if(!(approveList.getSelectionModel().isEmpty() ||
        reasonInput.getText().trim().isEmpty() ||
        scheduleList.getSelectionModel().isEmpty())) {

                //if they want to take the day off, change day_off to true in schedule
                if(tf.isDayOff()){
                    tf.setBeginTimeOffDate(sch.getScheduleTimeBegin());
                    tf.setEndTimeOffDate(sch.getScheduleTimeEnd());
                    timeOffRepository.save(tf);

                    Stage stage = (Stage) saveButton.getScene().getWindow();
                    System.out.println("Saved");
                    stage.close();
                }
                else{ //if day off is false, change the availability
                    //check if the selected time range is valid before saving
                    if (tf.getBeginTimeOffDate().before(tf.getEndTimeOffDate())
                            && tf.getEndTimeOffDate().after(tf.getBeginTimeOffDate())) {
                        scheduleRepository.save(sch);
                        timeOffRepository.save(tf);

                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        System.out.println("Saved");
                        stage.close();
                    }
                    else {
                        ErrorMessages.showErrorMessage("Invalid time values", "Time range for time" +
                                " off request is invalid", "Please edit time range for this time off request");
                    }
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

    private void setDataForHourPMLists(){
        //make a list of hours from 1 to 12
        hrList.addAll("12:00:00 AM", "01:00:00 AM", "02:00:00 AM", "03:00:00 AM",
                "04:00:00 AM", "05:00:00 AM", "06:00:00 AM", "07:00:00 AM",
                "08:00:00 AM", "09:00:00 AM", "10:00:00 AM", "11:00:00 AM",
                "12:00:00 PM","01:00:00 PM", "02:00:00 PM", "03:00:00 PM",
                "04:00:00 PM", "05:00:00 PM", "06:00:00 PM", "07:00:00 PM",
                "08:00:00 PM", "09:00:00 PM", "10:00:00 PM", "11:00:00 PM");

        //fill the hour, minute, and AM/PM drop-downs or spinners with values
        //beginPMList.setItems(pmList);
        //endPMList.setItems(pmList);

        SpinnerValueFactory<String> bHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);
        SpinnerValueFactory<String> eHours =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(hrList);

        beginHrList.setValueFactory(bHours);
        endHrList.setValueFactory(eHours);
    }

    private void enableApproveList(){
        //get the current user
        String currentUser = LoginController.userStore;

        //give only managers and owner ability to approve/disapprove
        if (userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")) {

            approveList.setDisable(false);
            errorMsgPane.setVisible(false);
        }
    }

    private void addToggleGroupForRadioButtons(){
        ToggleGroup toggleGroup = new ToggleGroup();

        dayOffCheck.setToggleGroup(toggleGroup);
        changeAvailCheck.setToggleGroup(toggleGroup);

    }

    @FXML
    private void hideTimeSpinner(){
        beginHrList.setDisable(true);
        endHrList.setDisable(true);
    }

    @FXML
    private void enableTimeSpinner(){
        beginHrList.setDisable(false);
        endHrList.setDisable(false);
    }

}
