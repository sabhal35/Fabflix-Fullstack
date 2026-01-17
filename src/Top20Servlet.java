import domain.Movie;
import domain.Star;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "Top20Servlet", urlPatterns = "/Top20Servlet")
public class Top20Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sort = request.getParameter("sort");
        try (Connection conn = DatabaseConnectionPool.getSlaveConnection()) {
            List<Movie> topMovies = getTop20Movies(conn, sort);
            request.setAttribute("topMovies", topMovies);
            request.getRequestDispatcher("/Top20.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error fetching top 20 movies", e);
        }
    }

    private List<Movie> getTop20Movies(Connection conn, String sort) throws Exception {
        String query = "SELECT m.id, m.title, m.year, m.director, COALESCE(r.rating, 0) as rating, " +
                "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name) as stars " +
                "FROM movies m " +
                "LEFT JOIN ratings r ON m.id = r.movieId " +
                "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                "LEFT JOIN genres g ON gm.genreId = g.id " +
                "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                "LEFT JOIN stars s ON sm.starId = s.id " +
                "GROUP BY m.id, m.title, m.year, m.director, r.rating " +
                "ORDER BY r.rating DESC " +
                "LIMIT 20;";

        List<Movie> topMovies = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getString("id"));
                movie.setTitle(rs.getString("title"));
                movie.setYear(rs.getInt("year"));
                movie.setDirector(rs.getString("director"));
                movie.setRating(rs.getDouble("rating"));
                movie.setGenres(String.join(", ", limitGenres(rs.getString("genres"))));
                movie.setStars(limitStars(rs.getString("stars")));
                topMovies.add(movie);
            }
        }

        if (sort != null) {
            switch (sort) {
                case "title_asc":
                    topMovies.sort(Comparator.comparing(Movie::getTitle));
                    break;
                case "title_desc":
                    topMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
                    break;
                case "rating_asc":
                    topMovies.sort(Comparator.comparing(Movie::getRating));
                    break;
                case "rating_desc":
                    topMovies.sort(Comparator.comparing(Movie::getRating).reversed());
                    break;
                default:
                    break;
            }
        }

        return topMovies;
    }

    private List<String> limitGenres(String genresStr) {
        List<String> genres = new ArrayList<>();
        if (genresStr != null && !genresStr.isEmpty()) {
            String[] allGenres = genresStr.split(",");
            for (int i = 0; i < Math.min(allGenres.length, 3); i++) {
                genres.add(allGenres[i]);
            }
        }
        return genres;
    }

    private List<Star> limitStars(String starsStr) {
        List<Star> stars = new ArrayList<>();
        if (starsStr != null && !starsStr.isEmpty()) {
            String[] allStars = starsStr.split(",");
            for (int i = 0; i < Math.min(allStars.length, 3); i++) {
                String[] parts = allStars[i].split(":");
                if (parts.length == 2) {
                    stars.add(new Star(parts[0], parts[1]));
                }
            }
        }
        return stars;
    }
}