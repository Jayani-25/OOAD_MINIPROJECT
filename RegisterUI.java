import javax.swing.*;
import java.awt.*;

public class RegisterUI {

    public RegisterUI() {

        JFrame frame = new JFrame("Register");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(new Color(76, 175, 80));
        header.setPreferredSize(new Dimension(400, 70));

        JLabel title = new JLabel("Create Account");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(title);

        // CARD
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(250, 30));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30));

        JButton registerBtn = new JButton("Register");

        styleButton(registerBtn, new Color(76, 175, 80));

        card.add(new JLabel("Email"));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));

        card.add(new JLabel("Password"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));

        card.add(registerBtn);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        // REGISTER LOGIC
        registerBtn.addActionListener(e -> {

            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fields cannot be empty!");
                return;
            }

            User user = new User(email, password);

            if (UserStore.addUser(user)) {
                JOptionPane.showMessageDialog(frame, "Registered Successfully!");
                frame.dispose();
                new LoginUI();
            } else {
                JOptionPane.showMessageDialog(frame, "User already exists!");
            }
        });
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