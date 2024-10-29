module quizgame {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens quizgame to javafx.fxml;
    exports quizgame;
}
