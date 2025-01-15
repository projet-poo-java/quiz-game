package quizgame.quizgame.models;

import java.sql.Timestamp;

import quizgame.quizgame.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Subject {
    private int id;
    private String name;
    private String description;
    private Timestamp createdAt;
    private boolean isPrivate;
    private String invitationCode;
    private int createdBy;

    public Subject(int id, String name, String description, Timestamp createdAt, boolean isPrivate, String invitationCode, int createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.isPrivate = isPrivate;
        this.invitationCode = invitationCode;
        this.createdBy = createdBy;
    }

    public Subject(int id, String name, String description, Timestamp createdAt) {
        this(id, name, description, createdAt, false, null, 0);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    
    public String getInvitationCode() { return invitationCode; }
    public void setInvitationCode(String invitationCode) { this.invitationCode = invitationCode; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() {
        return name;
    }

    public static int getSubjectIdByName(String name) {
        String query = "SELECT id FROM subjects WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static ObservableList<Subject> getAllSubjects() {
        ObservableList<Subject> subjects = FXCollections.observableArrayList();
        String query = "SELECT * FROM subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at"),
                    rs.getBoolean("is_private"),
                    rs.getString("invitation_code"),
                    rs.getInt("created_by")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
}
