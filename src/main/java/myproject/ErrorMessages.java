package myproject;

import javafx.scene.control.Alert;

public class ErrorMessages {

    public static void showErrorMessage(String title, String header, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
