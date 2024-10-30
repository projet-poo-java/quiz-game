module quizgame.quizgame {
    requires transitive javafx.controls;
    requires javafx.fxml;


    opens quizgame.quizgame to javafx.fxml;
    exports quizgame.quizgame;
}