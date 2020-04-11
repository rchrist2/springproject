package myproject.controllers.Dashboard.Calendar;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import myproject.repositories.EmployeeRepository;
import myproject.repositories.ScheduleRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;

import static java.time.temporal.TemporalAdjusters.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class WeeklyScheduleController implements Initializable {

    @FXML
    private AnchorPane calendarAnchorPane;

    @FXML
    private GridPane weeklyCalendarGridPane,
                dayGridPane;

    @FXML
    private ScrollPane gridpaneScrollPane;

    @FXML
    private Label titleLabel,
                monthYearLabel,
                weekLabel,
                sundayLabel,
                mondayLabel,
                tuesdayLabel,
                wednesdayLabel,
                thursdayLabel,
                fridayLabel,
                saturdayLabel;

    @FXML
    private Button nextMonthButton,
                previousMonthButton,
                printCalendarButton;

    @FXML
    private WritableImage writeImage;

    @FXML
    private VBox titleVBox;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    private List<Tblemployee> listOfEmployees;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("MM/dd");
    private DateTimeFormatter sqlDateTimeConvert = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ArrayList<String> days = new ArrayList<>(
            Arrays.asList("Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat")
    );

    private LocalDate today = LocalDate.now();
    public LocalDate sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
    public LocalDate saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        sunday = today.with(previousOrSame(DayOfWeek.SUNDAY));
        saturday = today.with(nextOrSame(DayOfWeek.SATURDAY));

        //everyone can see everyone's schedules
        listOfEmployees = employeeRepository.findAllEmployee();

        gridpaneScrollPane.setFitToHeight(true);

        monthYearLabel.setText(LocalDate.now().getMonth().toString() + ", " + LocalDate.now().getYear());

        weekLabel.setText(sunday.format(dateFormat) + " - " + saturday.format(dateFormat));

        refreshDayLabels();
        populateWeeklyCalendar();
    }

    private void populateWeeklyCalendar(){
        System.out.println("List of Employees from Populate Calendar: " + listOfEmployees);
        Instant start = Instant.now();

        String strDateFormat = "HH:mm a";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(strDateFormat);

        for (int i = 1; i <= listOfEmployees.size(); i++) {
            //The beginning of the current week, makes sure that with each new employee
            //the week "restarts" back to the first day
            LocalDate currentDay = sunday;

            Tblemployee name = listOfEmployees.get(i - 1);

            //Get the name of the employee in the list and add it to the gridpane
            Label employeeName = new Label(name.getName());
            employeeName.setStyle("-fx-padding: 5 0 5 0");

            //Remove name label to prevent labels placing on top of each other
            removeLabelFromPreviousDates(i, 0, weeklyCalendarGridPane);
            weeklyCalendarGridPane.add(employeeName, 0, i);

            //set hour formatting for 12-hr clock
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

            //Store the hours of the employees in a list
            List<Tblschedule> employeeSchedule = scheduleRepository.findScheduleForEmployee(name.getId());

            //Loop through the days of the week and check if the employee
            //is working on that day
            for (int j = 1; j <= 7; j++) {
                //Delete the existing label from gridpane for the new week
                removeLabelFromPreviousDates(i, j, weeklyCalendarGridPane);
            }

            for (Tblschedule tblSchedule : employeeSchedule){
                switch(tblSchedule.getDay().getDayDesc().toLowerCase()){
                    case "sunday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 1, i);
                        }
                        break;
                    case "monday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 2, i);
                        }
                        break;
                    case "tuesday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 3, i);
                        }
                        break;
                    case "wednesday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 4, i);
                        }
                        break;
                    case "thursday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 5, i);
                        }
                        break;
                    case "friday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 6, i);
                        }
                        break;
                    case "saturday":
                        if (tblSchedule.getScheduleDate().compareTo(Date.valueOf(sunday)) >= 0
                                && tblSchedule.getScheduleDate().compareTo(Date.valueOf(saturday)) <= 0
                                && tblSchedule.getEmployee().getName().equals(employeeName.getText())) {

                            Label hours = new Label();
                            hours.setText(timeFormat.format(tblSchedule.getScheduleTimeBegin()) + " - "
                                    + timeFormat.format(tblSchedule.getScheduleTimeEnd()));

                            hours.setFont(Font.font("System", 11));
                            hours.setStyle("-fx-padding: 0 0 0 5");
                            weeklyCalendarGridPane.add(hours, 7, i);
                        }
                        break;
                }
            }
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);

        System.out.println("The time elapsed is: " + timeElapsed);
    }

    private void removeLabelFromPreviousDates(final int row, final int column, GridPane gridPane){
        //Add all nodes of gridpane into a observable list
        ObservableList<Node> labelsInGridPane = gridPane.getChildren();

        //Go through each node to check if they exist in a [row][col] of Gridpane
        for(Node node : labelsInGridPane){
            //If node exists then delete from gridpane
            if(node instanceof Label && gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column){
                Label currentLabel = (Label) node;
                gridPane.getChildren().remove(currentLabel);
                break;
            }
        }
    }

    private void refreshDayLabels(){
        LocalDate dayOfWeek = sunday;

        sundayLabel.setText("Sun " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        mondayLabel.setText("Mon " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        tuesdayLabel.setText("Tues " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        wednesdayLabel.setText("Wed " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        thursdayLabel.setText("Thur " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        fridayLabel.setText("Fri " + dayOfWeek.format(dayFormat));
        dayOfWeek = dayOfWeek.plusDays(1);
        saturdayLabel.setText("Sat " + dayOfWeek.format(dayFormat));
    }

    @SuppressWarnings("Duplicates")
    public void handleNextMonth(){
        sunday = sunday.with(next(DayOfWeek.SUNDAY));
        saturday = saturday.with(next(DayOfWeek.SATURDAY));

        if(!sunday.getMonth().toString().equals(saturday.getMonth().toString())){
            monthYearLabel.setText(sunday.getMonth().toString() + " to " + saturday.getMonth().toString() + ", " + sunday.getYear());
            if(sunday.getYear() != saturday.getYear()){
                monthYearLabel.setText(sunday.getMonth().toString() + ", " + sunday.getYear() + " to " + saturday.getMonth().toString() + ", " + saturday.getYear());
            }
        } else {
            monthYearLabel.setText(sunday.getMonth().toString() + ", " + saturday.getYear());
        }

        weekLabel.setText(sunday.format(dateFormat) + " - " + saturday.format(dateFormat));
        refreshDayLabels();
        populateWeeklyCalendar();
    }

    @SuppressWarnings("Duplicates")
    public void handlePreviousMonth(){
        sunday = sunday.with(previous(DayOfWeek.SUNDAY));
        saturday = saturday.with(previous(DayOfWeek.SATURDAY));

        if(!sunday.getMonth().toString().equals(saturday.getMonth().toString())){
            monthYearLabel.setText(sunday.getMonth().toString() + " to " + saturday.getMonth().toString() + ", " + sunday.getYear());
            if(sunday.getYear() != saturday.getYear()){
                monthYearLabel.setText(sunday.getMonth().toString() + ", " + sunday.getYear() + " to " + saturday.getMonth().toString() + ", " + saturday.getYear());
            }
        } else {
            monthYearLabel.setText(sunday.getMonth().toString() + ", " + saturday.getYear());
        }

        weekLabel.setText(sunday.format(dateFormat) + " - " + saturday.format(dateFormat));
        refreshDayLabels();
        populateWeeklyCalendar();
    }

    @FXML
    public void printCalendar(ActionEvent event) throws IOException {
        System.out.println("Printing....");

        nextMonthButton.setVisible(false);
        previousMonthButton.setVisible(false);

        printCalendarButton.setVisible(false);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory == null){
            nextMonthButton.setVisible(true);
            previousMonthButton.setVisible(true);

            printCalendarButton.setVisible(true);
        } else {
            Path path = Paths.get(selectedDirectory.getAbsolutePath());

            WritableImage writableImage = calendarAnchorPane.snapshot(new SnapshotParameters(), null);

            File file = new File(path + "/ReportSchedule " + sunday + " - " + saturday + ".png");

            if (file.isFile()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save File");
                alert.setHeaderText("File already exists");
                alert.setContentText("Would you like to overwrite existing file?");

                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    TextInputDialog textInputDialog = new TextInputDialog();
                    textInputDialog.setHeaderText("Provide a name for file: ");
                    textInputDialog.showAndWait();

                    file = new File(path + "/" + textInputDialog.getEditor().getText() + ".png");

                    ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                    System.out.println("snapshot saved: " + file.getAbsolutePath());

                    nextMonthButton.setVisible(true);
                    previousMonthButton.setVisible(true);

                    printCalendarButton.setVisible(true);
                }
            } else {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("snapshot saved: " + file.getAbsolutePath());

                nextMonthButton.setVisible(true);
                previousMonthButton.setVisible(true);

                printCalendarButton.setVisible(true);
            }
        }
    }
}
