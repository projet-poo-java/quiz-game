
# Quiz Game 

<img width="300" src="screenshots/logo.png" />

*logo*

## Description
Un jeu de quiz interactif qui permet aux utilisateurs de tester leurs connaissances sur différents sujets. Les utilisateurs peuvent s'inscrire, se connecter et participer à des quiz. L'application offre une interface conviviale et garantit une authentification sécurisée.

## User Interface

### Home Screen
![Home Screen](screenshots/home.png)
*Home screen with navigation options and welcome message*

### Authentication
![Login Screen](screenshots/login.png)
*User login interface*

![Register Screen](screenshots/register.png)
*New user registration form*

### User Dashboard
![User Dashboard](screenshots/user-dash.png)
*Category and difficulty selection screen*

### Quiz Interface
![Quiz Selection](screenshots/quiz-select.png)
*Category and difficulty selection screen*

![Results Screen](screenshots/results.png)
*Quiz completion and score display*

### Admin Dashboard
![admin Dashboard statistics](screenshots/admin-statistics.png)
*admin dashboard statistics*

![admin Dashboard Users](screenshots/admin-users-list.png)
*admin dashboard users list*

![admin Dashboard Add User](screenshots/admin-add-user.png)
*admin dashboard add user*

![admin Dashboard Subjects](screenshots/admin-subjects-list.png)
*admin dashboard subjects list*

![admin Dashboard Add Subject](screenshots/admin-add-subject.png)
*admin dashboard Add Subject*

![admin Dashboard Quizzes](screenshots/admin-quizzes.png)
*admin dashboard quizzes list*
![admin Dashboard Add Quiz](screenshots/admin-add-quiz.png)
*admin dashboard Add Quiz*
![admin Dashboard Add Question](screenshots/admin-add-qst.png)
*admin dashboard Add question*
## Fonctionnalités
- Inscription et connexion des utilisateurs
- Contrôle d'accès basé sur les rôles
- Quiz interactifs
- Gestion du profil utilisateur
- Intégration avec l'API Open Trivia Database (OpenTDB)
- Plusieurs catégories et niveaux de difficulté

## Intégration API
Cette application utilise l'[API Open Trivia Database (OpenTDB)](https://opentdb.com/) pour récupérer les questions :
- Plus de 4 000 questions vérifiées
- 24 catégories différentes
- Plusieurs niveaux de difficulté
- Utilisation gratuite et open-source

## Prérequis
- Java JDK 17 ou supérieur
- Maven 3.6 ou supérieur
- MySQL 8.0 ou supérieur

## Installation et Configuration

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/projet-poo-java/quiz-game
   ```

2. **Créer une base de données MySQL :**
   - Créez une base de données nommée `quizgame`.
   - Importez et exécutez le fichier `quizgame.sql` dans MySQL Workbench.

3. **Configurer les informations de connexion :**
   - Modifiez votre nom d'utilisateur et mot de passe MySQL dans le fichier `DatabaseConnection.java`.

4. **Exécuter l'application :**
   - Lancez `App.java`.

## Licence

Ce projet est sous licence [MIT](https://opensource.org/licenses/MIT).


