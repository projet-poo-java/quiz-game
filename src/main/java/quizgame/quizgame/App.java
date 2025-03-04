package quizgame.quizgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 500);
            Image image = new Image(getClass().getResourceAsStream("assets/images/logo.png"));
            stage.getIcons().add(image);
            stage.setTitle("Quiz Game - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Application Error", "Failed to load the initial scene.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}