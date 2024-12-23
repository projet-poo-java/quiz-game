CREATE DATABASE IF NOT EXISTS quizgame;

USE quizgame;

DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS scores;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS users;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(100) DEFAULT 'NAN',
    password VARCHAR(255) NOT NULL,
    isEmailVerified TINYINT DEFAULT 0,
    role ENUM('admin', 'teacher', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: subjects (categories)
CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: quizzes
CREATE TABLE IF NOT EXISTS quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    level ENUM('easy', 'medium', 'hard', 'advanced') DEFAULT 'easy',
    subject_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

-- Table: scores
CREATE TABLE IF NOT EXISTS scores (
    id INT PRIMARY KEY AUTO_INCREMENT,  
    user_id INT,
    quiz_id INT,
    score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) 
);

-- Table: questions
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    quiz_id INT NOT NULL,    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Table: answers
CREATE TABLE IF NOT EXISTS answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    is_correct TINYINT DEFAULT 0,
    question_id INT NOT NULL,    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Insert real users
INSERT INTO users (name, email, password, role) VALUES
('Alice Johnson', 'abdo@abdo.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'admin'),
('Bob Smith', 'bob@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'teacher'),
('Charlie Davis', 'charlie@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user'),
('Diana Prince', 'diana@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user'),
('Ethan Hunt', 'ethan@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'teacher');

-- Insert categories (subjects)
INSERT INTO subjects (name, description) VALUES
('Mathematics', 'Quizzes about algebra, geometry, and calculus'),
('Science', 'Physics, Chemistry, and Biology'),
('History', 'World history and events'),
('Geography', 'Earth and its features'),
('Literature', 'Books and authors'),
('Music', 'Genres, instruments, and artists'),
('Sports', 'Athletics, games, and tournaments'),
('Technology', 'IT, programming, and gadgets'),
('Movies', 'Cinema, actors, and directors'),
('General Knowledge', 'Miscellaneous topics');

-- Insert quizzes for each category
INSERT INTO quizzes (title, duration, subject_id) VALUES
('Basic Algebra', 30, 1),
('Geometry Basics', 30, 1),
('Introduction to Calculus', 45, 1),
('Advanced Algebra', 60, 1),
('Math Puzzles', 30, 1),

('Physics Fundamentals', 30, 2),
('Chemistry Basics', 30, 2),
('Biology Essentials', 45, 2),
('Advanced Physics', 60, 2),
('Science Trivia', 30, 2),

('Ancient History', 30, 3),
('World Wars', 30, 3),
('Medieval History', 45, 3),
('Modern History', 60, 3),
('History Facts', 30, 3),

('World Capitals', 30, 4),
('Landmarks', 30, 4),
('Continents and Oceans', 45, 4),
('Countries and Flags', 60, 4),
('Geography Trivia', 30, 4),

('Classic Novels', 30, 5),
('Shakespeare Plays', 30, 5),
('Poetry Basics', 45, 5),
('Modern Literature', 60, 5),
('Literature Quiz', 30, 5);

-- Insert questions and answers
-- Example for one quiz (Basic Algebra)
INSERT INTO questions (content, quiz_id) VALUES
('What is the value of x in the equation 2x + 3 = 7?', 1),
('Simplify: 5x - 3x + 2', 1),
('What is the square root of 16?', 1),
('Solve: 3x = 9', 1),
('Which of these is a prime number?', 1);

INSERT INTO answers (content, is_correct, question_id) VALUES
('x = 2', 1, 1), ('x = 3', 0, 1), ('x = 4', 0, 1), ('x = 5', 0, 1),
('2x + 2', 0, 2), ('2x + 4', 0, 2), ('2x', 1, 2), ('x - 2', 0, 2),
('4', 1, 3), ('8', 0, 3), ('2', 0, 3), ('16', 0, 3),
('x = 3', 1, 4), ('x = 2', 0, 4), ('x = 1', 0, 4), ('x = 4', 0, 4),
('2', 0, 5), ('3', 0, 5), ('5', 1, 5), ('6', 0, 5);

-- End of SQL dump.