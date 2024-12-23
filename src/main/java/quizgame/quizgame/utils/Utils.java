package quizgame.quizgame.utils;

import javafx.scene.control.Alert;

public class Utils {

    public void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
