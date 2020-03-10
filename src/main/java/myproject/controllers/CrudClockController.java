package myproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import myproject.ErrorMessages;
import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.repositories.ClockRepository;
import myproject.repositories.ScheduleRepository;
import myproject.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CrudClockController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private ClockRepository clockRepository;

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
    public ComboBox<String> beginMinList;
    @FXML
    public ComboBox<Integer> endHrList;
    @FXML
    public ComboBox<String> endMinList;
    @FXML
    public ComboBox<String> beginPMList;
    @FXML
    public ComboBox<String> endPMList;

    public ObservableList<Tblschedule> scheduleData;

    private ObservableList<Integer> hrList;

    private ObservableList<String> minList;

    private ObservableList<String> pmList =
            FXCollections.observableArrayList(Arrays.asList("AM","PM"));

    private ClockInOutController clockController;

    //The time off returned from the TimeOffController
    private Tblclock selectedClock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //initialize drop down menus
        hrList = FXCollections.observableArrayList();
        hrList.addAll(IntStream.rangeClosed(1,12).boxed().collect(Collectors.toList()));

        minList = FXCollections.observableArrayList();
        List<Integer> minListInt = IntStream.rangeClosed(0,59).boxed().collect(Collectors.toList());


        List<String> minListString = new ArrayList<String>(minListInt.size());
        for (Integer myInt : minListInt) {
            minListString.add(String.format("%02d", myInt));
        }

        minList.addAll(minListString);

        //fill the hour, minute, and AM/PM drop-downs with values
        beginPMList.setItems(pmList);
        endPMList.setItems(pmList);

        beginMinList.setItems(minList);
        endMinList.setItems(minList);

        beginHrList.setItems(hrList);
        endHrList.setItems(hrList);
    }

    @Autowired
    public CrudClockController(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setController(ClockInOutController clockController) {
        this.clockController = clockController;
    }

    public void setLabel(String string){
        crudLabel.setText(string);
    }

    public void setClock(Tblclock selectedClock){
        this.selectedClock = selectedClock;
        setFieldsForEdit(this.selectedClock);
    }

    private void setFieldsForEdit(Tblclock cl1){
        //use Calendar class to find if time has AM or PM
        Calendar begPM=Calendar.getInstance();
        begPM.setTime(cl1.getPunchIn());

        Calendar endPM=Calendar.getInstance();
        endPM.setTime(cl1.getPunchOut());

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
        scheduleData = FXCollections.observableArrayList();
        scheduleData.addAll(scheduleRepository.findScheduleForUser(selectedClock.getSchedule().getEmployee().getUser().getUsername()));
        scheduleList.setItems(scheduleData);

        //get the schedule for this time off request and select it in drop-down
        scheduleList.getSelectionModel().select(selectedClock.getSchedule());

        //use Calendar class to extract only hour or minute from time
        Calendar calendarBeg=Calendar.getInstance();
        Calendar calendarEnd=Calendar.getInstance();

        //extract hours
        calendarBeg.setTime(cl1.getPunchIn());
        int beginHour = calendarBeg.get(Calendar.HOUR);

        calendarEnd.setTime(cl1.getPunchOut());
        int endHour = calendarEnd.get(Calendar.HOUR);

        //extract minutes
        calendarBeg.setTime(cl1.getPunchIn());
        int beginMin = calendarBeg.get(Calendar.MINUTE);

        calendarEnd.setTime(cl1.getPunchOut());
        int endMin = calendarEnd.get(Calendar.MINUTE);

        //assigns selected hour via array index since hrList is Int, so subtract 1 to display correct hour
        beginHrList.getSelectionModel().select(beginHour-1);
        endHrList.getSelectionModel().select(endHour-1);

        //assigns selected minute in drop down menu using leading zeroes
        beginMinList.getSelectionModel().select(String.format("%02d", beginMin));
        endMinList.getSelectionModel().select(String.format("%02d", endMin));

    }

    public void handleSave(ActionEvent event){
        Tblclock cl = selectedClock;

        //TODO make sure all fields are selected

        //convert combobox values to 24 hour clock depending if AM or PM was selected
        if (beginPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            cl.setPunchIn(Time.valueOf(beginHrList.getSelectionModel().getSelectedItem().toString()
                    + ":" + beginMinList.getSelectionModel().getSelectedItem()
                    + ":00"));
        } else if (beginPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            cl.setPunchIn(Time.valueOf((beginHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":" + beginMinList.getSelectionModel().getSelectedItem()
                    + ":00"));
        }

        if (endPMList.getSelectionModel().getSelectedItem().equals("AM")) {
            cl.setPunchOut(Time.valueOf(endHrList.getSelectionModel().getSelectedItem().toString()
                    + ":" + endMinList.getSelectionModel().getSelectedItem()
                    + ":00"));
        } else if (endPMList.getSelectionModel().getSelectedItem().equals("PM")) {
            cl.setPunchOut(Time.valueOf((endHrList.getSelectionModel().getSelectedItem() + 12)
                    + ":" + endMinList.getSelectionModel().getSelectedItem()
                    + ":00"));
        }

        cl.setSchedule(scheduleList.getSelectionModel().getSelectedItem());

        if(cl.getPunchIn().before(cl.getPunchOut())
                && cl.getPunchOut().after(cl.getPunchIn())){
            clockRepository.save(cl);

            Stage stage = (Stage)saveButton.getScene().getWindow();
            System.out.println("Saved");
            stage.close();
        }
        else{
            ErrorMessages.showErrorMessage("Invalid time values",
                    "Time range is invalid",
                    "Please edit time range for this clock in/out record");
        }
    }

    @FXML
    private void handleCancel(){
        Stage currStage = (Stage)cancelButton.getScene().getWindow();

        currStage.close();
    }
}
