module quizgame.quizgame {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens quizgame.quizgame to javafx.fxml;
    exports quizgame.quizgame;
    exports quizgame.quizgame.controllers to javafx.fxml;
    opens quizgame.quizgame.controllers to javafx.fxml;
}