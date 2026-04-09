package gov.Lab6.data;

import gov.Lab6.data.builder.GenreBuilder;

public class GenreData extends DataRepresentationModel {
    private final String name;

    public String getName() {
        return name;
    }
    
    public GenreData(GenreBuilder builder){
        super(builder.getID());
        name = builder.getName();
    }
}
