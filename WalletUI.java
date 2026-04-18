import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WalletUI extends JPanel {

    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color SUCCESS = new Color(76, 175, 80);
    private static final Color DANGER = new Color(244, 67, 54);
    private static final Color WARNING = new Color(255, 152, 0);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color CARD_BG = new Color(245, 245, 245);

    private final MainApp app;
    private final WalletFacade facade = new WalletFacade();
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private final JLabel headerUserLabel = new JLabel();
    private final JLabel welcomeLabel = new JLabel();
    private final JLabel balanceValueLabel = new JLabel();
    private final JPanel recentTransactionsPanel = new JPanel();
    private final JPanel allTransactionsPanel = new JPanel();
    private final JPanel notificationsPanel = new JPanel();

    private final JTextField addMoneyAmountField = new JTextField();
    private final JLabel addMoneyFeedback = new JLabel(" ");
    private final JComboBox<String> paymentMethodBox = new JComboBox<>(new String[]{"Wallet Top-up", "Bank Transfer", "UPI"});

    private final JTextField sendRecipientField = new JTextField();
    private final JTextField sendAmountField = new JTextField();
    private final JPanel sendOtpPanel = new JPanel();
    private final JLabel sendOtpCodeLabel = new JLabel(" ");
    private final JTextField sendOtpField = new JTextField();
    private final JLabel sendFeedback = new JLabel(" ");
    private String pendingSendOtp;
    private String pendingReceiverIdentifier;
    private float pendingSendAmount;

    private final JComboBox<String> merchantBox = new JComboBox<>();
    private final JTextField merchantAmountField = new JTextField();
    private final JPanel merchantOtpPanel = new JPanel();
    private final JLabel merchantOtpCodeLabel = new JLabel(" ");
    private final JTextField merchantOtpField = new JTextField();
    private final JLabel merchantFeedback = new JLabel(" ");
    private String pendingMerchantOtp;
    private String pendingMerchantUsername;
    private float pendingMerchantAmount;

    private final JButton allFilterButton = new JButton("All");
    private final JButton sentFilterButton = new JButton("Sent");
    private final JButton receivedFilterButton = new JButton("Received");
    private final JButton addedFilterButton = new JButton("Added");
    private String transactionFilter = "ALL";

    private JButton dashboardButton;
    private JButton addMoneyButton;
    private JButton sendMoneyButton;
    private JButton payMerchantButton;
    private JButton transactionsButton;
    private JButton notificationsButton;
    private JButton[] sidebarButtons;

    public WalletUI(MainApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        buildHomeCard();
        buildAddMoneyCard();
        buildSendMoneyCard();
        buildPayMerchantCard();
        buildTransactionsCard();
        buildNotificationsCard();
    }

    public void onShow() {
        refreshDashboard();
        showCard("HOME");
    }

    public void refreshDashboard() {
        User currentUser = facade.getCurrentUser();
        String username = facade.getDisplayName(currentUser);
        headerUserLabel.setText("\uD83D\uDC64 " + username);
        welcomeLabel.setText("Welcome back, " + username + "!");
        balanceValueLabel.setText(formatAmount(facade.getBalance()));

        rebuildRecentTransactions();
        rebuildTransactionsList();
        rebuildNotificationsList();
        reloadMerchants();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel menuLabel = new JLabel("\u2261  Menu");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setPreferredSize(new Dimension(120, 65));

        JLabel titleLabel = new JLabel("Digital Wallet", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS));
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(200, 65));

        String displayName = facade.getDisplayName(facade.getCurrentUser());
        if (displayName.isEmpty()) {
            displayName = "Guest";
        }
        headerUserLabel.setText("\uD83D\uDC64 " + displayName);
        headerUserLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headerUserLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setBackground(new Color(198, 40, 40));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(75, 32));
        logoutBtn.setMaximumSize(new Dimension(75, 32));
        logoutBtn.setMinimumSize(new Dimension(75, 32));
        logoutBtn.addActionListener(e -> app.showLogin(false));

        eastPanel.add(headerUserLabel);
        eastPanel.add(Box.createHorizontalStrut(12));
        eastPanel.add(logoutBtn);
        eastPanel.add(Box.createHorizontalStrut(5));

        header.add(menuLabel, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.add(eastPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(WHITE);

        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint shadow = new GradientPaint(
                        getWidth() - 8, 0, new Color(0, 0, 0, 25),
                        getWidth(), 0, new Color(0, 0, 0, 0)
                );
                g2.setPaint(shadow);
                g2.fillRect(getWidth() - 8, 0, 8, getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(250, 250, 250));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        dashboardButton = createSidebarButton("Dashboard");
        addMoneyButton = createSidebarButton("Add Money");
        sendMoneyButton = createSidebarButton("Send Money");
        payMerchantButton = createSidebarButton("Pay Merchant");
        transactionsButton = createSidebarButton("Transactions");
        notificationsButton = createSidebarButton("Notifications");
        sidebarButtons = new JButton[]{dashboardButton, addMoneyButton, sendMoneyButton, payMerchantButton, transactionsButton, notificationsButton};

        dashboardButton.addActionListener(e -> showCard("HOME"));
        addMoneyButton.addActionListener(e -> showCard("ADD_MONEY"));
        sendMoneyButton.addActionListener(e -> showCard("SEND_MONEY"));
        payMerchantButton.addActionListener(e -> showCard("PAY_MERCHANT"));
        transactionsButton.addActionListener(e -> showCard("TRANSACTIONS"));
        notificationsButton.addActionListener(e -> showCard("NOTIFICATIONS"));

        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(addMoneyButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(sendMoneyButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(payMerchantButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(transactionsButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(notificationsButton);

        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        body.add(sidebar, BorderLayout.WEST);
        body.add(contentPanel, BorderLayout.CENTER);
        return body;
    }

    private void buildHomeCard() {
        JPanel homePanel = createContentWrapper();

        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel balanceCard = createCardPanel();
        balanceCard.setLayout(new BoxLayout(balanceCard, BoxLayout.Y_AXIS));
        balanceCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel balanceTitle = new JLabel("Current Balance");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        balanceTitle.setForeground(new Color(66, 66, 66));
        balanceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        balanceValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balanceValueLabel.setForeground(TEXT_PRIMARY);
        balanceValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRow.setOpaque(false);
        JButton addBtn = createPrimaryActionButton("Add Money", SUCCESS);
        JButton sendBtn = createPrimaryActionButton("Send Money", PRIMARY);
        addBtn.addActionListener(e -> showCard("ADD_MONEY"));
        sendBtn.addActionListener(e -> showCard("SEND_MONEY"));
        btnRow.add(addBtn);
        btnRow.add(sendBtn);

        balanceCard.add(balanceTitle);
        balanceCard.add(Box.createVerticalStrut(10));
        balanceCard.add(balanceValueLabel);
        balanceCard.add(Box.createVerticalStrut(18));
        balanceCard.add(btnRow);

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        recentTitle.setForeground(new Color(66, 66, 66));
        recentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        recentTransactionsPanel.setLayout(new BoxLayout(recentTransactionsPanel, BoxLayout.Y_AXIS));
        recentTransactionsPanel.setOpaque(false);

        JPanel recentWrapper = createCardPanel();
        recentWrapper.setLayout(new BorderLayout());
        recentWrapper.add(recentTransactionsPanel, BorderLayout.CENTER);
        recentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createVerticalStrut(15));
        homePanel.add(balanceCard);
        homePanel.add(Box.createVerticalStrut(24));
        homePanel.add(recentTitle);
        homePanel.add(Box.createVerticalStrut(10));
        homePanel.add(recentWrapper);

        JScrollPane scrollPane = new JScrollPane(homePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        contentPanel.add(scrollPane, "HOME");
    }

    private void buildAddMoneyCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Add Money", () -> showCard("HOME")));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Select Payment Method:"));
        panel.add(Box.createVerticalStrut(15));
        styleComboBox(paymentMethodBox);
        panel.add(paymentMethodBox);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Amount:"));
        panel.add(Box.createVerticalStrut(15));
        styleInput(addMoneyAmountField);
        panel.add(addMoneyAmountField);
        panel.add(Box.createVerticalStrut(15));

        JButton addMoneyBtn = createPrimaryActionButton("ADD MONEY", SUCCESS);
        addMoneyBtn.addActionListener(e -> submitAddMoney());
        panel.add(addMoneyBtn);
        panel.add(Box.createVerticalStrut(15));

        configureFeedbackLabel(addMoneyFeedback);
        panel.add(addMoneyFeedback);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        contentPanel.add(scrollPane, "ADD_MONEY");
    }

    private void buildSendMoneyCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Send Money", () -> showCard("HOME")));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Recipient (username or email):"));
        panel.add(Box.createVerticalStrut(15));
        styleInput(sendRecipientField);
        panel.add(sendRecipientField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Amount:"));
        panel.add(Box.createVerticalStrut(15));
        styleInput(sendAmountField);
        panel.add(sendAmountField);
        panel.add(Box.createVerticalStrut(15));

        JButton continueBtn = createPrimaryActionButton("CONTINUE \u2192", WARNING);
        continueBtn.addActionListener(e -> startSendMoneyFlow());
        panel.add(continueBtn);
        panel.add(Box.createVerticalStrut(15));

        buildOtpSection(sendOtpPanel, sendOtpCodeLabel, sendOtpField,
                e -> confirmSendMoney(), e -> resetSendFlow(" "));
        panel.add(sendOtpPanel);
        panel.add(Box.createVerticalStrut(15));

        configureFeedbackLabel(sendFeedback);
        panel.add(sendFeedback);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        contentPanel.add(scrollPane, "SEND_MONEY");
    }

    private void buildPayMerchantCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Pay Merchant", () -> showCard("HOME")));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Select Merchant:"));
        panel.add(Box.createVerticalStrut(15));
        styleComboBox(merchantBox);
        panel.add(merchantBox);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFieldTitle("Amount:"));
        panel.add(Box.createVerticalStrut(15));
        styleInput(merchantAmountField);
        panel.add(merchantAmountField);
        panel.add(Box.createVerticalStrut(15));

        JButton continueBtn = createPrimaryActionButton("CONTINUE \u2192", WARNING);
        continueBtn.addActionListener(e -> startMerchantFlow());
        panel.add(continueBtn);
        panel.add(Box.createVerticalStrut(15));

        buildOtpSection(merchantOtpPanel, merchantOtpCodeLabel, merchantOtpField,
                e -> confirmMerchantPayment(), e -> resetMerchantFlow(" "));
        panel.add(merchantOtpPanel);
        panel.add(Box.createVerticalStrut(15));

        configureFeedbackLabel(merchantFeedback);
        panel.add(merchantFeedback);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        contentPanel.add(scrollPane, "PAY_MERCHANT");
    }

    private void buildTransactionsCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Transactions", () -> showCard("HOME")));
        panel.add(Box.createVerticalStrut(15));

        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabsPanel.setOpaque(false);
        configureFilterButton(allFilterButton, "ALL");
        configureFilterButton(sentFilterButton, "SENT");
        configureFilterButton(receivedFilterButton, "RECEIVED");
        configureFilterButton(addedFilterButton, "ADDED");
        tabsPanel.add(allFilterButton);
        tabsPanel.add(sentFilterButton);
        tabsPanel.add(receivedFilterButton);
        tabsPanel.add(addedFilterButton);
        panel.add(tabsPanel);
        panel.add(Box.createVerticalStrut(15));

        allTransactionsPanel.setLayout(new BoxLayout(allTransactionsPanel, BoxLayout.Y_AXIS));
        allTransactionsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(allTransactionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setPreferredSize(new Dimension(600, 380));
        panel.add(scrollPane);

        contentPanel.add(panel, "TRANSACTIONS");
    }

    private void buildNotificationsCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Notifications", () -> showCard("HOME")));
        panel.add(Box.createVerticalStrut(15));

        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        panel.add(scrollPane);

        contentPanel.add(panel, "NOTIFICATIONS");
    }

    private void submitAddMoney() {
        addMoneyFeedback.setText(" ");

        PaymentMethod paymentMethod = new PaymentMethod((String) paymentMethodBox.getSelectedItem(), "UI Selected");
        if (!paymentMethod.verifyMethod()) {
            showFeedback(addMoneyFeedback, "Please select a valid payment method.", DANGER);
            return;
        }

        try {
            float amount = Float.parseFloat(addMoneyAmountField.getText().trim());
            if (amount <= 0) {
                showFeedback(addMoneyFeedback, "Amount must be greater than zero.", DANGER);
                return;
            }

            if (facade.addMoney(amount)) {
                showFeedback(addMoneyFeedback, formatAmount(amount) + " added successfully!", SUCCESS);
                addMoneyAmountField.setText("");
                refreshDashboard();
            } else {
                showFeedback(addMoneyFeedback, "Unable to add money right now.", DANGER);
            }
        } catch (NumberFormatException ex) {
            showFeedback(addMoneyFeedback, "Enter a valid amount.", DANGER);
        }
    }

    private void startSendMoneyFlow() {
        resetSendFlow(" ");

        User sender = facade.getCurrentUser();
        if (sender == null) {
            showFeedback(sendFeedback, "Please login again.", DANGER);
            return;
        }

        pendingReceiverIdentifier = sendRecipientField.getText().trim();
        if (pendingReceiverIdentifier.isEmpty()) {
            showFeedback(sendFeedback, "Enter a recipient username or email.", DANGER);
            return;
        }

        if (pendingReceiverIdentifier.equalsIgnoreCase(sender.getEmail())
                || pendingReceiverIdentifier.equalsIgnoreCase(sender.getUsername())) {
            showFeedback(sendFeedback, "You cannot send money to yourself.", DANGER);
            return;
        }

        User receiver = facade.findUserByIdentifier(pendingReceiverIdentifier);
        if (receiver == null) {
            showFeedback(sendFeedback, "Recipient not found.", DANGER);
            return;
        }

        try {
            pendingSendAmount = Float.parseFloat(sendAmountField.getText().trim());
            if (pendingSendAmount <= 0) {
                showFeedback(sendFeedback, "Amount must be greater than zero.", DANGER);
                return;
            }
        } catch (NumberFormatException ex) {
            showFeedback(sendFeedback, "Enter a valid amount.", DANGER);
            return;
        }

        pendingSendOtp = AuthService.generateOTP();
        JOptionPane.showMessageDialog(this,
                "Your OTP is: " + pendingSendOtp + "\n(In production this would be sent via SMS)");
        sendOtpCodeLabel.setText("Your OTP: " + pendingSendOtp);
        sendOtpPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void confirmSendMoney() {
        if (pendingSendOtp == null) {
            showFeedback(sendFeedback, "Start the payment flow first.", DANGER);
            return;
        }

        if (!AuthService.verifyOTP(sendOtpField.getText())) {
            AuthService.clearOTP();
            resetSendFlow("Authorization Failed! Transaction Cancelled.");
            return;
        }

        AuthService.clearOTP();
        User receiver = facade.findUserByIdentifier(pendingReceiverIdentifier);
        boolean success = facade.sendMoney(pendingReceiverIdentifier, pendingSendAmount);

        if (success && receiver != null) {
            showFeedback(sendFeedback, "Money sent to " + facade.getDisplayName(receiver) + " successfully!", SUCCESS);
            sendRecipientField.setText("");
            sendAmountField.setText("");
            refreshDashboard();
        } else {
            showFailure(sendFeedback, pendingSendAmount, receiver);
        }

        pendingSendOtp = null;
        sendOtpPanel.setVisible(false);
    }

    private void resetSendFlow(String feedback) {
        pendingSendOtp = null;
        pendingReceiverIdentifier = null;
        pendingSendAmount = 0;
        sendOtpField.setText("");
        sendOtpCodeLabel.setText(" ");
        sendOtpPanel.setVisible(false);
        if (!" ".equals(feedback)) {
            showFeedback(sendFeedback, feedback, DANGER);
        } else {
            sendFeedback.setText(" ");
        }
    }

    private void startMerchantFlow() {
        resetMerchantFlow(" ");

        Object selected = merchantBox.getSelectedItem();
        if (selected == null) {
            showFeedback(merchantFeedback, "No merchant selected.", DANGER);
            return;
        }

        pendingMerchantUsername = selected.toString();
        try {
            pendingMerchantAmount = Float.parseFloat(merchantAmountField.getText().trim());
            if (pendingMerchantAmount <= 0) {
                showFeedback(merchantFeedback, "Amount must be greater than zero.", DANGER);
                return;
            }
        } catch (NumberFormatException ex) {
            showFeedback(merchantFeedback, "Enter a valid amount.", DANGER);
            return;
        }

        pendingMerchantOtp = AuthService.generateOTP();
        JOptionPane.showMessageDialog(this,
                "Your OTP is: " + pendingMerchantOtp + "\n(In production this would be sent via SMS)");
        merchantOtpCodeLabel.setText("Your OTP: " + pendingMerchantOtp);
        merchantOtpPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void confirmMerchantPayment() {
        if (pendingMerchantOtp == null) {
            showFeedback(merchantFeedback, "Start the payment flow first.", DANGER);
            return;
        }

        if (!AuthService.verifyOTP(merchantOtpField.getText())) {
            AuthService.clearOTP();
            resetMerchantFlow("Authorization Failed! Transaction Cancelled.");
            return;
        }

        AuthService.clearOTP();
        User merchant = facade.findUserByIdentifier(pendingMerchantUsername);
        boolean success = facade.payMerchant(pendingMerchantUsername, pendingMerchantAmount);

        if (success && merchant != null) {
            showFeedback(merchantFeedback, "Payment to " + facade.getDisplayName(merchant) + " successful!", SUCCESS);
            merchantAmountField.setText("");
            refreshDashboard();
        } else {
            showFailure(merchantFeedback, pendingMerchantAmount, merchant);
        }

        pendingMerchantOtp = null;
        merchantOtpPanel.setVisible(false);
    }

    private void resetMerchantFlow(String feedback) {
        pendingMerchantOtp = null;
        pendingMerchantUsername = null;
        pendingMerchantAmount = 0;
        merchantOtpField.setText("");
        merchantOtpCodeLabel.setText(" ");
        merchantOtpPanel.setVisible(false);
        if (!" ".equals(feedback)) {
            showFeedback(merchantFeedback, feedback, DANGER);
        } else {
            merchantFeedback.setText(" ");
        }
    }

    private void rebuildRecentTransactions() {
        recentTransactionsPanel.removeAll();

        ArrayList<Transaction> transactions = facade.getTransactionHistory();
        if (transactions.isEmpty()) {
            recentTransactionsPanel.add(createEmptyStateLabel("No recent transactions."));
        } else {
            int limit = Math.min(5, transactions.size());
            for (int i = 0; i < limit; i++) {
                recentTransactionsPanel.add(createTransactionRow(transactions.get(i), i % 2 == 1));
            }
        }

        recentTransactionsPanel.revalidate();
        recentTransactionsPanel.repaint();
    }

    private void rebuildTransactionsList() {
        allTransactionsPanel.removeAll();

        ArrayList<Transaction> transactions = facade.getTransactionHistory();
        int rowIndex = 0;
        for (Transaction transaction : transactions) {
            if (!"ALL".equals(transactionFilter) && !transactionFilter.equalsIgnoreCase(transaction.getType())) {
                continue;
            }
            allTransactionsPanel.add(createTransactionRow(transaction, rowIndex % 2 == 1));
            rowIndex++;
        }

        if (rowIndex == 0) {
            allTransactionsPanel.add(createEmptyStateLabel("No transactions available."));
        }

        allTransactionsPanel.revalidate();
        allTransactionsPanel.repaint();
        updateFilterStyles();
    }

    private void rebuildNotificationsList() {
        notificationsPanel.removeAll();

        List<Notification> notifications = facade.getPendingNotifications();
        if (notifications.isEmpty()) {
            notificationsPanel.add(createEmptyStateLabel("No new notifications \uD83D\uDD14"));
        } else {
            for (Notification notification : notifications) {
                notificationsPanel.add(createNotificationCard(notification));
                notificationsPanel.add(Box.createVerticalStrut(10));
            }
        }

        notificationsPanel.revalidate();
        notificationsPanel.repaint();
    }

    private void reloadMerchants() {
        merchantBox.removeAllItems();
        for (User merchant : facade.getUsersByRole("MERCHANT")) {
            merchantBox.addItem(merchant.getUsername());
        }
    }

    private JPanel createTransactionRow(Transaction transaction, boolean alternate) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(alternate ? new Color(249, 249, 249) : WHITE);
        row.setBorder(new EmptyBorder(12, 14, 12, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setMinimumSize(new Dimension(0, 60));

        JLabel arrowLabel = new JLabel(getTransactionIcon(transaction), SwingConstants.CENTER);
        arrowLabel.setPreferredSize(new Dimension(40, 40));
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        arrowLabel.setForeground(getTransactionColor(transaction));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(transaction.toString().replaceAll(": Rs\\..*$", ""));
        description.setFont(new Font("Segoe UI", Font.BOLD, 14));
        description.setForeground(TEXT_PRIMARY);

        JLabel dateLabel = new JLabel("Apr 18, 2026");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(158, 158, 158));

        center.add(description);
        center.add(dateLabel);

        JLabel amountLabel = new JLabel(formatAmount(transaction.getAmount()), SwingConstants.RIGHT);
        amountLabel.setPreferredSize(new Dimension(100, 40));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(getTransactionColor(transaction));

        row.add(arrowLabel, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        row.add(amountLabel, BorderLayout.EAST);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.add(row);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(238, 238, 238));
        wrapper.add(sep);
        return wrapper;
    }

    private JPanel createNotificationCard(Notification notification) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel message = new JLabel(notification.getMessage());
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(TEXT_PRIMARY);

        JLabel date = new JLabel(new java.text.SimpleDateFormat("MMM dd, yyyy").format(notification.getDate()));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        date.setForeground(TEXT_SECONDARY);

        card.add(message);
        card.add(Box.createVerticalStrut(6));
        card.add(date);
        return card;
    }

    private JPanel createContentWrapper() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(55, 55, 55));
        btn.setBackground(new Color(250, 250, 250));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 25, 14, 10));
        btn.setMaximumSize(new Dimension(210, 52));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(new Color(227, 242, 253))) {
                    btn.setBackground(new Color(240, 240, 240));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(new Color(227, 242, 253))) {
                    btn.setBackground(new Color(250, 250, 250));
                }
            }
        });
        return btn;
    }

    private JPanel createPageHeader(String title, Runnable onBack) {
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        headerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backBtn = new JButton("\u2190 Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setForeground(new Color(33, 150, 243));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(85, 35));
        backBtn.addActionListener(e -> onBack.run());

        JLabel pageTitle = new JLabel(title);
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(new Color(33, 33, 33));
        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);

        headerRow.add(backBtn, BorderLayout.WEST);
        headerRow.add(pageTitle, BorderLayout.CENTER);
        return headerRow;
    }

    private JLabel createFieldTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(66, 66, 66));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createPrimaryActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 42));
        button.setPreferredSize(new Dimension(180, 42));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private void buildOtpSection(JPanel panel, JLabel otpLabel, JTextField otpField,
                                 java.awt.event.ActionListener confirmAction,
                                 java.awt.event.ActionListener cancelAction) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setVisible(false);

        JLabel title = new JLabel("OTP Verification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_PRIMARY);

        otpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        otpLabel.setForeground(PRIMARY);

        JLabel otpInputLabel = new JLabel("Enter OTP:");
        otpInputLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        otpInputLabel.setForeground(TEXT_PRIMARY);

        styleInput(otpField);
        otpField.setMaximumSize(new Dimension(120, 40));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        JButton confirmButton = createPrimaryActionButton("CONFIRM PAYMENT", PRIMARY);
        JButton cancelButton = createPrimaryActionButton("CANCEL", DANGER);
        confirmButton.addActionListener(confirmAction);
        cancelButton.addActionListener(cancelAction);
        buttonRow.add(confirmButton);
        buttonRow.add(cancelButton);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(otpLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(otpInputLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(otpField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buttonRow);
    }

    private void configureFeedbackLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void configureFilterButton(JButton button, String filter) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.addActionListener(e -> {
            transactionFilter = filter;
            rebuildTransactionsList();
        });
    }

    private void updateFilterStyles() {
        styleFilterButton(allFilterButton, "ALL".equals(transactionFilter));
        styleFilterButton(sentFilterButton, "SENT".equals(transactionFilter));
        styleFilterButton(receivedFilterButton, "RECEIVED".equals(transactionFilter));
        styleFilterButton(addedFilterButton, "ADDED".equals(transactionFilter));
    }

    private void styleFilterButton(JButton button, boolean active) {
        button.setBackground(active ? PRIMARY : CARD_BG);
        button.setForeground(active ? WHITE : TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? PRIMARY : new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(320, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboBox.setPreferredSize(new Dimension(320, 40));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(WHITE);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void showCard(String card) {
        if ("HOME".equals(card)) {
            refreshDashboard();
            setActiveButton(dashboardButton, sidebarButtons);
        } else if ("ADD_MONEY".equals(card)) {
            setActiveButton(addMoneyButton, sidebarButtons);
        } else if ("SEND_MONEY".equals(card)) {
            setActiveButton(sendMoneyButton, sidebarButtons);
        } else if ("PAY_MERCHANT".equals(card)) {
            reloadMerchants();
            setActiveButton(payMerchantButton, sidebarButtons);
        } else if ("TRANSACTIONS".equals(card)) {
            rebuildTransactionsList();
            setActiveButton(transactionsButton, sidebarButtons);
        } else if ("NOTIFICATIONS".equals(card)) {
            rebuildNotificationsList();
            setActiveButton(notificationsButton, sidebarButtons);
        }
        contentLayout.show(contentPanel, card);
    }

    private void showFeedback(JLabel label, String message, Color color) {
        label.setForeground(color);
        label.setText(message);
    }

    private void showFailure(JLabel label, float amount, User receiver) {
        if (receiver != null && receiver.isBlocked()) {
            showFeedback(label, "Selected account is blocked!", DANGER);
            return;
        }

        if (amount > facade.getMaxTransactionLimit()) {
            showFeedback(label, "Transaction exceeds maximum allowed limit of " + formatAmount(facade.getMaxTransactionLimit()), DANGER);
        } else {
            showFeedback(label, "Insufficient balance!", DANGER);
        }
    }

    private JLabel createEmptyStateLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        label.setBorder(new EmptyBorder(12, 8, 12, 8));
        return label;
    }

    private String getTransactionIcon(Transaction transaction) {
        if ("SENT".equalsIgnoreCase(transaction.getType())) {
            return "\u2191";
        }
        if ("RECEIVED".equalsIgnoreCase(transaction.getType())) {
            return "\u2193";
        }
        return "+";
    }

    private Color getTransactionColor(Transaction transaction) {
        if ("SENT".equalsIgnoreCase(transaction.getType())) {
            return DANGER;
        }
        if ("RECEIVED".equalsIgnoreCase(transaction.getType()) || "ADDED".equalsIgnoreCase(transaction.getType())) {
            return SUCCESS;
        }
        return PRIMARY;
    }

    private String formatAmount(float amount) {
        return String.format("Rs. %,.2f", amount);
    }

    private void setActiveButton(JButton active, JButton[] all) {
        for (JButton b : all) {
            b.setBackground(new Color(250, 250, 250));
            b.setForeground(new Color(55, 55, 55));
            b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        active.setBackground(new Color(227, 242, 253));
        active.setForeground(new Color(21, 101, 192));
        active.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
}
