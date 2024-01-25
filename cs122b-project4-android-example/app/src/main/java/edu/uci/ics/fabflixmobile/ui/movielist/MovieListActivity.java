package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.single_movie.SingleMovieActivity;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        ArrayList<Movie> movies = getIntent().getParcelableArrayListExtra("MOVIE_LIST");
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, year: %d, id: %s", position, movie.getName(), movie.getYear(), movie.getMovieID());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            finish();
            Intent singleMovieIntent = new Intent(MovieListActivity.this, SingleMovieActivity.class);

            // Pass the list of movies to MovieListActivity using Intent
            singleMovieIntent.putExtra("SELECTED_MOVIE", movie);

            startActivity(singleMovieIntent);
        });
    }
}