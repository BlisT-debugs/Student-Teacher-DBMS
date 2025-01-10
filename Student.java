import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Student extends JFrame {

    private JTextField firstNameField, lastNameField, emailField, rollNoField, contactField, gradeField;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private JButton registerButton, viewMyDataButton, logoutButton, resetButton;
    private BufferedImage backgroundImage;
    private ButtonGroup genderGroup;
    private static Connection connection;

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
    // Connect to database function
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public Student() {
        connectToDatabase();
        initComponents();

    }

    private void initComponents() {
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo Loq\\Desktop\\java\\images\\tp.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Student Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2400, 2400);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0, 0, 0, 150));

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setForeground(Color.WHITE);
        gbc.gridx = 2;
        formPanel.add(lastNameLabel, gbc);
        
        lastNameField = new JTextField(10);
        gbc.gridx = 3;
        formPanel.add(lastNameField, gbc);  // Correctly adding lastNameField
        

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridwidth = 3;
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        gbc.gridwidth = 1;

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(genderLabel, gbc);

        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");

        maleRadioButton.setOpaque(false);
        femaleRadioButton.setOpaque(false);
        maleRadioButton.setForeground(Color.WHITE);
        femaleRadioButton.setForeground(Color.WHITE);

        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);

        gbc.gridx = 1;
        formPanel.add(maleRadioButton, gbc);

        gbc.gridx = 2;
        formPanel.add(femaleRadioButton, gbc);

    JLabel rollNoLabel = new JLabel("Roll No:");
    rollNoLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 3;
    formPanel.add(rollNoLabel, gbc);

    rollNoField = new JTextField(10);
    gbc.gridx = 1;
    formPanel.add(rollNoField, gbc);

    // Check if user has already registered
    if (userAlreadyRegistered()) {
        // Load user's data
        loadUserData();
    } else {
        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(registerButton, gbc);

        registerButton.addActionListener(e -> register());
    }

        viewMyDataButton = new JButton("View My Data");
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(viewMyDataButton, gbc);

        viewMyDataButton.addActionListener(e -> viewMyData());

        logoutButton = new JButton("Logout");
        gbc.gridx = 2;
        formPanel.add(logoutButton, gbc);

        logoutButton.addActionListener(e -> logout());

        backgroundPanel.add(formPanel);
        add(backgroundPanel);

        setVisible(true);
    }

    private boolean userAlreadyRegistered() {
        String firstName = firstNameField.getText();
        try (PreparedStatement ps = connection.prepareStatement("SELECT roll_no FROM registrationForm WHERE first_name  = ?")) {
            ps.setString(1, firstName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            return false;
        }
    }

    private void loadUserData() {
        int rollNo = Integer.parseInt(rollNoField.getText());
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM registrationForm WHERE roll_no = ?")) {
            ps.setInt(1, rollNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                emailField.setText(rs.getString("email"));
                rollNoField.setText(rs.getString("roll_no"));

                String gender = rs.getString("gender");
                if (gender.equals("Male")) maleRadioButton.setSelected(true);
                else femaleRadioButton.setSelected(true);

                // Disable fields since registration is complete
                firstNameField.setEnabled(false);
                lastNameField.setEnabled(false);
                emailField.setEnabled(false);
                maleRadioButton.setEnabled(false);
                femaleRadioButton.setEnabled(false);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void register() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String gender = maleRadioButton.isSelected() ? "Male" : "Female";
        int rollNo = Integer.parseInt(rollNoField.getText());

        String query = "INSERT INTO registrationForm (first_name, last_name, email, gender, roll_no) VALUES (?, ?, ?, ?, ?)";
        String query1 = "INSERT INTO users (username,password,type) VALUES (?, ?, 'student')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, gender);
            ps.setInt(5, rollNo);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            loadUserData(); // Reload to view-only mode
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        try (PreparedStatement ps1 = connection.prepareStatement(query1)) {
            ps1.setString(1, firstName);
            ps1.setString(2, lastName+rollNo);
            ps1.executeUpdate();       
    }   catch (SQLException er) {
        JOptionPane.showMessageDialog(this, "Error: " + er.getMessage());
        }
    }

    private void viewMyData() {
        JFrame tableFrame = new JFrame("My Data");
        tableFrame.setSize(600, 400);
        tableFrame.setLayout(new BorderLayout());

        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        tableFrame.add(scrollPane, BorderLayout.CENTER);

        int rollno = Integer.parseInt(rollNoField.getText());
        
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM registrationForm WHERE roll_no = ?")) {
            ps.setInt(1, rollno);
            ResultSet rs = ps.executeQuery();

            String[] columnNames = {"First Name", "Last Name", "Email", "Gender", "Roll No"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                int rollNo = rs.getInt("roll_no");

                Object[] rowData = {firstName, lastName, email, gender, rollNo};
                model.addRow(rowData);
            }

            table.setModel(model);
            tableFrame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        dispose();
        new LoginPage();
    }

}

