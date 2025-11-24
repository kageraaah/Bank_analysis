import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginScreen extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private Connection conn;

    public LoginScreen() {
        setTitle("Bank Analysis Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Username (First Last):"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password (Account ID):"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");
        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        add(btnPanel, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));

        connectToDatabase();
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/bank_analysis";
        String user = "root";
        String pass = "root";

        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to database for login");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            e.printStackTrace();
        }
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        try {
            String query = "SELECT * FROM accounts WHERE CONCAT(first_name, ' ', last_name)=? AND account_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setInt(2, Integer.parseInt(password));

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + username);
                int accountId = rs.getInt("account_id");
                this.dispose();
                new BankAppGUI(accountId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or account ID");
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Account ID must be a number!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
