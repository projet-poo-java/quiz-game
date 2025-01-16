package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
// import javafx.scene.text.Text;
import java.util.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import javafx.scene.Scene;
import quizgame.quizgame.App;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class QuizViewController {
    @FXML private Label categoryLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Label questionLabel;
    @FXML private RadioButton choice1RadioButton;
    @FXML private RadioButton choice2RadioButton;
    @FXML private RadioButton choice3RadioButton;
    @FXML private RadioButton choice4RadioButton;
    @FXML private Label timerLabel;

    private List<QuizData.Result> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private ToggleGroup answersGroup = new ToggleGroup();
    private Timer quizTimer;
    private int timeRemaining;

    @FXML
    public void initialize() {
        choice1RadioButton.setToggleGroup(answersGroup);
        choice2RadioButton.setToggleGroup(answersGroup);
        choice3RadioButton.setToggleGroup(answersGroup);
        choice4RadioButton.setToggleGroup(answersGroup);
    }

    public void initQuiz(String category, String categoryId, String numberOfQuestions, String difficulty, String timer) {
        if (category == null) category = "General";
        if (categoryId == null) categoryId = "9";
        if (numberOfQuestions == null) numberOfQuestions = "10";
        if (difficulty == null) difficulty = "easy";
        if (timer == null) timer = "10";
        
        categoryLabel.setText("Category: " + category);
        
        try {
            timeRemaining = Integer.parseInt(timer) * 60;
            startTimer();
        } catch (NumberFormatException e) {
            timeRemaining = 600; // Default 10 minutes
            startTimer();
        }
        
        fetchQuestions(categoryId, numberOfQuestions, difficulty.toLowerCase());
    }

    private void fetchQuestions(String categoryId, String numberOfQuestions, String difficulty) {
        HttpClient client = createHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://opentdb.com/api.php?amount=" + numberOfQuestions +
                        "&category=" + categoryId +
                        "&difficulty=" + difficulty.toLowerCase() +
                        "&encode=base64"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::processQuestionsBase64)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private HttpClient createHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpClient.newHttpClient();
        }
    }

    private void loadQuestions() {
        try {
            HttpClient client = createHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://your-api-endpoint.com/api/questions"))
                .GET()
                .build();

            System.out.println("Sending request to API..."); // Debug log
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("API Response: " + response.body()); // Debug log
                Gson gson = new Gson();
                QuizData quizData = gson.fromJson(response.body(), QuizData.class);
                
                if (quizData != null && quizData.getResults() != null) {
                    questions = quizData.getResults();
                    displayQuestion(currentQuestionIndex);
                } else {
                    showError("Error", "No questions received from API");
                }
            } else {
                showError("API Error", "Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load questions: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Décoder les questions Base64
    private void processQuestionsBase64(String jsonResponse) {
        Gson gson = new Gson();
        QuizData quizData = gson.fromJson(jsonResponse, QuizData.class);
        javafx.application.Platform.runLater(() -> {
            questions = quizData.getResults();

            // Décoder les chaînes Base64
            for (QuizData.Result question : questions) {
                question.setQuestion(new String(Base64.getDecoder().decode(question.getQuestion())));
                question.correct_answer = new String(Base64.getDecoder().decode(question.getCorrectAnswer()));
                List<String> decodedAnswers = new ArrayList<>();
                for (String incorrectAnswer : question.getIncorrectAnswers()) {
                    decodedAnswers.add(new String(Base64.getDecoder().decode(incorrectAnswer)));
                }
                question.incorrect_answers = decodedAnswers;
            }

            displayQuestion(0);
        });
    }

    private void displayQuestion(int index) {
        if (questions == null || questions.isEmpty()) {
            showAlert("No questions available");
            return;
        }
        QuizData.Result question = questions.get(index);
        questionNumberLabel.setText("Q°: " + (index + 1) + "/" + questions.size());
        questionLabel.setText(question.getQuestion());

        List<String> answers = new ArrayList<>(question.getIncorrectAnswers());
        answers.add(question.getCorrectAnswer());
        Collections.shuffle(answers);

        // Handle questions with less than 4 answers
        choice1RadioButton.setText(answers.get(0));
        choice2RadioButton.setText(answers.size() > 1 ? answers.get(1) : "");
        choice3RadioButton.setText(answers.size() > 2 ? answers.get(2) : "");
        choice4RadioButton.setText(answers.size() > 3 ? answers.get(3) : "");

        // Hide radio buttons with no answers
        choice2RadioButton.setVisible(answers.size() > 1);
        choice3RadioButton.setVisible(answers.size() > 2);
        choice4RadioButton.setVisible(answers.size() > 3);

        answersGroup.selectToggle(null);
    }

    private void startTimer() {
        if (timerLabel == null) {
            System.err.println("Timer label not initialized!");
            return;
        }
        
        if (quizTimer != null) {
            quizTimer.cancel();
        }
        
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    int minutes = timeRemaining / 60;
                    int seconds = timeRemaining % 60;
                    final String time = String.format("%02d:%02d", minutes, seconds);
                    
                    javafx.application.Platform.runLater(() -> {
                        if (timerLabel != null) {
                            timerLabel.setText(time);
                        }
                    });
                    
                    timeRemaining--;
                } else {
                    quizTimer.cancel();
                    javafx.application.Platform.runLater(() -> showResults());
                }
            }
        }, 0, 1000);
    }

    @FXML
    protected void onNextButtonClick() {
        if (answersGroup == null || answersGroup.getSelectedToggle() == null) {
            showAlert("Please select an answer");
            return;
        }

        RadioButton selected = (RadioButton) answersGroup.getSelectedToggle();
        String answer = selected.getText();

        if (currentQuestionIndex < questions.size() && 
            answer.equals(questions.get(currentQuestionIndex).getCorrectAnswer())) {
            score++;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion(currentQuestionIndex);
        } else {
            showResults();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Game");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showResults() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/ResultView.fxml"));
            Parent root = loader.load();
            
            ResultViewController controller = loader.getController();
            controller.initData(score, questions.size());
            
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to simple alert if loading ResultView fails
            showAlert("Quiz completed!\nYour score: " + score + "/" + questions.size());
        }
    }
    @FXML
    protected void onPreviousButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        } else {
            showAlert("This is the first question!");
        }
    }

    @FXML
    protected void onQuitButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/StartQuiz.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onChoiceSelected() {
        // This method will be called when a radio button is selected
        // You can add specific logic here if needed
        // For now, we'll just enable the next button if an answer is selected
        if (answersGroup.getSelectedToggle() != null) {
            // You might want to enable a next button here if you have one
        }
    }
}