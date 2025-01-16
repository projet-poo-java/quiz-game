package quizgame.quizgame.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quizgame.quizgame.App;
import quizgame.quizgame.utils.DatabaseConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.io.IOException;

public class SettingsController {
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordBtn;
    @FXML private Button deleteAccountBtn;
    @FXML private Label messageLabel;

    private String currentUserEmail; // Add this field

    public void setUserEmail(String email) {
        this.currentUserEmail = email;
        System.out.println("Email set in SettingsController: " + email); // Debug line
    }
    
    @FXML
    private void handleChangePassword() {
        if (currentUserEmail == null) {
            messageLabel.setText("Error: User email not set");
            return;
        }
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Clear previous messages
        messageLabel.setText("");
        
        // Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("New passwords do not match");
            return;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            
            // First verify old password
            String hashedOldPassword = hashPassword(oldPassword);
            PreparedStatement checkPass = connection.prepareStatement(
                "SELECT * FROM users WHERE email = ? AND password = ?"
            );
            checkPass.setString(1, currentUserEmail);
            checkPass.setString(2, hashedOldPassword);
            
            ResultSet rs = checkPass.executeQuery();
            if (!rs.next()) {
                messageLabel.setText("Current password is incorrect");
                return;
            }

            // Update with new password
            String hashedNewPassword = hashPassword(newPassword);
            PreparedStatement updatePass = connection.prepareStatement(
                "UPDATE users SET password = ? WHERE email = ?"
            );
            updatePass.setString(1, hashedNewPassword);
            updatePass.setString(2, currentUserEmail);
            updatePass.executeUpdate();

            messageLabel.setText("Password changed successfully!");
            clearFields();

        } catch (SQLException | NoSuchAlgorithmException e) {
            messageLabel.setText("Error updating password");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = null;
            try {
                connection = DatabaseConnection.getConnection();
                connection.setAutoCommit(false); // Start transaction

                // First, delete related scores
                PreparedStatement deleteScores = connection.prepareStatement(
                    "DELETE FROM scores WHERE user_id = (SELECT id FROM users WHERE email = ?)"
                );
                deleteScores.setString(1, currentUserEmail);
                deleteScores.executeUpdate();

                // Then delete the user
                PreparedStatement deleteUser = connection.prepareStatement(
                    "DELETE FROM users WHERE email = ?"
                );
                deleteUser.setString(1, currentUserEmail);
                int rowsAffected = deleteUser.executeUpdate();
                
                if (rowsAffected > 0) {
                    connection.commit(); // Commit transaction
                    // Load login view
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("views/login.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) deleteAccountBtn.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } else {
                    connection.rollback(); // Rollback if user not found
                    messageLabel.setText("Error deleting account");
                }
                
            } catch (SQLException | IOException e) {
                if (connection != null) {
                    try {
                        connection.rollback(); // Rollback on error
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                messageLabel.setText("Error deleting account");
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
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

    private void clearFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}
