package quizgame.quizgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Load the FXML file into a root layout
        VBox root = fxmlLoader.load();

        // Load the image
        Image image = new Image(getClass().getResource("/images/img.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(46);
        imageView.setFitWidth(45);
        imageView.setPreserveRatio(true);

        // Add the ImageView to the root layout
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Quiz Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
