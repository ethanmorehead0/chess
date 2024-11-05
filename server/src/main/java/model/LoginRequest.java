package model;

public class LoginRequest {
    String username;
    String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
