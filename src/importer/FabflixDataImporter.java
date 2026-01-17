package importer;

import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.*;
import java.sql.*;

public class FabflixDataImporter {

    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream("inconsistency_report.log"));
            System.setOut(fileOut);
            System.setErr(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Connection conn = null;
        // Prepared Statements for MovieHandler
        PreparedStatement pstmtInsertMovie = null;
        PreparedStatement pstmtCheckMovie = null;
        PreparedStatement pstmtGenreQuery = null;
        PreparedStatement pstmtInsertGenreInMovie = null;
        PreparedStatement pstmtInsertGenre = null;

        // Prepared statements for ActorHandler
        PreparedStatement pstmtInsertStar = null;
        PreparedStatement pstmtCheckStar = null;
        PreparedStatement pstmtGetMaxStarId = null;

        // Prepared statements for CastHandler
        PreparedStatement pstmtGetStarId = null;
        PreparedStatement pstmtGetMovieId = null;
        PreparedStatement pstmtInsertStarInMovie = null;
        PreparedStatement pstmtCheckStarInMovie = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            conn.setAutoCommit(false);

            // Movie Handler
            String maxIdQuery = "SELECT MAX(CAST(id AS UNSIGNED)) FROM movies";
            pstmtInsertMovie = conn.prepareStatement(maxIdQuery);
            ResultSet rs = pstmtInsertMovie.executeQuery();

            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            rs.close();
            pstmtInsertMovie.close();

            String sqlInsert = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
            pstmtInsertMovie = conn.prepareStatement(sqlInsert);

            String sqlCheck = "SELECT COUNT(*) FROM movies WHERE title = ? AND year = ? AND director = ?";
            pstmtCheckMovie = conn.prepareStatement(sqlCheck);

            String sqlGenreQuery = "SELECT id FROM genres WHERE name = ?";
            pstmtGenreQuery = conn.prepareStatement(sqlGenreQuery);

            String sqlInsertGenreInMovie = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
            pstmtInsertGenreInMovie = conn.prepareStatement(sqlInsertGenreInMovie);

            String sqlInsertGenre = "INSERT INTO genres (name) VALUES (?)";
            pstmtInsertGenre = conn.prepareStatement(sqlInsertGenre, PreparedStatement.RETURN_GENERATED_KEYS);

            MovieHandler movieHandler = new MovieHandler(pstmtCheckMovie, pstmtInsertMovie, pstmtGenreQuery, pstmtInsertGenre, pstmtInsertGenreInMovie, maxId);
            FileInputStream inputStream = new FileInputStream("xml_data/mains243.xml");
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
            InputSource is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            saxParser.parse(is, movieHandler);

            conn.commit();

            System.out.println("Data insertion completed successfully.");

            // ActorHandler
            String maxStarIdQuery = "SELECT MAX(CAST(id AS UNSIGNED)) FROM stars";
            pstmtGetMaxStarId = conn.prepareStatement(maxStarIdQuery);
            ResultSet rsStarId = pstmtGetMaxStarId.executeQuery();

            int maxStarId = 0;
            if (rsStarId.next()) {
                maxStarId = rsStarId.getInt(1);
            }

            String sqlInsertStar = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            pstmtInsertStar = conn.prepareStatement(sqlInsertStar);

            String sqlCheckStar = "SELECT CONCAT(name, IFNULL(birthYear, '')) AS starKey FROM stars";
            pstmtCheckStar = conn.prepareStatement(sqlCheckStar);

            ActorHandler actorHandler = new ActorHandler(pstmtCheckStar, pstmtInsertStar, maxStarId);
            inputStream = new FileInputStream("xml_data/actors63.xml");
            reader = new InputStreamReader(inputStream, "ISO-8859-1");
            is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            saxParser.parse(is, actorHandler);

            // CastHandler
            rsStarId = pstmtGetMaxStarId.executeQuery();
            if (rsStarId.next()) {
                maxStarId = rsStarId.getInt(1);
            }
            rsStarId.close();
            pstmtGetMaxStarId.close();

            // Notice that cast xml file includes actors that are not present in actors xml file,
            // so going to insert those actors and associate those with the movies while scrubbing through the
            // cast file

            String sqlGetStarId = "SELECT id FROM stars WHERE name = ?";
            pstmtGetStarId = conn.prepareStatement(sqlGetStarId);

            String sqlGetMovieId = "SELECT id FROM movies WHERE title = ?";
            pstmtGetMovieId = conn.prepareStatement(sqlGetMovieId);
            pstmtInsertStar = conn.prepareStatement(sqlInsertStar);

            String sqlInsertStarInMovie = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
            pstmtInsertStarInMovie = conn.prepareStatement(sqlInsertStarInMovie);

            String sqlCheckStarInMovie = "SELECT COUNT(*) FROM stars_in_movies WHERE starId = ? AND movieId = ?";
            pstmtCheckStarInMovie = conn.prepareStatement(sqlCheckStarInMovie);

            CastHandler castHandler = new CastHandler(
                    pstmtGetStarId,
                    pstmtGetMovieId,
                    pstmtInsertStar,
                    pstmtInsertStarInMovie,
                    pstmtCheckStarInMovie,
                    maxStarId
            );
            inputStream = new FileInputStream("xml_data/casts124.xml");
            reader = new InputStreamReader(inputStream, "ISO-8859-1");
            is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            saxParser.parse(is, castHandler);

            System.out.println("Data insertion completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.out.println("Rolling back due to errors.");
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                // Movie handler
                if (pstmtInsertMovie != null) pstmtInsertMovie.close();
                if (pstmtCheckMovie != null) pstmtCheckMovie.close();
                if (pstmtGenreQuery != null) pstmtGenreQuery.close();
                if (pstmtInsertGenreInMovie != null) pstmtInsertGenreInMovie.close();
                if (pstmtInsertGenre != null) pstmtInsertGenre.close();
                if (conn != null) conn.close();

                // Cast Handler
                if (pstmtGetStarId != null) pstmtGetStarId.close();
                if (pstmtGetMovieId != null) pstmtGetMovieId.close();
                if (pstmtInsertStarInMovie != null) pstmtInsertStarInMovie.close();
                if (pstmtCheckStarInMovie != null) pstmtCheckStarInMovie.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}