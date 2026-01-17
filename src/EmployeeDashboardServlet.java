import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/_dashboard")
public class EmployeeDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, List<String[]>> metadata = new HashMap<>();

        try (Connection connection = DatabaseConnectionPool.getSlaveConnection()) {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            String[] tableTypes = {"TABLE"};

            try (ResultSet tables = dbMetaData.getTables(null, null, "%", tableTypes)) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    if (!metadata.containsKey(tableName)) {
                        List<String[]> columns = new ArrayList<>();

                        try (ResultSet columnsResultSet = dbMetaData.getColumns(null, null, tableName, "%")) {
                            while (columnsResultSet.next()) {
                                String columnName = columnsResultSet.getString("COLUMN_NAME");
                                String columnType = columnsResultSet.getString("TYPE_NAME");
                                columns.add(new String[]{columnName, columnType});
                            }
                        }
                        metadata.put(tableName, columns);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("metadata", metadata);

        request.getRequestDispatcher("/EmployeeDashboard.jsp").forward(request, response);
    }
}