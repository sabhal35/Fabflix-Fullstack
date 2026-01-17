import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class UpdateSecurePassword {
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        int alterResult = statement.executeUpdate(alterQuery);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");

        String query = "SELECT id, password from customers";

        ResultSet rs = statement.executeQuery(query);

        ArrayList<String> updateQueryList = new ArrayList<>();

        System.out.println("retrieving passwords (no encryption needed)");
        while (rs.next()) {
            String id = rs.getString("id");
            String password = rs.getString("password");
            System.out.printf("User ID: %s, Password: %s%n", id, password);
        }
        rs.close();

        statement.close();
        connection.close();

        System.out.println("finished");

    }
}