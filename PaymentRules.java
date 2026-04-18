public class PaymentRules {

    private static float maxTransactionLimit = 10000.0f;

    public static float getMaxLimit() {
        return maxTransactionLimit;
    }

    public static void setMaxLimit(float limit) {
        maxTransactionLimit = limit;
    }
}
