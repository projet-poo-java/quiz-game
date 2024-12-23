package quizgame.quizgame.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import quizgame.quizgame.models.Question;
import quizgame.quizgame.models.Quiz;
import quizgame.quizgame.models.Subject;
import quizgame.quizgame.utils.DatabaseConnection;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import quizgame.quizgame.utils.Utils;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class QuizzesController {
    @FXML
    private TableView<Quiz> quizTable;
    @FXML
    private TableColumn<Quiz, Integer> idColumn;
    @FXML
    private TableColumn<Quiz, String> titleColumn;
    @FXML
    private TableColumn<Quiz, String> categoryColumn;
    @FXML
    private TableColumn<Quiz, String> durationColumn;
    @FXML
    private TableColumn<Quiz, String> levelColumn;
    @FXML
    private TableColumn<Quiz, Void> actionColumn;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Button addQuizBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button plusQuestionBtn;
    @FXML
    private ListView<Question> questionList;

    @FXML
    private TextArea titleField;
    @FXML
    private ComboBox<Subject> categoryComboBox;
    @FXML
    private TextField durationField;
    @FXML
    private ComboBox<String> levelSelect;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Quiz currentQuiz;
    private FilteredList<Quiz> filteredQuizzes;

    @FXML
    public void initialize() {
        if (addQuizBtn != null) {
            addQuizBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/quizgame/quizgame/views/admin/AddQuiz.fxml"));
                    Parent root = loader.load();
                    QuizzesController controller = loader.getController();
                    controller.setCurrentQuiz(null);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    loadQuizzes();
                } catch (IOException e) {
                    Utils utils = new Utils();
                    utils.showError("Error", "Failed to load AddQuiz.fxml");
                    e.printStackTrace();
                }
            });
        }

        if (quizTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

            addActionButtonsToTable();
            loadQuizzes();
        }

        if (saveButton != null) {
            saveButton.setOnAction(event -> saveQuiz());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        }

        if (searchButton != null) {
            searchButton.setOnAction(event -> filterQuizzes());
        }

        if (categoryComboBox != null) {
            ObservableList<Subject> subjects = Subject.getAllSubjects();
            categoryComboBox.setItems(subjects);

            categoryComboBox.setCellFactory(lv -> new ListCell<Subject>() {
                @Override
                protected void updateItem(Subject item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item != null ? item.getName() : ""));
                }
            });
        }

        if (plusQuestionBtn != null) {
            plusQuestionBtn.setDisable(true);

            plusQuestionBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/quizgame/quizgame/views/admin/AddQuestion.fxml"));
                    Parent root = loader.load();
                    QuestionsController controller = loader.getController();
                    controller.setQuizId(currentQuiz.getId());
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    setQuizData(currentQuiz);
                } catch (IOException e) {
                    Utils utils = new Utils();
                    utils.showError("Error", "Failed to load AddQuestion.fxml");
                    e.printStackTrace();
                }
            });
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
                    deleteButton.setLayoutX(506);
                    deleteButton.setLayoutY(10);
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

            questionList.getItems().addListener((ListChangeListener<Question>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        for (Question removedQuestion : change.getRemoved()) {
                            deleteQuestionFromDatabase(removedQuestion);
                        }
                    }
                }
            });
        }
    }

    private void loadQuizzes() {
        if (quizTable == null)
            return;

        Task<ObservableList<Quiz>> task = new Task<>() {
            @Override
            protected ObservableList<Quiz> call() {
                return getQuizzes();
            }
        };

        task.setOnRunning(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(true);
            }
        });

        task.setOnSucceeded(e -> {
            filteredQuizzes = new FilteredList<>(task.getValue(), p -> true);
            if (quizTable != null) {
                quizTable.setItems(filteredQuizzes);
            }
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
        });

        task.setOnFailed(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
            Utils utils = new Utils();
            utils.showError("Error", "Failed to load quizzes");
        });

        new Thread(task).start();
    }

    private ObservableList<Quiz> getQuizzes() {
        ObservableList<Quiz> quizzes = FXCollections.observableArrayList();
        String query = "SELECT * FROM quizzes";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Quiz quiz = new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("duration"),
                        rs.getString("level"),
                        rs.getInt("subject_id"),
                        rs.getTimestamp("created_at"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    private void saveQuiz() {
        String title = titleField.getText();
        Subject selectedSubject = categoryComboBox.getValue();
        String duration = durationField.getText();
        String level = levelSelect.getValue();

        if (currentQuiz == null) {
            Quiz newQuiz = new Quiz(0, title, duration, level,
                    selectedSubject != null ? selectedSubject.getId() : 1,
                    new Timestamp(System.currentTimeMillis()));
            addQuiz(newQuiz);
        } else {
            currentQuiz.setTitle(title);
            currentQuiz.setDuration(duration);
            currentQuiz.setLevel(level);
            updateQuiz(currentQuiz);
        }

        ((Stage) saveButton.getScene().getWindow()).close();
        loadQuizzes();
    }

    private void addQuiz(Quiz quiz) {
        String query = "INSERT INTO quizzes (title, duration, level, subject_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDuration());
            stmt.setString(3, quiz.getLevel());
            stmt.setInt(4, quiz.getSubjectId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quiz.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            Utils utils = new Utils();
            utils.showError("Error", "Failed to add quiz");
            e.printStackTrace();
        }
    }

    private void updateQuiz(Quiz quiz) {
        String query = "UPDATE quizzes SET title = ?, duration = ?, level = ?, subject_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDuration());
            stmt.setString(3, quiz.getLevel());
            stmt.setInt(4, quiz.getSubjectId());
            stmt.setInt(5, quiz.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Utils utils = new Utils();
            utils.showError("Error", "Failed to update quiz");
            e.printStackTrace();
        }
    }

    private void deleteQuiz(int quizId) {
        String query = "DELETE FROM quizzes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Utils utils = new Utils();
            utils.showError("Error", "Failed to delete quiz");
            e.printStackTrace();
        }
    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(10, updateButton, deleteButton);

            {
                updateButton.getStyleClass().add("update-btn");
                deleteButton.getStyleClass().add("delete-btn");

                updateButton.setOnAction(event -> {
                    Quiz quiz = getTableRow().getItem();
                    if (quiz != null) {
                        showUpdateQuizDialog(quiz);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Quiz quiz = getTableRow().getItem();
                    if (quiz != null) {
                        showDeleteConfirmationDialog(quiz);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });
    }

    private void showUpdateQuizDialog(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/admin/AddQuiz.fxml"));
            Parent root = loader.load();
            QuizzesController controller = loader.getController();
            controller.setQuizData(quiz);
            controller.setCurrentQuiz(quiz);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadQuizzes();
        } catch (IOException e) {
            Utils utils = new Utils();
            utils.showError("Error", "Failed to load AddQuiz.fxml");
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Quiz");
        alert.setHeaderText("Are you sure you want to delete this quiz?");
        alert.setContentText("Quiz: " + quiz.getTitle());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteQuiz(quiz.getId());
            loadQuizzes();
        }
    }

    public void setQuizData(Quiz quiz) {
        titleField.setText(quiz.getTitle());
        durationField.setText(quiz.getDuration());
        levelSelect.setValue(quiz.getLevel());

        ObservableList<Subject> subjects = Subject.getAllSubjects();
        categoryComboBox.setItems(subjects);

        for (Subject subject : subjects) {
            if (subject.getId() == quiz.getSubjectId()) {
                categoryComboBox.setValue(subject);
                break;
            }
        }

        if (questionList != null) {
            questionList.setItems(quiz.getQuestions());

            questionList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Question question, boolean empty) {
                    super.updateItem(question, empty);
                    if (empty || question == null) {
                        setText(null);
                    } else {
                        setText("Q" + question.getId() + ": " + question.getContent());
                    }
                }
            });

            questionList.getItems().addListener((ListChangeListener<Question>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        for (Question removedQuestion : change.getRemoved()) {
                            deleteQuestionFromDatabase(removedQuestion);
                        }
                    }
                }
            });
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

    private void deleteAnswersByQuestionId(Connection conn, int questionId) throws SQLException {
        String query = "DELETE FROM answers WHERE question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }

    public void setCurrentQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
        if (plusQuestionBtn != null) {
            plusQuestionBtn.setDisable(quiz == null);
        }
    }

    private void filterQuizzes() {
        String filter = searchField.getText().toLowerCase();
        filteredQuizzes.setPredicate(quiz -> filter == null || filter.isEmpty() ||
                quiz.getTitle().toLowerCase().contains(filter));
    }

    @FXML
    private void handleQuestionClick() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/quizgame/quizgame/views/admin/AddQuestion.fxml"));
                Parent root = loader.load();
                QuestionsController controller = loader.getController();
                controller.setQuestionData(selectedQuestion);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                setQuizData(currentQuiz);
            } catch (IOException e) {
                Utils utils = new Utils();
                utils.showError("Error", "Failed to load AddQuestion.fxml");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleQuestionDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/quizgame/quizgame/views/admin/AddQuestion.fxml"));
                    Parent root = loader.load();
                    QuestionsController controller = loader.getController();
                    controller.setQuestionData(selectedQuestion);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    setQuizData(currentQuiz);
                } catch (IOException e) {
                    Utils utils = new Utils();
                    utils.showError("Error", "Failed to load AddQuestion.fxml");
                    e.printStackTrace();
                }
            }
        }
    }
}
