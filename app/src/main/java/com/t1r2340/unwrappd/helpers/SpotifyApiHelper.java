package com.t1r2340.unwrappd.helpers;

import android.util.Log;
import android.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.t1r2340.unwrappd.models.api.SpotifyProfile;
import com.t1r2340.unwrappd.models.api.TopArtists;
import com.t1r2340.unwrappd.models.api.TopTracks;
import com.t1r2340.unwrappd.models.api.Wrapped;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Spotify API helper class
 *
 * <p>A Consumer<JsonObject> is essentially a method that has a JSONObject as a parameter to allow
 * fro easy use of the JSON object returned from the API without having to deal with the async
 * nature of the API call
 */
public class SpotifyApiHelper {

  /** Access token for Spotify API */
  private String accessToken;

  /** Callback for API call failures */
  private FailureCallback failureCallback;

  /** HTTP client for API calls */
  private OkHttpClient okHttpClient;

  /** Current API call */
  private Call call;

  /** JSON parser */
  private Gson gson;

  /** Time range for top artists and tracks */
  public enum TimeRange {
    /** 1 month */
    SHORT_TERM("short_term"),
    /** 6 months */
    MEDIUM_TERM("medium_term"),
    /** Several years */
    LONG_TERM("long_term");

    private final String value;

    TimeRange(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * Constructor for Spotify API
   *
   * @param failureCallback callback for API call failures
   * @param accessToken access token for Spotify API
   * @param okHttpClient HTTP client for API calls
   * @param mCall current API call
   */
  public SpotifyApiHelper(
      FailureCallback failureCallback, String accessToken, OkHttpClient okHttpClient, Call mCall) {
    this.failureCallback = failureCallback;
    this.accessToken = accessToken;
    this.okHttpClient = okHttpClient;
    this.call = mCall;

    this.gson = new Gson();
  }

  /**
   * Gets user profile
   *
   * @param successConsumer consumer for successful response
   */
  public void getProfile(Consumer<SpotifyProfile> successConsumer) {
    getJson(successConsumer, SpotifyProfile.class, "https://api.spotify.com/v1/me");
  }

  /**
   * Gets user top artists
   *
   * @param successConsumer consumer for successful response
   * @param limit number of artists to retrieve
   * @param timeRange time range for top artists
   */
  public void getTopArtists(Consumer<TopArtists> successConsumer, int limit, TimeRange timeRange) {
    getJson(
        successConsumer,
        TopArtists.class,
        "https://api.spotify.com/v1/me/top/artists?limit="
            + limit
            + "&time_range="
            + timeRange.getValue());
  }

  /**
   * Gets user top tracks
   *
   * @param successConsumer consumer for successful response
   * @param limit number of tracks to retrieve
   * @param timeRange time range for top tracks
   */
  public void getTopTracks(Consumer<TopTracks> successConsumer, int limit, TimeRange timeRange) {
    getJson(
        successConsumer,
        TopTracks.class,
        "https://api.spotify.com/v1/me/top/tracks?limit="
            + limit
            + "&time_range="
            + timeRange.getValue()
            + "&market=US");
  }

  /**
   * Generates a wrapped object
   */
  public void genWrapped(int trackLimit, int artistLimit, int genreLimit, TimeRange timeRange, Consumer<Pair<Wrapped, Boolean>> successConsumer) {

    getProfile(profile -> getTopArtists(artists -> getTopTracks(tracks -> {
      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      List<String> genres = getGenres(artists, genreLimit);
      Wrapped wrapped = new Wrapped(artists, tracks, new ArrayList<>(genres), Date.from(Instant.now()), user.getUid(), timeRange);

      Log.d("WRAPPED_ACTIVITY", "Premium: " + profile.isPremium());

      successConsumer.accept(new Pair<>(wrapped, profile.isPremium()));
    }, trackLimit, timeRange), artistLimit, timeRange));

  }

  /**
   * Creates request for API data and retrieves JSON
   *
   * @param successConsumer consumer for successful response
   * @param url url for API request
   */
  private <T> void getJson(Consumer<T> successConsumer, Class<T> c, String url) {
    Request request =
        new Request.Builder().url(url).addHeader("Authorization", "Bearer " + accessToken).build();

    cancelCall();
    call = okHttpClient.newCall(request);


    okHttpClient
        .newCall(request)
        .enqueue(
            new Callback() {
              @Override
              public void onFailure(Call call, IOException e) {
                failureCallback.onFailure(e);
              }

              @Override
              public void onResponse(Call call, Response response) throws IOException {
                try {
                  final JSONObject jsonObject = new JSONObject(response.body().string());
                  T object = gson.fromJson(jsonObject.toString(), c);
                  successConsumer.accept(object);
                } catch (JSONException e) {
                  failureCallback.onFailure(e);
                }
              }
            });
  }

  /** Cancels the current API call */
  private void cancelCall() {
    if (call != null) {
      call.cancel();
    }
  }

  private List<String> getGenres(TopArtists artists, int count) {
    List<String> genres =
        artists.getItems().stream()
            .flatMap(a -> a.getGenres().stream())
            .collect(Collectors.toList());

    return genres.stream().collect(Collectors.groupingBy(i ->i, Collectors.counting()))
            .entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .map(Map.Entry::getKey)
            .limit(count)
            .collect(Collectors.toList());
  }
}
