package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
//import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView Message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "204.236.184.162";
    private final String port = "8443";
    private final String domain = "cs122b-project1-yolo";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        Message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        Message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    try {
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(response);

                        // Retrieve status and message from the response
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        // Check if login was successful
                        if (message.equals("success")) {
                            Log.d("login.success", message);

                            // Complete and destroy login activity once successful
                            finish();

                            // Redirect to the appropriate activity/page
                            Intent searchIntent = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(searchIntent);
                        } else {
                            // Authentication failed
                            Message.setText("Wrong Username or Password");
                            Log.d("login.error", message);
                            // Update UI or show a message to the user indicating the failure
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("login.error", "Error parsing JSON response");
                    }
                },
                error -> {
                    // Handle network or other errors
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                params.put("isMobile", "true");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}