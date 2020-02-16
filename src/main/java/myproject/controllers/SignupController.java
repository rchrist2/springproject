package myproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class SignupController implements Initializable {

    public AnchorPane signupAnchor;
    public TextField signupUsernameText;

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
    private void sendCode() throws IOException, MessagingException{
        //this is just to make sure emailing works
        //it doesn't verify the testcode to let the user reset their password
        String userEmail = signupUsernameText.getText();
        String resetCode = "testcode";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Test from Spring Boot");
        message.setText(resetCode);
        emailSender.send(message);

        //maybe change this to a pop-up message or label
        System.out.println("Email sent");
        ErrorMessages.showInformationMessage("Success", "Successfully sent email", "Email sent");
    }

    //maybe move this to a different class
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        //used to take properties from .properties file for username/password
        Properties props1 = new Properties();

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


}
