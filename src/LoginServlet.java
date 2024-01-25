import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.Gson;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;
    private Gson gson = new Gson(); // Create a Gson instance

    public void init() throws ServletException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String isMobile = request.getParameter("isMobile");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        if (username != null && password != null) {
            try {
                if (isMobile == null || !isMobile.equalsIgnoreCase("true")) {
                    RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                }

                // reCAPTCHA verification succeeded, proceed with username/password validation
                String authenticationSuccess = verifyCredentials(username, password);

                if (authenticationSuccess == "employee") {
                    // Authentication successful, user found in the database
                    // You can retrieve user information from the database if needed

                    // Set user information in the session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", new User(username));

                    // Create a JSON response
                    responseJsonObject.addProperty("status", "employee");
                    responseJsonObject.addProperty("message", "success");
                } else if (authenticationSuccess == "customer"){
                    // Authentication successful, user found in the database
                    // You can retrieve user information from the database if needed

                    // Set user information in the session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", new User(username));

                    // Create a JSON response
                    responseJsonObject.addProperty("status", "customer");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    // Authentication failed, incorrect username or password
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect username or password");
                }
            } catch (Exception e) {
                // Handle reCAPTCHA verification exception or database errors
                e.printStackTrace();
                responseJsonObject.addProperty("status", "error");
                responseJsonObject.addProperty("message", "Error: " + e.getMessage());
            }
        } else {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Username and password are required.");
        }

        response.getWriter().write(responseJsonObject.toString());
    }


    private static String verifyCredentials(String email, String password) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        String status = "";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        String employee = String.format("SELECT * from employees where email='%s'", email);

        ResultSet employee_rs = statement.executeQuery(employee);

        boolean success = false;
        if (employee_rs.next()) {
            // get the encrypted password from the database
            String encryptedPassword = employee_rs.getString("password");
            // use the same encryptor to compare the user input password with encrypted password stored in DB
            success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            if (success == true) {
                status = "employee";
            } else {
                status = "fail";
            }
        } else {
            String customer = String.format("SELECT * from customers where email='%s'", email);
            ResultSet customer_rs = statement.executeQuery(customer);
            if (customer_rs.next()) {
                String customer_password = customer_rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, customer_password);
                if (success == true) {
                    status = "customer";
                } else {
                    status = "fail";
                }
            }
            customer_rs.close();
        }
        employee_rs.close();
        statement.close();
        connection.close();
        return status;
    }
}
