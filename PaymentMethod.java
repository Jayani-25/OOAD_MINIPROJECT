public class PaymentMethod {

    private String methodId;
    private String type;
    private String details;

    public PaymentMethod(String type, String details) {
        this.methodId = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.details = details;
    }

    public boolean verifyMethod() {
        return type != null && !type.isEmpty();
    }

    public String getMethodId() {
        return methodId;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }
}
