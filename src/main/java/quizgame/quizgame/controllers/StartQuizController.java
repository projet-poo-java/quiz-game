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
import javafx.stage.Modality;
import quizgame.quizgame.App;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import quizgame.quizgame.utils.DatabaseConnection;
import quizgame.quizgame.models.User;
import quizgame.quizgame.utils.AuthManager;

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
    @FXML private HBox privateSubjectsContainer;

    private static final java.util.Map<String, String> CATEGORY_IDS = java.util.Map.of(
        "Books", "10",
        "History", "23",
        "Nature", "17",
        "Mathematics", "19",
        "Computers", "18"
    );

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = AuthManager.getCurrentUser();
        if (currentUser == null) {
            showError("Error: Not logged in");
            return;
        }
        loadPrivateSubjects();
    }

    private void loadPrivateSubjects() {
        privateSubjectsContainer.getChildren().clear();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Current user ID: " + currentUser.getId());
            String sql = """
                SELECT s.* FROM subjects s 
                LEFT JOIN subject_access sa ON s.id = sa.subject_id 
                WHERE s.is_private = true 
                AND (s.created_by = ? OR sa.user_id = ?)
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(2, currentUser.getId());
            
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String code = rs.getString("invitation_code");
                int id = rs.getInt("id");
                
                // Debug print
                System.out.println("Found private subject: " + name + ", code: " + code + ", id: " + id);
                
                Button subjectButton = createSubjectButton(name, code, id);
                subjectButton.setStyle("-fx-background-color: #F7931D;");
                privateSubjectsContainer.getChildren().add(subjectButton);
            }
            
            System.out.println("Total private subjects found: " + count);
            
            if (count == 0) {
                javafx.scene.control.Label noSubjectsLabel = new javafx.scene.control.Label("No private subjects available");
                noSubjectsLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
                privateSubjectsContainer.getChildren().add(noSubjectsLabel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading private subjects: " + e.getMessage());
            showError("Error loading private subjects: " + e.getMessage());
        }
    }

    private Button createSubjectButton(String name, String invitationCode, int subjectId) {
        Button button = new Button(name);
        button.getStyleClass().add("categoryButton");
        button.setPrefSize(120, 60);
        button.setOnAction(e -> showInvitationCodeDialog(name, invitationCode, subjectId));
        HBox.setMargin(button, new Insets(0, 25, 0, 25));
        return button;
    }

    private void verifyInvitationCode(String subject, String code, int subjectId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String verifySql = "SELECT invitation_code FROM subjects WHERE id = ? AND invitation_code = ?";
            PreparedStatement pstmt = conn.prepareStatement(verifySql);
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, code);
            
            if (pstmt.executeQuery().next()) {
                grantAccess(subjectId, currentUser.getId());
                showPrivateQuizSelection(subject, subjectId);
            } else {
                showError("Invalid invitation code");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error verifying code");
        }
    }

    private void showInvitationCodeDialog(String subject, String correctCode, int subjectId) {
        try {
            if (hasAccess(subjectId)) {
                showPrivateQuizSelection(subject, subjectId);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/InvitationCodeModal.fxml"));
            Parent modalRoot = loader.load();
            
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(privateSubjectsContainer.getScene().getWindow());
            modalStage.setTitle("Enter Invitation Code");
            
            InvitationCodeModalController modalController = loader.getController();
            modalController.initData(subject);
            
            Scene modalScene = new Scene(modalRoot);
            modalStage.setScene(modalScene);
            modalStage.showAndWait();
            
            if (modalController.isConfirmed()) {
                verifyInvitationCode(subject, modalController.getInvitationCode(), subjectId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasAccess(int subjectId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT 1 FROM subject_access 
                WHERE subject_id = ? AND user_id = ?
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, subjectId);
            pstmt.setInt(2, currentUser.getId());
            
            return pstmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR,
            message
        );
        alert.showAndWait();
    }

    @FXML
    protected void onCategoryClick(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = clickedButton.getText();
        String categoryId = CATEGORY_IDS.get(category);
        
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/QuizSettingsModal.fxml"));
            Parent modalRoot = loader.load();
            
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(clickedButton.getScene().getWindow());
            modalStage.setTitle("Quiz Settings");
            
            QuizSettingsModalController modalController = loader.getController();
            modalController.initData(category, categoryId);
            
            Scene modalScene = new Scene(modalRoot);
            modalStage.setScene(modalScene);
            modalStage.showAndWait();
            
            if (modalController.isConfirmed()) {
                startQuiz(modalController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startQuiz(QuizSettingsModalController modalController) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/quizView.fxml"));
            Parent root = loader.load();
            
            // Use QuizViewController for both API and database quizzes
            QuizViewController controller = loader.getController();
            controller.initQuiz(
                modalController.getCategory(),
                modalController.getCategoryId(),
                modalController.getNumberOfQuestions(),
                modalController.getDifficulty().toLowerCase(),
                modalController.getTimer()
            );
            
            Stage stage = (Stage) booksButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error starting quiz");
        }
    }

    private void showPrivateQuizSelection(String category, int subjectId) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/PrivateQuizSelectionModal.fxml"));
            Parent modalRoot = loader.load();
            
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(privateSubjectsContainer.getScene().getWindow());
            modalStage.setTitle("Select Quiz");
            
            PrivateQuizSelectionController controller = loader.getController();
            controller.initData(subjectId);
            
            Scene modalScene = new Scene(modalRoot);
            modalStage.setScene(modalScene);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading quiz selection");
        }
    }


    private void grantAccess(int subjectId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT IGNORE INTO subject_access (subject_id, user_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, subjectId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
