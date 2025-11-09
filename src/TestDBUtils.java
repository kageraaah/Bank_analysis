import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Arrays;

public class TestDBUtils {

    public static void main(String[] args) {
        // ✅ Correct MAMP setup (port 3306)
        String url = "jdbc:mysql://127.0.0.1:3306/bank_analysis?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "root";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to Database!");
            DBUtils db = new DBUtils(conn);

            // Fetch all transaction amounts
            double[] amounts = db.fetchAllTransactionAmounts();
            System.out.println("\nTransaction Amounts:");
            System.out.println(Arrays.toString(amounts));

            // Fetch top 5 accounts
            List<DBUtils.AccountVolume> topAccounts = db.fetchTopAccounts(5);
            System.out.println("\nTop 5 Accounts by Transaction Volume:");
            for (DBUtils.AccountVolume av : topAccounts) {
                System.out.println(av.accountId + " -> " + av.totalAmount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
