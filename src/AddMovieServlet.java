import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/AddMovieServlet")
public class AddMovieServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieTitle = request.getParameter("movieTitle");
        String yearStr = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String birthYearStr = request.getParameter("birthYear");
        String genreName = request.getParameter("genreName");

        Integer year = null;
        Integer birthYear = null;

        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearStr.trim());
            } catch (NumberFormatException e) {
                appendMessage(request, "Error: Invalid year format.");
                request.getRequestDispatcher("AddMovie.jsp").forward(request, response);
                return;
            }
        } else {
            appendMessage(request, "Error: Year is required.");
            request.getRequestDispatcher("AddMovie.jsp").forward(request, response);
            return;
        }

        if (birthYearStr != null && !birthYearStr.trim().isEmpty()) {
            try {
                birthYear = Integer.parseInt(birthYearStr.trim());
            } catch (NumberFormatException e) {
                appendMessage(request, "Error: Invalid birth year format.");
                request.getRequestDispatcher("AddMovie.jsp").forward(request, response);
                return;
            }
        }

        try (Connection conn = DatabaseConnectionPool.getMasterConnection()) {
            conn.setAutoCommit(false);

            String callQuery = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = conn.prepareCall(callQuery)) {
                stmt.setString(1, movieTitle);
                stmt.setInt(2, year);
                stmt.setString(3, director);
                stmt.setString(4, starName);

                if (birthYear != null) {
                    stmt.setInt(5, birthYear);
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }

                stmt.setString(6, genreName);

                stmt.registerOutParameter(7, Types.VARCHAR);
                stmt.registerOutParameter(8, Types.VARCHAR);
                stmt.registerOutParameter(9, Types.INTEGER);

                stmt.execute();

                String actualMovieId = stmt.getString(7);
                String actualStarId = stmt.getString(8);
                int actualGenreId = stmt.getInt(9);

                conn.commit();

                appendMessage(request, "Movie added successfully!<br>" +
                        "Movie ID: " + actualMovieId + "<br>" +
                        "Star ID: " + actualStarId + "<br>" +
                        "Genre ID: " + actualGenreId);
            } catch (SQLException e) {
                conn.rollback();

                if ("45000".equals(e.getSQLState())) {
                    appendMessage(request, "Error: Duplicate movie. This movie already exists in the database.");
                } else {
                    appendMessage(request, "Error: " + e.getMessage());
                }
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            appendMessage(request, "Error: " + e.getMessage());
        }

        request.getRequestDispatcher("AddMovie.jsp").forward(request, response);
    }

    /**
     * Appends a message to the request attribute "movieMessage".
     *
     * @param request The HttpServletRequest object.
     * @param message The message to append.
     */
    private void appendMessage(HttpServletRequest request, String message) {
        String existingMessages = (String) request.getAttribute("movieMessage");
        if (existingMessages == null) {
            existingMessages = "";
        }
        request.setAttribute("movieMessage", existingMessages + message + "<br>");
    }
}