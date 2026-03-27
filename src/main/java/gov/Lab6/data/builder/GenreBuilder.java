package gov.Lab6.data.builder;

import gov.Lab6.data.GenreData;
import gov.Lab6.exceptions.NullDataException;

public class GenreBuilder extends DataBuilder<GenreData> {
    private String name;

    public String getName() {
        return name;
    }

    public GenreBuilder setID(int id){
        super.setID(id);
        return this;
    }

    public GenreBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public GenreBuilder(GenreData data) {
        setID(data.getID());
        name = data.getName();
    }
    public GenreBuilder(){}

    @Override
    public GenreData build() throws NullDataException {
        if (getID() == 0) throw new NullDataException("ID must be set");
        if (name == null || name.isEmpty()) throw new NullDataException("Name must be set");
        return new GenreData(this);
    }
}
