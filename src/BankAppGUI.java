import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankAppGUI extends JFrame {

    private Connection conn;

    public BankAppGUI() {

        setTitle("Bank Data Analysis System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        connectToDatabase();

        JLabel title = new JLabel("Bank Analysis Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10)); // Added gap for better UI

        JButton btnAccounts = new JButton("View Accounts");
        JButton btnDeposits = new JButton("Total Deposits/Withdrawals");
        JButton btnNegatives = new JButton("Negative Balances");
        JButton btnOutliers = new JButton("Outlier Transactions");
        JButton btnCharts = new JButton("Open Charts Dashboard");

        panel.add(btnAccounts);
        panel.add(btnDeposits);
        panel.add(btnNegatives);
        panel.add(btnOutliers);
        panel.add(btnCharts); // Added the 5th button to the panel

        add(panel, BorderLayout.CENTER);

        btnAccounts.addActionListener(e -> showAccounts());
        btnDeposits.addActionListener(e -> showTotals());
        btnNegatives.addActionListener(e -> showNegativeBalances());
        btnOutliers.addActionListener(e -> showOutliers());

        btnCharts.addActionListener(e -> {
            // CHECK: If you have a Dashboard.java file, uncomment the line below
            // new Dashboard().setVisible(true);

            // For now, show a message so the code doesn't crash
            JOptionPane.showMessageDialog(this, "Dashboard module is not connected yet.");
        });
    }
// databse connectors
    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/bank_analysis";
        String user = "root";
        String pass = "root";
        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to Database (via GUI)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!\n" + e.getMessage());
            e.printStackTrace();
        }
    }
// code for showing accounts
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
            // Used helper method for scrolling
            showMessageWithScroll(sb.toString(), "Accounts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// totals code
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
// desciptions of the negative balances

    private void showNegativeBalances() {
        try {
            // Added full GROUP BY for SQL Strict Mode compatibility
            String query = "SELECT a.account_id, a.first_name, a.last_name, " +
                    "(a.balance + IFNULL(SUM(CASE WHEN t.type='Deposit' THEN t.amount ELSE -t.amount END), 0)) AS final_balance " +
                    "FROM accounts a LEFT JOIN transactions t ON a.account_id = t.account_id " +
                    "GROUP BY a.account_id, a.first_name, a.last_name HAVING final_balance < 0";

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
            showMessageWithScroll(sb.toString(), "Negative Balances");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// showing descptiption of outlier transactions
    private void showOutliers() {
        try {
            Statement stmt1 = conn.createStatement();
            ResultSet rsCount = stmt1.executeQuery("SELECT COUNT(*) AS total FROM transactions");
            rsCount.next();
            int total = rsCount.getInt("total");

            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No transactions found.");
                return;
            }

            int offset = (int) Math.floor(0.9 * total);

            Statement stmt2 = conn.createStatement();
            ResultSet rsPercentile = stmt2.executeQuery(
                    "SELECT amount FROM transactions ORDER BY amount ASC LIMIT 1 OFFSET " + offset
            );

            if (!rsPercentile.next()) {
                JOptionPane.showMessageDialog(this, "Could not calculate 90th percentile.");
                return;
            }

            double p90 = rsPercentile.getDouble("amount");

            Statement stmt3 = conn.createStatement();
            ResultSet rs = stmt3.executeQuery("SELECT * FROM transactions WHERE amount > " + p90);

            StringBuilder sb = new StringBuilder();
            sb.append("Outlier Transactions (> 90th Percentile: ").append(p90).append(")\n\n");

            while (rs.next()) {
                // FIX APPLIED: Changed getInt to getString for transaction_id
                sb.append("ID: ").append(rs.getString("transaction_id"))
                        .append(" | Account: ").append(rs.getString("account_id"))
                        .append(" | Type: ").append(rs.getString("type"))
                        .append(" | Amount: ").append(rs.getDouble("amount"))
                        .append(" | Date: ").append(rs.getString("date"))
                        .append("\n");
            }

            if (sb.length() < 50) sb.append("No outlier transactions found.");

            showMessageWithScroll(sb.toString(), "Outlier Transactions");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error detecting outliers: " + e.getMessage());
        }
    }

    // scroll bar
    private void showMessageWithScroll(String message, String title) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setRows(15);
        textArea.setColumns(50);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankAppGUI app = new BankAppGUI();
            app.setVisible(true);
        });
    }
}