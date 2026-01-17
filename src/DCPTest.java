import java.sql.Connection;
import java.sql.SQLException;

public class DCPTest {
    public static void main(String[] args) {
        try (Connection masterConn = DatabaseConnectionPool.getMasterConnection()) {
            System.out.println("Connected to master database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection slaveConn = DatabaseConnectionPool.getSlaveConnection()) {
            System.out.println("Connected to slave database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
