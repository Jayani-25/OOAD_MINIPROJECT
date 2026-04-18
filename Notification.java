public class Notification {

    private int notificationId;
    private String message;
    private java.util.Date date;
    private String ownerEmail;

    public Notification(String message, String ownerEmail) {
        this.message = message;
        this.ownerEmail = ownerEmail;
        this.date = new java.util.Date();
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getMessage() {
        return message;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public java.util.Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "[" + date + "] " + message;
    }
}
