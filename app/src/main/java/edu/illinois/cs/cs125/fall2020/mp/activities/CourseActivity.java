package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * Course activity class.
 */
public class CourseActivity extends AppCompatActivity implements Client.CourseClientCallbacks {
  private static final String TAG = CourseActivity.class.getSimpleName();
    /**
     * course activity method.
     *
     * @param savedInstanceState bundle
     */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final int headSize = 35;
    Intent intent = getIntent();
    String title = intent.getStringExtra("TITLE");
    String number = intent.getStringExtra("NUMBER");
    String department = intent.getStringExtra("DEPARTMENT");
    String year = intent.getStringExtra("YEAR");
    String semester = intent.getStringExtra("SEMESTER");

    Summary newSummary = new Summary(year, semester, department, number, title);

    Client client = Client.start();
    Course course = new Course();
    CompletableFuture<Course> completableFuture = new CompletableFuture<>();
    client.getCourse(newSummary, new Client.CourseClientCallbacks() {
        @Override
        public void courseResponse(final Summary summary, final Course course) {
          completableFuture.complete(course);
        }
    });
    try {
      course = completableFuture.get();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    TextView tv = new TextView(this);
    tv.setTextSize(headSize);
    tv.setText(department + " " + number + ":" + " " + title + " " + course.getDescription());
    setContentView(tv);

  }
}
