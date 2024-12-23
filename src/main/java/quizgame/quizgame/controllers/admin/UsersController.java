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
import quizgame.quizgame.models.User;
import quizgame.quizgame.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsersController {
    @FXML
    private Button addUserBtn;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Integer> isEmailVerifiedColumn;
    @FXML
    private TableColumn<User, Void> actionColumn;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private CheckBox emailVerifiedCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;

    private User currentUser;
    private FilteredList<User> filteredUsers;

    @FXML
    public void initialize() {
        if (addUserBtn != null) {
            addUserBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/quizgame/quizgame/views/admin/AddUser.fxml"));
                    Parent root = loader.load();
                    UsersController controller = loader.getController();
                    controller.setCurrentUser(null); // Set currentUser to null for adding a new user
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    loadUsers(); // Reload users after closing the AddUser stage
                } catch (IOException e) {
                    showError("Error", "Failed to load AddUser.fxml");
                    e.printStackTrace();
                }
            });
        }

        if (usersTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
            isEmailVerifiedColumn.setCellValueFactory(new PropertyValueFactory<>("isEmailVerified"));

            addActionButtonsToTable();
            loadUsers();
        }

        if (saveButton != null) {
            saveButton.setOnAction(event -> saveUser());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        }

        if (searchButton != null) {
            searchButton.setOnAction(event -> filterUsers());
        }
    }

    private void saveUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText().isEmpty() ? null : phoneField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue() == null ? "User" : roleComboBox.getValue();
        int isEmailVerified = emailVerifiedCheckBox.isSelected() ? 1 : 0;

        if (role.length() > 50) {
            showError("Validation Error", "Role value is too long. Maximum length is 50 characters.");
            return;
        }

        if (currentUser == null) {
            // Add new user
            User newUser = new User(name, email, phone, password, role, isEmailVerified);
            addUser(newUser);
        } else {
            // Update existing user
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setPassword(password);
            currentUser.setRole(role);
            currentUser.setIsEmailVerified(isEmailVerified);
            updateUser(currentUser);
        }

        ((Stage) saveButton.getScene().getWindow()).close();
        loadUsers(); // Reload users to reflect the changes
    }

    private void loadUsers() {
        Task<ObservableList<User>> task = new Task<>() {
            @Override
            protected ObservableList<User> call() {
                return getUsers();
            }
        };

        task.setOnRunning(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(true);
            }
        });
        task.setOnSucceeded(e -> {
            if (usersTable != null) {
                filteredUsers = new FilteredList<>(task.getValue(), p -> true);
                usersTable.setItems(filteredUsers);
            }
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
        });
        task.setOnFailed(e -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
            showError("Error", "Failed to load users");
        });

        new Thread(task).start();
    }

    public ObservableList<User> getUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"),
                        rs.getString("phone"), rs.getInt("isEmailVerified"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void addUser(User user) {
        String query = "INSERT INTO users (name, email, password, role, phone, isEmailVerified) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getIsEmailVerified());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            showError("Error", "Failed to add user");
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String query = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, phone = ?, isEmailVerified = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getIsEmailVerified());
            stmt.setInt(7, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Error", "Failed to update user");
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Error", "Failed to delete user");
            e.printStackTrace();
        }
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");

                    {
                        updateButton.getStyleClass().add("update-btn");
                        deleteButton.getStyleClass().add("delete-btn");

                        updateButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            showUpdateUserDialog(user);
                        });

                        deleteButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            showDeleteConfirmationDialog(user);
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

        actionColumn.setCellFactory(cellFactory);
    }

    private void showUpdateUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/admin/AddUser.fxml"));
            Parent root = loader.load();

            UsersController controller = loader.getController();
            controller.setUserData(user);
            controller.setCurrentUser(user); // Set the current user for updating

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadUsers(); // Reload users after closing the update user dialog
        } catch (IOException e) {
            showError("Error", "Failed to load AddUser.fxml");
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(User user) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + user.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteUser(user.getId());
            loadUsers(); // Reload users after deleting the user
        }
    }

    public void setUserData(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        passwordField.setText(user.getPassword());
        roleComboBox.setValue(user.getRole());
        emailVerifiedCheckBox.setSelected(user.getIsEmailVerified() == 1);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterUsers() {
        String filter = searchField.getText();
        if (filter == null || filter.isEmpty()) {
            filteredUsers.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            filteredUsers.setPredicate(user -> user.getName().toLowerCase().contains(lowerCaseFilter) ||
                    user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                    (user.getPhone() != null && user.getPhone().toLowerCase().contains(lowerCaseFilter)));
        }
    }
}
