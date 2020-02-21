package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import myproject.ErrorMessages;
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
public class SignupController implements Initializable {

    public AnchorPane signupAnchor;
    public TextField signupUsernameText;
    public Pane paneLoadTextfield;
    public Pane paneLoadButton;
    public Button signupButton;

    @Autowired
    private ConfigurableApplicationContext springContext;

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
    private void sendCode() throws IOException, MessagingException {
        //currently not working, likely due to window freezing on button click
        //moving to separate method or using Thread.sleep does not work
        /*Label lb1 = new Label("Sending email...");
        paneLoadTextfield.getChildren().add(lb1);*/

        String userEmail = signupUsernameText.getText();
        String resetCode = getAlphaNumericString(5);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Test from Spring Boot");
        message.setText(resetCode);
        emailSender.send(message);

        //maybe change this to a pop-up message or label
        System.out.println("Email sent");
        ErrorMessages.showInformationMessage("Success", "Successfully sent email", "Email sent");

        //paneLoadTextfield.getChildren().remove(lb1);

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
            if(tf1.getText().equals(resetCode)){
                System.out.println("code matches");
            }
            else{
                System.out.println("code does not match");
            }
            ;
        });

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
