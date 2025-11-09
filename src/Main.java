import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Database connection details for MAMP
        String url = "jdbc:mysql://localhost:3306/bank_analysis";  // MAMP default port for MySQL
        String user = "root";
        String pass = "root";

        System.out.println("Starting Bank Analysis Application...");

        try {
            // ✅ Test connection before launching the dashboard
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Database connection successful!");
            conn.close();

            // ✅ Launch the GUI dashboard
            SwingUtilities.invokeLater(() -> {
                new Dashboard().setVisible(true);
            });

        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }
}
