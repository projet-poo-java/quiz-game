package quizgame.quizgame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import quizgame.quizgame.utils.DBUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button loginBtn;
    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginBtn.setOnAction(this::login);
    }

    public void login(ActionEvent event) {
        try {
            String email = emailInput.getText();
            String password = passwordInput.getText();

            DBUtil.loginUser(event, email, password);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Login Error", "An error occurred during login.");
        }
    }

    @FXML
    public void goToRegister(ActionEvent event) {
        DBUtil.changeScene(event, "register.fxml", "Register", null, null);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
