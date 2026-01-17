import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
public class InsertEmployeeRecord {

    public static void main(String[] args) throws Exception {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://mysql-primary:3306/moviedb";

        String email = "classta@email.edu";
        String plainPassword = "classta";
        String fullname = "TA CS122B";

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        try (Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd)) {

            String insertEmployeeSQL = "INSERT INTO employees (email, password, fullname) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
                preparedStatement.setString(1, email);
//                preparedStatement.setString(2, encryptedPassword);
                preparedStatement.setString(2, plainPassword);
                preparedStatement.setString(3, fullname);

                int result = preparedStatement.executeUpdate();
                System.out.println("Employee inserted successfully, " + result + " row(s) affected.");
            }
        }
    }
}
