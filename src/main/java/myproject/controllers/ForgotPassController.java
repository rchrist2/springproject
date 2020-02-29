package myproject.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import myproject.ErrorMessages;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.mail.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class ForgotPassController implements Initializable {

    public AnchorPane signupAnchor;
    public TextField signupUsernameText;
    public Pane paneLoadTextfield;
    public Pane paneLoadButton;
    public Button signupButton;

    public static String forgotPassUser;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private UserRepository userRepository;

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void changeToSignin(){

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signin.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Pane pane = fxmlLoader.load();

            signupAnchor.getChildren().setAll(pane);
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    //Follows example here: https://www.baeldung.com/spring-email
    @FXML
    private void sendCode() throws IOException, MessagingException, InterruptedException {
        Label lb1 = new Label("Sending email...");
        paneLoadTextfield.getChildren().add(lb1);

        if(!(signupUsernameText.getText().isEmpty())){
            //if the user exists in the database, then email the code to them
            if(userRepository.findUsername(signupUsernameText.getText()) != null) {
                String userEmail = signupUsernameText.getText();
                forgotPassUser = userEmail;

                //save the user's name for the email message
                String userEmpName = userRepository.findUsername(userEmail).getEmployee().getName();

                //generate the reset confirmation code
                String resetCode = getAlphaNumericString(5);

                //start a background thread to send the mail
                //this allows a "Sending email..." label to be shown while the user waits
                Thread thread = new Thread(() -> {
                    try {
                        //send the email
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(userEmail);
                        message.setSubject("Password Reset Confirmation");
                        message.setText("Hello " + userEmpName + ","
                                + "\n\nYou have requested to reset your account password."
                                + "\n\nYour confirmation code is: " + resetCode
                                + "\n\nIf you did not request this email, please ignore this message."
                                + "\n\nSincerely,"
                                + "\nSpace City Solutions"
                                + "\nHouston, TX 77583");
                        emailSender.send(message);

                        //show the rest of the UI elements to validate the code that was emailed
                        Platform.runLater(new Runnable() {
                            public void run() {
                                //maybe change this to a pop-up message or label
                                System.out.println("Email sent");
                                ErrorMessages.showInformationMessage("Success", "Successfully sent email", "Email sent");

                                paneLoadTextfield.getChildren().remove(lb1);

                                TextField tf1 = new TextField();
                                Button button = new Button();

                                paneLoadTextfield.getChildren().add(tf1);
                                paneLoadButton.setVisible(true);
                                paneLoadButton.getChildren().add(button);
                                signupButton.setVisible(false);

                                tf1.setPrefWidth(signupUsernameText.getPrefWidth());
                                tf1.setPrefHeight(signupUsernameText.getPrefHeight());
                                tf1.setPromptText("Enter code sent to email");

                                button.setPrefSize(paneLoadButton.getPrefWidth(), paneLoadButton.getPrefHeight());
                                button.setText("Submit Code");
                                button.setStyle("-fx-background-color: #de1c00; " +
                                        "-fx-font-style: italic; -fx-font-weight: bold; -fx-font-size: 18; -fx-font: System");

                                button.setOnAction(event -> {
                                    if(!(tf1.getText().isEmpty())){
                                        if (tf1.getText().equals(resetCode)) {
                                            //open replace password page
                                            try {
                                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ReplacePassView.fxml"));
                                                fxmlLoader.setControllerFactory(springContext::getBean);
                                                Pane pane = fxmlLoader.load();

                                                signupAnchor.getChildren().setAll(pane);
                                            } catch (IOException io) {
                                                io.printStackTrace();
                                            }
                                        } else {
                                            ErrorMessages.showErrorMessage("Code mismatch", "Code does not match",
                                                    "Please re-enter code");
                                        }
                                    }
                                    else{
                                        ErrorMessages.showErrorMessage("Field is empty","No code given",
                                                "Please enter the code sent to your email");
                                    }

                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace(); //can remove this if we don't want errors in console
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                paneLoadTextfield.getChildren().remove(lb1);
                                ErrorMessages.showErrorMessage("Failed to send email", "Email could not be sent",
                                        "Please retry or contact administrator");
                            }
                        });
                    }
                });

                thread.start();
            }
            else{
                paneLoadTextfield.getChildren().remove(lb1);
                ErrorMessages.showErrorMessage("User does not exist","No user exists with this email",
                        "Please enter a valid email address");
            }
        }
        else{
            ErrorMessages.showErrorMessage("Field is empty","No email address entered",
                    "Please enter a valid email address");
        }


        }



        //maybe move this to a different class
    @Bean
    public JavaMailSender getJavaMailSender() throws IOException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        //used to take properties from .properties file for username/password
        FileInputStream input = null;
        Properties props1 = new Properties();
        input = new FileInputStream("src/main/resources/application.properties");
        props1.load(input );

        //this is set here and in application.properties
        mailSender.setUsername(props1.getProperty("spring.mail.username"));
        mailSender.setPassword(props1.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

}
