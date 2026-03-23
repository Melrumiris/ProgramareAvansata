package gov.Lab6.data.builder;

import gov.Lab6.data.DataRepresentationModel;
import gov.Lab6.exceptions.NullDataException;

public abstract class DataBuilder<T extends DataRepresentationModel> {
    private int id;

    public int getID() {
        return id;
    }

    public DataBuilder<T> setID(int id) {
        this.id = id;
        return this;
    }

    public T build() throws NullDataException {
        if (id == 0) throw new NullDataException("ID must be set");
        return null;
    }
}
