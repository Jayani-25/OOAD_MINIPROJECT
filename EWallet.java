public class EWallet {

    private float balance;

    public EWallet() {
        this.balance = 0;
    }

    public void addMoney(float amount) {
        balance += amount;
    }

    public void sendMoney(float amount) {
        balance -= amount;
    }

    public float getBalance() {
        return balance;
    }

    // Optional (for your existing UI compatibility)
    public float checkBalance() {
        return balance;
    }
    public void setBalance(float balance) {
    this.balance = balance;
    }
}