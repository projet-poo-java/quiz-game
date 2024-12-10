package quizgame.quizgame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import quizgame.quizgame.utils.DBUtil;

public class RegisterController {

    @FXML
    private TextField emailInput;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private PasswordField confirmPasswordInput;

    @FXML
    public void register(ActionEvent event) {
        try {
            String email = emailInput.getText();
            String name = nameInput.getText();
            String password = passwordInput.getText();
            String confirmPassword = confirmPasswordInput.getText();

            DBUtil.registerUser(event, email, name, password, confirmPassword);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Registration Error", "An error occurred during registration.");
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        DBUtil.changeScene(event, "login.fxml", "Login", null, null);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
