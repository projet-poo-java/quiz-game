package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartQuizController {
    @FXML private Button booksButton;
    @FXML private Button historyButton;
    @FXML private Button natureButton;
    @FXML private Button mathButton;
    @FXML private Button computersButton;
    @FXML private TextField numberOfQuestionsField;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField timerField;
    @FXML private ListView<String> questionsListView;

    private static final java.util.Map<String, String> CATEGORY_IDS = java.util.Map.of(
        "Books", "10",
        "History", "23",
        "Nature", "17",
        "Mathematics", "19",
        "Computers", "18"
    );

    @FXML
    protected void onCategoryClick(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = clickedButton.getText();
        String categoryId = CATEGORY_IDS.get(category);
        
        // Add default values and validation
        String numberOfQuestions = "10";
        if (numberOfQuestionsField != null && !numberOfQuestionsField.getText().isEmpty()) {
            numberOfQuestions = numberOfQuestionsField.getText();
        }
        
        String difficulty = "easy";
        if (difficultyComboBox != null && difficultyComboBox.getValue() != null) {
            difficulty = difficultyComboBox.getValue().toLowerCase();
        }
        
        String timer = "10";
        if (timerField != null && !timerField.getText().isEmpty()) {
            timer = timerField.getText();
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/quizView.fxml"));
            Parent root = loader.load();
            
            QuizViewController controller = loader.getController();
            controller.initQuiz(category, categoryId, numberOfQuestions, difficulty, timer);
            
            Stage stage = (Stage) clickedButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
