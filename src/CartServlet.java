import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CartServlet", urlPatterns = {"/api/cart"})
public class CartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getParameter("method");
        String movieId = request.getParameter("movieId");
        String movieTitle = request.getParameter("movieTitle");
        HttpSession session = request.getSession();
        System.out.println("method: " + method);

        // Initialize or retrieve cartItems from the session
        JsonArray cartItems = (JsonArray) session.getAttribute("cartItems");

        // Create cartItems array if it doesn't exist
        if (cartItems == null) {
            cartItems = new JsonArray();
            session.setAttribute("cartItems", cartItems);
        }

        // Check if the item is already in the cart
        JsonObject existingItem = findItemInCart(cartItems, movieId);
        if ("+".equals(method)) {
            System.out.println("+");
            int quantity = existingItem.get("quantity").getAsInt();
            quantity++;
            existingItem.addProperty("quantity", quantity);
        } else if ("-".equals(method)) {
            System.out.println("-");
            int quantity = existingItem.get("quantity").getAsInt();
            quantity--;
            existingItem.addProperty("quantity", quantity);
        } else if ("delete".equals(method)){
            if (existingItem != null) {
                // Remove the item from the cartItems array
                cartItems.remove(existingItem);
                // Update the session attribute with the modified cartItems
                session.setAttribute("cartItems", cartItems);
            }
        } else if (method == null) {
            if (existingItem != null) {
                int quantity = existingItem.get("quantity").getAsInt();
                quantity++;
                existingItem.addProperty("quantity", quantity);
            } else {
                // If the item is not in the cart, add it
                JsonObject newItem = new JsonObject();
                newItem.addProperty("movieId", movieId);
                newItem.addProperty("movieTitle", movieTitle);
                newItem.addProperty("quantity", 1);
                cartItems.add(newItem);
            }
        }


        // Prepare the response JSON
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("message", "Item added to the cart");
        responseJsonObject.add("cartItems", cartItems);

        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        // Retrieve cartItems from the session
        JsonArray cartItems = (JsonArray) session.getAttribute("cartItems");

        // Prepare the response JSON
        JsonObject responseJsonObject = new JsonObject();
        if (cartItems != null) {
            responseJsonObject.addProperty("message", "Cart items retrieved successfully");
            responseJsonObject.add("cartItems", cartItems);
        } else {
            responseJsonObject.addProperty("message", "Cart is empty");
            responseJsonObject.add("cartItems", new JsonArray());
        }

        response.getWriter().write(responseJsonObject.toString());
    }


    // Helper method to find an item in the cart based on its movieId
    private JsonObject findItemInCart(JsonArray cartItems, String movieId) {
        for (int i = 0; i < cartItems.size(); i++) {
            JsonObject item = cartItems.get(i).getAsJsonObject();
            if (item.get("movieId").getAsString().equals(movieId)) {
                return item;
            }
        }
        return null;
    }
}
