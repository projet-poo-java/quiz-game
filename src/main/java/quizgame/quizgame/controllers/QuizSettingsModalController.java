package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class QuizSettingsModalController {
    @FXML private TextField numberOfQuestionsField;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField timerField;
    
    private String category;
    private String categoryId;
    private boolean confirmed = false;

    public void initData(String category, String categoryId) {
        this.category = category;
        this.categoryId = categoryId;
        difficultyComboBox.setValue("Easy");
    }

    @FXML
    protected void onStartQuiz() {
        confirmed = true;
        closeModal();
    }

    @FXML
    protected void onCancel() {
        confirmed = false;
        closeModal();
    }

    private void closeModal() {
        ((Stage) numberOfQuestionsField.getScene().getWindow()).close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNumberOfQuestions() {
        return numberOfQuestionsField.getText();
    }

    public String getDifficulty() {
        return difficultyComboBox.getValue();
    }

    public String getTimer() {
        return timerField.getText();
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
