package model;

public class User extends Person {
    private String password;
    protected int userId;
    public String email;

    public User(int userId, String fullName, String email, String password) {
        super(fullName);
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return "********"; // Masks the password
    }
}