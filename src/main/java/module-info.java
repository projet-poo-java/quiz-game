module quizgame.quizgame {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires jdk.crypto.ec;

    opens quizgame.quizgame to javafx.fxml, com.google.gson;
    opens quizgame.quizgame.controllers to javafx.fxml, com.google.gson;
    opens quizgame.quizgame.controllers.admin to javafx.fxml;
    opens quizgame.quizgame.models to javafx.base;
    opens quizgame.quizgame.controllers.auth to javafx.fxml;

    exports quizgame.quizgame.controllers to javafx.fxml;
    exports quizgame.quizgame;
    exports quizgame.quizgame.controllers.auth;
}