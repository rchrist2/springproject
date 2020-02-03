package myproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import myproject.models.TblUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
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

    //follows example here: https://www.baeldung.com/spring-email
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

        }

    //maybe move this to a different class
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        //this is set here and in application.properties
        mailSender.setUsername("email of sender account");
        mailSender.setPassword("application password (not normal password)");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }


}
