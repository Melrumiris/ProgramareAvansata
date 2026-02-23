package gov.Lab2;

public non-sealed class Restaurant extends Location {
    private float rating;
    private static final float MAX_RATING = 5.0f;
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

     public float getRating() {   return rating;  }
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
