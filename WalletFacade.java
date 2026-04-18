import java.util.ArrayList;
import java.util.List;

public class WalletFacade {

    private WalletController walletController = new WalletController();
    private AdminController adminController = new AdminController();
    private SessionManager session = SessionManager.getInstance();

    public User login(String email, String password) {
        return UserStore.validateUser(email, password);
    }

    public boolean register(String username, String email, String password) {
        return walletController.register(username, email, password);
    }

    public boolean addMoney(float amount) {
        User user = session.getCurrentUser();
        if (user == null || amount <= 0) {
            return false;
        }

        walletController.addMoney(user.getWallet(), amount);
        return true;
    }

    public boolean sendMoney(String receiverIdentifier, float amount) {
        User sender = session.getCurrentUser();
        if (sender == null || amount <= 0) {
            return false;
        }

        User receiver = UserStore.findUserByIdentifier(receiverIdentifier);
        if (receiver == null || receiver.isBlocked()) {
            return false;
        }

        if (receiverIdentifier.equalsIgnoreCase(sender.getEmail())
                || receiverIdentifier.equalsIgnoreCase(getDisplayName(sender))) {
            return false;
        }

        return walletController.sendMoney(sender, receiver, amount);
    }

    public boolean payMerchant(String merchantUsername, float amount) {
        User sender = session.getCurrentUser();
        if (sender == null || amount <= 0) {
            return false;
        }

        User merchant = UserStore.findUserByIdentifier(merchantUsername);
        if (merchant == null || merchant.isBlocked() || !"MERCHANT".equalsIgnoreCase(merchant.getRole())) {
            return false;
        }

        return walletController.sendMoney(sender, merchant, amount);
    }

    public float getBalance() {
        User user = session.getCurrentUser();
        if (user == null) {
            return 0;
        }

        return user.getWallet().getBalance();
    }

    public ArrayList<Transaction> getTransactionHistory() {
        User user = session.getCurrentUser();
        if (user == null) {
            return new ArrayList<>();
        }

        return TransactionStore.getTransactionsForUser(user.getEmail());
    }

    public List<Notification> getPendingNotifications() {
        User user = session.getCurrentUser();
        if (user == null) {
            return new ArrayList<>();
        }

        return NotificationStore.consumeNotifications(user.getEmail());
    }

    public ArrayList<User> getAllUsers() {
        return adminController.getAllUsers();
    }

    public ArrayList<User> getUsersByRole(String role) {
        return UserStore.getUsersByRole(role);
    }

    public float getTotalSystemBalance() {
        return adminController.getTotalSystemBalance();
    }

    public ArrayList<Transaction> getAllTransactions() {
        return adminController.getAllTransactions();
    }

    public int getTotalUserCount() {
        return adminController.getTotalUserCount();
    }

    public int getTotalTransactionCount() {
        return adminController.getTotalTransactionCount();
    }

    public float getTotalMoneyAdded() {
        return adminController.getTotalMoneyAdded();
    }

    public float getTotalMoneySent() {
        return adminController.getTotalMoneySent();
    }

    public void blockUser(String email) {
        UserStore.blockUser(email);
    }

    public void unblockUser(String email) {
        UserStore.unblockUser(email);
    }

    public boolean isBlocked(String email) {
        return UserStore.isBlocked(email);
    }

    public float getMaxTransactionLimit() {
        return PaymentRules.getMaxLimit();
    }

    public void setMaxTransactionLimit(float limit) {
        PaymentRules.setMaxLimit(limit);
    }

    public User getCurrentUser() {
        return session.getCurrentUser();
    }

    public User findUserByIdentifier(String identifier) {
        return UserStore.findUserByIdentifier(identifier);
    }

    public String getDisplayName(User user) {
        if (user == null) {
            return "";
        }

        String username = user.getUsername() != null ? user.getUsername().trim() : "";
        return !username.isEmpty() ? username : user.getEmail();
    }

    public void logout() {
        session.logout();
    }
}
