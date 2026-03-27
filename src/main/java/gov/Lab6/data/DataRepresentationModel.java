package gov.Lab6.data;

import gov.Lab6.data.builder.DataBuilder;

public abstract class DataRepresentationModel {
    protected final int id;

    protected DataRepresentationModel(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        DataRepresentationModel other = (DataRepresentationModel) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
