import java.sql.*;
import java.util.ArrayList;

public class TransactionStore {

    public static void addTransaction(Transaction t) {
        try {
            ensureTransactionSchema();

            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO transactions (type, amount, other_party_username, owner_email) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, t.getType());
            ps.setFloat(2, t.getAmount());
            ps.setString(3, t.getOtherParty());
            ps.setString(4, t.getOwnerEmail());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Transaction> getTransactions() {
        return getTransactionsByOwner(null);
    }

    public static ArrayList<Transaction> getTransactionsForUser(String ownerEmail) {
        return getTransactionsByOwner(ownerEmail);
    }

    private static ArrayList<Transaction> getTransactionsByOwner(String ownerEmail) {
        ArrayList<Transaction> list = new ArrayList<>();

        try {
            ensureTransactionSchema();

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps;

            if (ownerEmail == null) {
                ps = conn.prepareStatement("SELECT * FROM transactions ORDER BY id DESC");
            } else {
                ps = conn.prepareStatement("SELECT * FROM transactions WHERE owner_email=? ORDER BY id DESC");
                ps.setString(1, ownerEmail);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getString("type"),
                        rs.getFloat("amount"),
                        rs.getString("other_party_username"),
                        rs.getString("owner_email")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static void ensureTransactionSchema() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection unavailable");
        }

        DatabaseMetaData metaData = conn.getMetaData();
        Statement stmt = conn.createStatement();

        if (!hasColumn(metaData, "transactions", "other_party_username")) {
            stmt.executeUpdate("ALTER TABLE transactions ADD COLUMN other_party_username VARCHAR(100)");
        }

        if (!hasColumn(metaData, "transactions", "owner_email")) {
            stmt.executeUpdate("ALTER TABLE transactions ADD COLUMN owner_email VARCHAR(255)");
        }
    }

    private static boolean hasColumn(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
