import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class RegistrationForm extends JFrame {

    private JTextField firstNameField, lastNameField, emailField, rollNoField;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private JButton registerButton, viewAllButton, updateButton, deleteButton, findButton, resetButton, logoutButton;
    private BufferedImage backgroundImage;
    private ButtonGroup genderGroup;
    private static Connection connection;

    // Connect to database function
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public RegistrationForm() {
        connectToDatabase();

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo Loq\\Desktop\\java\\images\\tp.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1792, 1080);
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
        formPanel.add(lastNameField, gbc);

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

        Font radioButtonFont = new Font("Arial", Font.BOLD, 16);
        maleRadioButton.setFont(radioButtonFont);
        femaleRadioButton.setFont(radioButtonFont);
        maleRadioButton.setForeground(Color.WHITE);
        femaleRadioButton.setForeground(Color.WHITE);

        // Set a translucent background color
        maleRadioButton.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black
        femaleRadioButton.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black

        maleRadioButton.setOpaque(true);
        femaleRadioButton.setOpaque(true);

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

        findButton = new JButton("Find");
        gbc.gridx = 2;
        formPanel.add(findButton, gbc);

        resetButton = new JButton("Reset");
        gbc.gridx = 3;
        formPanel.add(resetButton, gbc);

        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(registerButton, gbc);

        viewAllButton = new JButton("View All");
        gbc.gridx = 1;
        formPanel.add(viewAllButton, gbc);

        updateButton = new JButton("Update");
        gbc.gridx = 2;
        formPanel.add(updateButton, gbc);

        deleteButton = new JButton("Delete");
        gbc.gridx = 3;
        formPanel.add(deleteButton, gbc);

        logoutButton = new JButton("Logout");
        gbc.gridx = 0; // Set a different position for the button, e.g., column 0
        gbc.gridy = 5; // Adjust row position
        gbc.gridwidth = 4; // Make the button span across multiple columns if needed
        formPanel.add(logoutButton, gbc);
        

        backgroundPanel.add(formPanel);
        add(backgroundPanel);

        // Action Listeners for the buttons
        registerButton.addActionListener(e -> register());
        viewAllButton.addActionListener(e -> viewAll());
        updateButton.addActionListener(e -> update());
        deleteButton.addActionListener(e -> delete());
        findButton.addActionListener(e -> find());
        resetButton.addActionListener(e -> resetFields());
        logoutButton.addActionListener(e -> logout());
        

        setVisible(true);
        connectToDatabase();
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
            resetFields();
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

    private void viewAll() {
        JFrame tableFrame = new JFrame("All Registrations");
        tableFrame.setSize(600, 400);
        tableFrame.setLayout(new BorderLayout());

        //dropdown for sorting options
        String[] sortOptions = {"Sort by Roll No", "Sort by Name"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        // Table to display data
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        tableFrame.add(scrollPane, BorderLayout.CENTER);

        // Add sort dropdown 
        tableFrame.add(sortComboBox, BorderLayout.NORTH);

        sortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSortOption = (String) sortComboBox.getSelectedItem();
                if (selectedSortOption.equals("Sort by Roll No")) {
                    loadSortedData(table, "roll_no");
                } else if (selectedSortOption.equals("Sort by Name")) {
                    loadSortedData(table, "first_name");
                }
            }
        });

        //sorted by Roll
        loadSortedData(table, "roll_no");

        tableFrame.setVisible(true);
    }

    private void loadSortedData(JTable table, String sortByColumn) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root")) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM registrationForm ORDER BY " + sortByColumn + " ASC");
            ResultSet rs = ps.executeQuery();

            //model to display the sorted results
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

            // Update model with the sorted data
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void update() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String gender = maleRadioButton.isSelected() ? "Male" : "Female";
        int rollno = Integer.parseInt(rollNoField.getText());

        String updateQuery = "UPDATE registrationForm SET first_name=?, last_name=?, email=?, gender=? WHERE roll_no=?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, gender);
            preparedStatement.setInt(5, rollno);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Record updated successfully!");
                resetFields(); 
            } else {
                JOptionPane.showMessageDialog(this, "No record found with the specified roll number.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

    }

    private void delete() {
        String rollno = rollNoField.getText();

        String deleteQuery = "DELETE FROM registrationForm WHERE roll_no = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setString(1, rollno);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!");
                resetFields();  
            } else {
                JOptionPane.showMessageDialog(this, "No record found with the specified roll number.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void find() {
        String rollNo = rollNoField.getText();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/university", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM registrationForm WHERE roll_no = ?")) {

            preparedStatement.setString(1, rollNo);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String gender = resultSet.getString("gender");

                firstNameField.setText(firstName);
                lastNameField.setText(lastName);
                emailField.setText(email);

                if (gender.equalsIgnoreCase("male")) {
                    maleRadioButton.setSelected(true);
                } else {
                    femaleRadioButton.setSelected(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No record found with the specified roll number.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

    }

    private void resetFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        rollNoField.setText("");
        genderGroup.clearSelection();
    }
    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        dispose();
        new LoginPage();
    }
}
