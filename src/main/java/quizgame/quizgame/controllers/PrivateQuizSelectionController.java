package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import quizgame.quizgame.App;
import quizgame.quizgame.utils.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class PrivateQuizSelectionController {
    @FXML private ListView<QuizInfo> quizListView;
    @FXML private Label quizInfoLabel;
    
    private boolean confirmed = false;
    private int subjectId;

    @FXML
    public void initialize() {
        quizListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateQuizInfo(newVal);
            }
        });
    }

    public void initData(int subjectId) {
        this.subjectId = subjectId;
        loadQuizzes();
    }

    private void loadQuizzes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT q.id, q.title, q.duration, q.level, 
                       COUNT(qu.id) as question_count 
                FROM quizzes q 
                LEFT JOIN questions qu ON q.id = qu.quiz_id 
                WHERE q.subject_id = ? 
                GROUP BY q.id
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, subjectId);
            ResultSet rs = pstmt.executeQuery();

            ObservableList<QuizInfo> quizzes = FXCollections.observableArrayList();
            while (rs.next()) {
                quizzes.add(new QuizInfo(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("duration"),
                    rs.getString("level"),
                    rs.getInt("question_count")
                ));
            }
            quizListView.setItems(quizzes);

            if (quizzes.isEmpty()) {
                quizInfoLabel.setText("No quizzes available for this subject");
            }
        } catch (Exception e) {
            e.printStackTrace();
            quizInfoLabel.setText("Error loading quizzes");
        }
    }

    private void updateQuizInfo(QuizInfo quiz) {
        quizInfoLabel.setText(String.format(
            "Level: %s | Duration: %d minutes | Questions: %d",
            quiz.getLevel(),
            quiz.getDuration(),
            quiz.getQuestionCount()
        ));
    }

    @FXML
    private void onStartQuiz() {
        QuizInfo selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("views/PrivateQuizView.fxml"));
                Parent root = loader.load();
                
                PrivateQuizViewController controller = loader.getController();
                controller.initQuiz(
                    selectedQuiz.getTitle(), 
                    String.valueOf(selectedQuiz.getId()),
                    String.valueOf(selectedQuiz.getQuestionCount()),
                    selectedQuiz.getLevel(),
                    String.valueOf(selectedQuiz.getDuration())
                );
                
                Stage stage = (Stage) quizListView.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error starting quiz");
            }
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onCancel() {
        confirmed = false;
        closeModal();
    }

    private void closeModal() {
        ((Stage) quizListView.getScene().getWindow()).close();
    }

    public boolean isConfirmed() { return confirmed; }
    
    public QuizInfo getSelectedQuiz() {
        return quizListView.getSelectionModel().getSelectedItem();
    }
}

class QuizInfo {
    private int id;
    private String title;
    private int duration;
    private String level;
    private int questionCount;

    public QuizInfo(int id, String title, int duration, String level, int questionCount) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.level = level;
        this.questionCount = questionCount;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public String getLevel() { return level; }
    public int getQuestionCount() { return questionCount; }

    @Override
    public String toString() {
        return title + " (" + level + ")";
    }
}
