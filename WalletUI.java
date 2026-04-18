import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WalletUI {

    private WalletController controller = new WalletController();

    public WalletUI() {
        JFrame frame = new JFrame("Digital Wallet");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Digital Wallet Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(40));

        JButton addMoneyBtn = createButton("Add Money", new Color(76, 175, 80));
        JButton sendMoneyBtn = createButton("Send Money", new Color(255, 152, 0));
        JButton balanceBtn = createButton("View Balance", new Color(33, 150, 243));
        JButton transactionBtn = createButton("View Transactions", new Color(156, 39, 176));
        JButton logoutBtn = createButton("Logout", new Color(244, 67, 54));

        mainPanel.add(addMoneyBtn);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(sendMoneyBtn);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(balanceBtn);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(transactionBtn);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(logoutBtn);

        frame.add(mainPanel);
        frame.setVisible(true);

        // ADD MONEY
        addMoneyBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter amount:");
            if (input != null) {
                try {
                    float amount = Float.parseFloat(input);

                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(frame, "Amount must be greater than 0!");
                        return;
                    }

                    controller.addMoney(SessionManager.getInstance().getCurrentUser().getWallet(), amount);
                    JOptionPane.showMessageDialog(frame, "Money Added Successfully!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount!");
                }
            }
        });

        // SEND MONEY (FIXED LOGIC)
        sendMoneyBtn.addActionListener(e -> {

            String receiverEmail = JOptionPane.showInputDialog(frame, "Enter receiver email:");
            if (receiverEmail == null || receiverEmail.trim().isEmpty()) {
                return;
            }

            // prevent sending to self
            if (receiverEmail.equalsIgnoreCase(SessionManager.getInstance().getCurrentUser().getEmail())) {
                JOptionPane.showMessageDialog(frame, "Cannot send money to yourself!");
                return;
            }

            User receiver = UserStore.findUserByEmail(receiverEmail.trim());

            if (receiver == null) {
                JOptionPane.showMessageDialog(frame, "Receiver not found!");
                return;
            }

            String input = JOptionPane.showInputDialog(frame, "Enter amount:");
            if (input != null) {
                try {
                    float amount = Float.parseFloat(input);

                    boolean success = controller.sendMoney(
                            SessionManager.getInstance().getCurrentUser().getWallet(),
                            receiver.getWallet(),
                            amount
                    );

                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Money Sent Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Insufficient Balance!");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount!");
                }
            }
        });

        // VIEW BALANCE
        balanceBtn.addActionListener(e -> {
            float balance = SessionManager.getInstance().getCurrentUser().getWallet().getBalance();
            JOptionPane.showMessageDialog(frame, "Current Balance: ₹" + balance);
        });

        // VIEW TRANSACTIONS
        transactionBtn.addActionListener(e -> {
            ArrayList<Transaction> list = TransactionStore.getTransactions();

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No Transactions Found!");
                return;
            }

            StringBuilder history = new StringBuilder();
            for (Transaction t : list) {
                history.append(t.toString()).append("\n");
            }

            JOptionPane.showMessageDialog(frame, history.toString());
        });

        // LOGOUT
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