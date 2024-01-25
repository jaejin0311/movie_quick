import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@WebServlet(name = "AddStarServlet", urlPatterns = {"/api/addStar"})
public class AddStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;

    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to initialize data source", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("here");
        JsonObject jsonObject = new JsonObject();
        String starName = request.getParameter("starName");
        Integer birthYear = null;

        try {
            birthYear = Integer.parseInt(request.getParameter("birthYear"));
        } catch (NumberFormatException e) {
            response.setStatus(400); // Bad Request
            jsonObject.addProperty("errorMessage", "Invalid birth year.");
            response.getWriter().write(jsonObject.toString());
            return;
        }

        if (starName != null && birthYear != null) {
            try (Connection conn = dataSource.getConnection()) {
                String query = "INSERT INTO stars (name, birthYear) VALUES (?, ?)";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, starName);
                statement.setInt(2, birthYear);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    jsonObject.addProperty("message", "Star added successfully.");
                    response.setStatus(200); // OK
                } else {
                    jsonObject.addProperty("errorMessage", "Failed to add star.");
                    response.setStatus(500); // Internal Server Error
                }
            } catch (SQLException e) {
                jsonObject.addProperty("errorMessage", "Database error: " + e.getMessage());
                response.setStatus(500); // Internal Server Error
            }
        } else {
            jsonObject.addProperty("errorMessage", "Invalid star data.");
            response.setStatus(400); // Bad Request
        }

        response.getWriter().write(jsonObject.toString());
    }
}
