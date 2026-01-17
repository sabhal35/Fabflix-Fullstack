import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String movieId = request.getParameter("id");
        if (movieId == null || movieId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Missing movie ID\"}");
            return;
        }

        try (Connection conn = DatabaseConnectionPool.getSlaveConnection()) {
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                    "(SELECT GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') " +
                    " FROM genres g " +
                    " JOIN genres_in_movies gm ON g.id = gm.genreId " +
                    " WHERE gm.movieId = m.id " +
                    " ORDER BY g.name) AS genres, " +
                    "(SELECT GROUP_CONCAT(CONCAT(top_stars.starId, ':', top_stars.starName) " +
                    " ORDER BY top_stars.movie_count DESC, top_stars.starName ASC SEPARATOR ', ') " +
                    " FROM (SELECT s.id AS starId, s.name AS starName, COUNT(sm.movieId) AS movie_count " +
                    " FROM stars s " +
                    " JOIN stars_in_movies sm ON s.id = sm.starId " +
                    " WHERE sm.movieId = m.id " +
                    " GROUP BY s.id, s.name " +
                    " ORDER BY movie_count DESC, s.name ASC) AS top_stars) AS stars " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN genres g ON gm.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN stars s ON sm.starId = s.id " +
                    "WHERE m.id = ? " +
                    "GROUP BY m.id, m.title, m.year, m.director, r.rating";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, movieId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        JsonObject movie = new JsonObject();
                        movie.addProperty("id", rs.getString("id"));
                        movie.addProperty("title", rs.getString("title"));
                        movie.addProperty("year", rs.getString("year"));
                        movie.addProperty("director", rs.getString("director"));
                        movie.addProperty("rating", rs.getString("rating"));
                        movie.addProperty("genres", rs.getString("genres"));

                        JsonArray array = new JsonArray();
                        String[] starPairs = rs.getString("stars").split(", ");
                        for (String pair : starPairs) {
                            String[] parts = pair.split(":");
                            if (parts.length == 2) {
                                JsonObject star = new JsonObject();
                                star.addProperty("id", parts[0]);
                                star.addProperty("name", parts[1]);
                                array.add(star);
                            }
                        }
                        movie.add("stars", array);

                        out.write(movie.toString());
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.write("{\"error\": \"Movie not found\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}