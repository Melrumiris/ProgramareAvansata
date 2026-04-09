package gov.Lab2;

/**
 * Represents an airport location on the map.
 * <p>
 * An airport has a web address in addition to the base {@link Location} properties.
 * </p>
 */
public non-sealed class Airport extends Location {
    private String webAddress;

    /**
     * Constructs a new Airport.
     *
     * @param x          the x-coordinate
     * @param y          the y-coordinate
     * @param name       the name of the airport
     * @param webAddress the official web address of the airport
     */
    public Airport(int x, int y,String name, String webAddress) {
        super(x,y,name);
        this.webAddress = webAddress;
    }

    @Override
    protected String getExtraString() {
        return ",\nwebAddress='" + webAddress + '\'';
    }
    @Override
    public Type getType() {
        return Type.AIRPORT;
    }

    /**
     * Returns the web address of this airport.
     *
     * @return the web address
     */
    public String getWebAddress() {   return webAddress;  }

    /**
     * Sets the web address of this airport.
     *
     * @param webAddress the new web address
     * @return this airport instance for method chaining
     */
    public Airport setWebAddress(String webAddress)
    {   this.webAddress = webAddress;
        return this;                   }
}
