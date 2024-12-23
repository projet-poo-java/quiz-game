package quizgame.quizgame.controllers.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import quizgame.quizgame.App;
import quizgame.quizgame.controllers.ProfileController;
import quizgame.quizgame.controllers.UserDashboardController;
import quizgame.quizgame.middlewares.RoleMiddleWare;
import quizgame.quizgame.utils.DatabaseConnection;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.IOException;
import java.util.regex.Pattern;

public class AuthController implements Initializable {

    @FXML
    private Button loginBtn;
    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField confirmPasswordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (loginBtn != null) {
            loginBtn.setOnAction(this::login);
        }
    }

    public void login(ActionEvent event) {
        try {
            String email = emailInput.getText();
            String password = passwordInput.getText();
            loginUser(event, email, password);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Login Error", "An error occurred during login.");
        }
    }

    public void register(ActionEvent event) {
        try {
            String email = emailInput.getText();
            String name = nameInput.getText();
            String password = passwordInput.getText();
            String confirmPassword = confirmPasswordInput.getText();
            registerUser(event, email, name, password, confirmPassword);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Registration Error", "An error occurred during registration.");
        }
    }

    public void goToRegister(ActionEvent event) {
        changeScene(event, "register.fxml", "Register", null, null);
    }

    public void goToLogin(ActionEvent event) {
        changeScene(event, "login.fxml", "Login", null, null);
    }

    public void loginUser(ActionEvent event, String email, String password) {
        Connection connection = null;
        PreparedStatement checkUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            checkUserExists = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            checkUserExists.setString(1, email);
            checkUserExists.setString(2, hashPassword(password));
            resultSet = checkUserExists.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                showError("Login Error", "Invalid credentials.");
            } else {
                String fetchedEmail = null;
                String fetchedName = null;
                String role = null;
                while (resultSet.next()) {
                    fetchedEmail = resultSet.getString("email");
                    fetchedName = resultSet.getString("name");
                    role = resultSet.getString("role");
                }
                RoleMiddleWare.redirectBasedOnRole(event, role, fetchedEmail, fetchedName);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            showError("Database Error", "An error occurred while accessing the database.");
        } finally {
            closeResources(resultSet, checkUserExists, null, connection);
        }
    }

    public void registerUser(ActionEvent event, String email, String name, String password,
            String password_confirmation) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement checkUserExists = null;
        ResultSet resultSet = null;
        try {
            if (!isValidEmail(email)) {
                showError("Registration Error", "Invalid email format!");
            } else if (password.length() < 8) {
                showError("Registration Error", "Password must be at least 8 characters long!");
            } else if (!password.equals(password_confirmation)) {
                showError("Registration Error", "Passwords do not match!");
            } else {
                connection = DatabaseConnection.getConnection();
                checkUserExists = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
                checkUserExists.setString(1, email);
                resultSet = checkUserExists.executeQuery();
                if (resultSet.isBeforeFirst()) {
                    showError("Registration Error", "Email already taken!");
                } else {
                    String hashedPassword = hashPassword(password);
                    psInsert = connection
                            .prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
                    psInsert.setString(1, name);
                    psInsert.setString(2, email);
                    psInsert.setString(3, hashedPassword);
                    psInsert.setString(4, "user"); // default role
                    psInsert.executeUpdate();

                    changeScene(event, "profile.fxml", "Profile", email, name);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            showError("Database Error", "An error occurred while accessing the database.");
        } finally {
            closeResources(resultSet, checkUserExists, psInsert, connection);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String email, String name) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 500);
            if (email != null && name != null) {
                if (fxmlFile.equals("userdashboard.fxml")) {
                    UserDashboardController userDashboardController = loader.getController();
                    if (userDashboardController != null) {
                        userDashboardController.setUserInfo(email, name);
                    }
                } else if (fxmlFile.equals("profile.fxml")) {
                    ProfileController profileController = loader.getController();
                    if (profileController != null) {
                        profileController.setUserInfo(email, name);
                    }
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load the requested page.");
        }
    }

    private static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void closeResources(ResultSet resultSet, PreparedStatement ps1, PreparedStatement ps2,
            Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps1 != null) {
            try {
                ps1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps2 != null) {
            try {
                ps2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
