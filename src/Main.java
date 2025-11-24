import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/bank_analysis";
        String user = "root";
        String pass = "root";

        System.out.println("Starting Bank Analysis Application...");

        try {

            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connection successful!");
            conn.close();

            SwingUtilities.invokeLater(() -> {
                new Dashboard().setVisible(true);
            });

        } catch (Exception e) {
            System.err.println(" Database connection failed!");
            e.printStackTrace();
        }
    }
}
