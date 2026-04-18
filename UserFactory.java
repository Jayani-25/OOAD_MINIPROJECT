public class UserFactory {

    public static User createUser(String username, String email, String password) {
        return new User(username, email, password, "USER");
    }

    public static User createAdmin(String username, String email, String password) {
        return new Admin(username, email, password);
    }

    public static User createMerchant(String username, String email, String password) {
        return new User(username, email, password, "MERCHANT");
    }
}
