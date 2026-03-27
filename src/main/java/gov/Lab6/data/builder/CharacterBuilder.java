package gov.Lab6.data.builder;

import gov.Lab6.data.ActorData;
import gov.Lab6.data.CharacterData;
import gov.Lab6.data.MovieData;
import gov.Lab6.exceptions.NullDataException;

import javax.xml.crypto.Data;

public class CharacterBuilder extends DataBuilder<CharacterData> {
    private String name;
    private ActorData actor;
    private MovieData movie;

    public String getName() {
        return name;
    }
    public ActorData getActor() { return actor; }
    public MovieData getMovie() { return movie; }

    public CharacterBuilder setID(int id){
        super.setID(id);
        return this;
    }

    public CharacterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterBuilder setActor(ActorData actor) {
        this.actor = actor;
        return this;
    }

    public CharacterBuilder setMovie(MovieData movie) {
        this.movie = movie;
        return this;
    }

    @Override
    public CharacterData build() throws NullDataException {
        if (getID() == 0) throw new NullDataException("ID must be set");
        if (name == null || name.isEmpty()) throw new NullDataException("Name must be set");
        if (movie == null) throw new NullDataException("Movie must be set");
        return new CharacterData(this);
    }
}
