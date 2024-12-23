package quizgame.quizgame.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private String content;
    private int quizId;
    private Timestamp createdAt;
    private List<Answer> answers;

    public Question(int id, String content, int quizId, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.quizId = quizId;
        this.createdAt = createdAt;
        this.answers = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
