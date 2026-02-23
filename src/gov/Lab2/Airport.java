package gov.Lab2;

public non-sealed class Airport extends Location {
    private String webAddress;
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

     public String getWebAddress() {   return webAddress;  }
     public Airport setWebAddress(String webAddress)
     {   this.webAddress = webAddress;
         return this;                   }
}
