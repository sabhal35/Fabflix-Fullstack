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
import java.util.List;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String genre = request.getParameter("genre");
        String startChar = request.getParameter("startChar");
        String sort = request.getParameter("sort");
        String limitParam = request.getParameter("limit");
        int limit = 10;
        if (limitParam != null) {
            limit = Integer.parseInt(limitParam);
        }

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        int offset = (page - 1) * limit;

        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getSlaveConnection()) {
            if (title != null && !title.isEmpty()) {
                movies = getMoviesBySearch(conn, title, sort, limit, offset);
            } else if (genre != null) {
                movies = getMoviesByGenre(conn, genre, sort, limit, offset);
            } else if (startChar != null) {
                movies = getMoviesByTitle(conn, startChar, sort, limit, offset);
            } else {
                movies = getTopMovies(conn, sort, limit, offset);
            }

            request.setAttribute("movies", movies);
            request.setAttribute("currentPage", page);
            request.setAttribute("limit", limit);
            request.getRequestDispatcher("/MovieList.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error fetching movie data", e);
        }
    }

    private List<Star> parseStars(String starsStr) {
        List<Star> stars = new ArrayList<>();
        if (starsStr != null && !starsStr.isEmpty()) {
            for (String star : starsStr.split(",")) {
                String[] parts = star.split(":");
                if (parts.length == 2) {
                    stars.add(new Star(parts[0], parts[1]));
                }
            }
        }
        return stars;
    }

    private List<Movie> getMoviesByGenre(Connection conn, String genre, String sort, int limit, int offset) throws Exception {
        String query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name) as stars " +
                "FROM movies m " +
                "LEFT JOIN ratings r ON m.id = r.movieId " +
                "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                "LEFT JOIN genres g ON gm.genreId = g.id " +
                "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                "LEFT JOIN stars s ON sm.starId = s.id " +
                "GROUP BY m.id, m.title, m.year, m.director, r.rating " +
                "HAVING GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) LIKE ? ";

        query += getSortingClause(sort);

        query += "LIMIT ? OFFSET ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, "%" + genre + "%");
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            return executeMovieQuery(statement);
        }
    }

    private List<Movie> getMoviesByTitle(Connection conn, String startChar, String sort, int limit, int offset) throws Exception {
        String query;
        if (startChar.equals("*")) {
            query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY " +
                    "(SELECT COUNT(sm2.movieId) FROM stars_in_movies sm2 WHERE sm2.starId = s.id) DESC, s.name ASC) as stars " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN genres g ON gm.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN stars s ON sm.starId = s.id " +
                    "WHERE m.title REGEXP '^[^a-zA-Z0-9]' " +
                    "GROUP BY m.id, m.title, m.year, m.director, r.rating ";
        } else {
            query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY " +
                    "(SELECT COUNT(sm2.movieId) FROM stars_in_movies sm2 WHERE sm2.starId = s.id) DESC, s.name ASC) as stars " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN genres g ON gm.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN stars s ON sm.starId = s.id " +
                    "WHERE m.title LIKE ? " +
                    "GROUP BY m.id, m.title, m.year, m.director, r.rating ";
        }

        query += getSortingClause(sort);

        query += "LIMIT ? OFFSET ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            if (!startChar.equals("*")) {
                statement.setString(1, startChar + "%");
                statement.setInt(2, limit);
                statement.setInt(3, offset);
            } else {
                statement.setInt(1, limit);
                statement.setInt(2, offset);
            }
            return executeMovieQuery(statement);
        }
    }

    private List<Movie> getMoviesBySearch(Connection conn, String title, String sort, int limit, int offset) throws Exception {
        String query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY " +
                "(SELECT COUNT(sm2.movieId) FROM stars_in_movies sm2 WHERE sm2.starId = s.id) DESC, s.name ASC) as stars " +
                "FROM movies m " +
                "LEFT JOIN ratings r ON m.id = r.movieId " +
                "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                "LEFT JOIN genres g ON gm.genreId = g.id " +
                "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                "LEFT JOIN stars s ON sm.starId = s.id " +
                "WHERE LOWER(m.title) LIKE LOWER(?) " +
                "GROUP BY m.id, m.title, m.year, m.director, r.rating ";

        query += getSortingClause(sort);
        query += "LIMIT ? OFFSET ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, "%" + title + "%");
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            return executeMovieQuery(statement);
        }
    }

    private List<Movie> getTopMovies(Connection conn, String sort, int limit, int offset) throws Exception {
        String query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) as genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name) as stars " +
                "FROM movies m " +
                "LEFT JOIN ratings r ON m.id = r.movieId " +
                "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                "LEFT JOIN genres g ON gm.genreId = g.id " +
                "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                "LEFT JOIN stars s ON sm.starId = s.id " +
                "WHERE r.rating IS NOT NULL " +
                "GROUP BY m.id, m.title, m.year, m.director, r.rating ";

        query += getSortingClause(sort);
        
        query += "LIMIT ? OFFSET ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            return executeMovieQuery(statement);
        }
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

    private String getSortingClause(String sort) {
        if ("title_asc_rating_asc".equals(sort)) {
            return "ORDER BY m.title ASC, r.rating ASC ";
        } else if ("title_asc_rating_desc".equals(sort)) {
            return "ORDER BY m.title ASC, r.rating DESC ";
        } else if ("title_desc_rating_asc".equals(sort)) {
            return "ORDER BY m.title DESC, r.rating ASC ";
        } else if ("title_desc_rating_desc".equals(sort)) {
            return "ORDER BY m.title DESC, r.rating DESC ";
        } else if ("rating_asc_title_asc".equals(sort)) {
            return "ORDER BY r.rating ASC, m.title ASC ";
        } else if ("rating_asc_title_desc".equals(sort)) {
            return "ORDER BY r.rating ASC, m.title DESC ";
        } else if ("rating_desc_title_asc".equals(sort)) {
            return "ORDER BY r.rating DESC, m.title ASC ";
        } else if ("rating_desc_title_desc".equals(sort)) {
            return "ORDER BY r.rating DESC, m.title DESC ";
        }
        return "ORDER BY m.title ASC, r.rating ASC ";
    }


    private List<Movie> executeMovieQuery(PreparedStatement statement) throws Exception {
        List<Movie> movies = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getString("id"));
                movie.setTitle(rs.getString("title"));
                movie.setYear(rs.getInt("year"));
                movie.setDirector(rs.getString("director"));
                movie.setRating(rs.getDouble("rating"));

                movie.setGenres(String.join(", ", limitGenres(rs.getString("genres"))));
                movie.setStars(limitStars(rs.getString("stars")));

                movies.add(movie);
            }
        }
        return movies;
    }
}