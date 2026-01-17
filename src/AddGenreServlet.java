import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "AddGenreServlet", urlPatterns = "/AddGenreServlet")
public class AddGenreServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String genreName = request.getParameter("genreName");
        String message;

        if (genreName == null || genreName.trim().isEmpty()) {
            message = "Genre name cannot be empty.";
        } else {
            boolean genreAdded = addGenre(genreName.trim());

            if (genreAdded) {
                message = "Genre added successfully!: " + genreName;
            } else {
                message = "Genre already exists or an error occurred.";
            }
        }

        request.setAttribute("genreMessage", message);
        request.getRequestDispatcher("/AddGenre.jsp").forward(request, response);
    }

    private boolean addGenre(String genreName) {
        boolean success = false;

        try (Connection connection = DatabaseConnectionPool.getMasterConnection()) {
            String checkQuery = "SELECT id FROM genres WHERE name = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, genreName);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }

            String insertQuery = "INSERT INTO genres (name) VALUES (?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, genreName);
                success = insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}
