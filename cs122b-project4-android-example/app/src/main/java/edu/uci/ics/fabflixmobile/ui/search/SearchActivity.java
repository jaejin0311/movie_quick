package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText search_title;
    private final String host = "204.236.184.162";
    private final String port = "8443";
    private final String domain = "cs122b-project1-yolo";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        search_title = binding.searchTitle;
        final Button searchButton = binding.search;

        searchButton.setOnClickListener(view -> performSearch());
    }

    @SuppressLint("SetTextI18n")
    public void performSearch() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/auto-complete?query=" + search_title.getText().toString(),
                response -> {
                    Log.d("search.success", response);

                    // TODO: Parse the response and handle the search results accordingly
                    // For now, let's just log the response and proceed to the MovieListActivity
                    ArrayList<Movie> movies = parseSearchResults(response);
                    showMovieList(movies);
                },
                error -> {
                    Log.d("search.error", error.toString());
                    // Handle the error, e.g., display an error message to the user
                }) {
        };
        queue.add(searchRequest);
    }

    private ArrayList<Movie> parseSearchResults(String response) {
        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("value");
                JSONObject data = jsonObject.getJSONObject("data");
                String movieID = data.getString("movieID");
                String director = data.getString("director");

                String starsString = data.getString("stars");
                String[] starsArray = starsString.split(",");
                String stars = String.join(", ", Arrays.copyOf(starsArray, Math.min(3, starsArray.length)));

                String genresString = data.getString("genres");
                String[] genresArray = genresString.split(",");

                String genres = String.join(", ", Arrays.copyOf(genresArray, Math.min(3, genresArray.length)));

                int year = data.getInt("year");
                // Create a Movie object and add it to the list
                movies.add(new Movie(title, (short) year, movieID, director, stars, starsString, genres));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private void showMovieList(ArrayList<Movie> movies) {
        finish();
        Intent movieListIntent = new Intent(SearchActivity.this, MovieListActivity.class);

        // Pass the list of movies to MovieListActivity using Intent
        movieListIntent.putExtra("MOVIE_LIST", movies);

        startActivity(movieListIntent);
    }
}

