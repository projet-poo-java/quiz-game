package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ResultViewController {
    @FXML private Label remark;
    @FXML private Label marks;
    @FXML private Label markstext;
    @FXML private Label correcttext;
    @FXML private Label wrongtext;
    @FXML private ProgressIndicator correct_progress;
    @FXML private ProgressIndicator wrong_progress;

    public void initData(int score, int totalQuestions) {
        double percentage = (double) score / totalQuestions;
        String remarkText;
        
        if (percentage >= 0.8) {
            remarkText = "Excellent! Your performance was outstanding!";
        } else if (percentage >= 0.6) {
            remarkText = "Good job! You've done well!";
        } else if (percentage >= 0.4) {
            remarkText = "Not bad, but there's room for improvement.";
        } else {
            remarkText = "Keep practicing to improve your score.";
        }

        remark.setText(remarkText);
        marks.setText(score + "/" + totalQuestions);
        markstext.setText(score + " Marks Scored");
        correcttext.setText("Correct Answers: " + score);
        wrongtext.setText("Incorrect Answers: " + (totalQuestions - score));
        
        correct_progress.setProgress((double) score / totalQuestions);
        wrong_progress.setProgress((double) (totalQuestions - score) / totalQuestions);
    }
}