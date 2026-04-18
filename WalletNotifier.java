import java.util.ArrayList;

public class WalletNotifier {

    private ArrayList<NotificationObserver> observers = new ArrayList<>();

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }
}