package gov.Lab6.data;

import gov.Lab6.data.builder.GenreBuilder;

public class GenreData extends DataRepresentationModel {
    private String name;

    public String getName() {
        return name;
    }

    public GenreData setName(String name) {
        this.name = name;
        return this;
    }

    public GenreData(GenreBuilder builder){
        id = builder.getID();
        name = builder.getName();
    }
}
