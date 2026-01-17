import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "AddStarServlet", urlPatterns = "/AddStarServlet")
public class AddStarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String starName = request.getParameter("starName");
        String birthYearStr = request.getParameter("birthYear");
        Integer birthYear = null;
        if (birthYearStr != null && !birthYearStr.isEmpty()) {
            birthYear = Integer.parseInt(birthYearStr);
        }

        String generatedStarId = null;
        try (Connection conn = DatabaseConnectionPool.getMasterConnection()) {
            generatedStarId = generateNewId("stars", "st", conn);

            String insertQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
                statement.setString(1, generatedStarId);
                statement.setString(2, starName);
                if (birthYear != null) {
                    statement.setInt(3, birthYear);
                } else {
                    statement.setNull(3, java.sql.Types.INTEGER);
                }
                statement.executeUpdate();
            }

            request.setAttribute("starMessage", "Star is added successfully with ID: " + generatedStarId);
        } catch (Exception e) {
            request.setAttribute("starMessage", "Error: " + e.getMessage());
        }

        request.getRequestDispatcher("AddStar.jsp").forward(request, response);
    }

    private String generateNewId(String tableName, String prefix, Connection conn) throws SQLException {
        String newId = null;
        String query = "SELECT MAX(id) FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String maxId = rs.getString(1);
                int idNum = Integer.parseInt(maxId.substring(prefix.length())) + 1;
                newId = prefix + String.format("%08d", idNum);
            } else {
                newId = prefix + "00000001";
            }
        }
        return newId;
    }
}