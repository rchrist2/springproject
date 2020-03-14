package myproject.controllers.Dashboard.TimeOff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import myproject.repositories.TimeOffRepository;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

@Component
public class TimeOffApproveController implements Initializable {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private TimeOffRepository timeOffRepository;

    @Autowired
    private UserRepository userRepository;

    /*@FXML
    public TableView<TblTimeOff> timeOffTable;

    @FXML
    public TableColumn<TblTimeOff, Date> timeOffDateCol;

    @FXML
    public TableColumn<TblTimeOff, String> approveTimeOffCol;

    @FXML
    public TableColumn<TblTimeOff, String> userTimeOffCol;

    private ObservableList<TblTimeOff> listOfTimeOffs;
    private FilteredList<TblTimeOff> filteredListOfTimeOff;

    public TblTimeOff selectedTimeOff;*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Initialize the observable list and add all the time offs to the list
        /*listOfTimeOffs = FXCollections.observableArrayList();
        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());

        reloadTimeOffTableView();
        setDataForTimeOffTableView();

        timeOffTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newv) -> {
            selectedTimeOff = newv;
        });*/
    }

    @FXML
    private void approveTimeOff(){

    }

    @FXML
    private void denyTimeOff(){

    }

    /*private void setDataForTimeOffTableView(){
        timeOffDateCol.setCellValueFactory(new PropertyValueFactory<>("timeOffDate"));
        approveTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("approved"));
        userTimeOffCol.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    private void reloadTimeOffTableView(){
        listOfTimeOffs.clear();

        listOfTimeOffs.addAll(timeOffRepository.findAllTimeOff());

        filteredListOfTimeOff = new FilteredList<>(listOfTimeOffs);

        timeOffTable.setItems(filteredListOfTimeOff);
    }*/
}
