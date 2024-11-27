package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    // Lier le Label welcomeText du fichier FXML
    @FXML
    private Label welcomeText;

    // Méthode appelée lorsque l'utilisateur clique sur "Start Quiz"
    @FXML
    private void onStartQuizButtonClick() {
        try {
            // Charger le fichier quiz_page-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/quiz_page-view.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
