package main;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TouristDashboard {

    private Connection con;
    private JFrame frame;
    private int loggedInUserId;

    public TouristDashboard(int userId, String userName) {
        this.loggedInUserId = userId;
        con = DBConnection.getConnection();

        frame = new JFrame("Tourist Dashboard - Welcome " + userName);
        frame.setSize(550, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Ensures we can add the logout button at the bottom

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: Explore Places ---
        JPanel explorePanel = new JPanel(new GridLayout(3, 1, 15, 15));
        explorePanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton viewPlacesBtn = new JButton("Explore Tourist Places");
        explorePanel.add(new JLabel("Discover Your Next Adventure", SwingConstants.CENTER));
        explorePanel.add(viewPlacesBtn);

        // --- TAB 2: Accommodations & Bookings ---
        JPanel accPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        accPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewAccBtn = new JButton("View Hotels & Resorts");
        JButton bookAccBtn = new JButton("Book an Accommodation");
        JButton myBookingsBtn = new JButton("View My Bookings");

        accPanel.add(viewAccBtn);
        accPanel.add(bookAccBtn);
        accPanel.add(myBookingsBtn);

        // --- TAB 3: Reviews & Activities ---
        JPanel activityPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        activityPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton addReviewBtn = new JButton("Write a Review");
        JButton updateReviewBtn = new JButton("Edit My Review");

        activityPanel.add(new JLabel("Share Your Experience", SwingConstants.CENTER));
        activityPanel.add(addReviewBtn);
        activityPanel.add(updateReviewBtn);

        tabbedPane.addTab("🗺️ Explore", explorePanel);
        tabbedPane.addTab("🏨 Accommodations", accPanel);
        tabbedPane.addTab("⭐ Reviews", activityPanel);

        // --- BOTTOM PANEL FOR LOGOUT ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69)); // Nice red danger color
        logoutBtn.setForeground(Color.WHITE);
        bottomPanel.add(logoutBtn);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // ACTION LISTENERS
        // ==========================================

        // Logout Action
        logoutBtn.addActionListener(e -> {
            frame.dispose(); // Closes the dashboard
            LoginApp.main(null); // Re-opens the Login screen
        });

        // View Tourist Places
        viewPlacesBtn.addActionListener(e -> {
            String query = "SELECT p.PlaceName, p.Category, c.CityName, co.CountryName " +
                    "FROM TouristPlace p " +
                    "JOIN City c ON p.CityID = c.CityID " +
                    "JOIN Country co ON c.CountryID = co.CountryID";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                StringBuilder sb = new StringBuilder("Must-Visit Places:\n------------------------------------\n");
                while (rs.next()) {
                    sb.append("📍 ").append(rs.getString("PlaceName"))
                            .append(" (").append(rs.getString("Category")).append(")\n")
                            .append("   Location: ").append(rs.getString("CityName")).append(", ").append(rs.getString("CountryName")).append("\n\n");
                }
                JOptionPane.showMessageDialog(frame, sb.toString(), "Explore Places", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // View Accommodations
        viewAccBtn.addActionListener(e -> {
            String query = "SELECT a.AccommodationID, a.AccommodationName, a.Type, a.PricePerNight, c.CityName " +
                    "FROM Accommodation a JOIN City c ON a.CityID = c.CityID";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                StringBuilder sb = new StringBuilder("Available Accommodations:\n------------------------------------\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("AccommodationID")).append(" | 🏨 ").append(rs.getString("AccommodationName"))
                            .append(" [").append(rs.getString("Type")).append("]\n")
                            .append("   City: ").append(rs.getString("CityName"))
                            .append(" | Price: ₹").append(rs.getInt("PricePerNight")).append(" / night\n\n");
                }
                JOptionPane.showMessageDialog(frame, sb.toString(), "Accommodations", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Book an Accommodation
        bookAccBtn.addActionListener(e -> {
            JTextField bookingIdField = new JTextField();
            JTextField accIdField = new JTextField();
            JTextField dateField = new JTextField("YYYY-MM-DD");

            Object[] fields = {
                    "New Booking ID (e.g., 5):", bookingIdField,
                    "Accommodation ID you want to book:", accIdField,
                    "Check-in Date (YYYY-MM-DD):", dateField
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Book Accommodation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO Booking VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, Integer.parseInt(bookingIdField.getText().trim()));
                    ps.setString(2, dateField.getText().trim());
                    ps.setInt(3, loggedInUserId);
                    ps.setInt(4, Integer.parseInt(accIdField.getText().trim()));

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Booking Confirmed! Have a great trip!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error booking accommodation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Jhanvi View Bookings
        myBookingsBtn.addActionListener(e -> {
            String query = "SELECT b.BookingID, b.BookingDate, a.AccommodationName, c.CityName " +
                    "FROM Booking b " +
                    "JOIN Accommodation a ON b.AccommodationID = a.AccommodationID " +
                    "JOIN City c ON a.CityID = c.CityID " +
                    "WHERE b.UserID = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, loggedInUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder("Your Bookings:\n------------------------------------\n");
                    boolean hasBookings = false;
                    while (rs.next()) {
                        hasBookings = true;
                        sb.append("Booking ID: ").append(rs.getInt("BookingID"))
                                .append("\n🏨 ").append(rs.getString("AccommodationName"))
                                .append(" in ").append(rs.getString("CityName"))
                                .append("\n📅 Date: ").append(rs.getString("BookingDate")).append("\n\n");
                    }
                    if (!hasBookings) {
                        sb.append("You have no bookings yet. Go explore!");
                    }
                    JOptionPane.showMessageDialog(frame, sb.toString(), "My Bookings", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error fetching bookings: " + ex.getMessage());
            }
        });

        // Indrani Write a Review
        addReviewBtn.addActionListener(e -> {
            JTextField reviewIdField = new JTextField();
            JTextField ratingField = new JTextField();
            JTextField commentField = new JTextField();
            JTextField placeIdField = new JTextField();

            Object[] fields = {
                    "New Review ID (e.g., 4):", reviewIdField,
                    "Place ID you visited (e.g., 1):", placeIdField,
                    "Rating (1 to 5):", ratingField,
                    "Your Comment:", commentField
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Write a Review", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO Review VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, Integer.parseInt(reviewIdField.getText()));
                    ps.setInt(2, Integer.parseInt(ratingField.getText()));
                    ps.setString(3, commentField.getText());
                    ps.setInt(4, loggedInUserId);
                    ps.setInt(5, Integer.parseInt(placeIdField.getText()));

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Thank you for your review!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error adding review: " + ex.getMessage());
                }
            }
        });

        // Ayush this is edit part My Review
        updateReviewBtn.addActionListener(e -> {
            JTextField reviewIdField = new JTextField();
            JTextField ratingField = new JTextField();
            JTextField commentField = new JTextField();

            Object[] fields = {
                    "Enter the Review ID you want to edit:", reviewIdField,
                    "New Rating (1 to 5):", ratingField,
                    "New Comment:", commentField
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Edit My Review", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    int reviewId = Integer.parseInt(reviewIdField.getText().trim());
                    int newRating = Integer.parseInt(ratingField.getText().trim());
                    String newComment = commentField.getText().trim();

                    String updateQuery = "UPDATE Review SET Rating=?, Comment=? WHERE ReviewID=? AND UserID=?";
                    try (PreparedStatement ps = con.prepareStatement(updateQuery)) {
                        ps.setInt(1, newRating);
                        ps.setString(2, newComment);
                        ps.setInt(3, reviewId);
                        ps.setInt(4, loggedInUserId);

                        int rowsAffected = ps.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Review updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Update failed! Either the Review ID is wrong, or you do not have permission to edit this review.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input! Please enter numbers for ID and Rating.", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}