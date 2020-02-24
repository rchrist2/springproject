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
    private ScheduleService scheduleService;

    //The employee returned from the EmployeeManagementController
    private Tbltimeoff selectedTimeOff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        approveData = FXCollections.observableArrayList(Arrays.asList("Approve","Deny"));
        approveList.setItems(approveData);

        //initialize the schedule dates for the current user
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(TimeOffController.selectedUser));
        scheduleList.setItems(scheduleData);

        hrList = FXCollections.observableArrayList();
        //minList = FXCollections.observableArrayList();

        hrList.addAll(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));
        //minList.addAll(IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList()));

        //fill the hour, minute, and AM/PM comboboxes with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        beginHrList.setItems(hrList);
        //beginMinList.setItems(minList);
        endHrList.setItems(hrList);
        //endMinList.setItems(minList);
    }

    @Autowired
    public CrudTimeOffController(ConfigurableApplicationContext springContext,
                                  ScheduleRepository scheduleRepository, ScheduleService scheduleService) {
        this.springContext = springContext;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
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
        //TODO set AM/PM list
        scheduleList.getSelectionModel().select(selectedTimeOff.getSchedule());

        Calendar calendarBeg=Calendar.getInstance();
        calendarBeg.setTime(tf1.getBeginTimeOffDate());
        int beginHour = calendarBeg.get(Calendar.HOUR);

        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.setTime(tf1.getEndTimeOffDate());
        int endHour = calendarEnd.get(Calendar.HOUR);

        beginHrList.getSelectionModel().select(beginHour-1);
        endHrList.getSelectionModel().select(endHour-1);

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
        //get the current user (String) from LoginController
        String currentUser = LoginController.userStore;

        Tbltimeoff tf = selectedTimeOff;

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

        timeOffRepository.save(tf);


        Stage stage = (Stage)saveButton.getScene().getWindow();
        System.out.println("Saved");
        stage.close();
        }

    @FXML
    private void handleCancel(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }

}
