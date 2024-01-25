import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson; // Import the Gson library

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/api/checkout"})
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String creditCard = request.getParameter("creditCard");
            String expirationDate = request.getParameter("expirationDate");
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);
            System.out.println("Credit Card: " + creditCard);
            System.out.println("Expiration Date: " + expirationDate);
            // Perform a database lookup to check if the credit card information exists
            String query = "SELECT * FROM creditcards WHERE firstName = ? AND lastName = ? AND id = ? AND expiration = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, creditCard);
            statement.setString(4, expirationDate);

            System.out.println(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Authentication successful, user found in the database
                    // You can retrieve user information from the ResultSet if needed
                    String first = resultSet.getString("firstName");
                    String last = resultSet.getString("lastName");
                    String cardNumber = resultSet.getString("id");
                    String expiration = resultSet.getString("expiration");
                    // Create a JSON response
                    responseJsonObject.addProperty("status", 200);
                    responseJsonObject.addProperty("message", "success");
                } else {
                    // Authentication failed, user not found or wrong password
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect username or password");
                }
            }
        } catch (SQLException e) {
            // Handle any exceptions related to database connection or query execution
            e.printStackTrace();
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "Database error");
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
