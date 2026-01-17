import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerifyPassword {

    public static void main(String[] args) throws Exception {

        System.out.println(verifyCredentials("classta@email.edu", "classta"));
        System.out.println(verifyCredentials("a@email.com", "a3"));

    }

    private static boolean verifyCredentials(String email, String password) throws Exception {

        Connection connection = DatabaseConnectionPool.getSlaveConnection();
        Statement statement = connection.createStatement();

        String query = String.format("SELECT * from employees where email='%s'", email);

        ResultSet rs = statement.executeQuery(query);

        boolean success = false;
        if (rs.next()) {
            String storedPassword = rs.getString("password");
            success = password.equals(storedPassword);
        }

        rs.close();
        statement.close();
        connection.close();

        System.out.println("verify " + email + " - " + password);

        return success;
    }
}