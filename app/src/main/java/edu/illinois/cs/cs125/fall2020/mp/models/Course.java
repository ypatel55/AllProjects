package edu.illinois.cs.cs125.fall2020.mp.models;

/**
 * Model course that extends summary.
 */
public class Course extends Summary {
  private String description;
  /**
   * Create an empty Course.
   */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Course() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setDescription the description for the course
   */
  public Course(final String setDescription) {
    description = setDescription;
  }
  /**
   * Get the description for this Course.
   *
   * @return the description for this Course
   */
  public final String getDescription() {
    return description;
  }
}

