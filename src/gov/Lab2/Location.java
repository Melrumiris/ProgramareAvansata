package gov.Lab2;

import javax.sound.sampled.Port;

/**
 * Abstract sealed base class representing a geographic location on a 2D map.
 * <p>
 * Only {@link City}, {@link Airport}, and {@link Restaurant} are permitted subclasses.
 * Each location has an (x, y) coordinate pair and a name.
 * </p>
 */
sealed abstract public class Location permits City, Airport, Restaurant {
    private int x, y;
    private String name;

    /**
     * Enumeration of possible location types.
     */
    public enum Type {CITY, VILLAGE, AIRPORT, PORT, GAS_STATION, RESTAURANT, HOSPITAL, SCHOOL, PARK, MUSEUM, OTHER}

    /**
     * Returns the x-coordinate of this location.
     *
     * @return the x-coordinate
     */
    public int getX() {     return x;     }

    /**
     * Sets the x-coordinate of this location.
     *
     * @param x the new x-coordinate
     * @return this location instance for method chaining
     */
    public Location setX(int x)
    {       this.x = x;
            return this;      }

    /**
     * Returns the y-coordinate of this location.
     *
     * @return the y-coordinate
     */
    public int getY()       {     return y;     }

    /**
     * Sets the y-coordinate of this location.
     *
     * @param y the new y-coordinate
     * @return this location instance for method chaining
     */
    public Location setY(int y)
    {     this.y = y;
          return this;      }

    /**
     * Returns the name of this location.
     *
     * @return the location name
     */
    public String getName() {   return name;    }

    /**
     * Sets the name of this location.
     *
     * @param name the new name
     * @return this location instance for method chaining
     */
    public Location setName(String name)
    {  this.name = name;
       return this;         }

    /**
     * Returns the {@link Type} of this location.
     *
     * @return the location type
     */
    abstract public Type getType();

    /**
     * Constructs a new Location with the given coordinates and name.
     *
     * @param x    the x-coordinate
     * @param y    the y-coordinate
     * @param name the name of the location
     */
    public Location(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     * Returns a string with subclass-specific extra fields to be appended in {@link #toString()}.
     *
     * @return extra field string
     */
    abstract protected String getExtraString();

    @Override
    public String toString() {
        return name + ": {" +
                "\ntype=" + getType() +
                ",\nx=" + x +
                ",\ny=" + y +
                getExtraString() +
                "\n}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (x != location.x) return false;
        return name.equals(location.name);
    }
}
