module quizgame.quizgame {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires java.net.http;
    requires com.google.gson;

    opens quizgame.quizgame to javafx.fxml, com.google.gson;
    opens quizgame.quizgame.controllers to javafx.fxml, com.google.gson;
    exports quizgame.quizgame.controllers to javafx.fxml;
    exports quizgame.quizgame;
}