import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            String query = "SELECT s.name AS star_name, " +
                    "CASE " +
                    "  WHEN s.birthYear IS NULL THEN 'N/A' " +
                    "  ELSE s.birthYear " +
                    "END AS star_birth_year, " +
                    "GROUP_CONCAT(DISTINCT m.title ORDER BY m.title ASC SEPARATOR ', ') AS movies, " +
                    "GROUP_CONCAT(DISTINCT m.id ORDER BY m.title ASC SEPARATOR ', ') AS movie_ids " +
                    "FROM stars AS s " +
                    "LEFT JOIN stars_in_movies AS sim ON s.id = sim.starId " +
                    "LEFT JOIN movies AS m ON sim.movieId = m.id " +
                    "WHERE s.id = ? " +
                    "GROUP BY s.id;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String star_name = rs.getString("star_name");
                String starDob = rs.getString("star_birth_year"); // Modified to match the query result
                String movieTitle = rs.getString("movies");
                String movieIds = rs.getString("movie_ids");

                // Create a JsonObject for each star
                JsonObject starObject = new JsonObject();
                starObject.addProperty("star_name", star_name);
                starObject.addProperty("star_dob", starDob);
                starObject.addProperty("movies", movieTitle);
                starObject.addProperty("movie_ids", movieIds);

                // Add the star object to the array
                jsonArray.add(starObject);
            }

            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}