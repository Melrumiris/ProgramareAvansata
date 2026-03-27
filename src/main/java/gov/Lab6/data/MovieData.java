package gov.Lab6.data;

import gov.Lab6.data.builder.MovieBuilder;

import java.sql.Date;
import java.sql.Time;

public class MovieData extends DataRepresentationModel {
    private final String title;
    private final Date releaseDate;
    private final Time duration;
    private final byte score;
    private final GenreData genre;

    public String getTitle() {
        return title;
    }
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
    public MovieData(MovieBuilder builder){
        super(builder.getID());
        title = builder.getTitle();
        releaseDate = builder.getReleaseDate();
        duration = builder.getDuration();
        score = builder.getScore();
        genre = builder.getGenre();
    }
}
