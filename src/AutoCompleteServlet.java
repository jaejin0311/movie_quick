import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

@WebServlet("/auto-complete")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;

    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to initialize data source", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            JsonArray jsonArray = new JsonArray();
            String query = request.getParameter("query");
//            System.out.println("Query: " + query);
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            try (Connection conn = dataSource.getConnection()) {
                String[] keywords = query.split("\\s+");
//                System.out.println("keywords: " + keywords);
                String sql = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name) AS genres, " +
                        "GROUP_CONCAT(DISTINCT s.name) AS stars " +
                        "FROM movies m " +
                        "LEFT JOIN genres_in_movies gim ON m.id = gim.movieId " +
                        "LEFT JOIN genres g ON gim.genreId = g.id " +
                        "LEFT JOIN stars_in_movies sim ON m.id = sim.movieId " +
                        "LEFT JOIN stars s ON sim.starId = s.id " +
                        "WHERE m.title LIKE ? ";

                for (int i = 0; i < keywords.length - 1; i++) {
                    sql += " AND title LIKE ?";
                }
                sql += " GROUP BY m.id, m.title, m.year, m.director";

                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    for (int i = 0; i < keywords.length; i++) {
                        preparedStatement.setString(i + 1, "%" + keywords[i].toLowerCase() + "%");
                    }
//                    System.out.println("prepared: " + preparedStatement);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        int count = 0;
                        while (resultSet.next() && count < 10) {
                            String movieID = resultSet.getString("id");
                            String title = resultSet.getString("title");
                            int year = resultSet.getInt("year");
                            String director = resultSet.getString("director");
                            String genres = resultSet.getString("genres");
                            String stars = resultSet.getString("stars");
                            jsonArray.add(generateJsonObject(movieID, title, year, director, genres, stars));
                            count++;
                        }
                    }
                }
            }
            response.getWriter().write(jsonArray.toString());
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace
            response.sendError(500, "SQL Exception: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace
            response.sendError(500, "Exception: " + e.getMessage());
        }
    }

    private static JsonObject generateJsonObject(String movieID, String title, int year, String director, String genres, String stars) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);
        additionalDataJsonObject.addProperty("year", year);
        additionalDataJsonObject.addProperty("director", director);
        System.out.println("director: " + director);
        additionalDataJsonObject.addProperty("genres", genres);
        additionalDataJsonObject.addProperty("stars", stars);
        System.out.println("stars: " + stars);
        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}
