package quizgame.quizgame.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import quizgame.quizgame.App;
import quizgame.quizgame.controllers.ProfileController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

public class DBUtil {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String email, String name) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("views/" + fxmlFile));
        try {
            Scene scene = new Scene(loader.load(), 900, 500);
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            if (email != null && name != null) {
                ProfileController profileController = loader.getController();
                if (profileController != null) {
                    profileController.setUserInfo(email, name);
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

    public static void registerUser(ActionEvent event, String email, String name, String password, String password_confirmation) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement checkUserExists = null;
        ResultSet resultSet = null;
        try {
            if (!password.equals(password_confirmation)) {
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
                    psInsert = connection.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
                    psInsert.setString(1, name);
                    psInsert.setString(2, email);
                    psInsert.setString(3, hashedPassword);
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

    public static void loginUser(ActionEvent event, String email, String password) {
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
                while (resultSet.next()) {
                    fetchedEmail = resultSet.getString("email");
                    fetchedName = resultSet.getString("name");
                }
                changeScene(event, "profile.fxml", "Profile", fetchedEmail, fetchedName);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            showError("Database Error", "An error occurred while accessing the database.");
        } finally {
            closeResources(resultSet, checkUserExists, null, connection);
        }
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static void closeResources(ResultSet resultSet, PreparedStatement ps1, PreparedStatement ps2, Connection connection) {
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
