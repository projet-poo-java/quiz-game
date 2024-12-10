package quizgame.quizgame.controllers;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import quizgame.quizgame.utils.DBUtil;

import java.io.IOException;
public class UserDashboardController {

    @FXML
    private Label Menu;

    @FXML
    private Label MenuBack;

    @FXML
    private AnchorPane slider;

    @FXML
    private Button startQuizButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button scoreButton;


    private boolean isMenuVisible = false;

    private Button startQuizInterfaceButton;

    @FXML
    private Button logoutBtn;
    @FXML
    private Label welcomeText;

    public void initialize() {
        // Initial state: Slider is hidden
        slider.setTranslateX(-200);

        // Add click events for Menu and MenuBack labels
        Menu.setOnMouseClicked(event -> toggleMenu());
        MenuBack.setOnMouseClicked(event -> toggleMenu());

        // Add navigation events for buttons
        startQuizButton.setOnAction(event -> openStartQuizInterface());
        dashboardButton.setOnAction(event -> showDashboard());
        scoreButton.setOnAction(event -> showScore());
        logoutBtn.setOnAction((ActionEvent event) -> {
            DBUtil.changeScene(event, "login.fxml", "Log In!", null, null);
        });
    }

    private void toggleMenu() {
        // Create a transition for sliding effect
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), slider);

        if (isMenuVisible) {
            // Hide the menu
            transition.setToX(-200);
            isMenuVisible = false;
        } else {
            // Show the menu
            transition.setToX(0);
            isMenuVisible = true;
        }

        transition.play();
    }

    private void openStartQuizInterface() {
        try {
            // Load the Start Quiz interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizgame/quizgame/views/StartQuiz.fxml"));
            Parent root = loader.load();

            // Create a new stage and display it
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Start Quiz");
            stage.show();
            // Show the "Start Quiz interface" button
            startQuizInterfaceButton.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDashboard() {
        // Logic to handle the Dashboard button click (e.g., show Dashboard UI)
        System.out.println("Dashboard button clicked!");
    }

    private void showScore() {
        // Logic to handle the Score button click (e.g., show Score UI)
        System.out.println("Score button clicked!");
    }


    public void setUserInfo(String email, String name) {
        welcomeText.setText("Welcome to quiz game " + name + ", " + email);
    }
}
