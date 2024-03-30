package com.t1r2340.spotifystats.models.api;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Wrapped {

    /** The top artists */
    private TopArtists topArtists;
    /** The top tracks */
    private TopTracks topTracks;
    /** The top genres */
    private List<String> topGenres;

    /** The time the wrapped was generated */
    @ServerTimestamp
    private Date generatedAt;
    /** The firebase user ID */
    private String userId;

    public Wrapped() {}

    /**
     * Constructor for Wrapped
     */
    public Wrapped(TopArtists topArtists, TopTracks topTracks, List<String> topGenres, Date generatedAt, String userId) {
        this.topArtists = topArtists;
        this.topTracks = topTracks;
        this.topGenres = topGenres;
        this.generatedAt = generatedAt;
        this.userId = userId;
    }

    /**
     * Gets the top artists
     * @return the top artists
     */
    public TopArtists getTopArtists() {
        return topArtists;
    }

    /**
     * Gets the top tracks
     * @return the top tracks
     */
    public TopTracks getTopTracks() {
        return topTracks;
    }

    /**
     * Gets the top genres
     * @return the top genres
     */
    public List<String> getTopGenres() {
        return topGenres;
    }

    /**
     * Gets the time the wrapped was generated
     * @return the time the wrapped was generated
     */
    public Date getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Gets the firebase user ID
     * @return the firebase user ID
     */
    public String getUserId() {
        return userId;
    }

    public String toString() {
        return "Top Artists: " + topArtists.toString() + "Top Tracks: " + topTracks.toString() + "Top Genres: " + topGenres.toString() + "Generated At: " + generatedAt.toString() + "User ID: " + userId;
    }
}