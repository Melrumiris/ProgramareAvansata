package gov.Lab2;

import javax.sound.sampled.Port;

sealed abstract public class Location permits City, Airport, Restaurant {
    private int x, y;
    private String name;
    public enum Type {CITY, VILLAGE, AIRPORT, PORT, GAS_STATION, RESTAURANT, HOSPITAL, SCHOOL, PARK, MUSEUM, OTHER}

    public int getX() {     return x;     }
    public Location setX(int x)
    {       this.x = x;
            return this;      }

    public int getY()       {     return y;     }
    public Location setY(int y)
    {     this.y = y;
          return this;      }

    public String getName() {   return name;    }
    public Location setName(String name)
    {  this.name = name;
       return this;         }

    abstract public Type getType();

    public Location(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

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
