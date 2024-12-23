package quizgame.quizgame.models;

import java.sql.Timestamp;

import quizgame.quizgame.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Quiz {
    private int id;
    private String title;
    private String duration;
    private String level;
    private int subjectId;
    private Timestamp createdAt;

    public Quiz(int id, String title, String duration, String level, int subjectId, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.level = level;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Add this getter
    public String getSubject() {
        // Get subject name from database using subjectId
        String query = "SELECT name FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.subjectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public ObservableList<Question> getQuestions() {
        ObservableList<Question> questions = FXCollections.observableArrayList();
        String query = "SELECT * FROM questions WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("content"),
                        this.id,
                        rs.getTimestamp("created_at"));
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
