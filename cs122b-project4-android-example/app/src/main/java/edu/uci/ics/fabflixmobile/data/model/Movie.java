package edu.uci.ics.fabflixmobile.data.model;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie implements Parcelable {
    private final String name;
    private final short year;
    private final String movieID;

    private final String director;
    private final String stars;
    private final String displayedStars;
    private final String genres;


    public Movie(String name, short year, String movieID, String director, String stars, String displayedStars, String genres) {
        this.name = name;
        this.year = year;
        this.movieID = movieID;
        this.director = director;
        this.stars = stars;
        this.displayedStars = displayedStars;
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getMovieID() {
        return movieID;
    }

    public String getDirector() {
        return director;
    }

    public String getStars() {
        return stars;
    }

    public String getDisplayedStars() {
        return displayedStars;
    }

    public String getGenres() {
        return genres;
    }

    protected Movie(Parcel in) {
        name = in.readString();
        year = (short) in.readInt();
        movieID = in.readString();
        director = in.readString();
        stars = in.readString();
        displayedStars = in.readString();
        genres = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(year);
        dest.writeString(movieID);
        dest.writeString(director);
        dest.writeString(stars);
        dest.writeString(displayedStars);
        dest.writeString(genres);
    }
}