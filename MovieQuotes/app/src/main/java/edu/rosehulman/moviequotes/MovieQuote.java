package edu.rosehulman.moviequotes;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Matt Boutell on 12/15/2015, based on earlier work by Dave Fisher.
 */
public class MovieQuote {
    public String quote;
    public String movie;

    @Exclude
    public String key;

    @ServerTimestamp
    public Date lastTouched;

    public MovieQuote() {}

    public MovieQuote(String quote, String movie) {
        this.movie = movie;
        this.quote = quote;
    }
}
