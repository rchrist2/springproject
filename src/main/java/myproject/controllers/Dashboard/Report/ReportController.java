package myproject.controllers.Dashboard.Report;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import myproject.ConnectionClass;
import myproject.ErrorMessages;
import myproject.controllers.WelcomeLoginSignup.LoginController;
import myproject.models.*;
import myproject.repositories.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class ReportController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @FXML
    private Label tableUserLabel;

    @FXML
    private SplitMenuButton reportList;

    @FXML
    private MenuItem perUserItem;

    @FXML
    private MenuItem currentUserHoursItem;

    @FXML
    private DatePicker beginDate, endDate, beginDate1, endDate1;

    @FXML
    private ComboBox<Tblemployee> employeeList;

    @FXML
    private Pane datePane;

    @FXML
    private Pane datePaneUser;

    @FXML
    private Pane datePaneNoUser;

    @FXML
    private Button submitButton, submitButton1;

    ConnectionClass connectClass;

    @FXML
    public TableView<ObservableList<String>> reportTable;

    public ObservableList<ObservableList<String>> tableData;

    private ObservableList<Tblemployee> employeeData;

    private Tblemployee selectedEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")
        || currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            perUserItem.setVisible(true);
        }

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            currentUserHoursItem.setVisible(false);
        }

        tableData = FXCollections.observableArrayList();

    }

    @FXML
    private void saveReport(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("sample");

        Row row = spreadsheet.createRow(0);

        for (int j = 0; j < reportTable.getColumns().size(); j++) {
            row.createCell(j).setCellValue(reportTable.getColumns().get(j).getText());
        }

        for (int i = 0; i < reportTable.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            for (int j = 0; j < reportTable.getColumns().size(); j++) {
                if(reportTable.getColumns().get(j).getCellData(i) != null) {
                    row.createCell(j).setCellValue(reportTable.getColumns().get(j).getCellData(i).toString());
                }
                else {
                    row.createCell(j).setCellValue("");
                }
            }
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Path path = Paths.get(selectedDirectory.getAbsolutePath());

        File file = new File(path
                + "/Report " + timeStamp  + " - " + tableUserLabel.getText() + ".xls");

        if(file.isFile()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save File");
            alert.setHeaderText("File already exists");
            alert.setContentText("Would you like to overwrite existing file?");

            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.get() == ButtonType.OK) {
                FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath()
                        + "/Report " + timeStamp  + " - " + tableUserLabel.getText() + ".xls");

                workbook.write(fileOut);
                fileOut.close();
            }
        }
        else{
            FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath()
                    + "/Report " + timeStamp  + " - " + tableUserLabel.getText() + ".xls");

            workbook.write(fileOut);
            fileOut.close();
        }

    }

    @FXML
    private void showHours(){
        //clear the columns and rows from the previous query
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
        datePane.setVisible(false);

        reportList.setText("Cumulative Hours");

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        tableUserLabel.setText("Cumulative Hours This Week for " + currUser.getEmployee().getName());

        //Connect to Database
        Connection c;
        try {
            c = connectClass.connect();
            String SQL = "SELECT CAST(ROUND(SUM(DATEDIFF(SECOND, punch_in, punch_out)/3600.0),2)AS DECIMAL(8,2)) AS \"Total Hours\"\n" +
                    ", CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE) as \"Week Of\" " +
                    "FROM tblclock c JOIN tblschedule s ON s.schedule_id=c.schedule_id \n" +
                    "JOIN tblemployee e ON s.employee_id=e.id JOIN tblusers u ON u.employee_id=e.id\n" +
                    "WHERE Username = '" + currentUser + "' AND DATEPART(week, date_created) = DATEPART(week, GETDATE())" +
                    " AND punch_out <> '00:00:00' " +
                    "GROUP BY CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE)";
            ResultSet rs = c.createStatement().executeQuery(SQL);
            int index = rs.getMetaData().getColumnCount();
            //dynamically add table columns, so they are made based off database columns
            //Not sure if this method will make it harder to add data later
            for (int i = 0; i < index; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                reportTable.getColumns().addAll(col);
                //System.out.println("Column [" + i + "] ");
                //add to observable list
                while (rs.next()) {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                        //Iterate Column
                        row.add(rs.getString(k));
                    }
                    //System.out.println("Row [1] added " + row);
                    tableData.add(row);
                }
                //add to tableview
                reportTable.setItems(tableData);
            }
            c.close();
        }
        catch(Exception e){ //catch any exceptions
            e.printStackTrace();
            System.out.println("Error on Building Reports Table Data");
        }

    }

    @FXML
    private void showHoursPerUser(){
        //clear the columns and rows from the previous query
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
        datePane.setVisible(false);

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        reportList.setText("Cumulative Hours All Employees");

        tableUserLabel.setText("Cumulative Hours for All Employees");

        //Connect to Database
        Connection c;

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")){
            try {
                c = connectClass.connect();
                String SQL = "SELECT CAST(ROUND(SUM(DATEDIFF(SECOND, punch_in, punch_out)/3600.0),2)AS DECIMAL(8,2)) AS \"Total Hours\"\n" +
                        ", CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE) as \"Week Of\", " +
                        "e.name as \"Name\" " +
                        "FROM tblclock c JOIN tblschedule s ON s.schedule_id=c.schedule_id \n" +
                        "JOIN tblemployee e ON s.employee_id=e.id JOIN tblusers u ON u.employee_id=e.id\n" +
                        "JOIN tblroles r ON r.role_id = e.roles_id " +
                        "WHERE DATEPART(week, date_created) = DATEPART(week, GETDATE())" +
                        " AND punch_out <> '00:00:00' " +
                        "AND role_name NOT IN ('Owner') " +
                        "GROUP BY CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE), e.name";
                ResultSet rs = c.createStatement().executeQuery(SQL);
                int index = rs.getMetaData().getColumnCount();
                //dynamically add table columns, so they are made based off database columns
                //Not sure if this method will make it harder to add data later
                for (int i = 0; i < index; i++) {
                    final int j = i;
                    TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                    reportTable.getColumns().addAll(col);
                    //System.out.println("Column [" + i + "] ");
                    //add to observable list
                    while (rs.next()) {
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                            //Iterate Column
                            row.add(rs.getString(k));
                        }
                        //System.out.println("Row [1] added " + row);
                        tableData.add(row);
                    }
                    //add to tableview
                    reportTable.setItems(tableData);
                }
                c.close();
            }
            catch(Exception e){ //catch any exceptions
                e.printStackTrace();
                System.out.println("Error on Building Reports Table Data");
            }
        }
        else{
            try {
                c = connectClass.connect();
                String SQL = "SELECT CAST(ROUND(SUM(DATEDIFF(SECOND, punch_in, punch_out)/3600.0),2)AS DECIMAL(8,2)) AS \"Total Hours\"\n" +
                        ", CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE) as \"Week Of\", " +
                        "e.name as \"Name\" " +
                        "FROM tblclock c JOIN tblschedule s ON s.schedule_id=c.schedule_id \n" +
                        "JOIN tblemployee e ON s.employee_id=e.id JOIN tblusers u ON u.employee_id=e.id\n" +
                        " JOIN tblroles r ON r.role_id=e.roles_id\n" +
                        " WHERE DATEPART(week, date_created) = DATEPART(week, GETDATE())" +
                        " AND role_name NOT IN('Owner','Manager') " +
                        " AND punch_out <> '00:00:00' " +
                        "GROUP BY CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE), e.name" +
                        " UNION " +
                        "SELECT CAST(ROUND(SUM(DATEDIFF(SECOND, punch_in, punch_out)/3600.0),2)AS DECIMAL(8,2)) AS \"Total Hours\"\n" +
                        ", CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE) as \"Week Of\", " +
                        "e.name as \"Name\" " +
                        "FROM tblclock c JOIN tblschedule s ON s.schedule_id=c.schedule_id \n" +
                        "JOIN tblemployee e ON s.employee_id=e.id JOIN tblusers u ON u.employee_id=e.id\n" +
                        "WHERE DATEPART(week, date_created) = DATEPART(week, GETDATE())" +
                        " AND Username = '" + currentUser + "' " +
                        " AND punch_out <> '00:00:00' " +
                        "GROUP BY CAST(DATEADD(day, -1*(DATEPART(WEEKDAY, date_created)-1), date_created) as DATE), e.name";
                ResultSet rs = c.createStatement().executeQuery(SQL);
                int index = rs.getMetaData().getColumnCount();
                //dynamically add table columns, so they are made based off database columns
                //Not sure if this method will make it harder to add data later
                for (int i = 0; i < index; i++) {
                    final int j = i;
                    TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                    reportTable.getColumns().addAll(col);
                    //System.out.println("Column [" + i + "] ");
                    //add to observable list
                    while (rs.next()) {
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                            //Iterate Column
                            row.add(rs.getString(k));
                        }
                        //System.out.println("Row [1] added " + row);
                        tableData.add(row);
                    }
                    //add to tableview
                    reportTable.setItems(tableData);
                }
                c.close();
            }
            catch(Exception e){ //catch any exceptions
                e.printStackTrace();
                System.out.println("Error on Building Reports Table Data");
            }
        }

    }

    @FXML
    private void showScheduleInRange(){
        //clear the columns, rows, and datepickers from the previous query
        reportTable.getColumns().clear();
        reportTable.getItems().clear();

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        if(currUser.getEmployee().getRole().getRoleName().equals("Owner")
                || currUser.getEmployee().getRole().getRoleName().equals("Manager")){
            datePane.setVisible(true);
            datePaneNoUser.setVisible(false);
            datePaneUser.setVisible(true);

            employeeData = FXCollections.observableArrayList();
            employeeData.addAll(employeeRepository.findAllEmployee());
            employeeList.setItems(employeeData);
        }
        else{
            datePane.setVisible(true);
            datePaneNoUser.setVisible(true);
        }

        reportList.setText("Schedule Within Date Range");

        tableUserLabel.setText("");

        DateTimeFormatter sqlDateTimeConvert = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        if(!(currUser.getEmployee().getRole().getRoleName().equals("Owner")
        || currUser.getEmployee().getRole().getRoleName().equals("Manager"))){
                    submitButton.setOnAction(event -> {
                        if(!(beginDate.getValue() == null || endDate.getValue() == null)){
                            //clear the columns and rows from the previous query
                            reportTable.getColumns().clear();
                            reportTable.getItems().clear();

                            tableUserLabel.setText("Schedule from " + beginDate.getValue().format(dateFormat) +
                                    " to " + endDate.getValue().format(dateFormat) + " for " + currUser.getEmployee().getName());

                            //Connect to Database
                            Connection c;
                            try {
                                c = connectClass.connect();
                                String SQL = "SELECT CONVERT(varchar(15),CAST(schedule_time_begin AS TIME),100) AS \"Start Time\"," +
                                        " CONVERT(varchar(15),CAST(schedule_time_end AS TIME),100) AS \"End Time\", " +
                                        "CAST(schedule_date AS DATE) AS \"Schedule Date\", " +
                                        "day_desc AS \"Day of Week\" FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN \n" +
                                        "tblusers u ON e.id=u.employee_id " +
                                        "LEFT JOIN tbltimeoff t ON t.schedule_id=s.schedule_id " +
                                        "JOIN tblday d ON s.day_id=d.day_id WHERE Username = '" + currentUser + "' " +
                                        "AND s.schedule_date >= '" + beginDate.getValue().format(sqlDateTimeConvert) +
                                        "' AND s.schedule_date <= '" + endDate.getValue().format(sqlDateTimeConvert) +
                                        "' " +
                                        " AND (t.schedule_id IS NULL OR NOT t.approved=1)";
                                ResultSet rs = c.createStatement().executeQuery(SQL);
                                int index = rs.getMetaData().getColumnCount();
                                //dynamically add table columns, so they are made based off database columns
                                //Not sure if this method will make it harder to add data later
                                for (int i = 0; i < index; i++) {
                                    final int j = i;
                                    TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                                    col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                                    reportTable.getColumns().addAll(col);
                                    //System.out.println("Column [" + i + "] ");
                                    //add to observable list
                                    while (rs.next()) {
                                        //Iterate Row
                                        ObservableList<String> row = FXCollections.observableArrayList();
                                        for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                                            //Iterate Column
                                            row.add(rs.getString(k));
                                        }
                                        //System.out.println("Row [1] added " + row);
                                        tableData.add(row);
                                    }
                                    //add to tableview
                                    reportTable.setItems(tableData);
                                }
                                c.close();
                            }
                            catch(Exception e){ //catch any exceptions
                                e.printStackTrace();
                                System.out.println("Error on Building Reports Table Data");
                            }
                        }
                        else{
                            ErrorMessages.showErrorMessage("Fields are empty",
                                    "Date selections are blank",
                                    "Please select a beginning and end date.");
                        }

                        });
        }
        else{
            submitButton1.setOnAction(event -> {
                    if(!(beginDate1.getValue() == null || endDate1.getValue() == null
                    || employeeList.getSelectionModel().isEmpty())){
                        //clear the columns and rows from the previous query
                        reportTable.getColumns().clear();
                        reportTable.getItems().clear();

                        selectedEmployee = employeeList.getSelectionModel().getSelectedItem();
                        String selectedUser = selectedEmployee.getUser().getUsername();

                        tableUserLabel.setText("Schedule from " + beginDate1.getValue().format(dateFormat) +
                                " to " + endDate1.getValue().format(dateFormat) + " for " + selectedEmployee.getName());

                        //Connect to Database
                        Connection c;
                        try {
                            c = connectClass.connect();
                            String SQL = "SELECT CONVERT(varchar(15),CAST(schedule_time_begin AS TIME),100) AS \"Start Time\"," +
                                    " CONVERT(varchar(15),CAST(schedule_time_end AS TIME),100) AS \"End Time\", " +
                                    "CAST(schedule_date AS DATE) AS \"Schedule Date\", " +
                                    "day_desc AS \"Day of Week\" FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN \n" +
                                    "tblusers u ON e.id=u.employee_id " +
                                    "LEFT JOIN tbltimeoff t ON t.schedule_id=s.schedule_id " +
                                    "JOIN tblday d ON s.day_id=d.day_id WHERE Username = '" + selectedUser + "' " +
                                    "AND s.schedule_date >= '" + beginDate1.getValue().format(sqlDateTimeConvert) +
                                    "' AND s.schedule_date <= '" + endDate1.getValue().format(sqlDateTimeConvert) +
                                    "' " +
                                    " AND (t.schedule_id IS NULL OR NOT t.approved=1)";
                            ResultSet rs = c.createStatement().executeQuery(SQL);
                            int index = rs.getMetaData().getColumnCount();
                            //dynamically add table columns, so they are made based off database columns
                            //Not sure if this method will make it harder to add data later
                            for (int i = 0; i < index; i++) {
                                final int j = i;
                                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                                reportTable.getColumns().addAll(col);
                                //System.out.println("Column [" + i + "] ");
                                //add to observable list
                                while (rs.next()) {
                                    //Iterate Row
                                    ObservableList<String> row = FXCollections.observableArrayList();
                                    for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                                        //Iterate Column
                                        row.add(rs.getString(k));
                                    }
                                    //System.out.println("Row [1] added " + row);
                                    tableData.add(row);
                                }
                                //add to tableview
                                reportTable.setItems(tableData);
                            }
                            c.close();
                        }
                        catch(Exception e){ //catch any exceptions
                            e.printStackTrace();
                            System.out.println("Error on Building Reports Table Data");
                        }
                    }
                    else{
                        ErrorMessages.showErrorMessage("Fields are empty",
                                "Date or employee selections are blank",
                                "Please select a beginning date, end date, and employee.");
                    }
                });

        }




    }

    @FXML
    private void showScheduleThisWeek(){
        //clear the columns and rows from the previous query
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
        datePane.setVisible(false);

        reportList.setText("Current Schedule");

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        tableUserLabel.setText("Schedule This Week for " + currUser.getEmployee().getName());

        //Connect to Database
        Connection c;
        try {
            c = connectClass.connect();
            String SQL = "SELECT CONVERT(varchar(15),CAST(schedule_time_begin AS TIME),100) AS \"Start Time\"," +
                    " CONVERT(varchar(15),CAST(schedule_time_end AS TIME),100) AS \"End Time\", " +
                    "CAST(schedule_date AS DATE) AS \"Schedule Date\", " +
                    "day_desc AS \"Day of Week\" FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN \n" +
                    "tblusers u ON e.id=u.employee_id " +
                    "LEFT JOIN tbltimeoff t ON t.schedule_id=s.schedule_id " +
                    "JOIN tblday d ON s.day_id=d.day_id WHERE Username = '" + currentUser + "' " +
                    "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())" +
                    " AND (t.schedule_id IS NULL OR NOT t.approved=1)";
            ResultSet rs = c.createStatement().executeQuery(SQL);
            int index = rs.getMetaData().getColumnCount();
            //dynamically add table columns, so they are made based off database columns
            //Not sure if this method will make it harder to add data later
            for (int i = 0; i < index; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                reportTable.getColumns().addAll(col);
                //System.out.println("Column [" + i + "] ");
                //add to observable list
                while (rs.next()) {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                        //Iterate Column
                        row.add(rs.getString(k));
                    }
                    //System.out.println("Row [1] added " + row);
                    tableData.add(row);
                }
                //add to tableview
                reportTable.setItems(tableData);
            }
            c.close();
        }
        catch(Exception e){ //catch any exceptions
            e.printStackTrace();
            System.out.println("Error on Building Reports Table Data");
        }

    }

    @FXML
    private void showDaysOff(){
        //clear the columns and rows from the previous query
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
        datePane.setVisible(false);

        reportList.setText("No. of Days Off");

        //get the current user
        String currentUser = LoginController.userStore;
        Tblusers currUser = userRepository.findUsername(currentUser);

        tableUserLabel.setText("Days Taken Off for " + currUser.getEmployee().getName());

        //Connect to Database
        Connection c;
        try {
            c = connectClass.connect();
            String SQL = "SELECT COUNT(*) AS \"No. of Approved Days Off\", " +
                    " YEAR(t.begin_time_off_date) AS \"Year Of\" " +
                    "FROM tblschedule s " +
                    "JOIN tbltimeoff t ON t.schedule_id=s.schedule_id JOIN \n" +
                    "tblemployee e ON s.employee_id=e.id JOIN \n" +
                    "tblusers u ON e.id=u.employee_id " +
                    "WHERE Username = '" + currentUser + "' " +
                    "AND t.approved = 1 " +
                    "AND YEAR(t.begin_time_off_date) = '2020' " +
                    "GROUP BY YEAR(t.begin_time_off_date)";
            ResultSet rs = c.createStatement().executeQuery(SQL);
            int index = rs.getMetaData().getColumnCount();
            //dynamically add table columns, so they are made based off database columns
            //Not sure if this method will make it harder to add data later
            for (int i = 0; i < index; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                reportTable.getColumns().addAll(col);
                //System.out.println("Column [" + i + "] ");
                //add to observable list
                while (rs.next()) {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
                        //Iterate Column
                        row.add(rs.getString(k));
                    }
                    //System.out.println("Row [1] added " + row);
                    tableData.add(row);
                }
                //add to tableview
                reportTable.setItems(tableData);
            }
            c.close();
        }
        catch(Exception e){ //catch any exceptions
            e.printStackTrace();
            System.out.println("Error on Building Reports Table Data");
        }

    }
}
