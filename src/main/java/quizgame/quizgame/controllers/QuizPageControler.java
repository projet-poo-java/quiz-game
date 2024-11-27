package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class QuizPageControler {

    @FXML
    private StackPane root;

    @FXML
    private Label questionNumberLabel;

    @FXML
    private Label questionLabel;

    @FXML
    private RadioButton choice1RadioButton;

    @FXML
    private RadioButton choice2RadioButton;

    @FXML
    private RadioButton choice3RadioButton;

    @FXML
    private RadioButton choice4RadioButton;

    private ToggleGroup choiceGroup;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;

    @FXML
    public void initialize() {
        // Initialisation du groupe de boutons radio
        choiceGroup = new ToggleGroup();
        choice1RadioButton.setToggleGroup(choiceGroup);
        choice2RadioButton.setToggleGroup(choiceGroup);
        choice3RadioButton.setToggleGroup(choiceGroup);
        choice4RadioButton.setToggleGroup(choiceGroup);

        // Appliquer le style du fond d'écran
        if (root != null) {
            root.setStyle("-fx-background-image: url('/images/backfroundPurple.jpeg');" +
                    "-fx-background-size: cover;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;");
        }

        // Charger la première question
        loadQuestion();
    }

    private void loadQuestion() {
        // Mettre à jour le numéro de la question
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + "/" + totalQuestions);

        // Exemple de question statique
        questionLabel.setText("What is 2 + 2?");

        // Initialiser les choix
        choice1RadioButton.setText("2");
        choice2RadioButton.setText("4"); // Bonne réponse
        choice3RadioButton.setText("6");
        choice4RadioButton.setText("8");

        // Désélectionner tous les choix au début
        choiceGroup.selectToggle(null);
    }

    @FXML
    private void onChoiceSelected() {
        // Vérifier le choix sélectionné
        RadioButton selectedChoice = (RadioButton) choiceGroup.getSelectedToggle();
        if (selectedChoice != null) {
            // Exemple d'action : afficher le texte du choix sélectionné
            questionLabel.setText("You selected: " + selectedChoice.getText());
        }
    }

    @FXML
    private void onNextButtonClick() {
        currentQuestionIndex++;
        if (currentQuestionIndex < totalQuestions) {
            loadQuestion();
        } else {
            questionLabel.setText("Quiz completed!");
        }
    }

    @FXML
    private void onQuitButtonClick() {
        System.exit(0); // Quitter l'application
    }
}
