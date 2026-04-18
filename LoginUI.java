import javax.swing.*;
import java.awt.*;

public class LoginUI {

    public LoginUI() {
        JFrame frame = new JFrame("Login");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));

        JPanel header = new JPanel();
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(400, 70));

        JLabel title = new JLabel("Digital Wallet");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(title);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(250, 30));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30));

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create Account");

        styleButton(loginBtn, new Color(33, 150, 243));
        styleButton(registerBtn, new Color(76, 175, 80));

        card.add(new JLabel("Email"));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(new JLabel("Password"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        // ✅ UPDATED LOGIN LOGIC WITH ROLE SUPPORT
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            User user = UserStore.validateUser(email, password);

            if (user != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                SessionManager.getInstance().setCurrentUser(user);
                frame.dispose();

                // ✅ ROLE-BASED REDIRECTION
                if (user.getRole().equals("ADMIN")) {
                    new AdminUI();
                } else {
                    new WalletUI();
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials!");
            }
        });

        registerBtn.addActionListener(e -> {
            frame.dispose();
            new RegisterUI();
        });
    }

    public static void main(String[] args) {
        // ✅ default admin account for demo
        UserStore.addUser(
            UserFactory.createAdmin("admin@wallet.com", "admin123")
        );

        new LoginUI();
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}