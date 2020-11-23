package edu.illinois.cs.cs125.fall2020.mp.models;


/**
 * Rating class for starting client ratings.
 */
public class Rating {
  /** rating indicating that the course has not been rated yet. */
  public static final double NOT_RATED = -1.0;
  private String id;
  private double rating;
  /**
   * Create an empty Rating.
   */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Rating() {}

  /**
   * Create a Rating with the provided fields.
   *
   * @param setId     the description for the course
   * @param setRating the description for the course
   */
  public Rating(final String setId, final double setRating) {
    id = setId;
    rating = setRating;
  }
  /**
   * Get the Rating for this Course.
   *
   * @return the id for this Course
   */
  public String getId() {
    return id;
  }
  /**
   * Get the Rating for this Course.
   *
   * @return the rating for this Course
   */
  public double getRating() {
    return rating;
  }
}
