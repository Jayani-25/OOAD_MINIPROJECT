import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class AdminUI extends JPanel {

    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color SUCCESS = new Color(76, 175, 80);
    private static final Color DANGER = new Color(244, 67, 54);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    private final MainApp app;
    private final WalletFacade facade = new WalletFacade();
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private final UserTableModel userTableModel = new UserTableModel();
    private final JTable usersTable = new JTable(userTableModel);
    private final JTextField newLimitField = new JTextField();
    private final JLabel paymentRuleFeedback = new JLabel(" ");

    private JPanel overviewPanel;
    private JButton overviewButton;
    private JButton manageUsersButton;
    private JButton paymentRulesButton;
    private JButton reportsButton;
    private JButton[] sidebarButtons;

    public AdminUI(MainApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        buildOverviewCard();
        buildManageUsersCard();
        buildPaymentRulesCard();
        buildReportsCard();
    }

    public void onShow() {
        refreshData();
        showCard("OVERVIEW");
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

        JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS));
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(200, 65));

        String displayName = facade.getDisplayName(facade.getCurrentUser());
        if (displayName.isEmpty()) {
            displayName = "Admin";
        }
        JLabel userLabel = new JLabel("\uD83D\uDC64 " + displayName);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

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

        eastPanel.add(userLabel);
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

        overviewButton = createSidebarButton("Overview");
        manageUsersButton = createSidebarButton("Manage Users");
        paymentRulesButton = createSidebarButton("Payment Rules");
        reportsButton = createSidebarButton("Reports");
        sidebarButtons = new JButton[]{overviewButton, manageUsersButton, paymentRulesButton, reportsButton};

        overviewButton.addActionListener(e -> showCard("OVERVIEW"));
        manageUsersButton.addActionListener(e -> showCard("USERS"));
        paymentRulesButton.addActionListener(e -> showCard("RULES"));
        reportsButton.addActionListener(e -> showCard("REPORTS"));

        sidebar.add(overviewButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(manageUsersButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(paymentRulesButton);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(reportsButton);

        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        body.add(sidebar, BorderLayout.WEST);
        body.add(contentPanel, BorderLayout.CENTER);
        return body;
    }

    private void buildOverviewCard() {
        overviewPanel = createContentWrapper();
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.add(overviewPanel, "OVERVIEW");
    }

    private void buildManageUsersCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Manage Users", () -> showCard("OVERVIEW")));

        usersTable.setRowHeight(42);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        usersTable.getTableHeader().setBackground(new Color(245, 245, 245));
        usersTable.setSelectionBackground(new Color(232, 245, 253));
        usersTable.setShowGrid(false);
        usersTable.setIntercellSpacing(new Dimension(0, 0));

        usersTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        usersTable.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());
        usersTable.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        panel.add(scrollPane);

        contentPanel.add(panel, "USERS");
    }

    private void buildPaymentRulesCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Payment Rules", () -> showCard("OVERVIEW")));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel currentLimitLabel = new JLabel();
        currentLimitLabel.setName("currentLimitLabel");
        currentLimitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentLimitLabel.setForeground(TEXT_PRIMARY);
        currentLimitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel newLimitLabel = new JLabel("New Limit:");
        newLimitLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        newLimitLabel.setForeground(TEXT_PRIMARY);
        newLimitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleInput(newLimitField);

        JButton updateButton = createActionButton("UPDATE LIMIT", PRIMARY);
        updateButton.addActionListener(e -> updatePaymentLimit());

        paymentRuleFeedback.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentRuleFeedback.setForeground(TEXT_SECONDARY);
        paymentRuleFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(currentLimitLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(newLimitLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(newLimitField);
        card.add(Box.createVerticalStrut(15));
        card.add(updateButton);
        card.add(Box.createVerticalStrut(15));
        card.add(paymentRuleFeedback);

        panel.add(card);
        contentPanel.add(panel, "RULES");
    }

    private void buildReportsCard() {
        JPanel panel = createContentWrapper();
        panel.add(createPageHeader("Reports", () -> showCard("OVERVIEW")));

        JPanel reportGrid = new JPanel(new GridLayout(5, 2, 0, 12));
        reportGrid.setBackground(new Color(245, 245, 245));
        reportGrid.setBorder(new EmptyBorder(24, 24, 24, 24));
        reportGrid.add(createReportLabel("Total Users"));
        reportGrid.add(createValueLabel("reportUsers"));
        reportGrid.add(createReportLabel("Total Transactions"));
        reportGrid.add(createValueLabel("reportTransactions"));
        reportGrid.add(createReportLabel("Total Money Added"));
        reportGrid.add(createValueLabel("reportAdded"));
        reportGrid.add(createReportLabel("Total Transferred"));
        reportGrid.add(createValueLabel("reportTransferred"));
        reportGrid.add(createReportLabel("System Balance"));
        reportGrid.add(createValueLabel("reportBalance"));

        panel.add(reportGrid);
        contentPanel.add(panel, "REPORTS");
    }

    public void refreshData() {
        refreshOverviewCards();
        userTableModel.setUsers(facade.getAllUsers());
        newLimitField.setText(String.valueOf(facade.getMaxTransactionLimit()));
        paymentRuleFeedback.setText(" ");

        updateNamedLabel(contentPanel, "currentLimitLabel", "Current Limit: " + formatAmount(facade.getMaxTransactionLimit()));
        updateNamedLabel(contentPanel, "reportUsers", String.valueOf(facade.getTotalUserCount()));
        updateNamedLabel(contentPanel, "reportTransactions", String.valueOf(facade.getTotalTransactionCount()));
        updateNamedLabel(contentPanel, "reportAdded", formatAmount(facade.getTotalMoneyAdded()));
        updateNamedLabel(contentPanel, "reportTransferred", formatAmount(facade.getTotalMoneySent()));
        updateNamedLabel(contentPanel, "reportBalance", formatAmount(facade.getTotalSystemBalance()));
    }

    private void refreshOverviewCards() {
        overviewPanel.removeAll();
        overviewPanel.add(createSectionTitle("Overview"));
        overviewPanel.add(Box.createVerticalStrut(24));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statsRow.setOpaque(false);
        statsRow.add(createStatCard("Total Users", String.valueOf(facade.getTotalUserCount()), new Color(33, 150, 243)));
        statsRow.add(createStatCard("Total Transactions", String.valueOf(facade.getTotalTransactionCount()), new Color(76, 175, 80)));
        statsRow.add(createStatCard("System Balance", formatAmount(facade.getTotalSystemBalance()), new Color(255, 152, 0)));
        overviewPanel.add(statsRow);
        overviewPanel.revalidate();
        overviewPanel.repaint();
    }

    private void updatePaymentLimit() {
        try {
            float limit = Float.parseFloat(newLimitField.getText().trim());
            if (limit <= 0) {
                showPaymentRuleMessage("Limit must be greater than zero.", DANGER);
                return;
            }

            facade.setMaxTransactionLimit(limit);
            showPaymentRuleMessage("Limit updated to " + formatAmount(limit), SUCCESS);
            refreshData();
        } catch (NumberFormatException ex) {
            showPaymentRuleMessage("Enter a valid amount.", DANGER);
        }
    }

    private void showPaymentRuleMessage(String message, Color color) {
        paymentRuleFeedback.setForeground(color);
        paymentRuleFeedback.setText(message);
    }

    private void showCard(String card) {
        refreshData();
        if ("OVERVIEW".equals(card)) setActiveButton(overviewButton, sidebarButtons);
        if ("USERS".equals(card)) setActiveButton(manageUsersButton, sidebarButtons);
        if ("RULES".equals(card)) setActiveButton(paymentRulesButton, sidebarButtons);
        if ("REPORTS".equals(card)) setActiveButton(reportsButton, sidebarButtons);
        contentLayout.show(contentPanel, card);
    }

    private JPanel createContentWrapper() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(117, 117, 117));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(accent);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLbl);
        return card;
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

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 45));
        button.setPreferredSize(new Dimension(220, 45));
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

    private JLabel createReportLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JLabel createValueLabel(String name) {
        JLabel label = new JLabel();
        label.setName(name);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_PRIMARY);
        return label;
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

    private String formatAmount(float amount) {
        return String.format("Rs. %,.2f", amount);
    }

    private void updateNamedLabel(Container parent, String name, String text) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JLabel && name.equals(component.getName())) {
                ((JLabel) component).setText(text);
            } else if (component instanceof Container) {
                updateNamedLabel((Container) component, name, text);
            }
        }
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

    private class UserTableModel extends AbstractTableModel {
        private final String[] columns = {"Username", "Email", "Role", "Status", "Actions"};
        private ArrayList<User> users = new ArrayList<>();

        public void setUsers(ArrayList<User> users) {
            this.users = users;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return users.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User user = users.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return user.getUsername();
                case 1:
                    return user.getEmail();
                case 2:
                    return user.getRole();
                case 3:
                    return user.isBlocked() ? "Blocked" : "Active";
                default:
                    return "Actions";
            }
        }

        public User getUserAt(int row) {
            return users.get(row);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 4;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean blocked = "Blocked".equals(value);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(WHITE);
            label.setBackground(blocked ? DANGER : SUCCESS);
            label.setOpaque(true);
            return label;
        }
    }

    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton blockButton = smallButton("Block", DANGER);
        private final JButton unblockButton = smallButton("Unblock", SUCCESS);

        ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);
            add(blockButton);
            add(unblockButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            User user = userTableModel.getUserAt(row);
            blockButton.setEnabled(!user.isBlocked());
            unblockButton.setEnabled(user.isBlocked());
            setBackground(row % 2 == 0 ? WHITE : new Color(249, 249, 249));
            return this;
        }
    }

    private class ActionCellEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private final JButton blockButton = smallButton("Block", DANGER);
        private final JButton unblockButton = smallButton("Unblock", SUCCESS);
        private int row;

        ActionCellEditor() {
            super(new JCheckBox());
            panel.add(blockButton);
            panel.add(unblockButton);

            blockButton.addActionListener(e -> {
                User user = userTableModel.getUserAt(row);
                facade.blockUser(user.getEmail());
                refreshData();
                fireEditingStopped();
            });

            unblockButton.addActionListener(e -> {
                User user = userTableModel.getUserAt(row);
                facade.unblockUser(user.getEmail());
                refreshData();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            User user = userTableModel.getUserAt(row);
            blockButton.setEnabled(!user.isBlocked());
            unblockButton.setEnabled(user.isBlocked());
            panel.setBackground(row % 2 == 0 ? WHITE : new Color(249, 249, 249));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private JButton smallButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return button;
    }
}
