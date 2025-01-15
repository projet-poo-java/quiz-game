package quizgame.quizgame.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.Optional;
import javafx.scene.Parent;
import quizgame.quizgame.App;

public class DashController {
    private String userEmail;
    private String userName;

    @FXML
    private AnchorPane contentPane;
    
    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        handleDashboard(null);
    }

    public void setUserInfo(String email, String name) {
        this.userEmail = email;
        this.userName = name;
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        loadContent("Statistics.fxml");
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        loadContent("Users.fxml");
    }

    @FXML
    private void handleSubjects(ActionEvent event) {
        loadContent("Subjects.fxml");
    }

    @FXML
    private void handleQuizes(ActionEvent event) {
        loadContent("Quizes.fxml");
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/admin/Settings.fxml"));
            Parent settingsView = loader.load();
            SettingsController settingsController = loader.getController();
            settingsController.setUserEmail(userEmail);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(settingsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login page.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Get the current stage
                Stage currentStage = (Stage) contentPane.getScene().getWindow();
                
                // Load login view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/Login.fxml"));
                Scene scene = new Scene(loader.load());
                
                // Set the login scene
                currentStage.setScene(scene);
                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadContent(String fxmlFile) {
        try {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(true);
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/admin/" + fxmlFile));
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
        }
    }
}
