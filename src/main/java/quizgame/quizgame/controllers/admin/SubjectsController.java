package quizgame.quizgame.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import quizgame.quizgame.models.Subject;
import quizgame.quizgame.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SubjectsController {

    @FXML
    private Button addSubjectBtn;
    @FXML
    private TableView<Subject> subjectsTable;
    @FXML
    private TableColumn<Subject, Integer> idCol;
    @FXML
    private TableColumn<Subject, String> nameCol;
    @FXML
    private TableColumn<Subject, String> descriptionCol;
    @FXML
    private TableColumn<Subject, String> createdAtCol;
    @FXML
    private TableColumn<Subject, Void> actionCol;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private CheckBox isPrivateCheckbox;
    @FXML
    private TextField invitationCodeField;

    private Subject currentSubject;
    private FilteredList<Subject> filteredSubjects;

    @FXML
    public void initialize() {
        if (addSubjectBtn != null) {
            addSubjectBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/quizgame/quizgame/views/admin/AddSubject.fxml"));
                    Parent root = loader.load();
                    SubjectsController controller = loader.getController();
                    controller.setCurrentSubject(null); // Set currentSubject to null for adding a new subject
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    loadSubjects(); // Reload subjects after closing the AddSubject stage
                } catch (IOException e) {
                    showError("Error", "Failed to load AddSubject.fxml");
                    e.printStackTrace();
                }
            });
        }

        if (subjectsTable != null) {
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

            addActionButtonsToTable();
            loadSubjects();
        }

        if (saveButton != null) {
            saveButton.setOnAction(event -> saveSubject());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        }

        if (searchButton != null) {
            searchButton.setOnAction(event -> filterSubjects());
        }

        if (isPrivateCheckbox != null) {
            isPrivateCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                invitationCodeField.setDisable(!newVal);
                if (!newVal) invitationCodeField.clear();
            });
        }
    }

    private void saveSubject() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        boolean isPrivate = isPrivateCheckbox != null && isPrivateCheckbox.isSelected();
        String invitationCode = isPrivate ? invitationCodeField.getText() : null;

        if (isPrivate && (invitationCode == null || invitationCode.trim().isEmpty())) {
            showError("Error", "Private subjects must have an invitation code");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            if (currentSubject == null) {
                sql = "INSERT INTO subjects (name, description, is_private, invitation_code, created_by) VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "UPDATE subjects SET name = ?, description = ?, is_private = ?, invitation_code = ? WHERE id = ?";
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setBoolean(3, isPrivate);
            pstmt.setString(4, invitationCode);
            
            if (currentSubject == null) {
                pstmt.setInt(5, getCurrentUserId()); // Implement this method to get logged-in user ID
            } else {
                pstmt.setInt(5, currentSubject.getId());
            }

            pstmt.executeUpdate();
            ((Stage) saveButton.getScene().getWindow()).close();
            loadSubjects();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error", "Failed to save subject");
        }
    }

    private void loadSubjects() {
        Task<ObservableList<Subject>> task = new Task<>() {
            @Override
            protected ObservableList<Subject> call() {
                return getSubjects();
            }
        };

        task.setOnRunning(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(true);
            }
        });
        task.setOnSucceeded(e -> {
            if (subjectsTable != null) {
                filteredSubjects = new FilteredList<>(task.getValue(), p -> true);
                subjectsTable.setItems(filteredSubjects);
            }
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
        });
        task.setOnFailed(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
            showError("Error", "Failed to load subjects");
        });

        new Thread(task).start();
    }

    public ObservableList<Subject> getSubjects() {
        ObservableList<Subject> subjects = FXCollections.observableArrayList();
        String query = "SELECT * FROM subjects";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Subject subject = new Subject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at"),
                    rs.getBoolean("is_private"),
                    rs.getString("invitation_code"),
                    rs.getInt("created_by")
                );
                subjects.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public void addSubject(Subject subject) {
        String query = "INSERT INTO subjects (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subject.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            showError("Error", "Failed to add subject");
            e.printStackTrace();
        }
    }

    public void updateSubject(Subject subject) {
        String query = "UPDATE subjects SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setInt(3, subject.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Error", "Failed to update subject");
            e.printStackTrace();
        }
    }

    public void deleteSubject(int subjectId) {
        String query = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subjectId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Error", "Failed to delete subject");
            e.printStackTrace();
        }
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Subject, Void>, TableCell<Subject, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Subject, Void> call(final TableColumn<Subject, Void> param) {
                final TableCell<Subject, Void> cell = new TableCell<>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");

                    {
                        updateButton.getStyleClass().add("update-btn");
                        deleteButton.getStyleClass().add("delete-btn");

                        updateButton.setOnAction(event -> {
                            Subject subject = getTableView().getItems().get(getIndex());
                            showUpdateSubjectDialog(subject);
                        });

                        deleteButton.setOnAction(event -> {
                            Subject subject = getTableView().getItems().get(getIndex());
                            showDeleteConfirmationDialog(subject);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(10, updateButton, deleteButton));
                        }
                    }
                };
                return cell;
            }
        };

        actionCol.setCellFactory(cellFactory);
    }

    private void showUpdateSubjectDialog(Subject subject) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/quizgame/quizgame/views/admin/AddSubject.fxml"));
            Parent root = loader.load();

            SubjectsController controller = loader.getController();
            controller.setSubjectData(subject);
            controller.setCurrentSubject(subject); // Set the current subject for updating

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadSubjects(); // Reload subjects after closing the update subject dialog
        } catch (IOException e) {
            showError("Error", "Failed to load AddSubject.fxml");
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(Subject subject) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Subject");
        alert.setHeaderText("Are you sure you want to delete this subject?");
        alert.setContentText("Subject: " + subject.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteSubject(subject.getId());
            loadSubjects(); // Reload subjects after deleting the subject
        }
    }

    public void setSubjectData(Subject subject) {
        nameField.setText(subject.getName());
        descriptionField.setText(subject.getDescription());
        if (isPrivateCheckbox != null) {
            isPrivateCheckbox.setSelected(subject.isPrivate());
            invitationCodeField.setText(subject.getInvitationCode());
            invitationCodeField.setDisable(!subject.isPrivate());
        }
    }

    public void setCurrentSubject(Subject subject) {
        this.currentSubject = subject;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterSubjects() {
        String filter = searchField.getText();
        if (filter == null || filter.isEmpty()) {
            filteredSubjects.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            filteredSubjects.setPredicate(subject -> subject.getName().toLowerCase().contains(lowerCaseFilter) ||
                    subject.getDescription().toLowerCase().contains(lowerCaseFilter));
        }
    }

    private int getCurrentUserId() {
        // Implement this method to get the ID of the currently logged-in user
        // This should come from your authentication system
        return 1; // Temporary return value
    }
}
