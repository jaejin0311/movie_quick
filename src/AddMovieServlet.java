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

@WebServlet(name = "AddMovieServlet", urlPatterns = {"/api/addMovie"})
public class AddMovieServlet extends HttpServlet {
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
        JsonObject jsonObject = new JsonObject();
        String movieTitle = request.getParameter("movieTitle");
        String director = request.getParameter("director");
        String star = request.getParameter("star");

        Integer movieYear = null;

        try {
            movieYear = Integer.parseInt(request.getParameter("movieYear"));
        } catch (NumberFormatException e) {
            response.setStatus(400); // Bad Request
            jsonObject.addProperty("errorMessage", "Invalid Movie year.");
            response.getWriter().write(jsonObject.toString());
            return;
        }

        System.out.println("here");

        if (movieTitle != null && movieYear != null && director != null && star != null) {
            try (Connection conn = dataSource.getConnection()) {
                String query = "INSERT INTO movies (id, title, year, director) VALUES ('100', ?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, movieTitle);
                statement.setInt(2, movieYear);
                statement.setString(3, director);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    jsonObject.addProperty("message", "Movie added successfully.");
                    response.setStatus(200); // OK
                } else {
                    jsonObject.addProperty("errorMessage", "Failed to add Movie.");
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
