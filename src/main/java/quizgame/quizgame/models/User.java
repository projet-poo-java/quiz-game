package quizgame.quizgame.models;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private int isEmailVerified;
    private String role;
    private Timestamp createdAt;

    // Constructor for loading users from database
    public User(int id, String name, String email, String role, String phone, int isEmailVerified) {
        this(id, name, email, phone, null, isEmailVerified, role, null);
    }

    // Default constructor with all fields
    public User(int id, String name, String email, String phone, String password, int isEmailVerified, String role, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isEmailVerified = isEmailVerified;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Constructor for registration
    public User(String name, String email, String phone, String password, String role) {
        this(0, name, email, phone, password, 0, role, null);
    }

    // Registration constructor
    public User(String name, String email, String phone, String password, String role, int isEmailVerified) {
        this(0, name, email, phone, password, isEmailVerified, role, null);
    }

    // Login constructor
    public User(String email, String password) {
        this(0, null, email, null, password, 0, null, null);
    }

    // Simple constructor for display
    public User(int id, String name, String email, String role) {
        this(id, name, email, null, null, 0, role, null);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getIsEmailVerified() { return isEmailVerified; }
    public void setIsEmailVerified(int isEmailVerified) { this.isEmailVerified = isEmailVerified; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
