import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private final Connection conn;

    // Constructor: initialize DBUtils with an open connection
    public DBUtils(Connection conn) {
        this.conn = conn;
    }

    // Fetch all transaction amounts
    public double[] fetchAllTransactionAmounts() throws SQLException {
        String sql = "SELECT amount FROM transactions";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<Double> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getDouble("amount"));
            }

            double[] arr = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return arr;
        }
    }

    // to fetch the top accounts
    public List<AccountVolume> fetchTopAccounts(int topN) throws SQLException {
        String sql = "SELECT account_id, SUM(amount) AS total_amount " +
                "FROM transactions GROUP BY account_id " +
                "ORDER BY total_amount DESC LIMIT ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, topN);
            try (ResultSet rs = pst.executeQuery()) {
                List<AccountVolume> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new AccountVolume(
                            rs.getString("account_id"),
                            rs.getDouble("total_amount")
                    ));
                }
                return result;
            }
        }
    }

    // to get the withdrawals
    public double[] fetchDepositWithdrawalTotals() throws SQLException {
        String sql = "SELECT type, SUM(amount) AS total FROM transactions GROUP BY type";
        double deposits = 0;
        double withdrawals = 0;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("type");
                double total = rs.getDouble("total");
                if (type.equalsIgnoreCase("Deposit")) deposits = total;
                else if (type.equalsIgnoreCase("Withdrawal")) withdrawals = total;
            }
        }
        return new double[]{deposits, withdrawals};
    }

    // account volume
    public static class AccountVolume {
        public final String accountId;
        public final double totalAmount;

        public AccountVolume(String accountId, double totalAmount) {
            this.accountId = accountId;
            this.totalAmount = totalAmount;
        }
    }
}
