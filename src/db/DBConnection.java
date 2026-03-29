package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ALASTKA",
                "root",//Ayush use this by  entering your user Name 
                "your_password" // and Password here
            );
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
        }
        return con;
    }
}
