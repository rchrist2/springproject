package myproject.controllers.WelcomeLoginSignup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Pair;
import myproject.ErrorMessages;
import myproject.SecurePassword;
import myproject.Validation;
import myproject.models.Tblusers;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

@Component
public class ReplacePassController implements Initializable {

    @FXML
    private AnchorPane signupAnchor;

    @FXML
    private TextField newPassInput;

    @FXML
    private TextField newPassConfirmInput;

    @FXML
    private Tooltip passwordRequirementTooltip;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordRequirementTooltip.setFont(Font.font("System", 12));
        passwordRequirementTooltip.setText("- Password must be 8 to 30 characters long\n" +
                "- Password must contain the following characters\n" +
                "\t- Uppercase characters [A-Z]\n" +
                "\t- Lowercase characters [a-z]\n" +
                "\t- Contains digit [0-9]\n" +
                "\t- Contains special characters (!, $, #, etc)");
    }

    @FXML
    private void replacePass(){
        String replacePassUser = ForgotPassController.forgotPassUser;
        Tblusers replacePassUserAcc = userRepository.findUsername(replacePassUser);

        Pair[] passwordError = Validation.validatePasswordRequirement(newPassInput.getText());

        if(!(newPassInput.getText().isEmpty() || newPassConfirmInput.getText().isEmpty()))
        {
            if(newPassInput.getText().equals(newPassConfirmInput.getText())) {
                if(!(Boolean)passwordError[0].getKey()) {
                    try {
                        System.out.println("The both match");
                        byte[] salt = SecurePassword.getSalt();
                        String changedPassword = SecurePassword.getSecurePassword(newPassInput.getText(), salt);

                        replacePassUserAcc.setSaltPassword(salt);
                        replacePassUserAcc.setHashedPassword(changedPassword);

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    userRepository.save(replacePassUserAcc);

                    System.out.println("Do they Match? :" + SecurePassword.checkPassword(replacePassUserAcc.getHashedPassword(),
                            newPassConfirmInput.getText(), replacePassUserAcc.getSaltPassword()));

                    ErrorMessages.showInformationMessage("Password reset", "Password has been successfully reset",
                            "You will be redirected to sign-in");
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signin.fxml"));
                        fxmlLoader.setControllerFactory(springContext::getBean);
                        Pane pane = fxmlLoader.load();

                        signupAnchor.getChildren().setAll(pane);
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                } else {
                    ErrorMessages.showErrorMessage("Error", "Invalid values provided", passwordError[0].getValue().toString());
                }
            }
            else{
                ErrorMessages.showErrorMessage("Password mismatch","Passwords do not match",
                        "Please re-enter password");
            }
        }
        else{
            ErrorMessages.showErrorMessage("Fields are empty","New password not provided",
                    "Please fill in the new password and confirm password fields");
        }

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
}
