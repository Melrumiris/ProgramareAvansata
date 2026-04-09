package gov.Lab6.data.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.Lab6.dao.GenreDAO;
import gov.Lab6.data.GenreData;
import gov.Lab6.data.MovieData;
import gov.Lab6.exception.IllegalDataException;
import gov.Lab6.exception.NullDataException;

import java.sql.Date;
import java.sql.Time;

public class MovieBuilder extends DataBuilder<MovieData> {
    private String title;
    private Date releaseDate;
    private Time duration;
    private byte score = -1;
    private GenreData genre;

    public Date getReleaseDate() {
        return releaseDate;
    }
    public Time getDuration() {
        return duration;
    }
    public byte getScore() {
        return score;
    }
    public GenreData getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public MovieBuilder setID(int id){
        super.setID(id);
        return this;
    }

    public MovieBuilder setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }
    public MovieBuilder setDuration(Time duration) {
        this.duration = duration;
        return this;
    }
    public MovieBuilder setScore(Integer score) throws IllegalDataException {
        if (score == null) {
            this.score = -1;
            return this;
        }
        if (score.byteValue() < 0 || score.byteValue() > 10) throw new IllegalDataException("Score must be between 0 and 10");
        this.score = score.byteValue();
        return this;
    }
    public MovieBuilder setGenre(GenreData genre) {
        this.genre = genre;
        return this;
    }
    @JsonProperty("genre")
    public MovieBuilder setGenreByName(String name) {
        GenreDAO dao = new GenreDAO();
        this.genre = dao.getByName(name).orElse(null);
        return this;
    }
    public MovieBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MovieBuilder(MovieData data) {
        setID(data.getId());
        title = data.getTitle();
        releaseDate = getReleaseDate();
        duration = data.getDuration();
        score = data.getScore();
        genre = data.getGenre();
    }
    public MovieBuilder(){}

    @Override
    public MovieData build() throws NullDataException {
        if (getID() == 0) throw new NullDataException("ID must be set");
        if (title == null || title.isEmpty()) throw new NullDataException("Name must be set");
        if (genre == null) throw new NullDataException("Genre must be set");
        return new MovieData(this);
    }
}
