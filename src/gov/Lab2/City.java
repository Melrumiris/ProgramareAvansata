package gov.Lab2;

/**
 * Represents a city location on the map.
 * <p>
 * A city has a population in addition to the base {@link Location} properties.
 * </p>
 */
public non-sealed class City extends Location{
    private int population;

    /**
     * Constructs a new City.
     *
     * @param x          the x-coordinate
     * @param y          the y-coordinate
     * @param name       the name of the city
     * @param population the population of the city (must be non-negative)
     * @throws InvalidMetrics if population is negative
     */
    public City(int x, int y, String name, int population) {
        super(x, y, name);
        this.population = population;
    }

    @Override
    public Type getType() {
        return Type.CITY;
    }
    @Override
    protected String getExtraString() {
        return ",\npopulation=" + population;
    }

    /**
     * Returns the population of this city.
     *
     * @return the population
     */
    public int getPopulation()
    {   return population;  }

    /**
     * Sets the population of this city.
     *
     * @param population the new population (must be non-negative)
     * @return this city instance for method chaining
     * @throws InvalidMetrics if population is negative
     */
    public City setPopulation(int population) {
        if (population < 0)
            throw new InvalidMetrics("Population cannot be negative");
        this.population = population;
        return this;
    }


}
