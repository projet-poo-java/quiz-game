package quizgame.quizgame.middlewares;

import quizgame.quizgame.controllers.admin.DashController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quizgame.quizgame.App;

import java.io.IOException;

public class RoleMiddleWare {

    public static void redirectBasedOnRole(ActionEvent event, String role, String email, String name) {
        String fxmlFile = role.equals("admin") ? "admin/Dash.fxml" : "userdashboard.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/" + fxmlFile));
            Parent root = loader.load();
            
            if (role.equals("admin")) {
                DashController dashController = loader.getController();
                dashController.setUserInfo(email, name);
            }

            Scene scene = new Scene(root, 900, 500);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not load the requested page.");
        }
    }

    private static void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
