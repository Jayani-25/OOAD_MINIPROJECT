import java.sql.*;
import java.util.ArrayList;

public class TransactionStore {

    public static void addTransaction(Transaction t) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO transactions (type, amount) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, t.getType());
            ps.setFloat(2, t.getAmount());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions");

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getString("type"),
                        rs.getFloat("amount")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}