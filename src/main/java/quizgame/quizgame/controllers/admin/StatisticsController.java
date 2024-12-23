package quizgame.quizgame.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import quizgame.quizgame.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatisticsController {
    @FXML
    private Label participantsLabel;
    @FXML
    private Label teachersLabel;
    @FXML
    private Label categoriesLabel;
    @FXML
    private Label quizesLabel;
    @FXML
    private PieChart usersPieChart;

    @FXML
    public void initialize() {
        loadStatistics();
        loadUserDistribution();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Count participants (users)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE role = 'user'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    participantsLabel.setText(String.valueOf(rs.getInt(1)) + " Participants");
                }
            }

            // Count teachers
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE role = 'teacher'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    teachersLabel.setText(String.valueOf(rs.getInt(1)) + " Teachers");
                }
            }

            // Count categories (subjects)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM subjects")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    categoriesLabel.setText(String.valueOf(rs.getInt(1)) + " Subjects");
                }
            }

            // Count quizzes
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM quizzes")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    quizesLabel.setText(String.valueOf(rs.getInt(1)) + " Quizzes");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserDistribution() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            // Get users count by role
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT role, COUNT(*) as count FROM users GROUP BY role")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String role = rs.getString("role");
                    int count = rs.getInt("count");
                    pieChartData.add(new PieChart.Data(role, count));
                }
            }
            
            usersPieChart.setData(pieChartData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
