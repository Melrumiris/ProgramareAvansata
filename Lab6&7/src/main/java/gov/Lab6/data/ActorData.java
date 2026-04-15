package gov.Lab6.data;

import gov.Lab6.data.builder.ActorBuilder;

public class ActorData extends DataRepresentationModel {
    private final String name;

    public String getName() {
        return name;
    }

    public ActorData(ActorBuilder builder){
        super(builder.getID());
        name = builder.getName();
    }
}
