import java.sql.*;

public class MainApp {

    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con == null) {
            System.out.println("Connection failed. Exiting...");
            return;
        }

        System.out.println("Connection successful!");

        // ================== SELECT ==================
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Country");

            System.out.println("\n=== Countries ===");
            while (rs.next()) {
                int id = rs.getInt("CountryID");
                String name = rs.getString("CountryName");
                System.out.println(id + " - " + name);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching countries: " + e.getMessage());
        }

        int newUserId = 0;

        // ================== INSERT ==================
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Users(Name, Email, Password) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, "TestUser");
            ps.setString(2, "test@gmail.com");
            ps.setString(3, "pass123");

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newUserId = rs.getInt(1);
            }

            System.out.println("\nUser inserted successfully! ID = " + newUserId);

        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }

        // ================== UPDATE ==================
        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE Users SET Name=? WHERE UserID=?"
            );

            ps.setString(1, "UpdatedUser");
            ps.setInt(2, newUserId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("User updated successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }

        // ================== DELETE ==================
        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Users WHERE UserID=?"
            );

            ps.setInt(1, newUserId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("User deleted successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }

        // ================== CLOSE ==================
        try {
            con.close();
            System.out.println("\nConnection closed.");
        } catch (SQLException e) {
            System.out.println("Error closing connection.");
        }
    }
}
