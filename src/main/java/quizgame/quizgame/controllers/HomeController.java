package quizgame.quizgame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import quizgame.quizgame.utils.DBUtil;

public class HomeController {

    @FXML
    public void goToLogin(ActionEvent event) {
        DBUtil.changeScene(event, "login.fxml", "Login", null, null);
    }
}