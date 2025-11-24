import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReadAccounts {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/bank_analysis";
        String user = "root";
        String pass = "root";

        try {

            Connection conn = DriverManager.getConnection(url, user, pass);


            Statement stmt = conn.createStatement();

            String query = "SELECT account_id, first_name, last_name, balance FROM accounts";
            ResultSet rs = stmt.executeQuery(query);


            System.out.println("Account List:");
            System.out.println("--------------------------------------");
            while (rs.next()) {
                String id = rs.getString("account_id");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                double balance = rs.getDouble("balance");

                System.out.println(id + " | " + first + " " + last + " | Balance: " + balance);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
