package importer;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieHandler extends DefaultHandler {

    private PreparedStatement pstmtCheckMovie;
    private PreparedStatement pstmtInsertMovie;
    private PreparedStatement pstmtGenreQuery;
    private PreparedStatement pstmtGenreInsert;
    private PreparedStatement pstmtGenreInMovie;

    private boolean inDirector = false;
    private boolean inDirname = false;
    private boolean inFilm = false;
    private boolean inTitle = false;
    private boolean inYear = false;
    private boolean inCat = false;

    private String directorName = "";
    private String filmTitle = "";
    private String filmYear = "";
    private String movieId = "";

    private ArrayList<String> genreList = new ArrayList<>();
    private int currentMovieId = 0;
    private int currentGenreId = 0;

    private Map<String, Integer> genreCache = new HashMap<>();

    private static final int BATCH_SIZE = 1000;

    private int movieBatchCount = 0;
    private int genreInMovieBatchCount = 0;

    public MovieHandler(
            PreparedStatement pstmtCheckMovie,
            PreparedStatement pstmtInsertMovie,
            PreparedStatement pstmtGenreQuery,
            PreparedStatement pstmtGenreInsert,
            PreparedStatement pstmtGenreInMovie,
            int maxMovieId
    ) {
        this.pstmtCheckMovie = pstmtCheckMovie;
        this.pstmtInsertMovie = pstmtInsertMovie;
        this.pstmtGenreQuery = pstmtGenreQuery;
        this.pstmtGenreInsert = pstmtGenreInsert;
        this.pstmtGenreInMovie = pstmtGenreInMovie;
        this.currentMovieId = maxMovieId;

        loadGenreCache();
    }

    private void loadGenreCache() {
        try {
            Statement stmt = pstmtGenreQuery.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM genres");
            while (rs.next()) {
                genreCache.put(rs.getString("name"), rs.getInt("id"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("director")) {
            inDirector = true;
        } else if (qName.equalsIgnoreCase("dirname") && inDirector) {
            inDirname = true;
            directorName = "";
        } else if (qName.equalsIgnoreCase("film")) {
            inFilm = true;
            filmTitle = "";
            filmYear = "";
            genreList.clear();
        } else if (qName.equalsIgnoreCase("t") && inFilm) {
            inTitle = true;
            filmTitle = "";
        } else if (qName.equalsIgnoreCase("year") && inFilm) {
            inYear = true;
            filmYear = "";
        } else if (qName.equalsIgnoreCase("cat") && inFilm) {
            inCat = true;
        }

    }

    public void endElement(String uri, String localName, String qName) {

        if (qName.equalsIgnoreCase("director")) {
            inDirector = false;
        } else if (qName.equalsIgnoreCase("dirname") && inDirector) {
            inDirname = false;
        } else if (qName.equalsIgnoreCase("cat") && inFilm) {
            inCat = false;
        } else if (qName.equalsIgnoreCase("film")) {
            inFilm = false;

            int year = 0;
            try {
                year = Integer.parseInt(filmYear.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid year format for film: " + filmTitle);
            }

            boolean movieExists = false;
            try {
                pstmtCheckMovie.setString(1, filmTitle.trim());
                pstmtCheckMovie.setInt(2, year);
                pstmtCheckMovie.setString(3, directorName.trim());
                ResultSet rs = pstmtCheckMovie.executeQuery();
                if (rs.next()) {
                    movieExists = rs.getInt(1) > 0;
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!movieExists) {
                // Generate new movie ID
                currentMovieId++;
                movieId = String.valueOf(currentMovieId);

                try {
                    pstmtInsertMovie.setString(1, movieId);
                    pstmtInsertMovie.setString(2, filmTitle.trim());
                    pstmtInsertMovie.setInt(3, year);
                    pstmtInsertMovie.setString(4, directorName.trim());
                    pstmtInsertMovie.addBatch();

                    movieBatchCount++;

                    if (movieBatchCount % BATCH_SIZE == 0) {
                        pstmtInsertMovie.executeBatch();
                        movieBatchCount = 0;

                        pstmtGenreInsert.executeBatch();

                        pstmtGenreInMovie.executeBatch();
                        genreInMovieBatchCount = 0;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for (String genreName : genreList) {
                    int genreId = getGenreId(genreName.trim());
                    if (genreId != -1) {
                        try {
                            pstmtGenreInMovie.setInt(1, genreId);
                            pstmtGenreInMovie.setString(2, movieId);
                            pstmtGenreInMovie.addBatch();

                            genreInMovieBatchCount++;

                            if (genreInMovieBatchCount % BATCH_SIZE == 0) {
                                pstmtGenreInMovie.executeBatch();
                                genreInMovieBatchCount = 0;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                System.out.println("Duplicate movie found. Skipping insertion for film: " + filmTitle);
            }

            filmTitle = "";
            filmYear = "";
            genreList.clear();

        } else if (qName.equalsIgnoreCase("t") && inFilm) {
            inTitle = false;
        } else if (qName.equalsIgnoreCase("year") && inFilm) {
            inYear = false;
        }

    }

    public void characters(char ch[], int start, int length) {

        if (inDirname) {
            directorName += new String(ch, start, length);
        } else if (inTitle) {
            filmTitle += new String(ch, start, length);
        } else if (inYear) {
            filmYear += new String(ch, start, length);
        } else if (inCat) {
            String genre = new String(ch, start, length).trim();
            if (!genre.isEmpty()) {
                genreList.add(genre);
            }
        }

    }

    private int getGenreId(String genreName) {
        // Check cache first
        if (genreCache.containsKey(genreName)) {
            return genreCache.get(genreName);
        }

        try {
            pstmtGenreQuery.setString(1, genreName);
            ResultSet rs = pstmtGenreQuery.executeQuery();
            if (rs.next()) {
                int genreId = rs.getInt("id");
                genreCache.put(genreName, genreId);
                rs.close();
                return genreId;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pstmtGenreInsert.setString(1, genreName);
            pstmtGenreInsert.executeUpdate();

            ResultSet rs = pstmtGenreInsert.getGeneratedKeys();
            int genreId = -1;
            if (rs.next()) {
                genreId = rs.getInt(1);
            }
            rs.close();

            genreCache.put(genreName, genreId);
            return genreId;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void endDocument() {
        try {
            if (movieBatchCount > 0) {
                pstmtInsertMovie.executeBatch();
                movieBatchCount = 0;
            }

            pstmtGenreInsert.executeBatch();

            if (genreInMovieBatchCount > 0) {
                pstmtGenreInMovie.executeBatch();
                genreInMovieBatchCount = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}