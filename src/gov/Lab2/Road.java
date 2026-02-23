package gov.Lab2;

public class Road {
    public enum Type {HIGHWAY, STREET, AVENUE, BOULEVARD, LANE, ALLEY, OTHER}
    int length, speedLimit;
    Type type;


    public int getSpeedLimit() {    return speedLimit;  }
    public Road setSpeedLimit(int speedLimit)
    {     if (speedLimit < 0)
            throw new InvalidMetrics("Speed limit cannot be negative");
          this.speedLimit = speedLimit;
          return this;                 }

    public Type getType() {     return type;    }
    public Road setType(Type type)
    {    this.type = type;
         return this;         }

    public int getLength() {    return length;  }
    public Road setLength(int length, Location start, Location end)
    {   if (length < Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)))
            throw new InvalidMetrics("Length is smaller than possible");
        this.length = length;
        return this;            }
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
