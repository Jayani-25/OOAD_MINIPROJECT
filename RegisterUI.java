import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterUI extends JPanel {

    private static final Color SUCCESS = new Color(0x4C, 0xAF, 0x50);
    private static final Color DANGER = new Color(0xF4, 0x43, 0x36);
    private static final Color TEXT_PRIMARY = new Color(0x21, 0x21, 0x21);
    private static final Color CARD_BG = new Color(0xF5, 0xF5, 0xF5);

    private final MainApp app;
    private final WalletFacade facade = new WalletFacade();

    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JLabel feedbackLabel = new JLabel(" ");

    public RegisterUI(MainApp app) {
        this.app = app;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setPreferredSize(new Dimension(360, 460));

        JButton backButton = createBackButton("Back");
        backButton.addActionListener(e -> app.showLogin(false));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleInput(usernameField);
        styleInput(emailField);
        styleInput(passwordField);
        styleInput(confirmPasswordField);

        JButton registerButton = createFilledButton("REGISTER", SUCCESS);
        registerButton.addActionListener(e -> handleRegister());

        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackLabel.setForeground(DANGER);
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(backButton);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(22));
        card.add(createFieldLabel("Username"));
        card.add(Box.createVerticalStrut(8));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Email"));
        card.add(Box.createVerticalStrut(8));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Password"));
        card.add(Box.createVerticalStrut(8));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Confirm"));
        card.add(Box.createVerticalStrut(8));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(22));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(14));
        card.add(feedbackLabel);

        add(card);
    }

    public void resetForm() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        feedbackLabel.setText(" ");
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields.", DANGER);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", DANGER);
            return;
        }

        boolean success = facade.register(username, email, password);
        if (!success) {
            showMessage("Unable to register. Email may already exist.", DANGER);
            return;
        }

        resetForm();
        app.showLogin(true);
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
        return label;
    }

    private JButton createBackButton(String text) {
        JButton button = new JButton("\u2190 " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(CARD_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JButton createFilledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(280, 45));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(280, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}
