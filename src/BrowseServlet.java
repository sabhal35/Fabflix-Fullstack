import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@WebServlet(name = "BrowseServlet", urlPatterns = "/Browse")
public class BrowseServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(BrowseServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if (type != null && type.equals("genre")) {
            List<String> genres = getGenresFromDatabase();
            if (genres.isEmpty()) {
                response.sendRedirect("MainPage.jsp?error=NoGenres");
            } else {
                request.setAttribute("genres", genres);
                request.getRequestDispatcher("BrowseByGenre.jsp").forward(request, response);
            }

        } else if (type != null && type.equals("title")) {
            List<String> characters = getAlphanumericCharacters();
            LOGGER.info("Fetched alphanumeric characters for titles.");
            request.setAttribute("characters", characters);
            request.getRequestDispatcher("BrowseByTitle.jsp").forward(request, response);
        } else {
            response.sendRedirect("MainPage.jsp");
        }
    }

    private List<String> getGenresFromDatabase() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT name FROM genres ORDER BY name ASC";

        try (Connection conn = DatabaseConnectionPool.getSlaveConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                genres.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            LOGGER.severe("Database error occurred while fetching genres: " + e.getMessage());
        }

        return genres;
    }

    private List<String> getAlphanumericCharacters() {
        List<String> characters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            characters.add(String.valueOf(c));
        }
        for (char c = '0'; c <= '9'; c++) {
            characters.add(String.valueOf(c));
        }
        characters.add("*");

        return characters;
    }
}
