package quizgame.quizgame.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import quizgame.quizgame.models.Question;
import quizgame.quizgame.utils.DatabaseConnection;
import quizgame.quizgame.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import java.util.Optional;

public class QuestionsController {

    @FXML
    private TextArea questionContentField;
    @FXML
    private TextArea answer1ContentField;
    @FXML
    private TextArea answer2ContentField;
    @FXML
    private TextArea answer3ContentField;
    @FXML
    private TextArea answer4ContentField;
    @FXML
    private CheckBox answer1CorrectCheck;
    @FXML
    private CheckBox answer2CorrectCheck;
    @FXML
    private CheckBox answer3CorrectCheck;
    @FXML
    private CheckBox answer4CorrectCheck;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ListView<Question> questionList;
    @FXML
    private Button deleteQuestionBtn;
    private Question currentQuestion;
    private int quizId;

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    @FXML
    public void initialize() {
        if (saveButton != null) {
            saveButton.setOnAction(event -> saveQuestion());
        }
        if (cancelButton != null) {
            cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        }

        if (questionList != null) {
            questionList.setCellFactory(listView -> new ListCell<>() {
                private final HBox content;
                private final Label questionLabel;
                private final Button deleteButton;

                {
                    questionLabel = new Label();
                    deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-text-fill: white; -fx-background-color: red;");
                    deleteButton.setOnAction(event -> {
                        Question question = getItem();
                        if (question != null) {
                            getListView().getItems().remove(question);
                            deleteQuestionFromDatabase(question);
                        }
                    });
                    content = new HBox(10, questionLabel, deleteButton);
                    content.setAlignment(Pos.CENTER_LEFT);
                }

                @Override
                protected void updateItem(Question question, boolean empty) {
                    super.updateItem(question, empty);
                    if (empty || question == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        questionLabel.setText("Q" + question.getId() + ": " + question.getContent());
                        setGraphic(content);
                    }
                }
            });
        }

        if (deleteQuestionBtn != null) {
            deleteQuestionBtn.setOnAction(event -> confirmDeleteQuestion());
        }
    }

    public void setQuestionData(Question question) {
        this.currentQuestion = question;
        questionContentField.setText(question.getContent());
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM answers WHERE question_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, question.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    int index = 1;
                    while (rs.next()) {
                        String content = rs.getString("content");
                        boolean isCorrect = rs.getBoolean("is_correct");
                        switch (index) {
                            case 1:
                                answer1ContentField.setText(content);
                                answer1CorrectCheck.setSelected(isCorrect);
                                break;
                            case 2:
                                answer2ContentField.setText(content);
                                answer2CorrectCheck.setSelected(isCorrect);
                                break;
                            case 3:
                                answer3ContentField.setText(content);
                                answer3CorrectCheck.setSelected(isCorrect);
                                break;
                            case 4:
                                answer4ContentField.setText(content);
                                answer4CorrectCheck.setSelected(isCorrect);
                                break;
                        }
                        index++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deleteQuestionBtn.setVisible(true);
    }

    private void saveQuestion() {
        String questionContent = questionContentField.getText().trim();
        String answer1 = answer1ContentField.getText().trim();
        String answer2 = answer2ContentField.getText().trim();
        String answer3 = answer3ContentField.getText().trim();
        String answer4 = answer4ContentField.getText().trim();

        if (questionContent.isEmpty() || answer1.isEmpty() || answer2.isEmpty()
                || answer3.isEmpty() || answer4.isEmpty()) {
            Utils utils = new Utils();
            utils.showError("Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            if (currentQuestion == null) {
                addQuestion(conn, questionContent);
            } else {
                updateQuestion(conn, questionContent);
                deleteAnswersByQuestionId(conn, currentQuestion.getId());
            }
            addAnswer(conn, answer1, answer1CorrectCheck.isSelected());
            addAnswer(conn, answer2, answer2CorrectCheck.isSelected());
            addAnswer(conn, answer3, answer3CorrectCheck.isSelected());
            addAnswer(conn, answer4, answer4CorrectCheck.isSelected());
            conn.commit();
            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (SQLException e) {
            Utils utils = new Utils();
            utils.showError("Database Error", "Failed to save the question.");
            e.printStackTrace();
        }
    }

    private void addQuestion(Connection conn, String content) throws SQLException {
        String query = "INSERT INTO questions (content, quiz_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, content);
            stmt.setInt(2, this.quizId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    currentQuestion = new Question(rs.getInt(1), content, this.quizId,
                            new Timestamp(System.currentTimeMillis()));
                }
            }
        }
    }

    private void updateQuestion(Connection conn, String content) throws SQLException {
        String query = "UPDATE questions SET content = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, content);
            stmt.setInt(2, currentQuestion.getId());
            stmt.executeUpdate();
        }
    }

    private void deleteAnswersByQuestionId(Connection conn, int questionId) throws SQLException {
        String query = "DELETE FROM answers WHERE question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }

    private void addAnswer(Connection conn, String content, boolean isCorrect) throws SQLException {
        String query = "INSERT INTO answers (content, is_correct, question_id, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, content);
            stmt.setBoolean(2, isCorrect);
            stmt.setInt(3, currentQuestion.getId());
            stmt.executeUpdate();
        }
    }

    private void confirmDeleteQuestion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("Question: " + currentQuestion.getContent());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteQuestionFromDatabase(currentQuestion);
            ((Stage) deleteQuestionBtn.getScene().getWindow()).close();
        }
    }

    private void deleteQuestionFromDatabase(Question question) {
        String query = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, question.getId());
            stmt.executeUpdate();

            deleteAnswersByQuestionId(conn, question.getId());
        } catch (SQLException e) {
            Utils utils = new Utils();
            utils.showError("Error", "Failed to delete question from database.");
            e.printStackTrace();
        }
    }

}
