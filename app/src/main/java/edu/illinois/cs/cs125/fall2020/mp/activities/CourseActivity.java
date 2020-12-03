package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Course activity class.
 */
public class CourseActivity extends AppCompatActivity implements Client.CourseClientCallbacks {
  private static final String TAG = CourseActivity.class.getSimpleName();

  // Binding to the layout in activity_main.xml
  private ActivityCourseBinding binding;
    /**
     * course activity method.
     *
     * @param savedInstanceState bundle
     */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //final int headSize = 35;
    Intent intent = getIntent();
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);
    String path = intent.getStringExtra("COURSE");
    Summary newSummary = new Summary();
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      newSummary = mapper.readValue(path, Summary.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    CourseableApplication application = (CourseableApplication) getApplication();
    Client client = Client.start();
    Course courseObj = new Course();
    CompletableFuture<Course> completableFuture = new CompletableFuture<>();
    client.getCourse(newSummary, new Client.CourseClientCallbacks() {
        @Override
        public void courseResponse(final Summary summary, final Course course) {
          completableFuture.complete(course);
        }
    });
    try {
      courseObj = completableFuture.get();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    CompletableFuture<Rating> completableFutureTwo = new CompletableFuture<>();
    final Summary s = newSummary;
    binding.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
      @Override
      public void onRatingChanged(
              final RatingBar ratingBar, final float rating, final boolean fromUser) {
        application.
                getCourseClient().
                postRating(s, new Rating(application.getClientID(), (
                double) rating), new Client.CourseClientCallbacks() {
                  @Override
                  public void yourRating(final Summary summary, final Rating rating) {
                    completableFutureTwo.complete(rating);
                  }
                });
      }
    });
    application.getCourseClient().getRating(s, application.getClientID(), new Client.
            CourseClientCallbacks() {
      @Override
      public void yourRating(final Summary summary, final Rating rating) {
        completableFutureTwo.complete(rating);
      }
    });
    Rating rating1 = new Rating();
    try {
      rating1 = completableFutureTwo.get();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String description = courseObj.getDescription();
    //binding.textview1.setTextSize(headSize);
    binding.textview1.setText(newSummary.getDepartment() + " " + newSummary.getNumber()
            + ":" + " " + newSummary.getTitle() + " ");
    binding.textview2.setText(description);
    binding.rating.setRating((float) rating1.getRating());
    System.out.println(rating1.getRating());
  }
}
