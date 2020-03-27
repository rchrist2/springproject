package myproject;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ConfigurableApplicationContext;

public class Validation {
    public static String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static String PHONE_NUMBER_PATTERN = "\\(?\\d+\\)?[-.\\s]?\\d+[-.\\s]?\\d+";

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
}
