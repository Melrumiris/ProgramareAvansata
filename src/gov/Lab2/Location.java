package gov.Lab2;

public class Location {
    private int x, y;
    private String name;
    public enum Type {CITY, VILLAGE, AIRPORT, PORT, GAS_STATION, RESTAURANT, HOSPITAL, SCHOOL, PARK, MUSEUM, OTHER}
    Type type;

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

    public Type getType() {   return type;    }
    public Location setType(Type type)
    {  this.type = type;
       return this;         }

    public Location(int x, int y, String name, Type type) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name + ": {" +
                "\ntype=" + type +
                ",\nx=" + x +
                ",\ny=" + y +
                "\n}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!name.equals(location.name)) return false;
        return type == location.type;
    }
}
