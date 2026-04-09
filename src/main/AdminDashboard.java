package main;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard {

    private Connection con;
    private JFrame frame;

    public AdminDashboard(String adminName) {
        con = DBConnection.getConnection();

        frame = new JFrame("Admin Dashboard - ALASTKA Tourism");
        frame.setSize(500, 480);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Ensures we can put tabs in center and logout at bottom

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: User Management ---
        JPanel userPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        userPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton viewUsersBtn = new JButton("View All Users");
        JButton addUserBtn = new JButton("Add New User");
        JButton deleteUserBtn = new JButton("Delete a User");

        userPanel.add(new JLabel("Manage Registered Users", SwingConstants.CENTER));
        userPanel.add(viewUsersBtn);
        userPanel.add(addUserBtn);
        userPanel.add(deleteUserBtn);

        // --- TAB 2: Tourist Place Management ---
        JPanel placePanel = new JPanel(new GridLayout(4, 1, 15, 15));
        placePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewPlaceBtn = new JButton("View All Tourist Places");
        JButton addPlaceBtn = new JButton("Add New Tourist Place");
        JButton deletePlaceBtn = new JButton("Delete Tourist Place");

        placePanel.add(new JLabel("Manage Destinations", SwingConstants.CENTER));
        placePanel.add(viewPlaceBtn);
        placePanel.add(addPlaceBtn);
        placePanel.add(deletePlaceBtn);

        tabbedPane.addTab("👤 User Management", userPanel);
        tabbedPane.addTab("🏛️ Tourist Places", placePanel);

        // --- BOTTOM PANEL FOR LOGOUT ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69)); // Optional: Gives it a nice red danger color
        logoutBtn.setForeground(Color.WHITE);
        bottomPanel.add(logoutBtn);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // ACTION LISTENERS
        // ==========================================

        // Logout Action
        logoutBtn.addActionListener(e -> {
            frame.dispose(); // Closes the current dashboard
            LoginApp.main(null); // Re-opens the Login screen
        });

        // View Users
        viewUsersBtn.addActionListener(e -> {
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Users")) {
                StringBuilder sb = new StringBuilder("Registered Users:\n-------------------\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt(1))
                            .append(" | ").append(rs.getString(2))
                            .append(" (").append(rs.getString("Role")).append(")\n");
                }
                JOptionPane.showMessageDialog(frame, sb.toString(), "User List", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Add New User
        addUserBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            String[] roles = {"USER", "ADMIN"};
            JComboBox<String> roleBox = new JComboBox<>(roles);

            Object[] fields = {
                    "Name:", nameField, "Email:", emailField,
                    "Password:", passwordField, "Assign Role:", roleBox
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Register New User", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleBox.getSelectedItem();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
                if (!email.matches(emailRegex)) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid email address!", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String query = "INSERT INTO Users (Name, Email, Password, Role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, name); ps.setString(2, email);
                    ps.setString(3, password); ps.setString(4, role);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Successfully added new " + role + "!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
                }
            }
        });

        // Delete User
        deleteUserBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(frame, "Enter UserID to delete:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Users WHERE UserID=? AND Role='USER'")) {
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, rows > 0 ? "Tourist Deleted!" : "User not found or cannot delete Admins.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Input or Error: " + ex.getMessage());
            }
        });
        // Add Tourist Place
        addPlaceBtn.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField cityIdField = new JTextField();

            Object[] fields = {
                    "New Place ID (e.g., 8):", idField, "Tourist Place Name:", nameField,
                    "Category (e.g., Historical):", categoryField, "City ID:", cityIdField
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Add Tourist Place", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    // 1. Trigger User-Defined Exception if required fields are empty
                    if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || cityIdField.getText().trim().isEmpty()) {
                        throw new model.TourismException("ID, Name, and City ID cannot be empty!");
                    }

                    // 2. Trigger NumberFormatException if ID or CityID are letters instead of numbers
                    int placeId = Integer.parseInt(idField.getText().trim());
                    int cityId = Integer.parseInt(cityIdField.getText().trim());
                    String name = nameField.getText().trim();
                    String category = categoryField.getText().trim();

                    // 3. Trigger SQLException for Database logic
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO TouristPlace VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, placeId);
                        ps.setString(2, name);
                        ps.setString(3, category);
                        ps.setInt(4, cityId);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Tourist Place Added Successfully!");
                    }

                } catch (model.TourismException ex) {
                    // CATCH 1: Our Custom Exception
                    JOptionPane.showMessageDialog(frame, "Validation Error: " + ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                } catch (NumberFormatException ex) {
                    // CATCH 2: Built-in Java Exception (Number format)
                    JOptionPane.showMessageDialog(frame, "Format Error: Place ID and City ID must be valid numbers!", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    // CATCH 3: Built-in Java Exception (Database error)
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // View Tourist Places
        viewPlaceBtn.addActionListener(e -> {
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT p.PlaceID, p.PlaceName, p.Category, c.CityName " +
                         "FROM TouristPlace p JOIN City c ON p.CityID = c.CityID")) {
                StringBuilder sb = new StringBuilder("Tourist Places:\n-------------------\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt(1)).append(" | ").append(rs.getString(2))
                            .append(" [").append(rs.getString(3)).append("] - in ").append(rs.getString(4)).append("\n");
                }
                JOptionPane.showMessageDialog(frame, sb.toString(), "Place List", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Delete Tourist Place
        deletePlaceBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(frame, "Enter PlaceID to delete:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM TouristPlace WHERE PlaceID=?")) {
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, rows > 0 ? "Tourist Place Deleted!" : "Place not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Input or Error: " + ex.getMessage());
            }
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}