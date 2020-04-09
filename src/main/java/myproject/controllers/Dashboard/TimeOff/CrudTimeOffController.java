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
import myproject.models.Tblusers;
import myproject.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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
    private DatePicker beginDate;
    @FXML
    private DatePicker endDate;

    @FXML
    private ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;
    @FXML
    private ComboBox<String> approveList;

    @FXML
    private TextArea reasonInput;

    private ObservableList<String> hrList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private ObservableList<String> approveData;

    private TimeOffController timeOffController;

    //The time off returned from the TimeOffController
    private Tbltimeoff selectedTimeOff;

    private List<Tblschedule> userSchedules = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialize drop down menus and their observable lists
        approveData = FXCollections.observableArrayList(Arrays.asList("Approve", "Deny"));
        approveList.setItems(approveData);

        hrList = FXCollections.observableArrayList();

        //disable past dates and dates already in the user's schedule for datepickers
        beginDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                if(date.compareTo(today) < 0)
                    setDisable(true);

                if(userSchedules != null){
                    for(Tblschedule day : userSchedules){
                        if(date.isEqual(day.getScheduleDate().toLocalDate()))
                            setDisable(true);
                    }
                }

            }
        });

        endDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                if(date.compareTo(today) < 0)
                    setDisable(true);

                if(userSchedules != null){
                    for(Tblschedule day : userSchedules){
                        if(date.isEqual(day.getScheduleDate().toLocalDate()))
                            setDisable(true);
                    }
                }

            }
        });

        setDataForHourPMLists();
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
        beginDate.setValue(selectedTimeOff.getBeginTimeOffDate().toLocalDate());
        endDate.setValue(selectedTimeOff.getEndTimeOffDate().toLocalDate());

        userSchedules = scheduleRepository.findAllScheduleForUser(selectedTimeOff.getEmployee().getUser().getUsername());

        //find whether request is approved or not, and set appropriate drop-down value
        String approveSelect = "null";
        if(tf1.isApproved()){
            approveSelect = "Approve";
        }
        else if(!tf1.isApproved()){
            approveSelect = "Deny";
        }
        approveList.getSelectionModel().select(approveSelect);
        enableApproveList(tf1);

        reasonInput.setText(tf1.getReasonDesc());

    }

    public void handleSave(ActionEvent event){
        //get the current user
        String currentUser = LoginController.userStore;

        //get the selected time off request
        Tbltimeoff tf = selectedTimeOff;
        Boolean isApproved = tf.isApproved();

        //check if the user has privileges to approve or deny the request
        if (userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                || userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner")) {
            //approve or deny time off based on selection in drop-down
            if(approveList.getSelectionModel().getSelectedItem().equals("Approve")) {
                isApproved = true;
            }
            else if(approveList.getSelectionModel().getSelectedItem().equals("Deny")) {
                isApproved = false;
            }
        }
        else{
            //if they don't have privileges, deny the request by default
            isApproved = false;
        }


        tf.setApproved(isApproved);
        tf.setReasonDesc(reasonInput.getText());
        //tf.setDayOff(true);

        //if the schedule list is disabled, don't get dates from the schedule list
        if(!(approveList.getSelectionModel().isEmpty()
                    || beginDate.getValue() == null
                    || endDate.getValue() == null
                    || reasonInput.getText().trim().isEmpty())) {

                        tf.setBeginTimeOffDate(Date.valueOf(beginDate.getValue()));
                        tf.setEndTimeOffDate(Date.valueOf(endDate.getValue()));
                        //tf.setSchedule(null);
                        tf.setDay(null);

                        if (tf.getBeginTimeOffDate().before(tf.getEndTimeOffDate())
                                && tf.getEndTimeOffDate().after(tf.getBeginTimeOffDate())
                                || tf.getBeginTimeOffDate().equals(tf.getEndTimeOffDate())){
                            timeOffRepository.save(tf);

                            Stage stage = (Stage) saveButton.getScene().getWindow();
                            System.out.println("Saved");
                            stage.close();
                        }
                        else{
                            ErrorMessages.showErrorMessage("Invalid date values", "Date range is invalid",
                                    "Please edit the date range for this time off request.");
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

        if (!(userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))
        || !((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                && !(selectedTimeOff.getEmployee().getRole().getRoleName().equals("Manager")
                || selectedTimeOff.getEmployee().getRole().getRoleName().equals("Owner"))))) {
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

        //beginHrList.setValueFactory(bHours);
       // endHrList.setValueFactory(eHours);
    }

    private void enableApproveList(Tbltimeoff t){
        //get the current user
        String currentUser = LoginController.userStore;

        //give only managers and owner ability to approve/disapprove
        if ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Owner"))
                || ((userRepository.findUsername(currentUser).getEmployee().getRole().getRoleName().equals("Manager")
                && !(t.getEmployee().getRole().getRoleName().equals("Manager"))))) {

            approveList.setDisable(false);
            errorMsgPane.setVisible(false);
        }
    }

}
