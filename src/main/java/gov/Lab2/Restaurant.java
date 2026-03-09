package gov.Lab2;

/**
 * Represents a restaurant location on the map.
 * <p>
 * A restaurant has an average star rating in addition to the base {@link Location} properties.
 * The rating is computed as the average of the provided individual ratings.
 * </p>
 */
public non-sealed class Restaurant extends Location {
    private float rating;
    private static final float MAX_RATING = 5.0f;

    /**
     * Constructs a new Restaurant with an averaged rating.
     *
     * @param x          the x-coordinate
     * @param y          the y-coordinate
     * @param name       the name of the restaurant
     * @param ratingList an array of individual ratings, each between 0 and {@value #MAX_RATING}
     * @throws InvalidMetrics if any rating is outside the valid range
     */
    public Restaurant(int x, int y, String name, int[] ratingList) {
        super(x, y, name);
        setRating(ratingList);
    }

    @Override
    protected String getExtraString() {
        return ",\nrating=" + rating + " stars";
    }
    @Override
    public Type getType() {
        return Type.RESTAURANT;
    }

    /**
     * Returns the average star rating of this restaurant.
     *
     * @return the average rating
     */
    public float getRating() {   return rating;  }

    /**
     * Calculates and sets the average rating from an array of individual ratings.
     *
     * @param ratingList an array of individual ratings, each between 0 and {@value #MAX_RATING}
     * @return this restaurant instance for method chaining
     * @throws InvalidMetrics if any rating is outside the valid range
     */
    public Restaurant setRating(int[] ratingList)
    {   this.rating = 0;
        for (int rating : ratingList) {
            if (rating < 0 || rating > MAX_RATING)
                throw new InvalidMetrics("Rating must be between 0 and " + MAX_RATING + "stars");
            this.rating += rating;
        }
        this.rating /= ratingList.length;
        return this;                   }
}
