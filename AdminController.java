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
}