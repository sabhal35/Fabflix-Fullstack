import domain.Movie;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import domain.Star;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "SearchServlet", urlPatterns = "/SearchServlet")
public class SearchServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SearchServlet.class.getName());

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long servletStartTime = System.currentTimeMillis();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String sort = request.getParameter("sort");

        if ("null".equals(genre) || genre == null) {
            genre = "";
        }
        if ("null".equals(year) || year == null) {
            year = "";
        }
        if ("null".equals(director) || director == null) {
            director = "";
        }
        if ("null".equals(star) || star == null) {
            star = "";
        }
        if ("null".equals(title) || title == null) {
            title = "";
        }
        if ("null".equals(sort) || sort == null) {
            sort = "title_asc";
        }

        if (title.isEmpty() && year.isEmpty() && director.isEmpty() && star.isEmpty() && genre.isEmpty()) {
            response.setContentType("text/html");
            response.getWriter().println("<script type='text/javascript'>");
            response.getWriter().println("alert('Please enter at least one search field.');");
            response.getWriter().println("location='MainPage.jsp';");
            response.getWriter().println("</script>");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("searchTitle", title);
        session.setAttribute("searchYear", year);
        session.setAttribute("searchDirector", director);
        session.setAttribute("searchStar", star);
        session.setAttribute("searchGenre", genre);
        session.setAttribute("searchSort", sort);

        int page = 1;
        int limit = 10;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        if (request.getParameter("limit") != null) {
            limit = Integer.parseInt(request.getParameter("limit"));
        }
        int offset = (page - 1) * limit;

        session.setAttribute("searchLimit", limit);
        session.setAttribute("searchPage", page);

        try {
            long jdbcStartTime = System.currentTimeMillis();
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://mysql-secondary:3306/moviedb?useSSL=false&allowPublicKeyRetrieval=true";
            String username = "mytestuser";
            String password = "My6$Password";
            Connection conn = DriverManager.getConnection(url, username, password);

            long jdbcEndTime = System.currentTimeMillis();
            LOGGER.info("JDBC Connection Time: " + (jdbcEndTime - jdbcStartTime) + "ms");

            StringBuilder query = new StringBuilder(
                    "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                            "(SELECT GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') " +
                            " FROM genres g " +
                            " JOIN genres_in_movies gm ON g.id = gm.genreId " +
                            " WHERE gm.movieId = m.id " +
                            " ORDER BY g.name LIMIT 3) AS genres, " +
                            "(SELECT GROUP_CONCAT(CONCAT(top_stars.starId, ':', top_stars.starName) " +
                            " ORDER BY top_stars.movie_count DESC, top_stars.starName ASC SEPARATOR ', ') " +
                            " FROM (SELECT s.id AS starId, s.name AS starName, COUNT(sm.movieId) AS movie_count " +
                            " FROM stars s " +
                            " JOIN stars_in_movies sm ON s.id = sm.starId " +
                            " WHERE sm.movieId = m.id " +
                            " GROUP BY s.id, s.name " +
                            " ORDER BY movie_count DESC, s.name ASC LIMIT 3) AS top_stars) AS stars " +
                            "FROM movies m " +
                            "LEFT JOIN ratings r ON m.id = r.movieId " +
                            "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                            "LEFT JOIN genres g ON gm.genreId = g.id " +
                            "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                            "LEFT JOIN stars s ON sm.starId = s.id WHERE 1=1 "
            );

            List<String> parameters = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                query.append("AND m.title LIKE ? ");
                parameters.add("%" + title + "%");
            }
            if (title != null && title.trim().equals("*")) {
                query.append("AND m.title REGEXP '^[^a-zA-Z0-9]' ");
            } else if (title != null && !title.trim().isEmpty()) {
                query.append("AND m.title LIKE ? ");
                parameters.add("%" + title + "%");
            }
            if (year != null && !year.trim().isEmpty()) {
                query.append("AND m.year = ? ");
                parameters.add(year);
            }
            if (director != null && !director.trim().isEmpty()) {
                query.append("AND m.director LIKE ? ");
                parameters.add("%" + director + "%");
            }
            if (star != null && !star.trim().isEmpty()) {
                query.append("AND s.name LIKE ? ");
                parameters.add("%" + star + "%");
            }
            if (genre != null && !genre.trim().isEmpty()) {
                query.append("AND m.id IN (SELECT gm.movieId FROM genres g " +
                        "JOIN genres_in_movies gm ON g.id = gm.genreId " +
                        "GROUP BY gm.movieId " +
                        "HAVING GROUP_CONCAT(g.name ORDER BY g.name) LIKE ?) ");
                parameters.add("%" + genre + "%");
            }

            query.append(" GROUP BY m.id, m.title, m.year, m.director, r.rating ");

            switch (sort) {
                case "title_asc_rating_asc":
                    query.append(" ORDER BY m.title ASC, r.rating ASC");
                    break;
                case "title_asc_rating_desc":
                    query.append(" ORDER BY m.title ASC, r.rating DESC");
                    break;
                case "title_desc_rating_asc":
                    query.append(" ORDER BY m.title DESC, r.rating ASC");
                    break;
                case "title_desc_rating_desc":
                    query.append(" ORDER BY m.title DESC, r.rating DESC");
                    break;
                case "rating_asc_title_asc":
                    query.append(" ORDER BY r.rating ASC, m.title ASC");
                    break;
                case "rating_asc_title_desc":
                    query.append(" ORDER BY r.rating ASC, m.title DESC");
                    break;
                case "rating_desc_title_asc":
                    query.append(" ORDER BY r.rating DESC, m.title ASC");
                    break;
                case "rating_desc_title_desc":
                    query.append(" ORDER BY r.rating DESC, m.title DESC");
                    break;
                default:
                    query.append(" ORDER BY m.title ASC, r.rating ASC");
                    break;
            }

            query.append(" LIMIT ? OFFSET ?");

            PreparedStatement ps = conn.prepareStatement(query.toString());

            for (int i = 0; i < parameters.size(); i++) {
                ps.setString(i + 1, parameters.get(i));
            }

            ps.setInt(parameters.size() + 1, limit);
            ps.setInt(parameters.size() + 2, offset);

            long queryStartTime = System.currentTimeMillis();

            ResultSet rs = ps.executeQuery();

            long queryEndTime = System.currentTimeMillis();
            LOGGER.info("Query Execution Time: " + (queryEndTime - queryStartTime) + "ms");

            List<Movie> movies = new ArrayList<>();
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getString("id"));
                movie.setTitle(rs.getString("title"));
                movie.setYear(rs.getInt("year"));
                movie.setDirector(rs.getString("director"));
                movie.setRating(rs.getObject("rating") != null ? rs.getDouble("rating") : 0.0);
                movie.setGenres(rs.getString("genres") != null ? rs.getString("genres") : "N/A");
                movie.setStars(parseStars(rs.getString("stars")));
                movies.add(movie);
            }

            rs.close();
            ps.close();
            conn.close();

            request.setAttribute("movies", movies);
            request.setAttribute("page", page);
            request.setAttribute("limit", limit);
            request.getRequestDispatcher("MovieList.jsp").forward(request, response);

        } catch (ClassNotFoundException e) {
            LOGGER.severe("MySQL Driver not found: " + e.getMessage());
            response.getWriter().write("MySQL Driver not found.");
        } catch (SQLException e) {
            LOGGER.severe("Database error occurred: " + e.getMessage());
            LOGGER.severe("SQL State: " + e.getSQLState());
            LOGGER.severe("Error Code: " + e.getErrorCode());
            response.getWriter().write("Database error occurred: " + e.getMessage());
        }
        long servletEndTime = System.currentTimeMillis();
        LOGGER.info("Search Servlet Total Time: " + (servletEndTime - servletStartTime) + "ms");
    }
}
