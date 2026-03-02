package gov.Lab2;

/**
 * Represents a road connecting two {@link Location}s on the map.
 * <p>
 * A road has a length (in km), a speed limit (in km/h), and a {@link Type}.
 * The length must be at least the Euclidean distance between the two endpoints.
 * </p>
 */
public class Road {

    /**
     * Enumeration of road types.
     */
    public enum Type {HIGHWAY, STREET, AVENUE, BOULEVARD, LANE, ALLEY, OTHER}

    int length, speedLimit;
    Type type;

    /**
     * Returns the speed limit of this road in km/h.
     *
     * @return the speed limit
     */
    public int getSpeedLimit() {    return speedLimit;  }

    /**
     * Sets the speed limit of this road.
     *
     * @param speedLimit the new speed limit (must be non-negative)
     * @return this road instance for method chaining
     * @throws InvalidMetrics if the speed limit is negative
     */
    public Road setSpeedLimit(int speedLimit)
    {     if (speedLimit < 0)
            throw new InvalidMetrics("Speed limit cannot be negative");
          this.speedLimit = speedLimit;
          return this;                 }

    /**
     * Returns the type of this road.
     *
     * @return the road type
     */
    public Type getType() {     return type;    }

    /**
     * Sets the type of this road.
     *
     * @param type the new road type
     * @return this road instance for method chaining
     */
    public Road setType(Type type)
    {    this.type = type;
         return this;         }

    /**
     * Returns the length of this road in km.
     *
     * @return the length
     */
    public int getLength() {    return length;  }

    /**
     * Sets the length of this road, validating that it is not shorter than the
     * Euclidean distance between {@code start} and {@code end}.
     *
     * @param length the new length in km
     * @param start  the starting location
     * @param end    the ending location
     * @return this road instance for method chaining
     * @throws InvalidMetrics if {@code length} is less than the straight-line distance
     */
    public Road setLength(int length, Location start, Location end)
    {   if (length < Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)))
            throw new InvalidMetrics("Length is smaller than possible");
        this.length = length;
        return this;            }

    /**
     * Constructs a new Road between two locations.
     *
     * @param start      the starting location
     * @param end        the ending location
     * @param length     the road length in km (must be &ge; Euclidean distance between start and end)
     * @param speedLimit the speed limit in km/h (must be non-negative)
     * @param type       the type of road
     * @throws InvalidMetrics if length or speed limit constraints are violated
     */
    public Road (Location start, Location end, int length, int speedLimit, Type type) {
        setLength(length, start, end);
        setSpeedLimit(speedLimit);
        setType(type);
    }

    @Override
    public String toString() {
            return "Road: {" +
                    "\ntype=" + type +
                    ",\nlength=" + length + " kms" +
                    ",\nspeedLimit=" + speedLimit + " km/min"+
                    "\n}";
        }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Road road = (Road) o;

        if (length != road.length) return false;
        if (speedLimit != road.speedLimit) return false;
        return type == road.type;
    }
}
