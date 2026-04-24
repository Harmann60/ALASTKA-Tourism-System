package main;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: Explore Places ---
        JPanel explorePanel = new JPanel(new GridLayout(4, 1, 15, 15));
        explorePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewPlacesBtn = new JButton("Explore Tourist Places");
        JButton suggestGemBtn = new JButton("Suggest a Hidden Gem 💎");
        suggestGemBtn.setBackground(new Color(40, 167, 69));
        suggestGemBtn.setForeground(Color.WHITE);

        explorePanel.add(new JLabel("Discover Your Next Adventure", SwingConstants.CENTER));
        explorePanel.add(viewPlacesBtn);
        explorePanel.add(suggestGemBtn);

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
        // --- TAB 3: Reviews & Activities ---
        JPanel activityPanel = new JPanel(new GridLayout(5, 1, 15, 15)); // Changed grid rows from 4 to 5
        activityPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton viewReviewsBtn = new JButton("Read Community Reviews"); // NEW BUTTON
        JButton addReviewBtn = new JButton("Write a Review");
        JButton updateReviewBtn = new JButton("Edit My Review");

        activityPanel.add(new JLabel("Share & Read Experiences", SwingConstants.CENTER));
        activityPanel.add(viewReviewsBtn); // ADDED TO PANEL
        activityPanel.add(addReviewBtn);
        activityPanel.add(updateReviewBtn);

        tabbedPane.addTab("🗺️ Explore", explorePanel);
        tabbedPane.addTab("🏨 Accommodations", accPanel);
        tabbedPane.addTab("⭐ Reviews", activityPanel);

        // --- BOTTOM PANEL FOR LOGOUT ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        bottomPanel.add(logoutBtn);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // ACTION LISTENERS
        // ==========================================

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            LoginApp.main(null);
        });

        // 1. View Tourist Places
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
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
            }
        });

        // 2. Suggest a Hidden Gem
        suggestGemBtn.addActionListener(e -> {
            JTextField locNameField = new JTextField();
            JTextArea descField = new JTextArea(4, 20);
            descField.setLineWrap(true);
            descField.setWrapStyleWord(true);

            Object[] fields = {
                    "Name of the Hidden Location:", locNameField,
                    "Description / Why is it special?:", new JScrollPane(descField)
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Submit a Hidden Gem", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String locName = locNameField.getText().trim();
                String desc = descField.getText().trim();

                if (locName.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Fields cannot be blank! Please provide a name and description.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String insertQuery = "INSERT INTO Hidden_Gems (DiscoveredBy_UserID, LocationName, Description, ApprovalStatus) VALUES (?, ?, ?, 'Pending')";
                try (PreparedStatement ps = con.prepareStatement(insertQuery)) {
                    ps.setInt(1, loggedInUserId);
                    ps.setString(2, locName);
                    ps.setString(3, desc);

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame,
                            "Awesome! Your discovery is submitted to the Admin for review.\nOnce approved, you will earn 50 Explorer Points!",
                            "Submission Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 3. View Accommodations
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

        // 4. Book an Accommodation
        bookAccBtn.addActionListener(e -> {
            int currentPoints = 0;
            try (PreparedStatement pointStmt = con.prepareStatement("SELECT ExplorerPoints FROM Users WHERE UserID = ?")) {
                pointStmt.setInt(1, loggedInUserId);
                ResultSet rsPoints = pointStmt.executeQuery();
                if (rsPoints.next()) {
                    currentPoints = rsPoints.getInt("ExplorerPoints");
                }
            } catch (SQLException ex) {
                System.out.println("Points check failed: " + ex.getMessage());
            }

            JTextField bookingIdField = new JTextField();
            JTextField accIdField = new JTextField();

            // Suggest tomorrow's date as a helpful placeholder
            String tmrw = LocalDate.now().plusDays(1).toString();
            JTextField dateField = new JTextField(tmrw);

            Object[] fields = {
                    "New Booking ID (e.g., 5):", bookingIdField,
                    "Accommodation ID you want to book:", accIdField,
                    "Check-in Date (YYYY-MM-DD):", dateField
            };

            if (JOptionPane.showConfirmDialog(frame, fields, "Book Accommodation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    int bId = Integer.parseInt(bookingIdField.getText().trim());
                    int aId = Integer.parseInt(accIdField.getText().trim());
                    String dateInput = dateField.getText().trim();

                    // === THE TIME TRAVEL CATCHER ===
                    LocalDate bookingDate;
                    try {
                        bookingDate = LocalDate.parse(dateInput);
                        if (bookingDate.isBefore(LocalDate.now())) {
                            JOptionPane.showMessageDialog(frame, "Time travel is not supported! 🚀\nYou cannot book an accommodation in the past.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid Date! Please use exactly YYYY-MM-DD (e.g., " + tmrw + ") and ensure it's a real calendar date.", "Format Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO Booking VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, bId);
                        ps.setString(2, bookingDate.toString());
                        ps.setInt(3, loggedInUserId);
                        ps.setInt(4, aId);

                        ps.executeUpdate();

                        // === CALLING THE MYSQL STORED FUNCTION ===
                        String successMsg = "Booking Confirmed! Have a great trip!";
                        if (currentPoints > 0) {
                            double discount = 0.0;
                            try (PreparedStatement funcStmt = con.prepareStatement("SELECT Calculate_Discount(?) AS DiscountAmt")) {
                                funcStmt.setInt(1, currentPoints);
                                ResultSet rsFunc = funcStmt.executeQuery();
                                if (rsFunc.next()) {
                                    discount = rsFunc.getDouble("DiscountAmt"); // Database does the math!
                                }
                            } catch (SQLException ex) {
                                System.out.println("Function call failed: " + ex.getMessage());
                            }

                            successMsg += "\n\n💎 LOYALTY REWARD APPLIED 💎\nYou used " + currentPoints + " Explorer Points.\nA discount of ₹" + discount + " will be applied at check-in!";
                        }

                        JOptionPane.showMessageDialog(frame, successMsg, "Booking Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(frame, "That Booking ID already exists, or the Accommodation ID is invalid!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "IDs must be valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 5. View Bookings (NOW USING A STORED PROCEDURE!)
        myBookingsBtn.addActionListener(e -> {
            // We use CallableStatement instead of PreparedStatement to call Procedures
            String query = "{CALL Get_Tourist_Itinerary(?)}"; // MAKE SURE THIS MATCHES YOUR PROCEDURE NAME IN MYSQL

            try (CallableStatement cstmt = con.prepareCall(query)) {
                cstmt.setInt(1, loggedInUserId); // Pass the logged-in user's ID

                try (ResultSet rs = cstmt.executeQuery()) {
                    StringBuilder sb = new StringBuilder("Your Bookings via Stored Procedure:\n------------------------------------\n");
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
                JOptionPane.showMessageDialog(frame, "Error fetching itinerary: " + ex.getMessage());
            }
        });

        // 6. Write a Review
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
                try {
                    int rId = Integer.parseInt(reviewIdField.getText().trim());
                    int pId = Integer.parseInt(placeIdField.getText().trim());
                    int rating = Integer.parseInt(ratingField.getText().trim());
                    String comment = commentField.getText().trim();

                    if (rating < 1 || rating > 5) {
                        JOptionPane.showMessageDialog(frame, "Rating must be between 1 and 5!", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO Review VALUES (?, ?, ?, ?, ?)")) {
                        ps.setInt(1, rId);
                        ps.setInt(2, rating);
                        ps.setString(3, comment);
                        ps.setInt(4, loggedInUserId);
                        ps.setInt(5, pId);

                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Thank you for your review!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Review ID, Place ID, and Rating must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
                }
            }
        });

        // 7. Edit My Review
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

                    if (newRating < 1 || newRating > 5) {
                        JOptionPane.showMessageDialog(frame, "Rating must be between 1 and 5!", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

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
                    JOptionPane.showMessageDialog(frame, "Review ID and Rating must be numbers!", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // 8. View All Reviews
        viewReviewsBtn.addActionListener(e -> {
            // We JOIN 3 tables to get the Review, the Place Name, and the User who wrote it
            String query = "SELECT r.Rating, r.Comment, p.PlaceName, u.Name " +
                    "FROM Review r " +
                    "JOIN TouristPlace p ON r.PlaceID = p.PlaceID " +
                    "JOIN Users u ON r.UserID = u.UserID " +
                    "ORDER BY r.ReviewID DESC";

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                StringBuilder sb = new StringBuilder("Community Reviews:\n------------------------------------\n");
                boolean hasReviews = false;

                while (rs.next()) {
                    hasReviews = true;
                    sb.append("📍 ").append(rs.getString("PlaceName"))
                            .append("\n👤 By: ").append(rs.getString("Name"))
                            .append(" | ⭐ ").append(rs.getInt("Rating")).append("/5")
                            .append("\n💬 \"").append(rs.getString("Comment")).append("\"\n\n");
                }

                if (!hasReviews) {
                    sb.append("No reviews yet. Be the first to write one!");
                }

                // Wrap in a JScrollPane so it's scrollable if there are many reviews
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setOpaque(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(frame, scrollPane, "Community Reviews", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}