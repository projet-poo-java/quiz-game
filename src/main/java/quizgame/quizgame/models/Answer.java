package quizgame.quizgame.models;

import java.sql.Timestamp;

public class Answer {
    private int id;
    private String content;
    private boolean isCorrect;
    private int questionId;
    private Timestamp createdAt;

    public Answer(int id, String content, boolean isCorrect, int questionId, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.isCorrect = isCorrect;
        this.questionId = questionId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
