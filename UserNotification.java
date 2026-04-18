import javax.swing.*;

public class UserNotification implements NotificationObserver {

    @Override
    public void update(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}