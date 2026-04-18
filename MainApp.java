import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String DASHBOARD = "DASHBOARD";
    public static final String ADMIN = "ADMIN";

    private final JFrame frame;
    private final JPanel rootPanel;
    private final CardLayout rootLayout;

    private final LoginUI loginUI;
    private final RegisterUI registerUI;
    private final WalletUI walletUI;
    private final AdminUI adminUI;

    public MainApp() {
        UserStore.seedDefaultUsers();

        frame = new JFrame("Digital Wallet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        loginUI = new LoginUI(this);
        registerUI = new RegisterUI(this);
        walletUI = new WalletUI(this);
        adminUI = new AdminUI(this);

        rootPanel.add(loginUI, LOGIN);
        rootPanel.add(registerUI, REGISTER);
        rootPanel.add(walletUI, DASHBOARD);
        rootPanel.add(adminUI, ADMIN);

        frame.setContentPane(rootPanel);
        showLogin(false);
        frame.setVisible(true);
    }

    public void showLogin(boolean registrationSuccess) {
        SessionManager.getInstance().logout();
        frame.setSize(480, 520);
        frame.setLocationRelativeTo(null);
        loginUI.resetForm();
        if (registrationSuccess) {
            loginUI.showRegistrationSuccess();
        }
        rootLayout.show(rootPanel, LOGIN);
    }

    public void showRegister() {
        frame.setSize(480, 520);
        frame.setLocationRelativeTo(null);
        registerUI.resetForm();
        rootLayout.show(rootPanel, REGISTER);
    }

    public void showDashboard() {
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        walletUI.onShow();
        rootLayout.show(rootPanel, DASHBOARD);
    }

    public void showAdmin() {
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        adminUI.onShow();
        rootLayout.show(rootPanel, ADMIN);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
