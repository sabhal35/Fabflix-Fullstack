import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    private DataSource source;

    @Override
    public void init() throws ServletException {
        try {
            source = (DataSource) new javax.naming.InitialContext().lookup("java:comp/env/jdbc/moviedb_slave");
        } catch (Exception e) {
            throw new ServletException("Unable to initialize DataSource for autocomplete", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        if (query == null || query.length() < 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Map<String, String>> suggestions = new ArrayList<>();

        try (Connection conn = DatabaseConnectionPool.getSlaveConnection()) {
            String[] tokens = query.split("\\s+");
            StringBuilder sql = new StringBuilder(
                    "SELECT DISTINCT m.id, m.title FROM movies m WHERE "
            );

            List<Object> parameters = new ArrayList<>();

            for (int i = 0; i < tokens.length; i++) {
                if (i > 0) sql.append(" OR ");
                sql.append("m.title LIKE ?");
                String token = tokens[i].replace("'", "''");
                parameters.add("%" + token + "%");
            }

            sql.append(" LIMIT 10");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            for (Object param : parameters) {
                stmt.setString(paramIndex++, (String) param);
            }

            System.out.println("Generated SQL Query: " + sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> suggestion = new HashMap<>();
                suggestion.put("value", rs.getString("title"));
                suggestion.put("data", rs.getString("id"));
                suggestions.add(suggestion);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setContentType("application/json");
        new Gson().toJson(suggestions, response.getWriter());
    }

}