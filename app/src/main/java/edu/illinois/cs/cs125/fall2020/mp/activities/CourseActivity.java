package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
//import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityMainBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationConfig;
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
    final int headSize = 35;
    Intent intent = getIntent();
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);

//    String title = intent.getStringExtra("TITLE");
//    String number = intent.getStringExtra("NUMBER");
//    String department = intent.getStringExtra("DEPARTMENT");
//    String year = intent.getStringExtra("YEAR");
//    String semester = intent.getStringExtra("SEMESTER");
    //Summary newSummary = new Summary(year, semester, department, number, title);

    String path = intent.getStringExtra("COURSE");

    Summary newSummary = new Summary();
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      newSummary = mapper.readValue(path, Summary.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }


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

    //TextView tv = new TextView(this);
    binding.textview1.setTextSize(headSize);
    binding.textview1.setText(newSummary.getDepartment() + " " + newSummary.getNumber() + ":" + " " + newSummary.getTitle() + " " );
    binding.textview2.setText(course.getDescription());
    //tv.setText(department + " " + number + ":" + " " + title + " " + course.getDescription());
    //setContentView(tv);

  }
}
