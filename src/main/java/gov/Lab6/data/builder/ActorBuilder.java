package gov.Lab6.data.builder;

import gov.Lab6.data.ActorData;
import gov.Lab6.data.GenreData;
import gov.Lab6.exceptions.NullDataException;

public class ActorBuilder extends DataBuilder<ActorData> {
    private String name;

    public String getName() {
        return name;
    }

    public ActorBuilder setID(int id){
        super.setID(id);
        return this;
    }

    public ActorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ActorBuilder(ActorData data) {
        setID(data.getID());
        name = data.getName();
    }
    public ActorBuilder(){}

    @Override
    public ActorData build() throws NullDataException {
        if (getID() == 0) throw new NullDataException("ID must be set");
        if (name == null || name.isEmpty()) throw new NullDataException("Name must be set");
        return new ActorData(this);
    }
}
