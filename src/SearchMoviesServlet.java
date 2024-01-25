import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SearchMoviesServlet", urlPatterns = "/api/search")
public class SearchMoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String sortOption = request.getParameter("sort");

        System.out.println("Debug: title=" + title);
        System.out.println("Debug: year=" + year);
        System.out.println("Debug: director=" + director);
        System.out.println("Debug: starName=" + starName);
        System.out.println("Debug: sortOption=" + sortOption);

        request.getServletContext().log("Searching for movies");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT " +
                    "m.id AS movie_id, " +
                    "MAX(m.title) AS movie_title, " +
                    "MAX(m.year) AS movie_year, " +
                    "MAX(m.director) AS movie_director, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genres, " +
                    "GROUP_CONCAT(DISTINCT s.name) AS star_names, " +
                    "GROUP_CONCAT(DISTINCT s.id) AS star_ids, " +
                    "MAX(r.rating) AS movie_rating " +
                    "FROM movies m " +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN genres g ON gm.genreId = g.id " +
                    "LEFT JOIN stars_in_movies ms ON m.id = ms.movieId " +
                    "LEFT JOIN stars s ON ms.starId = s.id " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "WHERE 1=1";  // Start with a true condition

            if (title != null && !title.isEmpty()) {
                query += " AND m.title LIKE ?";
            }

            if (year != null && !year.isEmpty()) {
                query += " AND m.year LIKE ?";
            }

            if (director != null && !director.isEmpty()) {
                query += " AND m.director LIKE ?";
            }

            if (starName != null && !starName.isEmpty()) {
                query += " AND s.name LIKE ?";
            }

            query += " GROUP BY m.id"; // Group by movie ID

            if (sortOption != null && !sortOption.isEmpty()) {
                switch (sortOption) {
                    case "1":
                        query += " ORDER BY movie_title ASC, movie_rating DESC";
                        break;
                    case "2":
                        query += " ORDER BY movie_title ASC, movie_rating ASC";
                        break;
                    case "3":
                        query += " ORDER BY movie_title DESC, movie_rating DESC";
                        break;
                    case "4":
                        query += " ORDER BY movie_title DESC, movie_rating ASC";
                        break;
                    case "5":
                        query += " ORDER BY movie_rating ASC, movie_title DESC";
                        break;
                    case "6":
                        query += " ORDER BY movie_rating ASC, movie_title ASC";
                        break;
                    case "7":
                        query += " ORDER BY movie_rating DESC, movie_title DESC";
                        break;
                    case "8":
                        query += " ORDER BY movie_rating DESC, movie_title ASC";
                        break;
                    default:
                        query += " ORDER BY movie_title ASC, movie_rating DESC";
                }
            } else {
                // Default sorting option
                query += " ORDER BY movie_title ASC, movie_rating DESC";
            }

            PreparedStatement statement = conn.prepareStatement(query);
            int parameterIndex = 1;

            if (title != null && !title.isEmpty()) {
                statement.setString(parameterIndex, "%" + title + "%");
                parameterIndex++;
            }

            if (year != null && !year.isEmpty()) {
                statement.setString(parameterIndex, "%" + year + "%");
                parameterIndex++;
            }

            if (director != null && !director.isEmpty()) {
                statement.setString(parameterIndex, "%" + director + "%");
                parameterIndex++;
            }

            if (starName != null && !starName.isEmpty()) {
                statement.setString(parameterIndex, "%" + starName + "%");
            }



            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movieId = rs.getString("movie_id");
                String movieTitle = rs.getString("movie_title");
                String movieYear = rs.getString("movie_year");
                String movieDirector = rs.getString("movie_director");
                String genres = rs.getString("genres");
                String star_names = rs.getString("star_names");
                String star_ids = rs.getString("star_ids");
                double rating = rs.getDouble("movie_rating");
                String ratingStr = (rs.wasNull() || rating == 0) ? "N/A" : String.valueOf(rating);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("star_names", star_names);
                jsonObject.addProperty("star_ids", star_ids);
                jsonObject.addProperty("rating", ratingStr);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
