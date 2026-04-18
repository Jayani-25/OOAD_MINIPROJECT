import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationStore {

    private static final Map<String, List<Notification>> notificationsByUser = new HashMap<>();

    public static void addNotification(String userEmail, String message) {
        if (userEmail == null || userEmail.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            return;
        }

        notificationsByUser
                .computeIfAbsent(userEmail, key -> new ArrayList<>())
                .add(new Notification(message, userEmail));
    }

    public static List<Notification> consumeNotifications(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Notification> messages = notificationsByUser.remove(userEmail);
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }
}
