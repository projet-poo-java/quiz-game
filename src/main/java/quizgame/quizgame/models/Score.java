package quizgame.quizgame.models;

import java.sql.Timestamp;

public class Score {
    private int id;
    private int userId;
    private int quizId;
    private int score;
    private Timestamp createdAt;

    public Score(int id, int userId, int quizId, int score, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
