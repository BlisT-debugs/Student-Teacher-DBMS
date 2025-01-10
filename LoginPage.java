import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private static Connection connection;
    private BufferedImage backgroundImage;

    public LoginPage() {
        loadImage(); // Load background image
        setTitle("Login Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel setup
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcBackground = new GridBagConstraints();
        gbcBackground.insets = new Insets(10, 10, 10, 10);
        gbcBackground.fill = GridBagConstraints.HORIZONTAL;

        // Form panel setup
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0, 0, 0, 150));
        addComponentsToFormPanel(formPanel);

        backgroundPanel.add(formPanel, gbcBackground);
        setContentPane(backgroundPanel); // Set background panel as the content pane

        // Database connection
        connectToDatabase();

        // Show JFrame
        setVisible(true);
    }

    private void loadImage() {
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo Loq\\Desktop\\java\\images\\tp.jpg"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load background image: " + e.getMessage());
            System.exit(1);
        }
    }

    private void addComponentsToFormPanel(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        // Action listener for login button
        loginButton.addActionListener(e -> login());
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String query = "SELECT type FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("type");
                handleUserType(userType, username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleUserType(String userType, String username) {
        if ("teacher".equalsIgnoreCase(userType)) {
            JOptionPane.showMessageDialog(this, "Login successful!\nAccess Type: TEACHER");
            new RegistrationForm(); // Open RegistrationForm for teachers
            dispose(); // Close the LoginPage
        } else if ("student".equalsIgnoreCase(userType)) {
            JOptionPane.showMessageDialog(this, "Login successful!\nAccess Type: STUDENT");
            Student studentPage = new Student();  // Create the student window
            studentPage.setLoggedInUsername(username);  // Set the logged-in username
            studentPage.setVisible(true);  // Make the student window visible
            dispose(); // Close the LoginPage
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
