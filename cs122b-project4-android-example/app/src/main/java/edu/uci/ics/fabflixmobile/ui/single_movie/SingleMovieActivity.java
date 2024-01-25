package edu.uci.ics.fabflixmobile.ui.single_movie;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);

        // Retrieve the selected movie from the Intent
        Movie selectedMovie = getIntent().getParcelableExtra("SELECTED_MOVIE");

        // Find the TextViews in your layout
        TextView titleTextView = findViewById(R.id.title);
        TextView subtitleTextView = findViewById(R.id.subtitle);
        TextView directorTextView = findViewById(R.id.director);
        TextView starsTextView = findViewById(R.id.stars);
        TextView genresTextView = findViewById(R.id.genres);

        // Set the values to the TextViews
        titleTextView.setText(selectedMovie.getName());
        subtitleTextView.setText("Year: " + selectedMovie.getYear());
        directorTextView.setText("Director: " + selectedMovie.getDirector());
        starsTextView.setText("Stars: " + selectedMovie.getDisplayedStars());
        genresTextView.setText("Genres: " + selectedMovie.getGenres());
    }
}
