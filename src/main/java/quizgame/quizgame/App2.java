package quizgame.quizgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import quizgame.quizgame.utils.Utils;

import java.io.IOException;

public class App2 extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/admin/Dash.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 500);
            Image image = new Image(getClass().getResourceAsStream("assets/images/logo.png"));
            stage.getIcons().add(image);
            stage.setTitle("Quiz Game - Admin Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

            Utils utils = new Utils();
            utils.showError("Application Error", "Failed to load the initial scene.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}