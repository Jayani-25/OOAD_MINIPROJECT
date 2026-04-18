public class User {

    private String username;
    private String email;
    private String password;
    private String role;
    private boolean isBlocked;
    private EWallet wallet;

    public User(String username, String email, String password) {
        this(username, email, password, "USER");
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBlocked = false;
        this.wallet = new EWallet();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public EWallet getWallet() {
        return wallet;
    }
}
