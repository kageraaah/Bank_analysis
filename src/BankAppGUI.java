import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankAppGUI extends JFrame {

    private Connection conn;

    public BankAppGUI() {
        // Window setup
        setTitle("Bank Data Analysis System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Database connection
        connectToDatabase();

        // Title label
        JLabel title = new JLabel("Bank Analysis Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Buttons panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));

        JButton btnAccounts = new JButton("View Accounts");
        JButton btnDeposits = new JButton("Total Deposits/Withdrawals");
        JButton btnNegatives = new JButton("Negative Balances");
        JButton btnOutliers = new JButton("Outlier Transactions");

        panel.add(btnAccounts);
        panel.add(btnDeposits);
        panel.add(btnNegatives);
        panel.add(btnOutliers);

        add(panel, BorderLayout.CENTER);

        // Action Listeners
        btnAccounts.addActionListener(e -> showAccounts());
        btnDeposits.addActionListener(e -> showTotals());
        btnNegatives.addActionListener(e -> showNegativeBalances());
        btnOutliers.addActionListener(e -> showOutliers());
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/bank_analysis";
        String user = "root";
        String pass = "root";
        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to Database (via GUI)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!");
            e.printStackTrace();
        }
    }

    private void showAccounts() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("account_id")).append(" - ")
                        .append(rs.getString("first_name")).append(" ")
                        .append(rs.getString("last_name")).append(" | Balance: ")
                        .append(rs.getDouble("balance")).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Accounts", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTotals() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT " +
                            "SUM(CASE WHEN type='Deposit' THEN amount ELSE 0 END) AS total_deposits, " +
                            "SUM(CASE WHEN type='Withdrawal' THEN amount ELSE 0 END) AS total_withdrawals " +
                            "FROM transactions"
            );

            if (rs.next()) {
                double deposits = rs.getDouble("total_deposits");
                double withdrawals = rs.getDouble("total_withdrawals");
                JOptionPane.showMessageDialog(this,
                        " Total Deposits: " + deposits + "\n Total Withdrawals: " + withdrawals,
                        "Totals", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNegativeBalances() {
        try {
            String query = "SELECT a.account_id, a.first_name, a.last_name, " +
                    "(a.balance + IFNULL(SUM(CASE WHEN t.type='Deposit' THEN t.amount ELSE -t.amount END), 0)) AS final_balance " +
                    "FROM accounts a LEFT JOIN transactions t ON a.account_id = t.account_id " +
                    "GROUP BY a.account_id HAVING final_balance < 0";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("account_id")).append(" - ")
                        .append(rs.getString("first_name")).append(" ")
                        .append(rs.getString("last_name")).append(" | Final Balance: ")
                        .append(rs.getDouble("final_balance")).append("\n");
            }

            if (sb.length() == 0) sb.append("No accounts with negative balance.");
            JOptionPane.showMessageDialog(this, sb.toString(), "Negative Balances", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOutliers() {
        JOptionPane.showMessageDialog(this, " Outlier detection to be implemented next!", "Outliers", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankAppGUI app = new BankAppGUI();
            app.setVisible(true);
        });
    }
}
