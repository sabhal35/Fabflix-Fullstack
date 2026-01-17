package importer;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ActorHandler extends DefaultHandler {

    private PreparedStatement pstmtCheckStar;
    private PreparedStatement pstmtInsertStar;

    private boolean inActor = false;
    private boolean inStageName = false;
    private boolean inDOB = false;

    private String name = "";
    private String birthYearStr = "";
    private int birthYear = 0;

    private int currentStarId = 0;

    private static final int BATCH_SIZE = 1000;
    private int starBatchCount = 0;

    private Set<String> existingStars = new HashSet<>();

    public ActorHandler(
            PreparedStatement pstmtCheckStar,
            PreparedStatement pstmtInsertStar,
            int maxStarId) {
        this.pstmtCheckStar = pstmtCheckStar;
        this.pstmtInsertStar = pstmtInsertStar;
        this.currentStarId = maxStarId;

        preloadExistingStars();
    }

    private void preloadExistingStars() {
        try {
            Statement stmt = pstmtCheckStar.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CONCAT(name, IFNULL(birthYear, '')) AS starKey FROM stars");
            while (rs.next()) {
                existingStars.add(rs.getString("starKey"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("actor")) {
            inActor = true;
            name = "";
            birthYearStr = "";
            birthYear = 0;
        } else if (qName.equalsIgnoreCase("stagename")) {
            inStageName = true;
        } else if (qName.equalsIgnoreCase("dob")) {
            inDOB = true;
        }

    }

    public void endElement(String uri, String localName, String qName) {

        if (qName.equalsIgnoreCase("actor")) {
            inActor = false;

            birthYearStr = birthYearStr.trim();
            birthYear = parseBirthYear(birthYearStr);
            String starKey = name + (birthYear > 0 ? birthYear : "");
            if (existingStars.contains(starKey)) {
                System.err.println("Star already exists: " + name + ", Birth Year: " + birthYear);
                return;
            }

            currentStarId++;
            String starId = String.valueOf(currentStarId);

            try {
                pstmtInsertStar.setString(1, starId);
                pstmtInsertStar.setString(2, name);
                if (birthYear > 0) {
                    pstmtInsertStar.setInt(3, birthYear);
                } else {
                    pstmtInsertStar.setNull(3, Types.INTEGER);
                }
                pstmtInsertStar.addBatch();

                starBatchCount++;

                if (starBatchCount % BATCH_SIZE == 0) {
                    pstmtInsertStar.executeBatch();
                    starBatchCount = 0;
                }

                existingStars.add(starKey);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else if (qName.equalsIgnoreCase("stagename")) {
            inStageName = false;
        } else if (qName.equalsIgnoreCase("dob")) {
            inDOB = false;
        }

    }

    public void characters(char ch[], int start, int length) {

        if (inStageName) {
            name += new String(ch, start, length);
        } else if (inDOB) {
            birthYearStr += new String(ch, start, length);
        }

    }

    private int parseBirthYear(String birthYearStr) {
        if (birthYearStr == null || birthYearStr.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(birthYearStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid birth year format: " + birthYearStr + ". Setting birth year to 0.");
            return 0;
        }
    }

    public void endDocument() {
        try {
            if (starBatchCount > 0) {
                pstmtInsertStar.executeBatch();
                starBatchCount = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}