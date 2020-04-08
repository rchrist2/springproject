package myproject;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ConfigurableApplicationContext;

import javafx.scene.control.ComboBox;
import javafx.util.Pair;
import myproject.models.TblRoles;
import myproject.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class Validation {
    public static String EMAIL_PATTERN = "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";
    public static String PHONE_NUMBER_PATTERN = "((\\(\\d{3}\\) ?)|(\\d{3}-))?\\d{3}-\\d{4}";
    public static String ALPHA = "^[a-zA-Z\\s]*$";
    public static String STREET_ADDRESS = "^(\\d{3,})\\s?(\\w{0,5})\\s([a-zA-Z]{2,30})\\s([a-zA-Z]{2,15})[.]?(\\s([a-zA-Z]{2,30})[,][\\s]?([a-zA-Z]{2,3})\\s([0-9]{5})[-]?([0-9]{4})?)?$";
    public static String PASSWORD_REQUIREMENT = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,8}$";

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

    public static boolean isAlpha(String word){
        return word.matches(ALPHA);
    }

    public static boolean validateStreetAddress(String address){
        return address.matches(STREET_ADDRESS);
    }

    public static Pair[] validatePasswordRequirement(String password){
        char[] passwordCharArray = password.toCharArray();

        String errorMessage = "Please fix the following errors:\n";
        boolean error = false, upper = true, lower = true, digit = true, special = true;

        int size = passwordCharArray.length;
        List<String> listOfErrors = new ArrayList<>();

        if(size < 8){
            errorMessage += "\t- Contains less than 8 characters\n";
            error = true;
        }

        if(size > 30){
            errorMessage += "\t- Contains greater than 30 characters\n";
            error = true;
        }

        for (char character : passwordCharArray) {
            System.out.println("Char: " + character);
            if(Character.isUpperCase(character) && upper)
                upper = false;

            if(Character.isLowerCase(character) && lower)
                lower = false;

            if(Character.isDigit(character) && digit)
                digit = false;

            if(!Character.isDigit(character) && !Character.isLetter(character) && !Character.isSpaceChar(character) && special)
                special = false;

        }

        if(upper){
            errorMessage += "\t- Contains no Uppercase Letters\n";
            error = true;
        }

        if(lower){
            errorMessage += "\t- Contains no Lowercase Letters\n";
            error = true;
        }

        if(digit){
            errorMessage += "\t- Contains no numbers\n";
            error = true;
        }

        if(special){
            errorMessage += "\t- Contains no Special Characters\n";
            error = true;
        }

        return new Pair[] { new Pair<>(error, errorMessage) };
    }

    public static Pair[] validateCrudAccount(String name, String email, String address, String phone, String username,
                                             TblRoles comboBoxRole, List<String> listOfEmails){
        String errorMessage = "Please fix the following errors:\n";
        boolean error = false;

        if((name.isEmpty() || email.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || username.isEmpty() || comboBoxRole == null)) {
            errorMessage += "\t- Please provides values for all fields\n";
            error = true;
        }

        if(!isAlpha(name)){
            errorMessage += "\t- Only letters are allowed for name\n";
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

        for (String duplicateEmails : listOfEmails) {
            if(duplicateEmails.equals(email)){
                errorMessage += "\t- Email already exists\n";
                error = true;

                break;
            }
        }

        return new Pair[] { new Pair<>(error, errorMessage) };
    }

    public static Pair[] checkRoleDuplicates(String roleName, List<String> listOfRoles){
        String errorMessage = "Please fix the following errors:\n";
        boolean error = false;

        if(!isAlpha(roleName)){
            errorMessage += "\t- The role name should only contain words\n";
            error = true;
        }

        for (String role: listOfRoles) {
            if(role.toLowerCase().equals(roleName.toLowerCase())){
                errorMessage += "\t- " + roleName + " already exists\n";
                error = true;
                break;
            }
        }

        return new Pair[] { new Pair<>(error, errorMessage)};
    }

    public static boolean checkForOwnerOrManager(String roleName){
        boolean noOwnerOrManager = true;

        if(roleName.toLowerCase().contains("owner") || roleName.toLowerCase().contains("manager")){
            noOwnerOrManager = false;
        }

        return noOwnerOrManager;
    }
}
