import java.sql.Connection;

public class MainApp {
    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con != null) {
            System.out.println("Connection successful!");
        }
    }
}
