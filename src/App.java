import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.WindowStyle;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/assets/css/style.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setScene(scene);

        WindowStyle.applyWindowStyle(primaryStage, root, scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}