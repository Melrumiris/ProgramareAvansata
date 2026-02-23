package gov.Lab2;

public non-sealed class City extends Location{
    private int population;
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

    public int getPopulation()
    {   return population;  }
    public City setPopulation(int population) {
        if (population < 0)
            throw new InvalidMetrics("Population cannot be negative");
        this.population = population;
        return this;
    }


}
