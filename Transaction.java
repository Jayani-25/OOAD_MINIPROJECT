public class Transaction {

    private String type;
    private float amount;
    private String otherParty;
    private String ownerEmail;

    public Transaction(String type, float amount) {
        this(type, amount, null, null);
    }

    public Transaction(String type, float amount, String otherParty, String ownerEmail) {
        this.type = type;
        this.amount = amount;
        this.otherParty = otherParty;
        this.ownerEmail = ownerEmail;
    }

    @Override
    public String toString() {
        if ("SENT".equalsIgnoreCase(type) && hasOtherParty()) {
            return "Sent to " + otherParty + ": Rs." + amount;
        }

        if ("RECEIVED".equalsIgnoreCase(type) && hasOtherParty()) {
            return "Received from " + otherParty + ": Rs." + amount;
        }

        if ("ADDED".equalsIgnoreCase(type)) {
            return "Added: Rs." + amount;
        }

        return formatType(type) + ": Rs." + amount;
    }

    public String getType() {
        return type;
    }

    public float getAmount() {
        return amount;
    }

    public String getOtherParty() {
        return otherParty;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    private boolean hasOtherParty() {
        return otherParty != null && !otherParty.trim().isEmpty();
    }

    private String formatType(String value) {
        if (value == null || value.isEmpty()) {
            return "Transaction";
        }

        String lower = value.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
