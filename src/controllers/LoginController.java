package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import utils.WindowStyle;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label userNameLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean hasError = false;

        // Reset labels
        userNameLabel.setTextFill(Color.TRANSPARENT);
        passwordLabel.setTextFill(Color.TRANSPARENT);

        if (username.isEmpty()) {
            userNameLabel.setTextFill(Color.RED);
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordLabel.setTextFill(Color.RED);
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // If login is successful, switch to quiz screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/quiz-screen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/assets/css/style.css").toExternalForm());

            Stage quizStage = new Stage();
            quizStage.setScene(scene);
            
            // Apply window styling
            WindowStyle.applyWindowStyle(quizStage, root, scene);

            // Get the current stage and close it
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Show the new stage
            quizStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load quiz screen", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Stage alertStage = new Stage();
        
        // Create the content
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #B918C8; -fx-text-fill: white; -fx-background-radius: 30px;");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(35);
        okButton.setOnAction(e -> alertStage.close());

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, contentLabel, okButton);
        layout.setStyle("-fx-background-color: #5D3282; -fx-background-radius: 20;");

        // Create scene
        Scene scene = new Scene(layout);
        alertStage.setScene(scene);
        alertStage.initModality(Modality.APPLICATION_MODAL);

        // Apply window styling
        WindowStyle.applyWindowStyle(alertStage, layout, scene);

        alertStage.showAndWait();
    }

    @FXML
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}