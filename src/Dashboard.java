import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Bank Analysis Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Buttons
        JPanel topPanel = new JPanel();
        JButton barBtn = new JButton("Show Top 5 Accounts");
        JButton pieBtn = new JButton("Show Deposits vs Withdrawals");
        topPanel.add(barBtn);
        topPanel.add(pieBtn);
        add(topPanel, BorderLayout.NORTH);

        // Button actions
        barBtn.addActionListener(e -> showTopAccountsChart());
        pieBtn.addActionListener(e -> showDepositWithdrawalChart());
    }

    private void showTopAccountsChart() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/bank_analysis?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String user = "root";
            String pass = "root";
            Connection conn = DriverManager.getConnection(url, user, pass);
            DBUtils db = new DBUtils(conn);

            List<DBUtils.AccountVolume> topAccounts = db.fetchTopAccounts(5);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (DBUtils.AccountVolume av : topAccounts) {
                dataset.addValue(av.totalAmount, "Transaction Volume", av.accountId);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Top 5 Accounts by Transaction Volume",
                    "Account ID",
                    "Total Amount",
                    dataset
            );

            setContentPane(new ChartPanel(chart));
            validate();
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showDepositWithdrawalChart() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/bank_analysis?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String user = "root";
            String pass = "root";
            Connection conn = DriverManager.getConnection(url, user, pass);
            DBUtils db = new DBUtils(conn);

            double[] totals = db.fetchDepositWithdrawalTotals();
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Deposits", totals[0]);
            dataset.setValue("Withdrawals", totals[1]);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Deposits vs Withdrawals",
                    dataset,
                    true, true, false
            );

            setContentPane(new ChartPanel(chart));
            validate();
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
