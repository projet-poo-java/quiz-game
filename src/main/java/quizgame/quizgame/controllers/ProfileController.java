package quizgame.quizgame.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import quizgame.quizgame.utils.DBUtil;
import javafx.event.ActionEvent;

public class ProfileController implements Initializable {

    @FXML
    private Button logoutBtn;
    @FXML
    private Label welcomeText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutBtn.setOnAction((ActionEvent event) -> {
            DBUtil.changeScene(event, "login.fxml", "Log In!", null, null);
        });
    }

    public void setUserInfo(String email, String name) {
        welcomeText.setText("Welcome to quiz game " + name + ", " + email);
    }
}
