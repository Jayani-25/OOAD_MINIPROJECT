import java.sql.*;

public class UserStore {

    public static boolean addUser(User user) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO users (email, password, role, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setFloat(4, user.getWallet().getBalance());

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User validateUser(String email, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );

                user.getWallet().setBalance(rs.getFloat("balance"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User findUserByEmail(String email) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );

                user.getWallet().setBalance(rs.getFloat("balance"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateBalance(String email, float balance) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "UPDATE users SET balance=? WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setFloat(1, balance);
            ps.setString(2, email);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.ArrayList<User> getAllUsers() {
    java.util.ArrayList<User> list = new java.util.ArrayList<>();

    try {
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM users");

        while (rs.next()) {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
            );

            user.getWallet().setBalance(rs.getFloat("balance"));
            list.add(user);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
    }
}

