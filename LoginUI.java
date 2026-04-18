import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JPanel {

    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color SUCCESS = new Color(76, 175, 80);
    private static final Color DANGER = new Color(244, 67, 54);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color CARD_BG = new Color(245, 245, 245);

    private final MainApp app;
    private final WalletFacade facade = new WalletFacade();

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel feedbackLabel = new JLabel(" ", SwingConstants.CENTER);

    public LoginUI(MainApp app) {
        this.app = app;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(45, 50, 45, 50));
        card.setPreferredSize(new Dimension(400, 420));
        card.setMaximumSize(new Dimension(400, 420));

        JLabel title = new JLabel("Digital Wallet", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(400, 50));

        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setMaximumSize(new Dimension(400, 25));

        JLabel emailLabel = createFieldLabel("Email");
        JLabel passwordLabel = createFieldLabel("Password");

        styleInput(emailField);
        styleInput(passwordField);

        JButton loginButton = createFilledButton("LOGIN", PRIMARY);
        JButton registerButton = createOutlinedButton("CREATE ACCOUNT", SUCCESS);

        JLabel forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.CENTER);
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordLabel.setForeground(TEXT_SECONDARY);
        forgotPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLabel.setMaximumSize(new Dimension(340, 25));

        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackLabel.setForeground(DANGER);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setMaximumSize(new Dimension(340, 35));

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> app.showRegister());

        card.add(title);
        card.add(Box.createVerticalStrut(15));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(15));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(emailField);
        card.add(Box.createVerticalStrut(15));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(15));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(15));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(15));
        card.add(feedbackLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(forgotPasswordLabel);

        add(card);
    }

    public void resetForm() {
        emailField.setText("");
        passwordField.setText("");
        showMessage(" ", DANGER);
    }

    public void showRegistrationSuccess() {
        showMessage("Account created successfully. Please login.", SUCCESS);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter email and password.", DANGER);
            return;
        }

        User user = facade.login(email, password);
        if (user == null) {
            showMessage("Invalid email or password. Please try again.", DANGER);
            return;
        }

        if (UserStore.isBlocked(email)) {
            showMessage("Your account has been blocked!", DANGER);
            return;
        }

        SessionManager.getInstance().setCurrentUser(user);
        showMessage(" ", DANGER);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            app.showAdmin();
        } else {
            app.showDashboard();
        }
    }

    private void showMessage(String message, Color color) {
        feedbackLabel.setForeground(color);
        feedbackLabel.setText(message);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(340, 22));
        return label;
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(340, 42));
        field.setPreferredSize(new Dimension(340, 42));
        field.setMinimumSize(new Dimension(340, 42));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton createFilledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(340, 46));
        button.setPreferredSize(new Dimension(340, 46));
        button.setMinimumSize(new Dimension(340, 46));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JButton createOutlinedButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(340, 46));
        button.setPreferredSize(new Dimension(340, 46));
        button.setMinimumSize(new Dimension(340, 46));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    public static void main(String[] args) {
        MainApp.main(args);
    }
}
