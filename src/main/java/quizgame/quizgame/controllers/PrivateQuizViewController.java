package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import quizgame.quizgame.App;
import quizgame.quizgame.utils.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;

public class PrivateQuizViewController {
    @FXML private Label questionLabel;
    @FXML private VBox answersContainer;
    @FXML private Label timerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    private ToggleGroup answerGroup;
    private List<Boolean> answeredQuestions;
    private List<RadioButton> selectedAnswers;
    
    private List<QuizQuestion> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Timeline timer;
    private int timeLeft;
    private String currentQuizId;

    public void initQuiz(String category, String quizId, String numQuestions, String difficulty, String timerMinutes) {
        this.currentQuizId = quizId;
        answeredQuestions = new ArrayList<>();
        selectedAnswers = new ArrayList<>();
        answerGroup = new ToggleGroup();
        loadQuestionsFromDatabase(Integer.parseInt(quizId));
        initTimer(Integer.parseInt(timerMinutes));
        displayQuestion();
    }

    private void loadQuestionsFromDatabase(int quizId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT q.id, q.content as question, 
                       a.id as answer_id, a.content as answer, a.is_correct 
                FROM questions q 
                JOIN answers a ON q.id = a.question_id 
                WHERE q.quiz_id = ? 
                ORDER BY q.id, a.id
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();

            QuizQuestion currentQuestion = null;
            while (rs.next()) {
                int questionId = rs.getInt("id");
                
                if (currentQuestion == null || currentQuestion.getId() != questionId) {
                    currentQuestion = new QuizQuestion(
                        questionId,
                        rs.getString("question"),
                        new ArrayList<>()
                    );
                    questions.add(currentQuestion);
                }
                
                currentQuestion.getAnswers().add(new QuizAnswer(
                    rs.getInt("answer_id"),
                    rs.getString("answer"),
                    rs.getBoolean("is_correct")
                ));
            }
            
            System.out.println("Loaded " + questions.size() + " questions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTimer(int minutes) {
        timeLeft = minutes * 60;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                timer.stop();
                finishQuiz();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            // Update question number
            questionNumberLabel.setText(String.format("Q°: %d/%d", currentQuestionIndex + 1, questions.size()));
            
            QuizQuestion question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestion());
            
            answersContainer.getChildren().clear();
            answerGroup = new ToggleGroup(); // Reset toggle group for new question
            
            // Ensure lists are initialized properly
            while (answeredQuestions.size() <= currentQuestionIndex) {
                answeredQuestions.add(false);
            }
            while (selectedAnswers.size() <= currentQuestionIndex) {
                selectedAnswers.add(null);
            }
            
            for (QuizAnswer answer : question.getAnswers()) {
                RadioButton rb = new RadioButton(answer.getContent());
                rb.setToggleGroup(answerGroup);
                rb.setUserData(answer);
                rb.setWrapText(true);
                rb.setStyle("-fx-font-size: 16; -fx-text-fill: #533B7C;");
                rb.setCursor(Cursor.HAND);
                
                // Restore previous selection if exists
                if (selectedAnswers.get(currentQuestionIndex) != null && 
                    selectedAnswers.get(currentQuestionIndex).getText().equals(answer.getContent())) {
                    rb.setSelected(true);
                }
                
                answersContainer.getChildren().add(rb);
            }
            
            // Update button states
            updateNavigationButtons();
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void onNextButtonClick() {
        // Save current selection
        RadioButton selectedButton = (RadioButton) answerGroup.getSelectedToggle();
        if (selectedButton != null) {
            answeredQuestions.set(currentQuestionIndex, true);
            selectedAnswers.set(currentQuestionIndex, selectedButton);
            
            QuizAnswer selected = (QuizAnswer) selectedButton.getUserData();
            if (selected.isCorrect()) {
                score++;
            }
            
            // Check if this is the last question
            if (currentQuestionIndex >= questions.size() - 1) {
                finishQuiz();
                return;
            }
            
            currentQuestionIndex++;
            displayQuestion();
            updateScore();
        }
    }

    @FXML
    private void onPreviousButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    @FXML
    private void onQuitButtonClick() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Quiz");
        alert.setHeaderText("Are you sure you want to quit?");
        alert.setContentText("Your progress will be lost.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Stop timer and return to main screen
            if (timer != null) {
                timer.stop();
            }
            returnToMainScreen();
        }
    }

    private void returnToMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/StartQuiz.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score + "/" + questions.size());
    }

    private void updateNavigationButtons() {
        if (previousButton != null) {
            previousButton.setDisable(currentQuestionIndex == 0);
        }
        if (nextButton != null) {
            // Change button text and behavior for last question
            if (currentQuestionIndex >= questions.size() - 1) {
                nextButton.setText("Finish");
                nextButton.setDisable(false);
            } else {
                nextButton.setText("Next Question");
                nextButton.setDisable(false);
            }
        }
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.stop();
        }
        
        try {
            // Save score before showing results
            saveScore();
            
            // Load the ResultView
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/ResultView.fxml"));
            Parent root = loader.load();
            
            ResultViewController controller = loader.getController();
            controller.initData(score, questions.size());
            
            // Switch to result view
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to alert if ResultView fails to load
            showError("Error showing results: " + e.getMessage());
        }
    }

    private void saveScore() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO scores (user_id, quiz_id, score) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Get the current user ID from your auth system
            // pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(1, 1); // Temporary user ID
            pstmt.setInt(2, Integer.parseInt(currentQuizId));
            pstmt.setInt(3, score);
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to save score");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

class QuizQuestion {
    private int id;
    private String question;
    private List<QuizAnswer> answers;

    public QuizQuestion(int id, String question, List<QuizAnswer> answers) {
        this.id = id;
        this.question = question;
        this.answers = answers;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public List<QuizAnswer> getAnswers() { return answers; }
}

class QuizAnswer {
    private int id;
    private String content;
    private boolean isCorrect;

    public QuizAnswer(int id, String content, boolean isCorrect) {
        this.id = id;
        this.content = content;
        this.isCorrect = isCorrect;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public boolean isCorrect() { return isCorrect; }
}