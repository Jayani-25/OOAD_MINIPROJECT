public class WalletPayment implements PaymentStrategy {

    @Override
    public boolean pay(EWallet sender, EWallet receiver, float amount) {

        if (amount <= 0) {
            return false;
        }

        if (amount > PaymentRules.getMaxLimit()) {
            return false;
        }

        if (sender.getBalance() >= amount) {
            sender.sendMoney(amount);
            receiver.addMoney(amount);
            return true;
        }

        return false;
    }
}
