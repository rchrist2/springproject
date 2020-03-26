package myproject.controllers;

public class Validation {
    public static String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static String PHONE_NUMBER_PATTERN = "\\(?\\d+\\)?[-.\\s]?\\d+[-.\\s]?\\d+";

    public static boolean validateEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean validatePhone(String phone){
        return phone.matches(PHONE_NUMBER_PATTERN);
    }
}
