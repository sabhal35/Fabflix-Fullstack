package importer;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CastHandler extends DefaultHandler {

    private PreparedStatement pstmtGetStarId;
    private PreparedStatement pstmtGetMovieId;
    private PreparedStatement pstmtInsertStar;
    private PreparedStatement pstmtInsertStarInMovie;
    private PreparedStatement pstmtCheckStarInMovie;

    private boolean inM = false;
    private boolean inT = false;
    private boolean inA = false;

    private String movieTitle = "";
    private String actorName = "";

    private int currentStarId;

    private static final int BATCH_SIZE = 1000;

    private int starBatchCount = 0;
    private int starInMovieBatchCount = 0;

    private Map<String, String> starIdCache = new HashMap<>();

    private Map<String, String> movieIdCache = new HashMap<>();

    public CastHandler(
            PreparedStatement pstmtGetStarId,
            PreparedStatement pstmtGetMovieId,
            PreparedStatement pstmtInsertStar,
            PreparedStatement pstmtInsertStarInMovie,
            PreparedStatement pstmtCheckStarInMovie,
            int maxStarId) {
        this.pstmtGetStarId = pstmtGetStarId;
        this.pstmtGetMovieId = pstmtGetMovieId;
        this.pstmtInsertStar = pstmtInsertStar;
        this.pstmtInsertStarInMovie = pstmtInsertStarInMovie;
        this.pstmtCheckStarInMovie = pstmtCheckStarInMovie;
        this.currentStarId = maxStarId;

        preloadStarIdCache();
        preloadMovieIdCache();
    }

    private void preloadStarIdCache() {
        try {
            Statement stmt = pstmtGetStarId.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM stars");
            while (rs.next()) {
                starIdCache.put(rs.getString("name").trim(), rs.getString("id"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void preloadMovieIdCache() {
        try {
            Statement stmt = pstmtGetMovieId.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, title FROM movies");
            while (rs.next()) {
                movieIdCache.put(rs.getString("title").trim(), rs.getString("id"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("m")) {
            inM = true;
            movieTitle = "";
            actorName = "";
        } else if (qName.equalsIgnoreCase("t") && inM) {
            inT = true;
        } else if (qName.equalsIgnoreCase("a") && inM) {
            inA = true;
        }
    }

    public void endElement(String uri, String localName, String qName) {

        if (qName.equalsIgnoreCase("m")) {
            inM = false;

            String starId = null;
            String movieId = null;

            starId = starIdCache.get(actorName.trim());
            if (starId == null) {
                currentStarId++;
                starId = String.valueOf(currentStarId);

                try {
                    pstmtInsertStar.setString(1, starId);
                    pstmtInsertStar.setString(2, actorName.trim());
                    pstmtInsertStar.setNull(3, Types.INTEGER);
                    pstmtInsertStar.addBatch();

                    starBatchCount++;

                    if (starBatchCount % BATCH_SIZE == 0) {
                        pstmtInsertStar.executeBatch();
                        starBatchCount = 0;
                    }

                    starIdCache.put(actorName.trim(), starId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            movieId = movieIdCache.get(movieTitle.trim());
            if (movieId == null) {
                System.err.println("Movie not found: " + movieTitle + ". Skipping entry.");
                return;
            }

            boolean associationExists = false;
            try {
                pstmtCheckStarInMovie.setString(1, starId);
                pstmtCheckStarInMovie.setString(2, movieId);
                ResultSet rsCheck = pstmtCheckStarInMovie.executeQuery();
                if (rsCheck.next()) {
                    associationExists = rsCheck.getInt(1) > 0;
                }
                rsCheck.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!associationExists) {
                try {
                    pstmtInsertStarInMovie.setString(1, starId);
                    pstmtInsertStarInMovie.setString(2, movieId);
                    pstmtInsertStarInMovie.addBatch();

                    starInMovieBatchCount++;

                    if (starInMovieBatchCount % BATCH_SIZE == 0) {
                        if (starBatchCount > 0) {
                            pstmtInsertStar.executeBatch();
                            starBatchCount = 0;
                        }

                        pstmtInsertStarInMovie.executeBatch();
                        starInMovieBatchCount = 0;
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        System.err.println("Duplicate stars_in_movies entry: Star ID " + starId + ", Movie ID " + movieId);
                    } else {
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("Association already exists: Star ID " + starId + ", Movie ID " + movieId);
            }

        } else if (qName.equalsIgnoreCase("t") && inM) {
            inT = false;
        } else if (qName.equalsIgnoreCase("a") && inM) {
            inA = false;
        }
    }

    public void characters(char ch[], int start, int length) {

        if (inT) {
            movieTitle += new String(ch, start, length);
        } else if (inA) {
            actorName += new String(ch, start, length);
        }
    }

    public void endDocument() {
        try {
            if (starBatchCount > 0) {
                pstmtInsertStar.executeBatch();
                starBatchCount = 0;
            }

            if (starInMovieBatchCount > 0) {
                pstmtInsertStarInMovie.executeBatch();
                starInMovieBatchCount = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

