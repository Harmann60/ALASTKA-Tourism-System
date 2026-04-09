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

            // Secure Database Query using PreparedStatement
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

                        // Close the login window
                        frame.dispose();

                        // Route to the correct dashboard based on Role
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

    // --- DASHBOARD ROUTING STUBS ---

    private static void openAdminDashboard(String adminName) {
        // We will build the actual Admin Dashboard in the next step
        JFrame adminFrame = new JFrame("Admin Dashboard - ALASTKA");
        adminFrame.setSize(400, 300);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.add(new JLabel("Welcome, Admin " + adminName + "!", SwingConstants.CENTER));
        adminFrame.setVisible(true);
    }

    private static void openUserDashboard(int userId, String userName) {
        // We will build the actual User Dashboard in the next step
        JFrame userFrame = new JFrame("Tourist Dashboard - ALASTKA");
        userFrame.setSize(400, 300);
        userFrame.setLocationRelativeTo(null);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userFrame.add(new JLabel("Welcome, Tourist " + userName + "!", SwingConstants.CENTER));
        userFrame.setVisible(true);
    }
}