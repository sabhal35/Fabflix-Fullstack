import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MetadataServlet", urlPatterns = "/MetadataServlet")
public class MetadataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, List<String[]>> metadata = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnectionPool.getSlaveConnection()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet tables = dbMetaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                List<String[]> columns = new ArrayList<>();

                ResultSet columnsResult = dbMetaData.getColumns(null, null, tableName, "%");
                while (columnsResult.next()) {
                    String columnName = columnsResult.getString("COLUMN_NAME");
                    String columnType = columnsResult.getString("TYPE_NAME");
                    columns.add(new String[]{columnName, columnType});
                }
                columnsResult.close();
                metadata.put(tableName, columns);
            }
            tables.close();
        } catch (SQLException e) {
            throw new ServletException("Cannot retrieve database metadata", e);
        }

        request.setAttribute("metadata", metadata);
        request.getRequestDispatcher("EmployeeDashboard.jsp").forward(request, response);
    }
}