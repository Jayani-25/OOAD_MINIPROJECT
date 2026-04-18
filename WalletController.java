public class WalletController {

    public boolean register(String username, String email, String password) {
        User user = UserFactory.createUser(username, email, password);
        return UserStore.addUser(user);
    }

    public User login(String email, String password) {
        return UserStore.validateUser(email, password);
    }

    public void addMoney(EWallet wallet, float amount) {
        wallet.addMoney(amount);

        User user = SessionManager.getInstance().getCurrentUser();
        TransactionStore.addTransaction(new Transaction("ADDED", amount, null, user.getEmail()));
        UserStore.updateBalance(user.getEmail(), user.getWallet().getBalance());
    }

    public boolean sendMoney(User senderUser, User receiverUser, float amount) {
        PaymentStrategy strategy = new WalletPayment();
        boolean success = strategy.pay(senderUser.getWallet(), receiverUser.getWallet(), amount);

        if (!success) {
            return false;
        }

        String receiverName = getDisplayName(receiverUser);
        String senderName = getDisplayName(senderUser);

        TransactionStore.addTransaction(
                new Transaction("SENT", amount, receiverName, senderUser.getEmail())
        );
        TransactionStore.addTransaction(
                new Transaction("RECEIVED", amount, senderName, receiverUser.getEmail())
        );

        UserStore.updateBalance(senderUser.getEmail(), senderUser.getWallet().getBalance());
        UserStore.updateBalance(receiverUser.getEmail(), receiverUser.getWallet().getBalance());
        queueRecipientNotification(senderUser, receiverUser, amount);
        return true;
    }

    public void queueRecipientNotification(User senderUser, User receiverUser, float amount) {
        String senderName = getDisplayName(senderUser);
        NotificationStore.addNotification(
                receiverUser.getEmail(),
                "You received Rs." + amount + " from " + senderName + " successfully."
        );
    }

    public float getBalance(EWallet wallet) {
        return wallet.getBalance();
    }

    public String getDisplayName(User user) {
        String username = user.getUsername() != null ? user.getUsername().trim() : "";
        return !username.isEmpty() ? username : user.getEmail();
    }
}
