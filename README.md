# How to Run This Project Locally

To run the QuizApp project locally, follow these steps:

1. **Clone the repository:**

    ```sh
    git clone https://github.com/projet-poo-java/quiz-game.git
    cd quizApp
    ```

    Add the following configuration to ``launch.json`` in `.vscode` folder the application:

    ```json
    {
        "version": "0.2.0",
        "configurations": [
            {
                "vmArgs": "--module-path \"<javaFx lib folder link like this C:/javafx-21.0.5/lib>\" --add-modules javafx.controls,javafx.fxml",
                "type": "java",
                "name": "Launch Current File",
                "request": "launch",
                "mainClass": "${file}"
            },
            {
                "vmArgs": "--module-path \"<javaFx lib folder link like this C:/javafx-21.0.5/lib>\" --add-modules javafx.controls,javafx.fxml",
                "type": "java",
                "name": "Launch App",
                "request": "launch",
                "mainClass": "App"
            }
        ]
    }
    ```
    

2. **Open the project in your preferred IDE.**



3. **Add JavaFX to your project:**
    - Download JavaFX from [Gluon's website](https://gluonhq.com/products/javafx/).
    - Add the JavaFX library to your project's referenced libraries.

4. **Run the application from your IDE:**
    - Configure your IDE to use the JavaFX runtime.
    - Run the main class of the application.


You should now be able to use the QuizApp locally on your machine.
