public interface PaymentStrategy {
    boolean pay(EWallet sender, EWallet receiver, float amount);
}