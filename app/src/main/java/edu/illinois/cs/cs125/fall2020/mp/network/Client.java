package edu.illinois.cs.cs125.fall2020.mp.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * Course API client.
 *
 * <p>You will add functionality to the client as part of MP1 and MP2.
 */
public final class Client {
  private static final String TAG = Client.class.getSimpleName();
  private static final int INITIAL_CONNECTION_RETRY_DELAY = 1000;

  /**
   * Course API client callback interface.
   *
   * <p>Provides a way for the client to pass back information obtained from the course API server.
   */
  public interface CourseClientCallbacks {
    /**
     * Return course summaries for the given year and semester.
     *
     * @param year the year that was retrieved
     * @param semester the semester that was retrieved
     * @param summaries an array of course summaries
     */
    default void summaryResponse(String year, String semester, Summary[] summaries) {}

    /**
     *
     * @param summary the summary that was retrieved
     * @param course the course that was retrieved
     */
    default void courseResponse(Summary summary, Course course) {}

    /**
     *
     * @param summary the summary that was retrieved
     * @param rating the rating that was retrieved
     */
    default void yourRating(Summary summary, Rating rating) {}
    /**
     *
     * @param theString the test string that was retrieved
     */
    default void testPost(String theString) { }
  }

  /**
   * Retrieve course summaries for a given year and semester.
   *
   * @param year the year to retrieve
   * @param semester the semester to retrieve
   * @param callbacks the callback that will receive the result
   */
  public void getSummary(
      @NonNull final String year,
      @NonNull final String semester,
      @NonNull final CourseClientCallbacks callbacks) {
    String url = CourseableApplication.SERVER_URL + "summary/" + year + "/" + semester;
    StringRequest summaryRequest =
        new StringRequest(
            Request.Method.GET,
            url,
            response -> {
              try {
                Summary[] courses = objectMapper.readValue(response, Summary[].class);
                callbacks.summaryResponse(year, semester, courses);
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
            },
            error -> Log.e(TAG, error.toString()));
    requestQueue.add(summaryRequest);
  }
  /**
   * Testing functions.
   *
   * @param theString test string
   * @param callbacks the callback that will receive the result
   */
  public void setString(
          @NonNull final String theString,
          @NonNull final CourseClientCallbacks callbacks) {
    String url = CourseableApplication.SERVER_URL + "test/";
    StringRequest summaryRequest =
            new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> callbacks.testPost(response.toString()),
                    error -> Log.e(TAG, error.toString())) {
          @Override
          public byte[] getBody() throws AuthFailureError {
            return theString.getBytes();
          }
        };
    requestQueue.add(summaryRequest);
  }
  /**
   * Testing functions.
   *
   * @param callbacks the callback that will receive the result
   */
  public void getString(
          @NonNull final CourseClientCallbacks callbacks) {
    String url = CourseableApplication.SERVER_URL + "test/";
    StringRequest summaryRequest =
            new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> callbacks.testPost(response.toString()),
                    error -> Log.e(TAG, error.toString()));
    requestQueue.add(summaryRequest);
  }

  /**
   *
   * @param summary the summary to retrieve
   * @param callbacks the callback that will receive the result
   */
  public void getCourse(
      @NonNull final Summary summary,
      @NonNull final CourseClientCallbacks callbacks
  ) {
    String url = CourseableApplication.SERVER_URL + "course/" + summary.getYear() + "/" + summary.
            getSemester() + "/" + summary.getDepartment() + "/" + summary.getNumber();
    StringRequest summaryRequest =
            new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                      try {
                        Course course = objectMapper.readValue(response, Course.class);
                        callbacks.courseResponse(summary, course);
                      } catch (JsonProcessingException e) {
                        e.printStackTrace();
                      }
                    },
                    error -> Log.e(TAG, error.toString()));
    requestQueue.add(summaryRequest);
  }

  /**
   * Testing functions.
   *
   * @param summary   rating this summary
   * @param clientID  id string of rating
   * @param callbacks the callback that will receive the result
   */
  public void getRating(
          @NonNull final Summary summary,
          @NonNull final String clientID,
          @NonNull final CourseClientCallbacks callbacks
  ) {
    String url = CourseableApplication.SERVER_URL + "rating/" + summary.getYear() + "/" + summary.
            getSemester() + "/" + summary.getDepartment() + "/" + summary.getNumber() + "?client=" + clientID;
    StringRequest summaryRequest =
            new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                      try {
                        Rating rating = objectMapper.readValue(response, Rating.class);
                        callbacks.yourRating(summary, rating);
                      } catch (JsonProcessingException e) {
                        e.printStackTrace();
                      }
                    },
                    error -> Log.e(TAG, error.toString()));
    requestQueue.add(summaryRequest);
  }
  /**
   * Testing functions.
   *
   * @param summary   rating this summary
   * @param rating    Rating object
   * @param callbacks the callback that will receive the result
   */
  public void postRating(
          @NonNull final Summary summary,
          @NonNull final Rating rating,
          @NonNull final CourseClientCallbacks callbacks
  ) {
    String url = CourseableApplication.SERVER_URL + "rating/" + summary.getYear() + "/" + summary.
            getSemester() + "/" + summary.getDepartment() + "/" + summary.getNumber() + "?client=" + rating.getRating();
    StringRequest summaryRequest =
            new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> callbacks.yourRating(summary, rating),
                    error -> Log.e(TAG, error.toString())) {
          @Override
          public byte[] getBody() throws AuthFailureError {
            String r = "";
            ObjectMapper m = new ObjectMapper();
            m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
              r = m.writeValueAsString(rating);
            } catch (JsonProcessingException e) {
              e.printStackTrace();
            }
            return r.getBytes();
          }
    };
    requestQueue.add(summaryRequest);
  }

  private static Client instance;

  /**
   * Retrieve the course API client. Creates one if it does not already exist.
   *
   * @return the course API client
   */
  public static Client start() {
    if (instance == null) {
      instance = new Client();
    }
    return instance;
  }

  private static final int MAX_STARTUP_RETRIES = 8;
  private static final int THREAD_POOL_SIZE = 4;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RequestQueue requestQueue;

  /*
   * Set up our client, create the Volley queue, and establish a backend connection.
   */
  private Client() {
    // Configure the Volley queue used for our network requests
    Cache cache = new NoCache();
    Network network = new BasicNetwork(new HurlStack());
    HttpURLConnection.setFollowRedirects(true);
    requestQueue = new RequestQueue(cache, network, THREAD_POOL_SIZE, new ExecutorDelivery(
            Executors.newSingleThreadExecutor()));

    // Configure the Jackson object mapper to ignore unknown properties
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Make sure the backend URL is valid
    URL serverURL;
    try {
      serverURL = new URL(CourseableApplication.SERVER_URL);
    } catch (MalformedURLException e) {
      Log.e(TAG, "Bad server URL: " + CourseableApplication.SERVER_URL);
      return;
    }

    // Start a background thread to establish the server connection
    new Thread(
            () -> {
              for (int i = 0; i < MAX_STARTUP_RETRIES; i++) {
                try {
                  // Issue a HEAD request for the root URL
                  HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
                  connection.setRequestMethod("HEAD");
                  connection.connect();
                  connection.disconnect();
                  // Once this succeeds, we can start the Volley queue
                  requestQueue.start();
                  break;
                } catch (Exception e) {
                  Log.e(TAG, e.toString());
                }
                // If the connection fails, delay and then retry
                try {
                  Thread.sleep(INITIAL_CONNECTION_RETRY_DELAY);
                } catch (InterruptedException ignored) {
                }
              }
            })
        .start();
  }
}
