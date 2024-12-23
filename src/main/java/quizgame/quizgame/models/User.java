package quizgame.quizgame.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String phone;
    private int isEmailVerified;

    public User(int id, String name, String email, String role, String phone, int isEmailVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role != null ? role : "User";
        this.phone = phone;
        this.isEmailVerified = isEmailVerified;
        this.password = generateDefaultPassword(name, this.role);
    }

    public User(String name, String email, String phone, String password, String role, int isEmailVerified) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role != null ? role : "User";
        this.isEmailVerified = isEmailVerified;
        this.password = password != null ? password : generateDefaultPassword(name, this.role);
    }

    private String generateDefaultPassword(String name, String role) {
        String firstName = name.split(" ")[0];
        return firstName + role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(int isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
}
