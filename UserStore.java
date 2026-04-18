import java.sql.*;
import java.util.ArrayList;

public class UserStore {

    public static boolean addUser(User user) {
        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO users (username, email, password, role, balance, isBlocked) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            String username = normalizeUsername(user.getUsername(), user.getEmail());

            ps.setString(1, username);
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setFloat(5, user.getWallet().getBalance());
            ps.setBoolean(6, user.isBlocked());

            ps.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User validateUser(String email, String password) {
        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User findUserByEmail(String email) {
        return findUserByIdentifier(email);
    }

    public static User findUserByIdentifier(String identifier) {
        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE email=? OR username=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void blockUser(String email) {
        updateBlockedStatus(email, true);
    }

    public static void unblockUser(String email) {
        updateBlockedStatus(email, false);
    }

    public static boolean isBlocked(String email) {
        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "SELECT isBlocked FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isBlocked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<User> getUsersByRole(String role) {
        ArrayList<User> users = new ArrayList<>();

        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE role = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, role);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void seedDefaultUsers() {
        addUser(UserFactory.createAdmin("admin", "admin@wallet.com", "admin123"));
        addUser(UserFactory.createMerchant("amazonpay", "amazon@merchant.com", "merchant123"));
        addUser(UserFactory.createMerchant("flipkart", "flipkart@merchant.com", "merchant123"));
        addUser(UserFactory.createMerchant("myntra", "myntra@merchant.com", "merchant123"));
    }

    public static void updateBalance(String email, float balance) {
        try {
            ensureUserSchema();

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

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();

        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static void updateBlockedStatus(String email, boolean blocked) {
        try {
            ensureUserSchema();

            Connection conn = DBConnection.getConnection();
            String query = "UPDATE users SET isBlocked=? WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1, blocked);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String username = normalizeUsername(rs.getString("username"), email);
        String role = rs.getString("role");
        User user;

        if ("ADMIN".equalsIgnoreCase(role)) {
            user = new Admin(username, email, rs.getString("password"));
        } else {
            user = new User(username, email, rs.getString("password"), role);
        }

        user.setBlocked(rs.getBoolean("isBlocked"));
        user.getWallet().setBalance(rs.getFloat("balance"));
        return user;
    }

    private static void ensureUserSchema() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection unavailable");
        }

        DatabaseMetaData metaData = conn.getMetaData();
        Statement stmt = conn.createStatement();
        if (!hasColumn(metaData, "users", "username")) {
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN username VARCHAR(100)");
        }
        if (!hasColumn(metaData, "users", "isBlocked")) {
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN isBlocked BOOLEAN DEFAULT FALSE");
        }

        stmt.executeUpdate("UPDATE users SET username = SUBSTRING_INDEX(email, '@', 1) WHERE username IS NULL OR TRIM(username) = ''");
        stmt.executeUpdate("UPDATE users SET isBlocked = FALSE WHERE isBlocked IS NULL");
    }

    private static boolean hasColumn(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    private static String normalizeUsername(String username, String email) {
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }

        if (email == null || email.trim().isEmpty()) {
            return "";
        }

        String trimmedEmail = email.trim();
        int atIndex = trimmedEmail.indexOf('@');
        if (atIndex > 0) {
            return trimmedEmail.substring(0, atIndex);
        }

        return trimmedEmail;
    }
}
