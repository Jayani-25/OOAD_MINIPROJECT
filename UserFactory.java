public class UserFactory {

    // create normal user
    public static User createUser(String email, String password) {
        return new User(email, password, "USER");
    }

    // create admin user
    public static User createAdmin(String email, String password) {
        return new User(email, password, "ADMIN");
    }
}