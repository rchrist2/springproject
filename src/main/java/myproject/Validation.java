package myproject;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ConfigurableApplicationContext;

import javafx.scene.control.ComboBox;
import javafx.util.Pair;
import myproject.models.TblRoles;

public class Validation {
    public static String EMAIL_PATTERN = "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";
    public static String PHONE_NUMBER_PATTERN = "((\\(\\d{3}\\) ?)|(\\d{3}-))?\\d{3}-\\d{4}";
    public static String ALPHA = "^[a-zA-Z]*$";
    public static String STREET_ADDRESS = "^\\d+?[A-Za-z]*\\s\\w*\\s?\\w+?\\s\\w{2}\\w*\\s*\\w*$";

   /* private static ConfigurableApplicationContext springContext;

    public static void setBean(ConfigurableApplicationContext springC){
        springContext = springC;
    }*/

    public static boolean validateEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean validatePhone(String phone){
        return phone.matches(PHONE_NUMBER_PATTERN);
    }

    public static boolean checkPasswordForEquality(String currentPassword, String changedPassword){
        return currentPassword.equals(changedPassword);
    }

    public static boolean isAlpha(String word){
        return word.matches(word);
    }

    public static boolean validateStreetAddress(String address){
        return address.matches(STREET_ADDRESS);
    }

    public static Pair[] validateCrudAccount(String name, String email, String address, String phone, String username, TblRoles comboBoxRole){
        String errorMessage = "Please fix the following errors:\n";
        boolean error = false;

        if((name.isEmpty() || email.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || username.isEmpty() || comboBoxRole == null)) {
            errorMessage += "\t- Please provides values for all fields\n";
            error = true;
        }

        if(comboBoxRole == null){
            errorMessage += "\t- Please choose an option from the drop down box\n";
            error = true;
        }

        if(!validateEmail(email)){
            errorMessage += "\t- Email provided is not a valid email address\n";
            error = true;
        }

        if(!validateStreetAddress(address)){
            errorMessage += "\t- Address provided is not a valid address\n";
            error = true;
        }

        if(!validatePhone(phone)){
            errorMessage += "\t- Phone number provided is not a valid phone number\n";
            error = true;
        }

        if(!username.equals(email)){
            errorMessage += "\t- Username and email should be the same\n";
            error = true;
        }

        return new Pair[] { new Pair<>(error, errorMessage) };
    }
}
