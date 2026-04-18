import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AdminUI {

    private AdminController controller = new AdminController();

    public AdminUI() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton usersBtn = createButton("View Users", new Color(33, 150, 243));
        JButton transactionsBtn = createButton("View Transactions", new Color(156, 39, 176));
        JButton reportsBtn = createButton("View Reports", new Color(76, 175, 80));
        JButton logoutBtn = createButton("Logout", new Color(244, 67, 54));

        panel.add(title);
        panel.add(Box.createVerticalStrut(40));
        panel.add(usersBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(transactionsBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(reportsBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(logoutBtn);

        frame.add(panel);
        frame.setVisible(true);

        usersBtn.addActionListener(e -> {
            ArrayList<User> users = controller.getAllUsers();

            StringBuilder sb = new StringBuilder();
            for (User user : users) {
                sb.append(user.getEmail()).append("\n");
            }

            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        transactionsBtn.addActionListener(e -> {
            ArrayList<Transaction> transactions = controller.getAllTransactions();

            StringBuilder sb = new StringBuilder();
            for (Transaction t : transactions) {
                sb.append(t.toString()).append("\n");
            }

            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        reportsBtn.addActionListener(e -> {
            float total = controller.getTotalSystemBalance();
            JOptionPane.showMessageDialog(frame,
                    "Total Money in System: ₹" + total);
        });

        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            frame.dispose();
            new LoginUI();
        });
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(300, 50));
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}