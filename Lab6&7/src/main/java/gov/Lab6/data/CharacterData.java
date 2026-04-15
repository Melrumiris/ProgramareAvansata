package gov.Lab6.data;

import gov.Lab6.data.builder.CharacterBuilder;

public class CharacterData extends DataRepresentationModel {
    private final String name;
    private final ActorData actor;
    private final MovieData movie;

    public String getName() {
        return name;
    }

    public ActorData getActor() { return actor; }

    public MovieData getMovie() { return movie; }

    public CharacterData(CharacterBuilder builder){
        super(builder.getID());
        name = builder.getName();
        actor = builder.getActor();
        movie = builder.getMovie();
    }
}
