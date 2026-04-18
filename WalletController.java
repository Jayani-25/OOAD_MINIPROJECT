public class WalletController {

    public boolean register(String email, String password) {
        User user = UserFactory.createUser(email, password);
        return UserStore.addUser(user);
    }

    public User login(String email, String password) {
        return UserStore.validateUser(email, password);
    }

    public void addMoney(EWallet wallet, float amount) {
        wallet.addMoney(amount);
        TransactionStore.addTransaction(new Transaction("Added", amount));

        User user = SessionManager.getInstance().getCurrentUser();
        UserStore.updateBalance(user.getEmail(), user.getWallet().getBalance());
    }

    // ✅ Strategy + Observer Pattern used here
    public boolean sendMoney(EWallet sender, EWallet receiver, float amount) {

    PaymentStrategy strategy = new WalletPayment();

    boolean success = strategy.pay(sender, receiver, amount);

    if (success) {
        TransactionStore.addTransaction(new Transaction("Sent", amount));

        // ✅ update DB BEFORE return
        User senderUser = SessionManager.getInstance().getCurrentUser();
        UserStore.updateBalance(senderUser.getEmail(), sender.getBalance());

        // ✅ Observer (if present)
        WalletNotifier notifier = new WalletNotifier();
        notifier.addObserver(new UserNotification());
        notifier.notifyObservers("Money received successfully: ₹" + amount);
    }

    return success;   // ✅ must be LAST line
    }

    public float getBalance(EWallet wallet) {
        return wallet.getBalance();
    }
}