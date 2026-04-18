import java.util.ArrayList;

public class AdminController {

    public ArrayList<User> getAllUsers() {
        return UserStore.getAllUsers();
    }

    public ArrayList<Transaction> getAllTransactions() {
        return TransactionStore.getTransactions();
    }

    public float getTotalSystemBalance() {
        float total = 0;

        for (User user : UserStore.getAllUsers()) {
            total += user.getWallet().getBalance();
        }

        return total;
    }

    public int getTotalUserCount() {
        return UserStore.getAllUsers().size();
    }

    public int getTotalTransactionCount() {
        return TransactionStore.getTransactions().size();
    }

    public float getTotalMoneyAdded() {
        float total = 0;

        for (Transaction transaction : TransactionStore.getTransactions()) {
            if ("ADDED".equalsIgnoreCase(transaction.getType())) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    public float getTotalMoneySent() {
        float total = 0;

        for (Transaction transaction : TransactionStore.getTransactions()) {
            if ("SENT".equalsIgnoreCase(transaction.getType())) {
                total += transaction.getAmount();
            }
        }

        return total;
    }
}
