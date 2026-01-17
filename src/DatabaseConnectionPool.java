import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionPool {

    private static final boolean IS_STANDALONE = true;
    private static final String MASTER_URL = "jdbc:mysql://mysql-primary:3306/moviedb?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String SLAVE_URL = "jdbc:mysql://mysql-secondary:3306/moviedb?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "mytestuser";
    private static final String DB_PASSWORD = "My6$Password";

    public static Connection getMasterConnection() throws SQLException {
        return getConnection("java:/comp/env/jdbc/moviedb_master", MASTER_URL);
    }

    public static Connection getSlaveConnection() throws SQLException {
        return getConnection("java:/comp/env/jdbc/moviedb_slave", SLAVE_URL);
    }

    private static Connection getConnection(String jndiName, String fallbackUrl) throws SQLException {
        if (IS_STANDALONE) {
            return getDirectConnection(fallbackUrl);
        } else {
            return getJndiConnection(jndiName);
        }
    }

    private static Connection getDirectConnection(String url) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        System.out.println("Using direct JDBC for: " + url);
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    private static Connection getJndiConnection(String jndiName) throws SQLException {
        try {
            Context initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup(jndiName);
            System.out.println("JNDI lookup successful for: " + jndiName);
            return ds.getConnection();
        } catch (NamingException e) {
            throw new SQLException("Failed to obtain connection for: " + jndiName, e);
        }
    }
}
