import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FabflixXMLParser extends DefaultHandler {
    private StringBuilder currentValue;
    private Connection connection;

    private String currentFid;
    private String currentTitle;
    private String currentYear;

    public FabflixXMLParser(Connection connection) {
        this.connection = connection;
        this.currentValue = new StringBuilder();
    }

    public void parseXML(String xmlFileName) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFileName, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentValue.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = currentValue.toString().trim();

        if (qName.equalsIgnoreCase("fid")) {
            currentFid = value;
        } else if (qName.equalsIgnoreCase("t")) {
            currentTitle = value;
        } else if (qName.equalsIgnoreCase("year")) {
            currentYear = value;
        } else if (qName.equalsIgnoreCase("film")) {
            // Insert the movie into the database
            insertMovie(currentFid, currentTitle, currentYear);
        }
    }

    private void insertMovie(String fid, String title, String year) {
        try {
            // Create a PreparedStatement to insert the movie data into your database
            String sql = "INSERT INTO movies (fid, title, year) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, fid);
            statement.setString(2, title);
            statement.setString(3, year);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");

            FabflixXMLParser parser = new FabflixXMLParser(connection);
            parser.parseXML("src/mains243.xml");

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
