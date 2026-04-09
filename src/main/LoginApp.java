package main;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginApp {

    private static Connection con;

    public static void main(String[] args) {
        // Initialize Database Connection
        con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Launch the Login UI
        SwingUtilities.invokeLater(LoginApp::createLoginWindow);
    }

    private static void createLoginWindow() {
        JFrame frame = new JFrame("ALASTKA - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(4, 1, 10, 10));

        // Create UI Components
        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email: "));
        JTextField emailField = new JTextField(15);
        emailPanel.add(emailField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password: "));
        JPasswordField passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));

        // Add components to the frame
        frame.add(new JLabel("Welcome to ALASTKA Tourism", SwingConstants.CENTER));
        frame.add(emailPanel);
        frame.add(passwordPanel);
        frame.add(loginBtn);

        // --- LOGIN LOGIC ---
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both Email and Password.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String query = "SELECT UserID, Name, Role FROM Users WHERE Email=? AND Password=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("UserID");
                        String name = rs.getString("Name");
                        String role = rs.getString("Role");
                        JOptionPane.showMessageDialog(frame, "Login Successful! Welcome " + name + "\nRole: " + role);
                        frame.dispose();
                        if ("ADMIN".equalsIgnoreCase(role)) {
                            openAdminDashboard(name);
                        } else {
                            openUserDashboard(userId, name);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid Email or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
    private static void openAdminDashboard(String adminName) {
        AdminDashboard adminDash = new AdminDashboard(adminName);
        adminDash.setVisible(true);
    }
    private static void openUserDashboard(int userId, String userName) {
        TouristDashboard userDash = new TouristDashboard(userId, userName);
        userDash.setVisible(true);
    }
}
