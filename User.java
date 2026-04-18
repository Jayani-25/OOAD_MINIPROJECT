public class User {

    private String email;
    private String password;
    private String role;
    private EWallet wallet;

    public User(String email, String password) {
        this(email, password, "USER");
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.wallet = new EWallet();
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

    public EWallet getWallet() {
        return wallet;
    }
}