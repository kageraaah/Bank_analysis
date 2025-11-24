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

        loadHomeScreen();
    }

    // Homescreen
    private void loadHomeScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton barBtn = new JButton("Show Top 5 Accounts");
        JButton pieBtn = new JButton("Show Deposits vs Withdrawals");
        topPanel.add(barBtn);
        topPanel.add(pieBtn);
        add(topPanel, BorderLayout.NORTH);

        barBtn.addActionListener(e -> showTopAccountsChart());
        pieBtn.addActionListener(e -> showDepositWithdrawalChart());

        validate();
        repaint();
    }

    // charts  back
    private void showChartWithBackButton(JFreeChart chart) {
        JPanel panel = new JPanel(new BorderLayout());

        // Add chart
        panel.add(new ChartPanel(chart), BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back to Home");
        backBtn.addActionListener(e -> loadHomeScreen());

        JPanel topBar = new JPanel();
        topBar.add(backBtn);
        panel.add(topBar, BorderLayout.NORTH);

        setContentPane(panel);
        validate();
    }

    //Bar-graph
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

            showChartWithBackButton(chart);
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Charts
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

            showChartWithBackButton(chart);
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }}



